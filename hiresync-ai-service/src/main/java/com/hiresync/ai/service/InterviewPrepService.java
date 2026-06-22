package com.hiresync.ai.service;

import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.UserMessage;
import dev.langchain4j.service.V;
import dev.langchain4j.service.spring.AiService;

@AiService
public interface InterviewPrepService {

    @SystemMessage("""
        You are an expert technical interviewer and career coach.
        Generate interview preparation questions based on the job description and interview history.
        
        Return ONLY valid JSON with this exact structure:
        {
          "questions": [
            {
              "question": "the interview question",
              "category": "TECHNICAL|BEHAVIORAL|SITUATIONAL|COMPANY",
              "difficulty": "EASY|MEDIUM|HARD",
              "framework": "suggested answer framework (e.g., STAR method)",
              "tips": "key points to cover"
            }
          ],
          "focusAreas": ["area1", "area2"],
          "prepAdvice": "overall preparation advice"
        }
        Generate exactly 10 questions.
        """)
    @UserMessage("""
        Job Description: {{jobDescription}}
        
        Previous Interview History: {{interviewHistory}}
        
        Company: {{company}}
        Role: {{role}}
        """)
    String generateInterviewQuestions(
            @V("jobDescription") String jobDescription,
            @V("interviewHistory") String interviewHistory,
            @V("company") String company,
            @V("role") String role
    );
}
