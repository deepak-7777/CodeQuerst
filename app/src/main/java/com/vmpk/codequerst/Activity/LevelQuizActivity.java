package com.vmpk.codequerst.Activity;

import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.Window;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.android.material.progressindicator.CircularProgressIndicator;
import com.vmpk.codequerst.Model.LevelQuiz;
import com.vmpk.codequerst.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LevelQuizActivity extends AppCompatActivity {

    private TextView tvQuestionLevel, tvCounterLevel, levelTitle, tvTimerLevel;
    private RadioGroup radioGroupLevel;
    private RadioButton option1Level, option2Level, option3Level, option4Level;
    private CircularProgressIndicator circularProgressIndicator;
    private View levelLayout, quizToolbar, levelAll;

    private List<LevelQuiz> questionList = new ArrayList<>();
    private int currentIndex = 0;
    private int levelNumber;

    private CountDownTimer countDownTimer;
    private boolean timerRunning = false;
    private long timeLeftInMillis = 12000; // 12 seconds per question

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_level_quiz);

        tvQuestionLevel = findViewById(R.id.tvQuestionLevel);
        tvCounterLevel = findViewById(R.id.tvCounterLevel);
        radioGroupLevel = findViewById(R.id.radioGroupLevel);
        option1Level = findViewById(R.id.option1Level);
        option2Level = findViewById(R.id.option2Level);
        option3Level = findViewById(R.id.option3Level);
        option4Level = findViewById(R.id.option4Level);
        levelTitle = findViewById(R.id.level);
        tvTimerLevel = findViewById(R.id.tvTimerLevel);
        circularProgressIndicator = findViewById(R.id.circularProgressLevel);

        // Views for progress visibility
        levelLayout = findViewById(R.id.levelLayout);
        quizToolbar = findViewById(R.id.quizToolbar);
        levelAll = findViewById(R.id.levelAll);

        levelNumber = getIntent().getIntExtra("levelNumber", 1);
        loadLevelTitle(levelNumber);
        loadQuestionsFromFirebase(levelNumber);

        option1Level.setOnClickListener(v -> checkAnswer(1, option1Level));
        option2Level.setOnClickListener(v -> checkAnswer(2, option2Level));
        option3Level.setOnClickListener(v -> checkAnswer(3, option3Level));
        option4Level.setOnClickListener(v -> checkAnswer(4, option4Level));

        // 7️ Status bar styling
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
        int nightModeFlags =
                getResources().getConfiguration().uiMode &
                        Configuration.UI_MODE_NIGHT_MASK;
        return nightModeFlags == Configuration.UI_MODE_NIGHT_YES;
    }

    private void loadLevelTitle(int levelNum) {
        FirebaseDatabase.getInstance()
                .getReference("levels")
                .child("level" + levelNum)
                .child("title")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        String title = snapshot.getValue(String.class);
                        if (title != null) {
                            levelTitle.setText("Level " + levelNum + ": " + title);
                        } else {
                            levelTitle.setText("Level " + levelNum);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        levelTitle.setText("Level " + levelNum);
                    }
                });
    }

    private void loadQuestionsFromFirebase(int levelNum) {
        // 🔄 Show loading
        levelLayout.setVisibility(View.VISIBLE);
        quizToolbar.setVisibility(View.GONE);
        levelAll.setVisibility(View.GONE);

        FirebaseDatabase.getInstance()
                .getReference("levels")
                .child("level" + levelNum)
                .child("questions")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        questionList.clear();
                        for (DataSnapshot qSnap : snapshot.getChildren()) {
                            LevelQuiz question = qSnap.getValue(LevelQuiz.class);
                            if (question != null) questionList.add(question);
                        }

                        // 🔓 Hide loading and show quiz
                        levelLayout.setVisibility(View.GONE);
                        quizToolbar.setVisibility(View.VISIBLE);
                        levelAll.setVisibility(View.VISIBLE);

                        if (!questionList.isEmpty()) {
                            currentIndex = 0;
                            showQuestion(currentIndex);
                        } else {
                            Toast.makeText(LevelQuizActivity.this,
                                    "No questions found for this level", Toast.LENGTH_SHORT).show();
                            finish();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        levelLayout.setVisibility(View.GONE);
                        quizToolbar.setVisibility(View.VISIBLE);
                        levelAll.setVisibility(View.VISIBLE);
                        Toast.makeText(LevelQuizActivity.this,
                                "Error loading questions", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void showQuestion(int index) {
        if (index < questionList.size()) {
            LevelQuiz q = questionList.get(index);
            tvQuestionLevel.setText(q.getQuestion());
            option1Level.setText(q.getOption1());
            option2Level.setText(q.getOption2());
            option3Level.setText(q.getOption3());
            option4Level.setText(q.getOption4());
            tvCounterLevel.setText((index + 1) + " / " + questionList.size());

            radioGroupLevel.clearCheck();
            resetOptionColors();
            enableOptions();
            startTimer();
        } else {
            levelComplete();
        }
    }

    private void resetOptionColors() {
        option1Level.setBackgroundResource(R.drawable.createquiz_bg);
        option2Level.setBackgroundResource(R.drawable.createquiz_bg);
        option3Level.setBackgroundResource(R.drawable.createquiz_bg);
        option4Level.setBackgroundResource(R.drawable.createquiz_bg);

        option1Level.setChecked(false);
        option2Level.setChecked(false);
        option3Level.setChecked(false);
        option4Level.setChecked(false);
    }

    private void startTimer() {
        stopTimer();
        timeLeftInMillis = 12000;
        circularProgressIndicator.setMax(12);
        circularProgressIndicator.setProgress(12);

        countDownTimer = new CountDownTimer(timeLeftInMillis, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                int secondsLeft = (int) (millisUntilFinished / 1000);
                tvTimerLevel.setText(String.valueOf(secondsLeft));
                circularProgressIndicator.setProgress(secondsLeft);
            }

            @Override
            public void onFinish() {
                tvTimerLevel.setText("0");
                circularProgressIndicator.setProgress(0);
                disableOptions();
                nextQuestion();
            }
        }.start();

        timerRunning = true;
    }

    private void stopTimer() {
        if (countDownTimer != null) countDownTimer.cancel();
        timerRunning = false;
    }

    private void checkAnswer(int selectedOption, RadioButton selectedButton) {
        if (!timerRunning) return;

        stopTimer();
        disableOptions();

        LevelQuiz currentQuestion = questionList.get(currentIndex);
        int correctAnswer = currentQuestion.getAnswer();

        if (selectedOption == correctAnswer) {
            selectedButton.setBackgroundResource(R.drawable.option_correct_bg);
        } else {
            selectedButton.setBackgroundResource(R.drawable.option_wrong_bg);
            highlightCorrectAnswer(correctAnswer);
        }

        selectedButton.postDelayed(this::nextQuestion, 1000);
    }

    private void highlightCorrectAnswer(int correctAnswer) {
        if (correctAnswer == 1)
            option1Level.setBackgroundResource(R.drawable.option_correct_bg);
        else if (correctAnswer == 2)
            option2Level.setBackgroundResource(R.drawable.option_correct_bg);
        else if (correctAnswer == 3)
            option3Level.setBackgroundResource(R.drawable.option_correct_bg);
        else if (correctAnswer == 4)
            option4Level.setBackgroundResource(R.drawable.option_correct_bg);
    }

    private void disableOptions() {
        option1Level.setEnabled(false);
        option2Level.setEnabled(false);
        option3Level.setEnabled(false);
        option4Level.setEnabled(false);
    }

    private void enableOptions() {
        option1Level.setEnabled(true);
        option2Level.setEnabled(true);
        option3Level.setEnabled(true);
        option4Level.setEnabled(true);
    }

    private void nextQuestion() {
        if (currentIndex < questionList.size() - 1) {
            currentIndex++;
            showQuestion(currentIndex);
        } else {
            levelComplete();
        }
    }

    private void levelComplete() {
        markLevelCompleteInFirebase(levelNumber);
        Toast.makeText(this, "🎉 Level " + levelNumber + " completed!", Toast.LENGTH_SHORT).show();
        finish();
    }

    private void markLevelCompleteInFirebase(int levelNumber) {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) return;

        DatabaseReference ref = FirebaseDatabase.getInstance()
                .getReference("userProgress")
                .child(user.getUid());
        // User ka email bhi save kar do
        Map<String, Object> updates = new HashMap<>();
        updates.put("level_" + levelNumber + "_complete", true);
        updates.put("email", user.getEmail());

        ref.updateChildren(updates);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        stopTimer();
    }

    @Override
    public void onBackPressed() {
        new androidx.appcompat.app.AlertDialog.Builder(this)
                .setTitle("Exit Quiz")
                .setMessage("Are you sure you want to exit? Your progress will be lost.")
                .setPositiveButton("Yes", (dialog, which) -> {
                    stopTimer();
                    finish();
                })
                .setNegativeButton("No", (dialog, which) -> {
                    dialog.dismiss();
                })
                .setCancelable(false)
                .show();
    }
}
