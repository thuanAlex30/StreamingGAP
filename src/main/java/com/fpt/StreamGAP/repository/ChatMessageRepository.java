package com.fpt.StreamGAP.repository;

import com.fpt.StreamGAP.entity.ChatMessage;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ChatMessageRepository extends JpaRepository<ChatMessage, Long> {
    List<ChatMessage> findByReceiverNameOrSenderName(String receiverName, String senderName);
}
