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
    private Long id;

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


}
