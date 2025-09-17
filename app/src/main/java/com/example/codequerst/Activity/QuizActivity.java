package com.example.codequerst.Activity;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.content.res.Configuration;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.codequerst.Model.Question;
import com.example.codequerst.R;
import com.google.android.material.progressindicator.CircularProgressIndicator;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

public class QuizActivity extends AppCompatActivity {

    private TextView tvQuestion, tvCounter, tvCTitle, tvTimer;
    private RadioGroup radioGroup;
    private RadioButton option1, option2, option3, option4;
    private LinearLayout loadingLayout;
    private ProgressBar progressBar;
    private CircularProgressIndicator circularProgressIndicator;

    private List<Question> questionList = new ArrayList<>();
    private int currentIndex = 0;
    private HashMap<Integer, Integer> userAnswers = new HashMap<>();

    private DatabaseReference ref;
    private String language, topic;
    private int numQuestions = 0;

    private CountDownTimer countDownTimer;
    private boolean timerRunning = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz);

        tvQuestion = findViewById(R.id.tvQuestion);
        tvCounter = findViewById(R.id.tvCounter);
        tvCTitle = findViewById(R.id.CTitle);
        radioGroup = findViewById(R.id.radioGroup);
        option1 = findViewById(R.id.option1);
        option2 = findViewById(R.id.option2);
        option3 = findViewById(R.id.option3);
        option4 = findViewById(R.id.option4);
        loadingLayout = findViewById(R.id.loadingLayout);
        progressBar = findViewById(R.id.progressbar);
        tvTimer = findViewById(R.id.tvTimer);
        circularProgressIndicator = findViewById(R.id.circularProgressIndicator);

        language = getIntent().getStringExtra("language");
        topic = getIntent().getStringExtra("topic");
        numQuestions = getIntent().getIntExtra("numQuestions", 0);

        if (language == null || topic == null) {
            Toast.makeText(this, "Invalid quiz data!", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        loadingLayout.setVisibility(View.VISIBLE);
        tvQuestion.setVisibility(View.GONE);
        radioGroup.setVisibility(View.GONE);
        tvCounter.setVisibility(View.GONE);
        tvCTitle.setVisibility(View.GONE);
        circularProgressIndicator.setVisibility(View.GONE);
        tvTimer.setVisibility(View.GONE);

        ref = FirebaseDatabase.getInstance().getReference("questions")
                .child(language)
                .child(topic);

        loadTopicTitle();
        loadQuestions();

        option1.setOnClickListener(v -> checkAnswer(1, option1));
        option2.setOnClickListener(v -> checkAnswer(2, option2));
        option3.setOnClickListener(v -> checkAnswer(3, option3));
        option4.setOnClickListener(v -> checkAnswer(4, option4));

        // status bar styling
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

    private void loadTopicTitle() {
        ref.child("title").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                tvCTitle.setText(snapshot.exists() ? snapshot.getValue(String.class) : topic);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                tvCTitle.setText(topic);
            }
        });
    }

    private void loadQuestions() {
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                questionList.clear();
                for (DataSnapshot qSnap : snapshot.getChildren()) {
                    if (!qSnap.getKey().equals("title")) {
                        Question q = qSnap.getValue(Question.class);
                        if (q != null) questionList.add(q);
                    }
                }

                if (!questionList.isEmpty()) {
                    Collections.shuffle(questionList);
                    if (numQuestions > 0 && numQuestions < questionList.size()) {
                        questionList = new ArrayList<>(questionList.subList(0, numQuestions));
                    }
                    currentIndex = 0;
                    showQuestion(currentIndex);

                    loadingLayout.setVisibility(View.GONE);
                    tvQuestion.setVisibility(View.VISIBLE);
                    radioGroup.setVisibility(View.VISIBLE);
                    tvCounter.setVisibility(View.VISIBLE);
                    tvCTitle.setVisibility(View.VISIBLE);
                    circularProgressIndicator.setVisibility(View.VISIBLE);
                    tvTimer.setVisibility(View.VISIBLE);

                    startTimer();
                } else {
                    loadingLayout.setVisibility(View.GONE);
                    Toast.makeText(QuizActivity.this, "No questions found!", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                loadingLayout.setVisibility(View.GONE);
                Toast.makeText(QuizActivity.this, "Failed to load questions!", Toast.LENGTH_SHORT).show();
                Log.e("QuizActivity", "Firebase error: " + error.getMessage());
            }
        });
    }

    private void showQuestion(int index) {
        if (index < 0 || index >= questionList.size()) return;

        Question q = questionList.get(index);
        tvQuestion.setText(q.getQuestion());
        option1.setText(q.getOption1());
        option2.setText(q.getOption2());
        option3.setText(q.getOption3());
        option4.setText(q.getOption4());

        tvCounter.setText((index + 1) + " / " + questionList.size());
        radioGroup.clearCheck();
        enableOptions();
        resetOptionColors();

        if (userAnswers.containsKey(index)) {
            int savedAnswer = userAnswers.get(index);
            if (savedAnswer == 1) option1.setChecked(true);
            else if (savedAnswer == 2) option2.setChecked(true);
            else if (savedAnswer == 3) option3.setChecked(true);
            else if (savedAnswer == 4) option4.setChecked(true);
        }
    }

    private void startTimer() {
        circularProgressIndicator.setMax(15);
        circularProgressIndicator.setProgress(15);
        long timeLeftInMillis = 15000;

        countDownTimer = new CountDownTimer(timeLeftInMillis, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                int secondsLeft = (int) (millisUntilFinished / 1000);
                tvTimer.setText(String.valueOf(secondsLeft));
                circularProgressIndicator.setProgress(secondsLeft);
            }

            @Override
            public void onFinish() {
                tvTimer.setText("0");
                circularProgressIndicator.setProgress(0);
                disableOptions();
                saveAnswer(-1);
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

        Question currentQuestion = questionList.get(currentIndex);
        int correctAnswer = currentQuestion.getAnswer(); // use getAnswer()

        Log.d("QuizActivity", "Correct Answer is: " + correctAnswer);
        Log.d("QuizActivity", "Selected Answer is: " + selectedOption);

        saveAnswer(selectedOption);

        if (selectedOption == correctAnswer) {
            selectedButton.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#4CAF50"))); // Green
        } else {
            selectedButton.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#F44336"))); // Red
            highlightCorrectAnswer(correctAnswer);
        }

        selectedButton.postDelayed(this::nextQuestion, 1000);
    }

    private void highlightCorrectAnswer(int correctAnswer) {
        if (correctAnswer == 1) option1.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#4CAF50")));
        else if (correctAnswer == 2) option2.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#4CAF50")));
        else if (correctAnswer == 3) option3.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#4CAF50")));
        else if (correctAnswer == 4) option4.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#4CAF50")));
    }

    private void saveAnswer(int selectedOption) {
        userAnswers.put(currentIndex, selectedOption);
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
        option1.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#FFFFFF")));
        option2.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#FFFFFF")));
        option3.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#FFFFFF")));
        option4.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#FFFFFF")));
    }

    private void nextQuestion() {
        if (currentIndex < questionList.size() - 1) {
            currentIndex++;
            showQuestion(currentIndex);
            startTimer();
        } else {
            openResultActivity();
        }
    }

    private void openResultActivity() {
        Intent i = new Intent(this, ResultActivity.class);
        i.putExtra("questionList", new ArrayList<>(questionList));
        i.putExtra("userAnswers", userAnswers);
        i.putExtra("language", language);
        i.putExtra("topic", topic);
//        i.putExtra("numQuestions", numQuestions);
        startActivity(i);
        finish();
    }

    @Override
    public void onBackPressed() {
        new androidx.appcompat.app.AlertDialog.Builder(this)
                .setTitle("Exit Quiz")
                .setMessage("Are you sure you want to exit? Your progress will be lost.")
                .setPositiveButton("Yes", (dialog, which) -> {
                    stopTimer(); // Stop the timer if it's running
                    finish();   // Exit the quiz
                })
                .setNegativeButton("No", (dialog, which) -> {
                    dialog.dismiss(); // Stay on the quiz
                })
                .setCancelable(false)
                .show();
    }

}
