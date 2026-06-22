package com.hiresync.ai.service;

import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.UserMessage;
import dev.langchain4j.service.V;
import dev.langchain4j.service.spring.AiService;

@AiService
public interface ResumeAnalysisService {

    @SystemMessage("""
        You are an expert ATS (Applicant Tracking System) analyst and career coach.
        Analyze the provided resume against the job description.
        
        Return ONLY valid JSON with this exact structure, no markdown code blocks, no explanation:
        {
          "overallScore": <0-100 integer>,
          "atsPrediction": "<LIKELY_PASS|BORDERLINE|LIKELY_FAIL>",
          "sectionScores": {
            "summary": <0-100>,
            "skills": <0-100>,
            "experience": <0-100>,
            "education": <0-100>
          },
          "matchedKeywords": ["keyword1", "keyword2"],
          "missingKeywords": ["keyword1", "keyword2"],
          "strengths": ["strength1", "strength2", "strength3"],
          "improvements": ["improvement1", "improvement2", "improvement3"],
          "summary": "2-3 sentence human-readable summary"
        }
        """)
    String analyzeResume(
            @UserMessage @V("resume") String resumeText,
            @V("jd") String jobDescription
    );
}
