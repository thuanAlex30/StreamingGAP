package com.fpt.StreamGAP.dto;

import lombok.Data;

import java.util.Date;

@Data
public class MusicGameDTO {

    private Long id;
    private String Question_text;
    private Integer correct_answer;
    private String Answer_1;
    private String Answer_2;
    private String Answer_3;
    private String Answer_4;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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



}
