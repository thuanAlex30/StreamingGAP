package com.fpt.StreamGAP.controller;

import com.fpt.StreamGAP.entity.ChatMessage;
import com.fpt.StreamGAP.entity.Message;
import com.fpt.StreamGAP.entity.User;
import com.fpt.StreamGAP.repository.ChatMessageRepository;
import com.fpt.StreamGAP.service.UserManagementService;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;

import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping("/chatmessages")
public class ChatMessageController {

    @Autowired
    private final SimpMessagingTemplate simpMessagingTemplate;
    @Autowired
    private final ChatMessageRepository chatMessageRepository;
    @Autowired
    private UserManagementService userManagementService;
    @GetMapping("/search")
    public ResponseEntity<User> findByUsername(@RequestParam String username) {
        User user = userManagementService.findByUsernameOrThrow(username);
        if (user == null) {

            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
        return ResponseEntity.ok(user);
    }

    @MessageMapping("/message")
    @SendTo("/chatroom/public")
    public Message receiveMessage(Message message) throws InterruptedException {
        // Save to the database
        chatMessageRepository.save(new ChatMessage(
                message.getSenderName(),
                message.getReceiverName(),
                message.getMessage(),
                message.getMedia(),
                message.getMediaType(),
                message.getStatus(),
                System.currentTimeMillis()  // Current timestamp
        ));

        // Simulate delay for demonstration (optional)
        Thread.sleep(1000);

        return message;
    }
    @MessageMapping("/private-message")
    public void privateMessage(Message message) {
        String receiver = message.getReceiverName();
        simpMessagingTemplate.convertAndSendToUser(receiver, "/private", message);

        // Save private message to the database
        chatMessageRepository.save(new ChatMessage(
                message.getSenderName(),
                message.getReceiverName(),
                message.getMessage(),
                message.getMedia(),
                message.getMediaType(),
                message.getStatus(),
                System.currentTimeMillis()  // Current timestamp
        ));
    }

    @GetMapping("/api/messages/history/{user1}/{user2}")
    public ResponseEntity<List<ChatMessage>> getChatHistory(
            @PathVariable String user1,
            @PathVariable String user2
    ) {
        List<ChatMessage> messages = chatMessageRepository.findByReceiverNameOrSenderName(user1, user2);
        return ResponseEntity.ok(messages);
    }
}
