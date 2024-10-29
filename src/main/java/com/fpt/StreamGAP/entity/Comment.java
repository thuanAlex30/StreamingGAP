package com.fpt.StreamGAP.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.util.Date;

@Entity
@Table(name = "comment")
@Data
public class Comment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer comment_id;

    @ManyToOne
    @JoinColumn(name = "user_id", referencedColumnName = "user_id")
    private User user;

    @ManyToOne
    @JoinColumn(name = "song_id", referencedColumnName = "song_id")
    private Song song; // Ensure this is correctly defined

    private String content;
    private Date createdAt; // Changed from Date to LocalDateTime
}
