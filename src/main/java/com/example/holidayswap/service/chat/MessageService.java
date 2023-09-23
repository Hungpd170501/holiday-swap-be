package com.example.holidayswap.service.chat;

import com.example.holidayswap.domain.dto.request.chat.MessageRequest;
import com.example.holidayswap.domain.dto.response.chat.MessageResponse;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;

@Service
public interface MessageService {
    List<MessageResponse> getConversationMessages(Long conversationId);

    MessageResponse createMessage(MessageRequest messageRequest, String conversationId) throws IOException;
}
