package com.vmpk.codequerst.Model;

import com.google.firebase.database.Exclude;

public class UserScore {
    private String uid; // Firebase key
    private String name;
    private String profileImage;
    private int correctAnswers;
    private long totalTime;
    private long timestamp;

    // Default constructor (Firebase ke liye zaroori hai)
    public UserScore() {}

    // Parameterized constructor (optional)
    public UserScore(String uid, String name, String profileImage, int correctAnswers, long totalTime, long timestamp) {
        this.uid = uid;
        this.name = name;
        this.profileImage = profileImage;
        this.correctAnswers = correctAnswers;
        this.totalTime = totalTime;
        this.timestamp = timestamp;
    }

    // Getters and Setters
    @Exclude
    public String getUid() { return uid; }
    public void setUid(String uid) { this.uid = uid; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getProfileImage() { return profileImage; }
    public void setProfileImage(String profileImage) { this.profileImage = profileImage; }

    public int getCorrectAnswers() { return correctAnswers; }
    public void setCorrectAnswers(int correctAnswers) { this.correctAnswers = correctAnswers; }

    public long getTotalTime() { return totalTime; }
    public void setTotalTime(long totalTime) { this.totalTime = totalTime; }

    public long getTimestamp() { return timestamp; }
    public void setTimestamp(long timestamp) { this.timestamp = timestamp; }
}
