package com.hiresync.ai.service;

import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.UserMessage;
import dev.langchain4j.service.V;
import dev.langchain4j.service.spring.AiService;

@AiService
public interface CoverLetterService {

    @SystemMessage("""
        You are an expert career writer specializing in compelling cover letters.
        Write in the specified tone. Use the exact company name and role title provided.
        Do not use generic phrases like "I am writing to express my interest".
        Start with a hook that references something specific about the company or role.
        Keep it to 3-4 paragraphs, under 400 words.
        Do NOT include a subject line or letter header — just the body paragraphs.
        """)
    @UserMessage("""
        Write a cover letter for the role of {{role}} at {{company}}.
        
        Tone: {{tone}}
        Candidate resume summary: {{resumeSummary}}
        Key job requirements: {{requirements}}
        Why they want this specific company: {{motivation}}
        """)
    String generateCoverLetter(
            @V("role") String roleTitle,
            @V("company") String companyName,
            @V("tone") String tone,
            @V("resumeSummary") String resumeSummary,
            @V("requirements") String keyRequirements,
            @V("motivation") String motivation
    );
}
