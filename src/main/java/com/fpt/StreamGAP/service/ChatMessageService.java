package com.fpt.StreamGAP.service;

import com.fpt.StreamGAP.entity.ChatMessage;
import com.fpt.StreamGAP.repository.ChatMessageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ChatMessageService {

    @Autowired
    private ChatMessageRepository chatMessageRepository;

    public List<ChatMessage> getAllChatMessages() {
        return chatMessageRepository.findAll();
    }

    public Optional<ChatMessage> getChatMessageById(Long id) {
        return chatMessageRepository.findById(id);
    }

    public ChatMessage saveChatMessage(ChatMessage chatMessage) {
        return chatMessageRepository.save(chatMessage);
    }

    public void deleteChatMessage(Long id) {
        chatMessageRepository.deleteById(id);
    }
}
