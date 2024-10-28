package com.fpt.StreamGAP.controller;

import com.fpt.StreamGAP.dto.ChatChannelDTO;
import com.fpt.StreamGAP.dto.MessageDTO;
import com.fpt.StreamGAP.dto.ReqRes;
import com.fpt.StreamGAP.entity.ChatChannel;
import com.fpt.StreamGAP.entity.Message;
import com.fpt.StreamGAP.entity.User;
import com.fpt.StreamGAP.repository.UserRepo;
import com.fpt.StreamGAP.service.ChatChannelService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/ChatChannel")
public class ChatChannelController {
    @Autowired
    private ChatChannelService chatChannelService;
    @Autowired
    private UserRepo userRepo;

    @GetMapping
    public ReqRes getAllChatChannel() {
        List<ChatChannel> chatChannels = chatChannelService.getAllChatChannel();

        List<ChatChannelDTO> chatChannelDTOS = chatChannels.stream()
                .map(chatChannel -> {
                    ChatChannelDTO dto = new ChatChannelDTO();
                    dto.setChannel_id(chatChannel.getChannel_id());
                    dto.setChannel_name(chatChannel.getChannel_name());
                    dto.setCreated_at(chatChannel.getCreated_at());
                    dto.setParticipants(chatChannel.getUsers().stream()
                            .map(User::getUsername)
                            .collect(Collectors.toList()));

                    List<MessageDTO> messages = chatChannel.getMessages().stream()
                            .map(message -> {
                                MessageDTO messageDTO = new MessageDTO();
                                messageDTO.setContent(message.getContent());
                                messageDTO.setSentBy(message.getSentBy().getUsername());
                                messageDTO.setSentAt(message.getSentAt());
                                return messageDTO;
                            })
                            .collect(Collectors.toList());

                    dto.setMessages(messages);
                    return dto;
                })
                .collect(Collectors.toList());

        ReqRes response = new ReqRes();
        response.setStatusCode(200);
        response.setMessage("ChatChannel retrieved successfully");
        response.setChatChannel(chatChannelDTOS);
        return response;
    }

    @GetMapping("/{id}")
    public ResponseEntity<ReqRes> getChatChannelById(@PathVariable Integer id) {
        ReqRes response = new ReqRes();
        User currentUser = chatChannelService.getCurrentUser();

        try {
            ChatChannel chatChannel = chatChannelService.getChatChannelById(id)
                    .orElseThrow(() -> new RuntimeException("Chat channel not found"));

            ChatChannelDTO dto = new ChatChannelDTO();
            dto.setChannel_id(chatChannel.getChannel_id());
            dto.setChannel_name(chatChannel.getChannel_name());
            dto.setCreated_at(chatChannel.getCreated_at());
            dto.setParticipants(chatChannel.getUsers().stream()
                    .map(User::getUsername)
                    .collect(Collectors.toList()));

            List<MessageDTO> messages = chatChannel.getMessages().stream()
                    .map(message -> {
                        MessageDTO messageDTO = new MessageDTO();
                        messageDTO.setContent(message.getContent());
                        messageDTO.setSentBy(message.getSentBy().getUsername());
                        messageDTO.setSentAt(message.getSentAt());
                        return messageDTO;
                    })
                    .collect(Collectors.toList());
            dto.setMessages(messages);

            response.setStatusCode(200);
            response.setMessage("ChatChannel retrieved successfully");
            response.setChatChannel(List.of(dto));
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            response.setStatusCode(403); // Forbidden
            response.setMessage(e.getMessage());
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
        }
    }

