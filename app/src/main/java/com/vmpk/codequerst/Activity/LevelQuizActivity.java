package com.vmpk.codequerst.Activity;

import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.view.Window;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.progressindicator.CircularProgressIndicator;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.*;
import com.vmpk.codequerst.Model.LevelQuiz;
import com.vmpk.codequerst.Model.LevelViewModel;
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

    private final List<LevelQuiz> questionList = new ArrayList<>();
    private int currentIndex = 0;
    private int correctCount = 0;
    private int levelNumber;

    private CountDownTimer countDownTimer;
    private boolean timerRunning = false;
    private long timeLeftInMillis = 12000; // 12 seconds

    private LevelViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_level_quiz);

        // 🔹 Initialize views
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
        levelLayout = findViewById(R.id.levelLayout);
        quizToolbar = findViewById(R.id.quizToolbar);
        levelAll = findViewById(R.id.levelAll);

        levelNumber = getIntent().getIntExtra("levelNumber", 1);

        // 🔹 Initialize ViewModel
        viewModel = new ViewModelProvider(this).get(LevelViewModel.class);

        loadLevelTitle(levelNumber);
        loadQuestionsFromFirebase(levelNumber);

        // 🔹 Option listeners
        option1Level.setOnClickListener(v -> checkAnswer(1, option1Level));
        option2Level.setOnClickListener(v -> checkAnswer(2, option2Level));
        option3Level.setOnClickListener(v -> checkAnswer(3, option3Level));
        option4Level.setOnClickListener(v -> checkAnswer(4, option4Level));

        setupStatusBar();
    }

    private void setupStatusBar() {
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
        int nightModeFlags = getResources().getConfiguration().uiMode &
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
                        levelTitle.setText(title != null ? "Level " + levelNum + ": " + title : "Level " + levelNum);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        levelTitle.setText("Level " + levelNum);
                    }
                });
    }

    private void loadQuestionsFromFirebase(int levelNum) {
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
                            LevelQuiz q = qSnap.getValue(LevelQuiz.class);
                            if (q != null) questionList.add(q);
                        }

                        levelLayout.setVisibility(View.GONE);
                        quizToolbar.setVisibility(View.VISIBLE);
                        levelAll.setVisibility(View.VISIBLE);

                        if (!questionList.isEmpty()) {
                            currentIndex = 0;
                            correctCount = 0;
                            showQuestion(currentIndex);
                        } else {
                            Toast.makeText(LevelQuizActivity.this, "No questions found for this level", Toast.LENGTH_SHORT).show();
                            finish();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        levelLayout.setVisibility(View.GONE);
                        quizToolbar.setVisibility(View.VISIBLE);
                        levelAll.setVisibility(View.VISIBLE);
                        Toast.makeText(LevelQuizActivity.this, "Error loading questions", Toast.LENGTH_SHORT).show();
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

        LevelQuiz current = questionList.get(currentIndex);
        int correctAnswer = current.getAnswer();

        if (selectedOption == correctAnswer) {
            selectedButton.setBackgroundResource(R.drawable.option_correct_bg);
            correctCount++;
        } else {
            selectedButton.setBackgroundResource(R.drawable.option_wrong_bg);
            highlightCorrectAnswer(correctAnswer);
        }

        selectedButton.postDelayed(this::nextQuestion, 1000);
    }

    private void highlightCorrectAnswer(int correctAnswer) {
        if (correctAnswer == 1) option1Level.setBackgroundResource(R.drawable.option_correct_bg);
        else if (correctAnswer == 2) option2Level.setBackgroundResource(R.drawable.option_correct_bg);
        else if (correctAnswer == 3) option3Level.setBackgroundResource(R.drawable.option_correct_bg);
        else if (correctAnswer == 4) option4Level.setBackgroundResource(R.drawable.option_correct_bg);
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
        stopTimer();
        int stars = calculateStars();
        LevelViewModel viewModel = new ViewModelProvider(this).get(LevelViewModel.class);
        // ✅ Save star only if not already saved
        viewModel.markLevelComplete(levelNumber, stars);
        // 🔹 Show star from ViewModel (Firebase/LiveData)
        viewModel.getLevelStars().observe(this, starsMap -> {
            int starToShow = starsMap.getOrDefault(levelNumber, stars);
            String starEmoji = starToShow == 3 ? "⭐⭐⭐" :
                    starToShow == 2 ? "⭐⭐" :
                            starToShow == 1 ? "⭐" : "❌";
//            Toast.makeText(this,
//                    "🎉 Level " + levelNumber + " Completed! " + starEmoji,
//                    Toast.LENGTH_LONG).show();
        });

        Intent intent = new Intent(LevelQuizActivity.this, LevelResultActivity.class);
        intent.putExtra("levelNumber", levelNumber);
        intent.putExtra("totalQuestions", questionList.size());
        intent.putExtra("correctAnswers", correctCount);
        startActivity(intent);
        finish();
        finish();
    }

    private int calculateStars() {
        int total = questionList.size();
        if (total == 0) return 0;
        float ratio = (float) correctCount / total;
        if (ratio >= 0.9f) return 3;
        else if (ratio >= 0.6f) return 2;
        else if (ratio >= 0.3f) return 1;
        else return 0;
    }
    private void saveProgressToFirebase(int levelNumber, int stars) {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) return;

        DatabaseReference ref = FirebaseDatabase.getInstance()
                .getReference("userProgress")
                .child(user.getUid())
                .child("level_" + levelNumber);

        Map<String, Object> updates = new HashMap<>();
        updates.put("complete", true);
        updates.put("stars", stars);
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
                .setNegativeButton("No", (dialog, which) -> dialog.dismiss())
                .setCancelable(false)
                .show();
    }
}
