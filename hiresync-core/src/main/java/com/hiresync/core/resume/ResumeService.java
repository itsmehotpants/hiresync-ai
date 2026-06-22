package com.hiresync.core.resume;

import com.hiresync.common.events.ResumeUploadedEvent;
import com.hiresync.common.exception.HireSyncException;
import com.hiresync.core.entity.Resume;
import com.hiresync.core.kafka.KafkaEventPublisher;
import com.hiresync.core.repository.ResumeRepository;
import com.hiresync.core.resume.dto.ResumeDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.apache.poi.xwpf.extractor.XWPFWordExtractor;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.tika.Tika;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.Instant;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class ResumeService {

    private static final long MAX_FILE_SIZE = 5 * 1024 * 1024L; // 5 MB
    private static final Set<String> ALLOWED_MIME_TYPES = Set.of(
            "application/pdf",
            "application/vnd.openxmlformats-officedocument.wordprocessingml.document"
    );

    private final ResumeRepository resumeRepository;
    private final KafkaEventPublisher eventPublisher;
    private final Tika tika = new Tika();

    @Value("${app.upload.dir:./uploads}")
    private String uploadDir;

    public ResumeDTO uploadResume(MultipartFile file, String label, String userId) throws IOException {
        // 1. Validate file size
        if (file.getSize() > MAX_FILE_SIZE) {
            throw HireSyncException.badRequest("File size exceeds 5 MB limit");
        }

        // 2. Detect MIME type using Apache Tika (do NOT trust Content-Type header)
        String mimeType = tika.detect(file.getInputStream());
        if (!ALLOWED_MIME_TYPES.contains(mimeType)) {
            throw HireSyncException.badRequest(
                    "Invalid file type. Only PDF and DOCX are allowed. Detected: " + mimeType);
        }

        // 3. Generate safe filename
        String safeFilename = userId + "_" + Instant.now().toEpochMilli() + "_" + sanitizeFilename(file.getOriginalFilename());

        // 4. Save to disk
        Path userDir = Paths.get(uploadDir, userId);
        Files.createDirectories(userDir);
        Path filePath = userDir.resolve(safeFilename);
        Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

        // 5. Extract text
        String rawText = extractText(file, mimeType);

        // 6. Save entity
        Resume resume = new Resume();
        resume.setUserId(userId);
        resume.setLabel(label != null ? label : file.getOriginalFilename());
        resume.setFileName(safeFilename);
        resume.setFilePath(filePath.toString());
        resume.setFileSize(file.getSize());
        resume.setContentType(mimeType);
        resume.setRawText(rawText);
        resume = resumeRepository.save(resume);

        // 7. Publish event — AI service will create embeddings asynchronously
        final String resumeId = resume.getId();
        eventPublisher.publishResumeUploaded(ResumeUploadedEvent.builder()
                .userId(userId)
                .resumeId(resumeId)
                .rawText(rawText)
                .label(resume.getLabel())
                .build());

        log.info("Resume {} uploaded for user {}, text extracted ({} chars)",
                resumeId, userId, rawText != null ? rawText.length() : 0);
        return toDTO(resume);
    }

    @Transactional(readOnly = true)
    public List<ResumeDTO> getResumes(String userId) {
        return resumeRepository.findByUserIdOrderByCreatedAtDesc(userId)
                .stream().map(this::toDTO).toList();
    }

    @Transactional(readOnly = true)
    public Resume getResumeEntity(String resumeId, String userId) {
        return resumeRepository.findByIdAndUserId(resumeId, userId)
                .orElseThrow(() -> HireSyncException.forbidden());
    }

    public void deleteResume(String resumeId, String userId) {
        Resume resume = resumeRepository.findByIdAndUserId(resumeId, userId)
                .orElseThrow(() -> HireSyncException.forbidden());
        try {
            Files.deleteIfExists(Paths.get(resume.getFilePath()));
        } catch (IOException e) {
            log.warn("Could not delete file {}: {}", resume.getFilePath(), e.getMessage());
        }
        resumeRepository.delete(resume);
    }

    public ResumeDTO setPrimary(String resumeId, String userId) {
        resumeRepository.clearPrimaryForUser(userId);
        Resume resume = resumeRepository.findByIdAndUserId(resumeId, userId)
                .orElseThrow(() -> HireSyncException.forbidden());
        resume.setPrimary(true);
        return toDTO(resumeRepository.save(resume));
    }

    private String extractText(MultipartFile file, String mimeType) {
        try {
            if ("application/pdf".equals(mimeType)) {
                try (PDDocument doc = org.apache.pdfbox.Loader.loadPDF(new org.apache.pdfbox.io.RandomAccessReadBuffer(file.getInputStream()))) {
                    return new PDFTextStripper().getText(doc);
                }
            } else {
                try (XWPFDocument doc = new XWPFDocument(file.getInputStream())) {
                    return new XWPFWordExtractor(doc).getText();
                }
            }
        } catch (IOException e) {
            log.error("Failed to extract text from resume: {}", e.getMessage());
            return "";
        }
    }

    private String sanitizeFilename(String filename) {
        if (filename == null) return "resume";
        return filename.replaceAll("[^a-zA-Z0-9._-]", "_");
    }

    private ResumeDTO toDTO(Resume r) {
        return new ResumeDTO(r.getId(), r.getLabel(), r.getFileName(),
                r.getFileSize(), r.getContentType(), r.isPrimary(),
                r.getVersionNumber(), r.getCreatedAt());
    }
}
