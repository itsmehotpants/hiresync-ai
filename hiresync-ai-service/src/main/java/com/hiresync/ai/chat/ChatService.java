package com.hiresync.ai.chat;

import com.hiresync.ai.tools.JobHuntTools;
import dev.langchain4j.memory.chat.MessageWindowChatMemory;
import dev.langchain4j.model.chat.StreamingChatLanguageModel;
import dev.langchain4j.service.AiServices;
import dev.langchain4j.service.MemoryId;
import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.UserMessage;
import dev.langchain4j.store.memory.chat.ChatMemoryStore;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicBoolean;

@Service
@Slf4j
public class ChatService {

    interface JobHuntAssistant {
        @SystemMessage("""
            You are a personal career coach AI assistant for a job seeker using HireSync.
            You have access to their real application data through tools.
            Always use tools to get actual data before answering questions about their applications.
            Be specific, actionable, and concise.
            When drafting emails or messages, provide the complete text ready to send.
            Format your responses using markdown for readability.
            """)
        String chat(@MemoryId String userId, @UserMessage String message);
    }

    private final JobHuntAssistant assistant;
    private final ChatMemoryStore chatMemoryStore;

    public ChatService(
            StreamingChatLanguageModel streamingModel,
            ChatMemoryStore chatMemoryStore,
            JobHuntTools tools
    ) {
        this.chatMemoryStore = chatMemoryStore;
        this.assistant = AiServices.builder(JobHuntAssistant.class)
                .streamingChatLanguageModel(streamingModel)
                .chatMemoryProvider(memoryId -> MessageWindowChatMemory.builder()
                        .id(memoryId)
                        .maxMessages(20)
                        .chatMemoryStore(chatMemoryStore)
                        .build())
                .tools(tools)
                .build();
    }

    public String chat(String userId, String message) {
        return assistant.chat(userId, message);
    }

    public void clearHistory(String userId) {
        chatMemoryStore.deleteMessages(userId);
    }

    public void streamChat(String userId, String message, SseEmitter emitter) {
        // Run in virtual thread to avoid blocking the servlet thread
        Thread.startVirtualThread(() -> {
            AtomicBoolean errorOccurred = new AtomicBoolean(false);
            try {
                // Use non-streaming for now — stream via chunked response
                // Full streaming requires StreamingChatLanguageModel wiring — simplified here
                String response = assistant.chat(userId, message);
                try {
                    // Send response in chunks to simulate streaming
                    String[] words = response.split(" ");
                    for (String word : words) {
                        emitter.send(SseEmitter.event().data(word + " "));
                        Thread.sleep(30); // 30ms delay between words for streaming feel
                    }
                    emitter.send(SseEmitter.event().name("complete").data("[DONE]"));
                    emitter.complete();
                } catch (IOException | InterruptedException e) {
                    log.warn("SSE stream interrupted for user {}: {}", userId, e.getMessage());
                    errorOccurred.set(true);
                }
            } catch (Exception e) {
                log.error("Chat error for user {}: {}", userId, e.getMessage());
                if (!errorOccurred.get()) {
                    try {
                        emitter.send(SseEmitter.event().data("Sorry, I encountered an error. Please try again."));
                        emitter.send(SseEmitter.event().name("complete").data("[DONE]"));
                        emitter.complete();
                    } catch (IOException ignored) {}
                }
            }
        });
    }
}
