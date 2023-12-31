package com.example.holidayswap.controller.chat;

import com.example.holidayswap.domain.dto.request.chat.MessageRequest;
import com.example.holidayswap.domain.dto.response.chat.MessageResponse;
import com.example.holidayswap.domain.entity.auth.RoleName;
import com.example.holidayswap.domain.exception.VerificationException;
import com.example.holidayswap.service.chat.MessageService;
import com.example.holidayswap.utils.AuthUtils;
import com.example.holidayswap.utils.ChatUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;
import java.util.Objects;

import static com.example.holidayswap.constants.ErrorMessage.USER_NOT_IN_CONVERSATION;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/v1/message")
public class MessageController {
    private final SimpMessagingTemplate messagingTemplate;
    private final MessageService messageService;
    private final ChatUtils chatUtils;
    private final AuthUtils authUtils;

    @GetMapping("/{conversationId}/messages")
    public ResponseEntity<List<MessageResponse>> getConversationMessages(
            @PathVariable("conversationId") Long conversationId) {
        var messages = messageService.getConversationMessages(conversationId);
        return ResponseEntity.ok(messages);
    }

    @PostMapping( value = "/{conversationId}/send", consumes= MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Void> createMessage(
            @PathVariable("conversationId") Long conversationId,
            @ModelAttribute MessageRequest messageRequest
    ) throws IOException {
        var user = authUtils.getAuthenticatedUser();
        var userId = Long.parseLong(user.getUserId().toString());
        var conversationIdLong = Long.parseLong(String.valueOf(conversationId));
        var isUserNotInConversation = chatUtils.isUserNotInConversation(userId, conversationIdLong);
        if (chatUtils.isStaff(user) && isUserNotInConversation) {
            messageService.createConversationParticipantIfNotExist(user, conversationIdLong);
        }
        if(!chatUtils.isStaff(user) && isUserNotInConversation) {
            throw new VerificationException(USER_NOT_IN_CONVERSATION);
        }
        var message =messageService.createMessage(messageRequest, String.valueOf(conversationId));
        messagingTemplate.convertAndSend("/topic/" + conversationId, message);
        return ResponseEntity.noContent().build();
    }

    @MessageMapping("/{conversationId}")
    public void sendChatMessage(
            @DestinationVariable String conversationId,
            @Payload MessageRequest messageRequest,
            SimpMessageHeaderAccessor headerAccessor
    ) throws IOException {
        String userId = String.valueOf(Objects.requireNonNull(headerAccessor.getUser()).getName());
        if (chatUtils.isUserNotInConversation(Long.parseLong(userId),
                Long.parseLong(conversationId))) {
            throw new VerificationException(USER_NOT_IN_CONVERSATION);
        }
        var message = messageService.createMessage(messageRequest, conversationId);
        messagingTemplate.convertAndSend("/topic/" + conversationId, message);
    }

    @MessageMapping("/{conversationId}/typing")
    public void sendChatTyping(
            @DestinationVariable String conversationId,
            @Payload String userId
    ) {
        if (chatUtils.isUserNotInConversation(Long.parseLong(userId),
                Long.parseLong(String.valueOf(conversationId)))) {
            throw new VerificationException(USER_NOT_IN_CONVERSATION);
        }
        messagingTemplate.convertAndSend("/queue/typing-" + conversationId, userId);
    }

    @PatchMapping("/{conversationId}/read-all")
    public void sendMarkAllAsRead(
            @PathVariable("conversationId") Long conversationId
    ) {
        var user = authUtils.getAuthenticatedUser();
        if (chatUtils.isUserNotInConversation(Long.parseLong(user.getUserId().toString()),
                Long.parseLong(String.valueOf(conversationId)))) {
            throw new VerificationException(USER_NOT_IN_CONVERSATION);
        }
        messageService.markAllAsRead(Long.parseLong(user.getUserId().toString()),
                Long.parseLong(String.valueOf(conversationId)));
    }

    @PatchMapping("/{conversationId}/{messageId}/read")
    public void sendMarkAllAsRead(
            @PathVariable("conversationId") Long conversationId,
            @PathVariable("messageId") Long messageId
    ) {
        var user = authUtils.getAuthenticatedUser();
        if (chatUtils.isUserNotInConversation(Long.parseLong(user.getUserId().toString()),
                Long.parseLong(String.valueOf(conversationId)))) {
            throw new VerificationException(USER_NOT_IN_CONVERSATION);
        }
        messageService.markReadByMessageId(Long.parseLong(user.getUserId().toString()),
                Long.parseLong(String.valueOf(conversationId)),
                Long.parseLong(String.valueOf(messageId)));
    }
}
