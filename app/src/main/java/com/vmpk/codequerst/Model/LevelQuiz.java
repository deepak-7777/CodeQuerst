package com.vmpk.codequerst.Model;

public class LevelQuiz {
    private String question;
    private String option1;
    private String option2;
    private String option3;
    private String option4;
    private String correctAnswer;

    public LevelQuiz() {}

    public String getQuestion() { return question; }
    public String getOption1() { return option1; }
    public String getOption2() { return option2; }
    public String getOption3() { return option3; }
    public String getOption4() { return option4; }
    public String getCorrectAnswer() { return correctAnswer; }

    public void setQuestion(String question) { this.question = question; }
    public void setOption1(String option1) { this.option1 = option1; }
    public void setOption2(String option2) { this.option2 = option2; }
    public void setOption3(String option3) { this.option3 = option3; }
    public void setOption4(String option4) { this.option4 = option4; }
    public void setCorrectAnswer(String correctAnswer) { this.correctAnswer = correctAnswer; }

    // ✅ Add this method to work like QuizActivity expects
    public int getAnswer() {
        if (correctAnswer == null) return -1;

        // If correctAnswer is stored as a number ("1", "2", "3", "4")
        try {
            int answerIndex = Integer.parseInt(correctAnswer.trim());
            if (answerIndex >= 1 && answerIndex <= 4) {
                return answerIndex;
            }
        } catch (NumberFormatException ignored) {}

        // Otherwise, match text with options
        String ca = correctAnswer.trim();
        if (option1 != null && ca.equalsIgnoreCase(option1.trim())) return 1;
        if (option2 != null && ca.equalsIgnoreCase(option2.trim())) return 2;
        if (option3 != null && ca.equalsIgnoreCase(option3.trim())) return 3;
        if (option4 != null && ca.equalsIgnoreCase(option4.trim())) return 4;

        return -1; // No match found
    }
}
