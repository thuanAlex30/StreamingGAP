package com.fpt.StreamGAP.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.util.Date;

@Entity
@Table(name = "Music_Games")
@Data
public class MusicGame {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer game_id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
    private Integer score;
    
    @Column(length = 20)
    private String game_type;

    @Column(nullable = false)
    private String question_text;

    @Column(nullable = false)
    private String answer_1;

    @Column(nullable = false)
    private String answer_2;

    private String answer_3;
    private String answer_4;

    @Column(nullable = false)
    private Integer correct_answer;

    private Integer user_answer;

    private Date played_at;

    @PrePersist
    protected void onCreate() {
        this.played_at = new Date(System.currentTimeMillis());
    }
}
