package com.vmpk.codequerst.Activity;

import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.view.Window;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.material.progressindicator.CircularProgressIndicator;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.vmpk.codequerst.R;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class StreakQuizActivity extends AppCompatActivity {
    LinearLayout progressBarLayout;
    Toolbar streakToolbar;
    TextView tvTitle, tvQuestion, tvCounter, tvTimer;
    RadioGroup radioGroup;
    RadioButton option1, option2, option3, option4;
    CircularProgressIndicator circularProgressStreak;
    DatabaseReference databaseReference;
    List<Question> questionList = new ArrayList<>();
    int currentIndex = 0;
    CountDownTimer countDownTimer;
    boolean timerRunning = false;
    int timePerQuestion = 12; // seconds
    String streakStartDate = "2025-12-1"; // first day of streak
    String dayKey;
    int correctCount = 0; // ✅ to count correct answers

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_streak_quiz);

        // Views
        circularProgressStreak = findViewById(R.id.circularProgressStreak);
        streakToolbar = findViewById(R.id.streakToolbar);
        tvTitle = findViewById(R.id.tvTitleStreak);
        tvQuestion = findViewById(R.id.tvQuestionStreak);
        tvCounter = findViewById(R.id.tvCounterStreak);
        tvTimer = findViewById(R.id.tvTimerStreak);
        radioGroup = findViewById(R.id.radioGroupStreak);
        option1 = findViewById(R.id.option1Streak);
        option2 = findViewById(R.id.option2Streak);
        option3 = findViewById(R.id.option3Streak);
        option4 = findViewById(R.id.option4Streak);
        progressBarLayout = findViewById(R.id.streakLayout);

        // Initially show only progress bar
        progressBarLayout.setVisibility(View.VISIBLE);
        tvTitle.setVisibility(View.GONE);
        tvQuestion.setVisibility(View.GONE);
        tvCounter.setVisibility(View.GONE);
        tvTimer.setVisibility(View.GONE);
        radioGroup.setVisibility(View.GONE);
        streakToolbar.setVisibility(View.GONE);
        circularProgressStreak.setVisibility(View.GONE);

        // Calculate current day based on streakStartDate
        Calendar streakStartCal = Calendar.getInstance();
        String[] parts = streakStartDate.split("-");
        streakStartCal.set(Calendar.YEAR, Integer.parseInt(parts[0]));
        streakStartCal.set(Calendar.MONTH, Integer.parseInt(parts[1]) - 1);
        streakStartCal.set(Calendar.DAY_OF_MONTH, Integer.parseInt(parts[2]));
        streakStartCal.set(Calendar.HOUR_OF_DAY, 0);
        streakStartCal.set(Calendar.MINUTE, 0);
        streakStartCal.set(Calendar.SECOND, 0);
        streakStartCal.set(Calendar.MILLISECOND, 0);

        long streakStartMillis = streakStartCal.getTimeInMillis();
        long nowMillis = System.currentTimeMillis();
        int dayNumber = (int) ((nowMillis - streakStartMillis) / (24 * 60 * 60 * 1000)) + 1;
        if (dayNumber < 1) dayNumber = 1;
        dayKey = "day" + dayNumber;

        // ✅ Firebase reference for questions (same for all users)
        databaseReference = FirebaseDatabase.getInstance()
                .getReference("streak")
                .child(dayKey)
                .child("questions");

        loadQuestions();

        // Option click listeners
        option1.setOnClickListener(v -> checkAnswer(option1));
        option2.setOnClickListener(v -> checkAnswer(option2));
        option3.setOnClickListener(v -> checkAnswer(option3));
        option4.setOnClickListener(v -> checkAnswer(option4));

        // Status bar styling
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.getDecorView().setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
            window.setStatusBarColor(Color.TRANSPARENT);
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            View decorView = getWindow().getDecorView();
            int flags = decorView.getSystemUiVisibility();
            if (isDarkModeOn()) {
                decorView.setSystemUiVisibility(flags & ~View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
            } else {
                decorView.setSystemUiVisibility(flags | View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
            }
        }
    }

    private boolean isDarkModeOn() {
        int nightModeFlags = getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK;
        return nightModeFlags == Configuration.UI_MODE_NIGHT_YES;
    }

    private void loadQuestions() {
        databaseReference.get().addOnCompleteListener(task -> {
            progressBarLayout.setVisibility(View.GONE);

            if (!task.isSuccessful() || task.getResult() == null || !task.getResult().exists()) {
                Toast.makeText(this, "No questions found for " + dayKey, Toast.LENGTH_SHORT).show();
                finish();
                return;
            }

            questionList.clear();
            for (DataSnapshot qSnap : task.getResult().getChildren()) {
                Question q = qSnap.getValue(Question.class);
                if (q != null) questionList.add(q);
            }

            if (!questionList.isEmpty()) {
                tvTitle.setVisibility(View.VISIBLE);
                tvQuestion.setVisibility(View.VISIBLE);
                tvCounter.setVisibility(View.VISIBLE);
                tvTimer.setVisibility(View.VISIBLE);
                radioGroup.setVisibility(View.VISIBLE);
                streakToolbar.setVisibility(View.VISIBLE);
                circularProgressStreak.setVisibility(View.VISIBLE);

                tvTitle.setText(dayKey.toUpperCase());
                currentIndex = 0;
                showQuestion(currentIndex);
            } else {
                Toast.makeText(this, "No questions found for " + dayKey, Toast.LENGTH_SHORT).show();
                finish();
            }
        });
    }

    private void showQuestion(int index) {
        if (index >= questionList.size()) return;

        Question q = questionList.get(index);
        tvQuestion.setText(q.getQuestion());
        option1.setText(q.getOption1());
        option2.setText(q.getOption2());
        option3.setText(q.getOption3());
        option4.setText(q.getOption4());
        radioGroup.clearCheck();

        resetOptionColors();
        enableOptions();
        tvCounter.setText((index + 1) + " / " + questionList.size());
        startTimer();
    }

    private void startTimer() {
        if (countDownTimer != null) countDownTimer.cancel();
        timerRunning = true;

        circularProgressStreak.setMax(timePerQuestion);
        circularProgressStreak.setProgress(timePerQuestion);

        countDownTimer = new CountDownTimer(timePerQuestion * 1000, 1000) {
            int secondsLeft = timePerQuestion;

            @Override
            public void onTick(long millisUntilFinished) {
                tvTimer.setText(String.valueOf(secondsLeft));
                circularProgressStreak.setProgress(secondsLeft);
                secondsLeft--;
            }

            @Override
            public void onFinish() {
                tvTimer.setText("0");
                circularProgressStreak.setProgress(0);
                timerRunning = false;
                autoSubmitAnswer();
            }
        }.start();
    }

    private void stopTimer() {
        if (countDownTimer != null) countDownTimer.cancel();
        timerRunning = false;
    }

    private void checkAnswer(RadioButton selectedButton) {
        if (!timerRunning) return;

        stopTimer();
        disableOptions();

        Question currentQuestion = questionList.get(currentIndex);
        String correctAnswer = currentQuestion.getCorrectAnswer();
        String selectedText = selectedButton.getText().toString();

        if (selectedText.equals(correctAnswer)) {
            selectedButton.setBackgroundResource(R.drawable.option_correct_bg);
            correctCount++; // ✅ increase correct count
        } else {
            selectedButton.setBackgroundResource(R.drawable.option_wrong_bg);
            highlightCorrectAnswer(correctAnswer);
        }

        selectedButton.postDelayed(this::nextQuestion, 1000);
    }

    private void autoSubmitAnswer() {
        disableOptions();
        String correctAnswer = questionList.get(currentIndex).getCorrectAnswer();
        highlightCorrectAnswer(correctAnswer);
        option1.postDelayed(this::nextQuestion, 1000);
    }

    private void highlightCorrectAnswer(String correctAnswer) {
        if (option1.getText().toString().equals(correctAnswer))
            option1.setBackgroundResource(R.drawable.option_correct_bg);
        if (option2.getText().toString().equals(correctAnswer))
            option2.setBackgroundResource(R.drawable.option_correct_bg);
        if (option3.getText().toString().equals(correctAnswer))
            option3.setBackgroundResource(R.drawable.option_correct_bg);
        if (option4.getText().toString().equals(correctAnswer))
            option4.setBackgroundResource(R.drawable.option_correct_bg);
    }

    private void nextQuestion() {
        if (currentIndex < questionList.size() - 1) {
            currentIndex++;
            showQuestion(currentIndex);
        } else {
            // ✅ Get current user's unique Firebase key from email
            String userKey = getCurrentUserKey();

            // ✅ Save progress per user, not hardcoded "user1"
            DatabaseReference userProgressRef = FirebaseDatabase.getInstance()
                    .getReference("user_progress")
                    .child(userKey);

            userProgressRef.child(dayKey).setValue(true)
                    .addOnSuccessListener(aVoid -> {
                        Intent intent = new Intent(this, StreakResultActivity.class);
                        intent.putExtra("day", dayKey);
                        intent.putExtra("correctCount", correctCount);
                        intent.putExtra("totalCount", questionList.size());
                        startActivity(intent);
                        finish();
                    })
                    .addOnFailureListener(e -> {
                        Intent intent = new Intent(this, StreakResultActivity.class);
                        intent.putExtra("day", dayKey);
                        intent.putExtra("correctCount", correctCount);
                        intent.putExtra("totalCount", questionList.size());
                        startActivity(intent);
                        finish();
                    });
        }
    }

    // ✅ Generate Firebase-safe key from user email
    private String getCurrentUserKey() {
        String email = null;
        try {
            email = FirebaseAuth.getInstance().getCurrentUser().getEmail();
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (email == null || email.isEmpty()) {
            return "guest_user";
        }

        return email.replace(".", "_")
                .replace("#", "_")
                .replace("$", "_")
                .replace("[", "_")
                .replace("]", "_");
    }

    private void disableOptions() {
        option1.setEnabled(false);
        option2.setEnabled(false);
        option3.setEnabled(false);
        option4.setEnabled(false);
    }

    private void enableOptions() {
        option1.setEnabled(true);
        option2.setEnabled(true);
        option3.setEnabled(true);
        option4.setEnabled(true);
    }

    private void resetOptionColors() {
        option1.setBackgroundResource(R.drawable.createquiz_bg);
        option2.setBackgroundResource(R.drawable.createquiz_bg);
        option3.setBackgroundResource(R.drawable.createquiz_bg);
        option4.setBackgroundResource(R.drawable.createquiz_bg);
    }

    @Override
    public void onBackPressed() {
        new AlertDialog.Builder(this)
                .setTitle("Exit Quiz")
                .setMessage("Are you sure you want to exit? Your progress will be lost.")
                .setPositiveButton("Yes", (dialog, which) -> {
                    stopTimer();
                    finish();
                })
                .setNegativeButton("No", (dialog, which) -> dialog.dismiss())
                .setCancelable(false)
                .show();
    }

    // ✅ Same Question model (untouched)
    public static class Question {
        public String question, option1, option2, option3, option4, correctAnswer;

        public Question() {}

        public String getQuestion() { return question; }
        public String getOption1() { return option1; }
        public String getOption2() { return option2; }
        public String getOption3() { return option3; }
        public String getOption4() { return option4; }
        public String getCorrectAnswer() { return correctAnswer; }
    }
}
