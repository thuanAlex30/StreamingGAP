package com.fpt.StreamGAP.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "Chat_Channel")
@Data
public class ChatChannel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer channel_id;
    @ManyToOne
    @JoinColumn(name = "creator_id")
    private User creator;
    @ManyToMany
    @JoinTable(
            name = "chat_channel_users",
            joinColumns = @JoinColumn(name = "chat_channel_id"),
            inverseJoinColumns = @JoinColumn(name = "user_id")
    )
    private Set<User> users= new HashSet<>();

    @OneToMany(mappedBy = "chatChannel", cascade = CascadeType.ALL)
    private List<Message> messages;

    private String channel_name;
    private Date created_at;
}
