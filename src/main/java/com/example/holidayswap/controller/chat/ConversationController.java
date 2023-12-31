package com.example.holidayswap.controller.chat;

import com.example.holidayswap.domain.dto.request.chat.ConversationRequest;
import com.example.holidayswap.domain.dto.response.chat.ConversationParticipantResponse;
import com.example.holidayswap.domain.dto.response.chat.ConversationResponse;
import com.example.holidayswap.service.chat.ConversationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/v1/conversation")
public class ConversationController {
    private final ConversationService conversationService;
    @GetMapping("/current-user")
    public ResponseEntity<List<ConversationResponse>> getUserConversations() {
        return ResponseEntity.ok(conversationService.getUserConversations());
    }

    @PostMapping("/create")
    public ResponseEntity<Void> createConversation(@RequestBody ConversationRequest conversationRequest) {
        conversationService.createConversation(conversationRequest);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{conversationId}/participants")
    public ResponseEntity<List<ConversationParticipantResponse>> getConversationParticipants(@PathVariable("conversationId") Long conversationId) {
        var participants = conversationService.getConversationParticipants(conversationId);
        return ResponseEntity.ok(participants);
    }

    @GetMapping("/current-user/contact/{userId}")
    public ResponseEntity<ConversationResponse> getConversationByUserId(@PathVariable("userId") Long userId) {
        var conversation = conversationService.getCurrentConverastionWithUserId(userId);
        return ResponseEntity.ok(conversation);
    }

    @PostMapping("/current-user/contact/{userId}")
    public ResponseEntity<Optional<ConversationResponse>> createConversationWithUser(@PathVariable("userId") Long userId) {
        var conversation = conversationService.createCurrentConversationWithUserId(userId);
        return ResponseEntity.ok(conversation);
    }

    @GetMapping("/current-user/support")
    public ResponseEntity<ConversationResponse> getConversationTypeEqualsSupportByUserId() {
        var conversation = conversationService.getConversationTypeEqualsSupportByCurrentUser();
        return ResponseEntity.ok(conversation);
    }

    @PostMapping("/current-user/support")
    public ResponseEntity<Optional<ConversationResponse>> createConversationTypeEqualsSupportByUserId() {
        var conversation = conversationService.createConversationTypeEqualsSupportByCurrentUser();
        return ResponseEntity.ok(conversation);
    }
}
