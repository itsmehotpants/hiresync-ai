package com.hiresync.ai.kafka;

import dev.langchain4j.data.embedding.Embedding;
import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.store.embedding.EmbeddingStore;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

@Component
@RequiredArgsConstructor
@Slf4j
public class ResumeEmbeddingPipeline {

    private final EmbeddingModel embeddingModel;
    private final EmbeddingStore<TextSegment> embeddingStore;

    private static final Pattern SECTION_PATTERN = Pattern.compile(
            "(?i)(EDUCATION|EXPERIENCE|WORK EXPERIENCE|SKILLS|TECHNICAL SKILLS|" +
            "PROJECTS|CERTIFICATIONS|SUMMARY|OBJECTIVE|PUBLICATIONS)",
            Pattern.MULTILINE
    );

    public void processResume(String resumeId, String userId, String rawText) {
        if (rawText == null || rawText.isBlank()) {
            log.warn("Empty raw text for resume {}", resumeId);
            return;
        }

        List<TextSegment> chunks = chunkBySection(rawText, resumeId, userId);
        log.info("Created {} chunks for resume {}", chunks.size(), resumeId);

        // Embed and store each chunk
        for (TextSegment chunk : chunks) {
            try {
                Embedding embedding = embeddingModel.embed(chunk.text()).content();
                embeddingStore.add(embedding, chunk);
            } catch (Exception e) {
                log.error("Failed to embed chunk for resume {}: {}", resumeId, e.getMessage());
            }
        }
        log.info("Completed embedding pipeline for resume {}", resumeId);
    }

    private List<TextSegment> chunkBySection(String text, String resumeId, String userId) {
        List<TextSegment> chunks = new ArrayList<>();
        String[] lines = text.split("\\n");
        StringBuilder currentChunk = new StringBuilder();
        String currentSection = "GENERAL";
        int chunkIndex = 0;

        for (String line : lines) {
            if (SECTION_PATTERN.matcher(line.trim()).matches() && currentChunk.length() > 50) {
                // Save current chunk
                chunks.add(TextSegment.from(
                        currentChunk.toString().trim(),
                        dev.langchain4j.data.document.Metadata.from("resumeId", resumeId)
                ));
                currentChunk = new StringBuilder();
                currentSection = line.trim().toUpperCase();
                chunkIndex++;
            }

            currentChunk.append(line).append("\n");

            // Max chunk size: ~400 words
            if (currentChunk.toString().split("\\s+").length >= 400) {
                chunks.add(TextSegment.from(
                        currentChunk.toString().trim(),
                        dev.langchain4j.data.document.Metadata.from("resumeId", resumeId)
                ));
                currentChunk = new StringBuilder();
                chunkIndex++;
            }
        }

        // Last chunk
        if (currentChunk.length() > 50) {
            chunks.add(TextSegment.from(
                    currentChunk.toString().trim(),
                    dev.langchain4j.data.document.Metadata.from("resumeId", resumeId)
            ));
        }

        return chunks;
    }
}
