package com.fpt.StreamGAP.service;

import com.fpt.StreamGAP.entity.ChatChannel;
import com.fpt.StreamGAP.entity.Message;
import com.fpt.StreamGAP.entity.User;
import com.fpt.StreamGAP.repository.ChatChannelRepository;
import com.fpt.StreamGAP.repository.MessageRepository;
import com.fpt.StreamGAP.repository.UserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class ChatChannelService {
    @Autowired
    private ChatChannelRepository chatChannelRepository;

    @Autowired
    private MessageRepository messageRepository;

    @Autowired
    private UserRepo userRepo;

    private String getCurrentUsername() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (principal instanceof UserDetails) {
            return ((UserDetails) principal).getUsername();
        }
        return null;
    }
    private boolean isAdmin() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (principal instanceof UserDetails) {
            Collection<? extends GrantedAuthority> authorities = ((UserDetails) principal).getAuthorities();
            return authorities.stream()
                    .anyMatch(grantedAuthority -> grantedAuthority.getAuthority().equals("ADMIN"));
        }
        return false;
    }
    public List<ChatChannel> getAllChatChannel() {
        User currentUser = getCurrentUser();
        List<ChatChannel> chatChannels;

        if (isAdmin()) {
            chatChannels = chatChannelRepository.findAll();
            chatChannels.forEach(chatChannel -> {
                if (!chatChannel.getUsers().contains(currentUser)) {
                    chatChannel.setMessages(Collections.emptyList());
                }
            });
        } else {
            chatChannels = chatChannelRepository.findByUsers(currentUser);
        }

        return chatChannels;
    }
    public Optional<ChatChannel> getChatChannelById(Integer id) {
        ChatChannel chatChannel = chatChannelRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Chat channel not found"));

        User currentUser = getCurrentUser();

        if (isAdmin() && !chatChannel.getUsers().contains(currentUser)) {
            chatChannel.setMessages(Collections.emptyList());
            return Optional.of(chatChannel);
        }
        if (!chatChannel.getUsers().contains(currentUser)) {
            throw new RuntimeException("You do not have permission to view this chat channel");
        }

        return Optional.of(chatChannel);
    }

    public ChatChannel saveChatChannel(ChatChannel chatChannel) {
        User creator = getCurrentUser();
        chatChannel.setCreator(creator);
        if (chatChannel.getUsers() == null) {
            chatChannel.setUsers(new HashSet<>());
        }
        chatChannel.getUsers().add(creator);
        return chatChannelRepository.save(chatChannel);
    }

    public ChatChannel updateChatChannel(Integer id, ChatChannel updatedChatChannel) {
        ChatChannel existingChatChannel = chatChannelRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Chat channel not found"));
        User currentUser = getCurrentUser();
        if (!existingChatChannel.getCreator().equals(currentUser)) {
            throw new RuntimeException("Only the creator can update the chat channel name");
        }
        existingChatChannel.setChannel_name(updatedChatChannel.getChannel_name());
        return chatChannelRepository.save(existingChatChannel);
    }

    public void deleteChatChannel(Integer id) {
        ChatChannel chatChannel = chatChannelRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Chat channel not found"));

        User user = getCurrentUser();
        if (isAdmin() || chatChannel.getCreator().equals(user)) {
            chatChannelRepository.deleteById(id);
        } else {
            throw new RuntimeException("You do not have permission to delete this chat channel");
        }
    }

    public void addUserToChatChannel(Integer chatChannelId, User user, User requester) {
        ChatChannel chatChannel = chatChannelRepository.findById(chatChannelId)
                .orElseThrow(() -> new RuntimeException("Chat channel not found"));

        if (chatChannel.getCreator().equals(requester)) {
            chatChannel.getUsers().add(user);
            chatChannelRepository.save(chatChannel);
        } else {
            throw new RuntimeException("You do not have permission to add users to this chat channel");
        }
    }

    public User getCurrentUser() {
        String username = getCurrentUsername();
        return userRepo.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    public Message sendMessageToChatChannel(Integer chatChannelId, String content) {
        ChatChannel chatChannel = chatChannelRepository.findById(chatChannelId)
                .orElseThrow(() -> new RuntimeException("Chat channel not found"));
        Message message = new Message();
        message.setContent(content);
        message.setChatChannel(chatChannel);
        message.setSentBy(getCurrentUser());
        message.setSentAt(new Date());
        return messageRepository.save(message);

    }

}
