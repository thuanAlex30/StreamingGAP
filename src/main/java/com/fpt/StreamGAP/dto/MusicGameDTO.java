package com.fpt.StreamGAP.dto;

import lombok.Data;

import java.util.Date;

@Data
public class MusicGameDTO {
    private Integer gameId;
    private String username;
    private Integer score;

    public String getGame_type() {
        return game_type;
    }

    public void setGame_type(String game_type) {
        this.game_type = game_type;
    }

    private String game_type;
    private String Question_text;
    private Integer correct_answer;
    private Integer user_answer;
    private String Answer_1;
    private String Answer_2;

    public Integer getGameId() {
        return gameId;
    }

    public void setGameId(Integer gameId) {
        this.gameId = gameId;
    }


    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public Integer getScore() {
        return score;
    }

    public void setScore(Integer score) {
        this.score = score;
    }

    public String getQuestion_text() {
        return Question_text;
    }

    public void setQuestion_text(String question_text) {
        Question_text = question_text;
    }

    public Integer getCorrect_answer() {
        return correct_answer;
    }

    public void setCorrect_answer(Integer correct_answer) {
        this.correct_answer = correct_answer;
    }

    public Integer getUser_answer() {
        return user_answer;
    }

    public void setUser_answer(Integer user_answer) {
        this.user_answer = user_answer;
    }

    public String getAnswer_1() {
        return Answer_1;
    }

    public void setAnswer_1(String answer_1) {
        Answer_1 = answer_1;
    }

    public String getAnswer_2() {
        return Answer_2;
    }

    public void setAnswer_2(String answer_2) {
        Answer_2 = answer_2;
    }

    public String getAnswer_3() {
        return Answer_3;
    }

    public void setAnswer_3(String answer_3) {
        Answer_3 = answer_3;
    }

    public String getAnswer_4() {
        return Answer_4;
    }

    public void setAnswer_4(String answer_4) {
        Answer_4 = answer_4;
    }



    public Date getPlayedAt() {
        return playedAt;
    }

    public void setPlayedAt(Date playedAt) {
        this.playedAt = playedAt;
    }

    private String Answer_3;
    private String Answer_4;
    private Date playedAt;
}
