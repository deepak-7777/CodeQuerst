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

    // Default constructor for Firebase
    public UserScore() {
        this.topics = new HashMap<>();
        this.quizzes = new HashMap<>();
    }

    // Parameterized constructor
    public UserScore(String uid, String name, String profileImage, int correctAnswers,
                     long totalTime, long timestamp, String email) {
        this.uid = uid;
        this.name = name;
        this.profileImage = profileImage;
        this.correctAnswers = correctAnswers;
        this.totalTime = totalTime;
        this.timestamp = timestamp;
        this.email = email;
        this.totalPoints = 0;
        this.weeklyPoints = 0;
        this.topics = new HashMap<>();
        this.quizzes = new HashMap<>();
    }

    // Getters & setters
    @Exclude
    public String getUid() { return uid; }
    public void setUid(String uid) { this.uid = uid; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getProfileImage() { return profileImage; }
    public void setProfileImage(String profileImage) { this.profileImage = profileImage; }

    public int getCorrectAnswers() { return correctAnswers; }
    public void setCorrectAnswers(int correctAnswers) { this.correctAnswers = correctAnswers; }

    public int getTotalPoints() { return totalPoints; }
    public void setTotalPoints(int totalPoints) { this.totalPoints = totalPoints; }

    public int getWeeklyPoints() { return weeklyPoints; }
    public void setWeeklyPoints(int weeklyPoints) { this.weeklyPoints = weeklyPoints; }

    public long getTotalTime() { return totalTime; }
    public void setTotalTime(long totalTime) { this.totalTime = totalTime; }

    public long getTimestamp() { return timestamp; }
    public void setTimestamp(long timestamp) { this.timestamp = timestamp; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public Map<String, Integer> getTopics() { return topics; }
    public void setTopics(Map<String, Integer> topics) { this.topics = topics; }

    public Map<String, QuizAttempt> getQuizzes() { return quizzes; }
    public void setQuizzes(Map<String, QuizAttempt> quizzes) { this.quizzes = quizzes; }

    // Nested class for each quiz attempt
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

    // Helper class for RecyclerView display
    public static class QuizAttemptItem {
        private String language;
        private String topic;
        private String difficulty;
        private int points;

        public QuizAttemptItem() {}

        public QuizAttemptItem(String language, String topic, String difficulty, int points) {
            this.language = language;
            this.topic = topic;
            this.difficulty = difficulty;
            this.points = points;
        }

        public String getLanguage() { return language; }
        public String getTopic() { return topic; }
        public String getDifficulty() { return difficulty; }
        public int getPoints() { return points; }
    }
}
