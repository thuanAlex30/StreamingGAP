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
    @JoinColumn(name = "game_id", nullable = true)
    private Integer gameId;
    @Column(length = 50)
    private String question_text;
    @Column(nullable = true)
    private String answer_1;
    @Column(nullable = true)
    private String answer_2;
    @Column(nullable = true)
    private String answer_3;
    @Column(nullable = true)
    private String answer_4;
    @Column(nullable = true)
    private Integer correct_answer;




    public MusicGame() {

    }

    public Integer getCorrect_answer() {
        return correct_answer;
    }

    public void setCorrect_answer(Integer correct_answer) {
        this.correct_answer = correct_answer;
    }

    public Integer getGameId() {
        return gameId;
    }

    public void setGameId(Integer gameId) {
        this.gameId = gameId;
    }
    public String getQuestion_text() {
        return question_text;
    }

    public void setQuestion_text(String question_text) {
        this.question_text = question_text;
    }

    public String getAnswer_1() {
        return answer_1;
    }

    public void setAnswer_1(String answer_1) {
        this.answer_1 = answer_1;
    }

    public String getAnswer_2() {
        return answer_2;
    }

    public void setAnswer_2(String answer_2) {
        this.answer_2 = answer_2;
    }

    public String getAnswer_3() {
        return answer_3;
    }

    public void setAnswer_3(String answer_3) {
        this.answer_3 = answer_3;
    }

    public String getAnswer_4() {
        return answer_4;
    }

    public void setAnswer_4(String answer_4) {
        this.answer_4 = answer_4;
    }

}
