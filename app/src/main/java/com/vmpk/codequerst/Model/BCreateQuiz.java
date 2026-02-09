package com.vmpk.codequerst.Model;

import java.io.Serializable;
import java.util.List;

public class BCreateQuiz implements Serializable {

    private String title;
    private String topic;
    private String difficulty;
    private String timePerQuestion;
    private List<ACreateQuiz> questions;
    private String id;

    // 🔥 NEW FIELDS
    private String creatorEmail;
    private String creatorUid;

    // 🔹 Empty constructor (Firebase requirement)
    public BCreateQuiz() {
    }

    // 🔹 Main constructor
    public BCreateQuiz(String title,
                       String topic,
                       String difficulty,
                       String timePerQuestion,
                       List<ACreateQuiz> questions,
                       String creatorEmail) {

        this.title = title;
        this.topic = topic;
        this.difficulty = difficulty;
        this.timePerQuestion = timePerQuestion;
        this.questions = questions;
        this.creatorEmail = creatorEmail;
    }

    // =======================
    // 🔹 GETTERS
    // =======================
    public String getTitle() {
        return title;
    }

    public String getTopic() {
        return topic;
    }

    public String getDifficulty() {
        return difficulty;
    }

    public String getTimePerQuestion() {
        return timePerQuestion;
    }

    public List<ACreateQuiz> getQuestions() {
        return questions;
    }

    public String getId() {
        return id;
    }

    public String getCreatorEmail() {
        return creatorEmail;
    }

    public String getCreatorUid() {
        return creatorUid;
    }

    // =======================
    // 🔹 SETTERS
    // =======================
    public void setTitle(String title) {
        this.title = title;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    public void setDifficulty(String difficulty) {
        this.difficulty = difficulty;
    }

    public void setTimePerQuestion(String timePerQuestion) {
        this.timePerQuestion = timePerQuestion;
    }

    public void setQuestions(List<ACreateQuiz> questions) {
        this.questions = questions;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setCreatorEmail(String creatorEmail) {
        this.creatorEmail = creatorEmail;
    }

    // 🔥 THIS WAS MISSING (MAIN FIX)
    public void setCreatorUid(String creatorUid) {
        this.creatorUid = creatorUid;
    }
}
