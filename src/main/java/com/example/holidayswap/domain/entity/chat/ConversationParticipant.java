package com.example.holidayswap.domain.entity.chat;

import com.example.holidayswap.domain.entity.auth.User;
import com.example.holidayswap.domain.entity.common.BaseEntityAudit;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "conversation_participant")
public class ConversationParticipant extends BaseEntityAudit {
    @EmbeddedId
    private ConversationParticipantPK conversationParticipantId;

    @Column(name = "left_chat", columnDefinition = "boolean default false")
    private boolean leftChat = false;

    @Column(name = "message_id")
    private Long messageId;

    @MapsId("conversationId")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "conversation_id")
    private Conversation conversation;

    @MapsId("userId")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    public long countUnreadMessages() {
        if (leftChat) {
            return 0;
        }
        List<Message> allMessages = conversation.getMessages();
        if (messageId == null || messageId == 0) {
            if (allMessages != null && !allMessages.isEmpty()) {
                return allMessages.size();
            } else {
                return messageId == null ? 1 : 0;
            }
        }
        return allMessages.stream()
                .filter(message -> (message != null) && message.getMessageId() > messageId)
                .count();
    }
}
