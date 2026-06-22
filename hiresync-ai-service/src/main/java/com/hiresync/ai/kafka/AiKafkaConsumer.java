package com.hiresync.ai.kafka;

import com.hiresync.common.events.AiReviewRequestedEvent;
import com.hiresync.common.events.KafkaEvent;
import com.hiresync.common.events.ResumeUploadedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class AiKafkaConsumer {

    private final ResumeEmbeddingPipeline embeddingPipeline;
    private final KafkaTemplate<String, Object> kafkaTemplate;

    @KafkaListener(topics = "resume.uploaded", groupId = "ai-service-group")
    public void onResumeUploaded(KafkaEvent event) {
        if (!(event instanceof ResumeUploadedEvent e)) return;
        log.info("Processing resume embedding for resumeId={}", e.getResumeId());
        try {
            embeddingPipeline.processResume(e.getResumeId(), e.getUserId(), e.getRawText());
        } catch (Exception ex) {
            log.error("Failed to process resume embeddings for {}: {}", e.getResumeId(), ex.getMessage());
        }
    }

    @KafkaListener(topics = "ai.review.requested", groupId = "ai-service-group")
    public void onAiReviewRequested(KafkaEvent event) {
        if (!(event instanceof AiReviewRequestedEvent e)) return;
        log.info("AI review requested for jobId={}", e.getJobApplicationId());
        // Publish completed event back — actual analysis triggered by REST API
        // This is a placeholder; full implementation in the REST controller
    }
}