    @PostMapping
    public ResponseEntity<ReqRes> createChatChannel(@RequestBody ChatChannel chatChannel) {
        ReqRes response = new ReqRes();
        try {
            chatChannel.setCreated_at(new Date());
            User currentUser = chatChannelService.getCurrentUser();
            chatChannel.setCreator(currentUser);
            chatChannel.getUsers().add(currentUser);
            ChatChannel savedChatChannel = chatChannelService.saveChatChannel(chatChannel);

            ChatChannelDTO dto = new ChatChannelDTO();
            dto.setChannel_id(savedChatChannel.getChannel_id());
            dto.setChannel_name(savedChatChannel.getChannel_name());
            dto.setCreated_at(savedChatChannel.getCreated_at());

            response.setStatusCode(201);
            response.setMessage("ChatChannel created successfully");
            response.setChatChannel(List.of(dto));
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (Exception e) {
            response.setStatusCode(500);
            response.setMessage("Error occurred: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<ReqRes> updateChatChannel(@PathVariable Integer id, @RequestBody ChatChannel chatChannel) {
        return chatChannelService.getChatChannelById(id)
                .map(existingChatChannel -> {
                    chatChannel.setChannel_id(existingChatChannel.getChannel_id());
                    chatChannel.setCreated_at(existingChatChannel.getCreated_at()); // Giữ nguyên thời gian tạo

                    ChatChannel updatedChatChannel = chatChannelService.updateChatChannel(id, chatChannel);

                    ChatChannelDTO dto = new ChatChannelDTO();
                    dto.setChannel_id(updatedChatChannel.getChannel_id());
                    dto.setChannel_name(updatedChatChannel.getChannel_name());
                    dto.setCreated_at(updatedChatChannel.getCreated_at());
                    if (updatedChatChannel.getUsers().contains(chatChannelService.getCurrentUser())) {
                        List<MessageDTO> messages = updatedChatChannel.getMessages().stream()
                                .map(message -> {
                                    MessageDTO messageDTO = new MessageDTO();
                                    messageDTO.setContent(message.getContent());
                                    messageDTO.setSentBy(message.getSentBy().getUsername());
                                    return messageDTO;
                                })
                                .collect(Collectors.toList());

                        dto.setMessages(messages);
                    }

                    ReqRes response = new ReqRes();
                    response.setStatusCode(200);
                    response.setMessage("ChatChannel updated successfully");
                    response.setChatChannel(List.of(dto));
                    return ResponseEntity.ok(response);
                })
                .orElseGet(() -> {
                    ReqRes response = new ReqRes();
                    response.setStatusCode(404);
                    response.setMessage("ChatChannel not found");
                    return ResponseEntity.status(404).body(response);
                });
    }


    @DeleteMapping("/{id}")
    public ResponseEntity<ReqRes> deleteChatChannel(@PathVariable Integer id) {
        if (chatChannelService.getChatChannelById(id).isPresent()) {
            chatChannelService.deleteChatChannel(id);
            ReqRes response = new ReqRes();
            response.setStatusCode(200);
            response.setMessage("ChatChannel deleted successfully");
            return ResponseEntity.ok(response);
        } else {
            ReqRes response = new ReqRes();
            response.setStatusCode(404);
            response.setMessage("ChatChannel not found");
            return ResponseEntity.status(404).body(response);
        }
    }


    @PostMapping("/{id}/addUser")
    public ResponseEntity<ReqRes> addUserToChatChannel(@PathVariable Integer id, @RequestBody Map<String, Integer> requestBody) {
        try {
            Integer userId = requestBody.get("userId");
            User user = userRepo.findById(userId)
                    .orElseThrow(() -> new RuntimeException("User not found"));
            chatChannelService.addUserToChatChannel(id, user, chatChannelService.getCurrentUser());
            ReqRes response = new ReqRes();
            response.setStatusCode(200);
            response.setMessage("User added to ChatChannel successfully");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            ReqRes response = new ReqRes();
            response.setStatusCode(500);
            response.setMessage("Error occurred: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @PostMapping("/{chatChannelId}/messages")
    public ResponseEntity<ReqRes> sendMessage(@PathVariable Integer chatChannelId, @RequestBody String messageContent) {
        ReqRes response = new ReqRes();
        try {
            messageContent = messageContent.trim();
            ChatChannel chatChannel = chatChannelService.getChatChannelById(chatChannelId)
                    .orElseThrow(() -> new RuntimeException("Chat channel not found"));
            Message sentMessage = chatChannelService.sendMessageToChatChannel(chatChannelId, messageContent);

            MessageDTO messageDTO = new MessageDTO();
            messageDTO.setSentBy(sentMessage.getSentBy().getUsername());
            messageDTO.setSentAt(sentMessage.getSentAt());
            messageDTO.setContent(sentMessage.getContent());
            response.setStatusCode(201);
            response.setMessage("Message sent successfully");
            response.setMessages(List.of(messageDTO));
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (RuntimeException e) {
            response.setStatusCode(404);
            response.setMessage("Error: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        } catch (Exception e) {
            response.setStatusCode(500);
            response.setMessage("Error occurred: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

}