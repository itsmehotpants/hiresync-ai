package com.hiresync.notification.email;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.retry.annotation.Retryable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailService {

    private final JavaMailSender mailSender;

    @Async
    @Retryable(maxAttempts = 3)
    public void sendHtmlEmail(String to, String subject, String htmlBody) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            helper.setFrom("noreply@hiresync.ai");
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(htmlBody, true);
            mailSender.send(message);
            log.info("Email sent to {}: {}", to, subject);
        } catch (MessagingException e) {
            log.error("Failed to send email to {}: {}", to, e.getMessage());
            throw new RuntimeException("Email send failed", e);
        }
    }

    public String buildWelcomeEmail(String name) {
        return """
            <html><body style='font-family: Arial, sans-serif; max-width: 600px; margin: 0 auto;'>
            <div style='background: linear-gradient(135deg, #667eea 0%, #764ba2 100%); padding: 40px; border-radius: 12px 12px 0 0;'>
              <h1 style='color: white; margin: 0;'>🎯 Welcome to HireSync AI!</h1>
            </div>
            <div style='background: #f9f9f9; padding: 40px; border-radius: 0 0 12px 12px;'>
              <p>Hi <strong>%s</strong>!</p>
              <p>Your AI-powered job hunt command center is ready. Here's what you can do:</p>
              <ul>
                <li>📋 <strong>Track applications</strong> on your Kanban board</li>
                <li>🤖 <strong>Get AI feedback</strong> on your resume vs. any job description</li>
                <li>💬 <strong>Chat with your AI coach</strong> for personalized advice</li>
                <li>📊 <strong>Analyze your job hunt</strong> with smart analytics</li>
              </ul>
              <a href='http://localhost:3000' style='display: inline-block; background: #667eea; color: white; padding: 12px 24px; border-radius: 8px; text-decoration: none; margin-top: 16px;'>Open HireSync AI →</a>
            </div>
            </body></html>
            """.formatted(name);
    }

    public String buildOfferEmail(String name, String company, String role) {
        return """
            <html><body style='font-family: Arial, sans-serif; max-width: 600px; margin: 0 auto;'>
            <div style='background: linear-gradient(135deg, #11998e 0%, #38ef7d 100%); padding: 40px; border-radius: 12px 12px 0 0;'>
              <h1 style='color: white; margin: 0;'>🎉 Congratulations!</h1>
            </div>
            <div style='background: #f9f9f9; padding: 40px; border-radius: 0 0 12px 12px;'>
              <p>Hi <strong>%s</strong>,</p>
              <p>You received an offer for <strong>%s</strong> at <strong>%s</strong>!</p>
              <p>This is a big milestone. Before you decide:</p>
              <ul>
                <li>💰 Research market compensation ranges</li>
                <li>🤝 Don't be afraid to negotiate</li>
                <li>📅 Ask about start date flexibility</li>
              </ul>
              <p>Your AI coach can help you draft a negotiation email. Just open the chat!</p>
            </div>
            </body></html>
            """.formatted(name, role, company);
    }

    public String buildGhostAlertEmail(String name, int stalledCount) {
        return """
            <html><body style='font-family: Arial, sans-serif; max-width: 600px; margin: 0 auto;'>
            <div style='background: #f59e0b; padding: 40px; border-radius: 12px 12px 0 0;'>
              <h1 style='color: white; margin: 0;'>👻 Ghost Alert</h1>
            </div>
            <div style='background: #f9f9f9; padding: 40px; border-radius: 0 0 12px 12px;'>
              <p>Hi <strong>%s</strong>,</p>
              <p>You have <strong>%d application(s)</strong> with no response in 14+ days.</p>
              <p>Consider:</p>
              <ul>
                <li>📧 Sending a polite follow-up email</li>
                <li>🔗 Connecting with someone at the company on LinkedIn</li>
                <li>🗑️ Marking as Ghosted and moving focus to active opportunities</li>
              </ul>
              <a href='http://localhost:3000/board' style='display: inline-block; background: #f59e0b; color: white; padding: 12px 24px; border-radius: 8px; text-decoration: none; margin-top: 16px;'>View Applications →</a>
            </div>
            </body></html>
            """.formatted(name, stalledCount);
    }
}
