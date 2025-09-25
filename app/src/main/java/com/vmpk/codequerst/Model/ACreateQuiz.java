package com.vmpk.codequerst.Model;

import java.io.Serializable;
import java.util.List;

public class ACreateQuiz implements Serializable {
    private String question;
    private List<String> options;
    private int correctIndex;

    public ACreateQuiz() { }

    public ACreateQuiz(String question, List<String> options, int correctIndex) {
        this.question = question;
        this.options = options;
        this.correctIndex = correctIndex;
    }

    public String getQuestion() { return question; }
    public void setQuestion(String question) { this.question = question; }

    public List<String> getOptions() { return options; }
    public void setOptions(List<String> options) { this.options = options; }

    public int getCorrectIndex() { return correctIndex; }
    public void setCorrectIndex(int correctIndex) { this.correctIndex = correctIndex; }
}
