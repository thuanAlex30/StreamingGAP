package com.fpt.StreamGAP.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.util.Date;

@Entity
@Data
public class Message {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String content;

    @ManyToOne
    @JoinColumn(name = "chat_channel_id")
    private ChatChannel chatChannel;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User sentBy;
    @Temporal(TemporalType.TIMESTAMP)
    private Date sentAt;
}
