package com.hiresync.ai.chat;

import com.hiresync.ai.auth.AiJwtService;
import com.hiresync.common.dto.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@RestController
@RequestMapping("/api/ai/chat")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "AI Chat", description = "Streaming AI career coach chat")
public class ChatController {

    private final ChatService chatService;
    private final AiJwtService jwtService;

    @PostMapping
    @Operation(summary = "Send a message to the AI career coach (non-streaming)")
    public ResponseEntity<ApiResponse<String>> chat(
            @RequestBody ChatRequest request,
            HttpServletRequest httpRequest) {
        String userId = jwtService.extractUserIdFromRequest(httpRequest);
        String response = chatService.chat(userId, request.message());
        return ResponseEntity.ok(ApiResponse.ok(response));
    }

    @GetMapping(value = "/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    @Operation(summary = "Stream a chat response via SSE")
    public SseEmitter streamChat(
            @RequestParam String message,
            @RequestParam String token) { // token as query param for SSE (can't set headers)
        String userId = jwtService.extractUserIdFromToken(token);
        SseEmitter emitter = new SseEmitter(60_000L);
        chatService.streamChat(userId, message, emitter);
        return emitter;
    }

    @DeleteMapping("/history")
    @Operation(summary = "Clear chat history for current user")
    public ResponseEntity<ApiResponse<Void>> clearHistory(HttpServletRequest request) {
        String userId = jwtService.extractUserIdFromRequest(request);
        chatService.clearHistory(userId);
        return ResponseEntity.ok(ApiResponse.ok("Chat history cleared", null));
    }

    public record ChatRequest(String message) {}
}
