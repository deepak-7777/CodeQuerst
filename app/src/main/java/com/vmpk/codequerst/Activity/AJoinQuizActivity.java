package com.vmpk.codequerst.Activity;

import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.view.View;
import android.view.Window;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.progressindicator.CircularProgressIndicator;
import com.google.firebase.firestore.FirebaseFirestore;
import com.vmpk.codequerst.Model.BJoinQuiz;
import com.vmpk.codequerst.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class AJoinQuizActivity extends AppCompatActivity {
    private TextView tvQuestion, tvCounter, tvTimer, backJoinQuizActivity;
    private RadioGroup radioGroup;
    private RadioButton option1, option2, option3, option4;
    private CircularProgressIndicator circularProgress;
    private LinearLayout loadingLayout, contentLayout;
    private List<BQuestion> questionList = new ArrayList<>();
    private int currentQuestionIndex = 0;
    private CountDownTimer countDownTimer;
    private Handler handler = new Handler();
    private boolean answerClicked = false;
    private int correctCount = 0;
    private String quizId;
    private FirebaseFirestore db;
    private int globalTimePerQuestion = 15; // fallback by default

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ajoin_quiz);

        tvQuestion = findViewById(R.id.tvQuestionJoinQuiz);
        tvCounter = findViewById(R.id.tvCounterJoinQuiz);
        tvTimer = findViewById(R.id.tvTimerJoinQuiz);
        radioGroup = findViewById(R.id.radioGroupJoinQuiz);
        option1 = findViewById(R.id.option1JoinQuiz);
        option2 = findViewById(R.id.option2JoinQuiz);
        option3 = findViewById(R.id.option3JoinQuiz);
        option4 = findViewById(R.id.option4JoinQuiz);
        circularProgress = findViewById(R.id.circularProgressJoinQuiz);

        loadingLayout = findViewById(R.id.loadingJoinQuiz);
        contentLayout = findViewById(R.id.contentJoinQuiz);

        backJoinQuizActivity = findViewById(R.id.backJoinQuizActivity);
        backJoinQuizActivity.setOnClickListener(v -> onBackPressed());

        quizId = getIntent().getStringExtra("quizId");
        db = FirebaseFirestore.getInstance();

        loadQuizData();
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
        int nightModeFlags =
                getResources().getConfiguration().uiMode &
                        Configuration.UI_MODE_NIGHT_MASK;
        return nightModeFlags == Configuration.UI_MODE_NIGHT_YES;
    }

    private void loadQuizData() {
        loadingLayout.setVisibility(View.VISIBLE);
        contentLayout.setVisibility(View.GONE);

        db.collection("quizzes").document(quizId).get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        loadingLayout.setVisibility(View.GONE);
                        contentLayout.setVisibility(View.VISIBLE);

                        String title = documentSnapshot.getString("title");
                        globalTimePerQuestion = parseTime(documentSnapshot.get("timePerQuestion"));

                        TextView titleJoin = findViewById(R.id.titleJoinQuiz);
                        titleJoin.setText(title);

                        List<Map<String, Object>> questions =
                                (List<Map<String, Object>>) documentSnapshot.get("questions");

                        if (questions != null) {
                            for (Map<String, Object> qMap : questions) {
                                String questionText = (String) qMap.get("question");
                                List<String> options = (List<String>) qMap.get("options");
                                int correctIndex = ((Long) qMap.get("correctIndex")).intValue();

                                int timeSec = parseTime(qMap.get("timePerQuestion"));
                                if (timeSec == -1) timeSec = globalTimePerQuestion;
                                if (timeSec == -1) timeSec = 15;

                                questionList.add(new BQuestion(questionText, options, correctIndex, timeSec));
                            }
                            loadQuestion(currentQuestionIndex);
                        }
                    } else {
                        Toast.makeText(this, "Quiz data not found!", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                })
                .addOnFailureListener(e -> {
                    loadingLayout.setVisibility(View.GONE);
                    contentLayout.setVisibility(View.GONE);
                    Toast.makeText(this, "Failed to load quiz: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    finish();
                });
    }

    private int parseTime(Object timeObj) {
        try {
            if (timeObj == null) return -1;
            String timeStr = timeObj.toString().trim();
            if (timeStr.contains(" ")) timeStr = timeStr.split(" ")[0];
            return Integer.parseInt(timeStr);
        } catch (Exception e) {
            return -1;
        }
    }

    private void loadQuestion(int index) {
        answerClicked = false;
        stopTimer();

        BQuestion q = questionList.get(index);

        tvQuestion.setText(q.question);
        option1.setText(q.options.get(0));
        option2.setText(q.options.get(1));
        option3.setText(q.options.get(2));
        option4.setText(q.options.get(3));

        option1.setBackgroundResource(R.drawable.rounded_option_bg);
        option2.setBackgroundResource(R.drawable.rounded_option_bg);
        option3.setBackgroundResource(R.drawable.rounded_option_bg);
        option4.setBackgroundResource(R.drawable.rounded_option_bg);

        enableOptions();
        tvCounter.setText((index + 1) + " / " + questionList.size());

        startTimer(q.timePerQuestion);

        radioGroup.setOnCheckedChangeListener((group, checkedId) -> {
            if (answerClicked) return;
            answerClicked = true;

            int selectedIndex = -1;
            if (checkedId == option1.getId()) selectedIndex = 0;
            else if (checkedId == option2.getId()) selectedIndex = 1;
            else if (checkedId == option3.getId()) selectedIndex = 2;
            else if (checkedId == option4.getId()) selectedIndex = 3;

            checkAnswer(selectedIndex);
        });
    }

    private void startTimer(int seconds) {
        stopTimer();

        circularProgress.setMax(seconds);
        circularProgress.setProgress(seconds);
        tvTimer.setText(String.valueOf(seconds));

        countDownTimer = new CountDownTimer(seconds * 1000L, 1000) {
            int remaining = seconds;

            @Override
            public void onTick(long millisUntilFinished) {
                tvTimer.setText(String.valueOf(remaining));
                circularProgress.setProgress(remaining);
                remaining--;
            }

            @Override
            public void onFinish() {
                if (!answerClicked) {
                    answerClicked = true;
                    tvTimer.setText("0");
                    circularProgress.setProgress(0);
                    disableOptions();
                    moveToNextQuestion();
                }
            }
        };
        countDownTimer.start();
    }

    private void checkAnswer(int selectedIndex) {
        stopTimer();

        BQuestion currentQ = questionList.get(currentQuestionIndex);
        currentQ.selectedIndex = selectedIndex; // store user selection

        if (selectedIndex == currentQ.correctIndex) {
            getOptionByIndex(selectedIndex).setBackgroundResource(R.drawable.option_correct_bg);
            correctCount++;
        } else {
            getOptionByIndex(selectedIndex).setBackgroundResource(R.drawable.option_wrong_bg);
            getOptionByIndex(currentQ.correctIndex).setBackgroundResource(R.drawable.option_correct_bg);
        }

        disableOptions();
        handler.postDelayed(this::moveToNextQuestion, 1000);
    }

    private void moveToNextQuestion() {
        currentQuestionIndex++;
        if (currentQuestionIndex < questionList.size()) {
            radioGroup.clearCheck();
            loadQuestion(currentQuestionIndex);
        } else {
            // Prepare questionList to send to BJoinQuizActivity
            ArrayList<BJoinQuiz> questionListToSend = new ArrayList<>();
            for (BQuestion q : questionList) {
                questionListToSend.add(new BJoinQuiz(
                        q.question,
                        new ArrayList<>(q.options),
                        q.correctIndex,
                        q.selectedIndex
                ));
            }

            Intent intent = new Intent(AJoinQuizActivity.this, BJoinQuizActivity.class);
            intent.putExtra("totalQuestions", questionList.size());
            intent.putExtra("correctAnswers", correctCount);
            intent.putExtra("questionList", questionListToSend); //  send question list
            startActivity(intent);
            finish();
        }
    }

    private RadioButton getOptionByIndex(int index) {
        switch (index) {
            case 0: return option1;
            case 1: return option2;
            case 2: return option3;
            case 3: return option4;
        }
        return null;
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

    private void stopTimer() {
        if (countDownTimer != null) {
            countDownTimer.cancel();
            countDownTimer = null;
        }
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

    // ===== BQuestion Class =====
    static class BQuestion {
        String question;
        List<String> options;
        int correctIndex;
        int timePerQuestion;
        int selectedIndex = -1; // track user selection

        public BQuestion(String question, List<String> options, int correctIndex, int timePerQuestion) {
            this.question = question;
            this.options = options;
            this.correctIndex = correctIndex;
            this.timePerQuestion = timePerQuestion;
            this.selectedIndex = -1;
        }
    }
}
