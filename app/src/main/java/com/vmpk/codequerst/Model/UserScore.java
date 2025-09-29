package com.vmpk.codequerst.Model;

import com.google.firebase.database.Exclude;
import java.util.HashMap;
import java.util.Map;

public class UserScore {

    private String uid; // Firebase key
    private String name;
    private String profileImage;
    private int correctAnswers;   // optional, legacy
    private long totalTime;
    private long timestamp;
    private String email;
    private int totalPoints;      // all-time points
    private int weeklyPoints;     // last 7 days points
    private Map<String, Integer> topics;       // optional, per topic points
    private Map<String, QuizAttempt> quizzes;  // each quiz attempt stored

    // Default constructor (Firebase ke liye zaroori)
    public UserScore() {
        topics = new HashMap<>();
        quizzes = new HashMap<>();
    }

    // Parameterized constructor (optional)
    public UserScore(String uid, String name, String profileImage, int correctAnswers, long totalTime, long timestamp, String email) {
        this.uid = uid;
        this.name = name;
        this.profileImage = profileImage;
        this.correctAnswers = correctAnswers;
        this.totalTime = totalTime;
        this.timestamp = timestamp;
        this.email = email;
        this.totalPoints = 0;
        this.weeklyPoints = 0;
        topics = new HashMap<>();
        quizzes = new HashMap<>();
    }

    // Uid
    @Exclude
    public String getUid() { return uid; }
    public void setUid(String uid) { this.uid = uid; }

    // Name
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    // Profile Image
    public String getProfileImage() { return profileImage; }
    public void setProfileImage(String profileImage) { this.profileImage = profileImage; }

    // Correct answers (legacy)
    public int getCorrectAnswers() { return correctAnswers; }
    public void setCorrectAnswers(int correctAnswers) { this.correctAnswers = correctAnswers; }

    // Total points (all-time)
    public int getTotalPoints() { return totalPoints; }
    public void setTotalPoints(int totalPoints) { this.totalPoints = totalPoints; }

    // Weekly points
    public int getWeeklyPoints() { return weeklyPoints; }
    public void setWeeklyPoints(int weeklyPoints) { this.weeklyPoints = weeklyPoints; }

    // Total time
    public long getTotalTime() { return totalTime; }
    public void setTotalTime(long totalTime) { this.totalTime = totalTime; }

    // Timestamp
    public long getTimestamp() { return timestamp; }
    public void setTimestamp(long timestamp) { this.timestamp = timestamp; }

    // Email
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    // Topics
    public Map<String, Integer> getTopics() { return topics; }
    public void setTopics(Map<String, Integer> topics) { this.topics = topics; }

    // Quizzes
    public Map<String, QuizAttempt> getQuizzes() { return quizzes; }
    public void setQuizzes(Map<String, QuizAttempt> quizzes) { this.quizzes = quizzes; }

    // Nested class for per-quiz tracking
    public static class QuizAttempt {
        private int points;
        private long timestamp;

        public QuizAttempt() {}

        public QuizAttempt(int points, long timestamp) {
            this.points = points;
            this.timestamp = timestamp;
        }

        public int getPoints() { return points; }
        public void setPoints(int points) { this.points = points; }

        public long getTimestamp() { return timestamp; }
        public void setTimestamp(long timestamp) { this.timestamp = timestamp; }
    }
}
