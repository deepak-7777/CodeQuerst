package com.vmpk.codequerst.Activity;

import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.vmpk.codequerst.Adapter.JoinQuizAdapter;
import com.vmpk.codequerst.Model.BJoinQuiz;
import com.vmpk.codequerst.R;

import java.util.ArrayList;

public class BJoinQuizActivity extends AppCompatActivity {
    LinearLayout homeJoinQuiz;
    private TextView tvQuestionsAsked, tvCorrect;
    private AppCompatButton btnShowSolution;
    private RecyclerView rvShowQuiz;
    private ArrayList<BJoinQuiz> questionList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bjoin_quiz);

        tvQuestionsAsked = findViewById(R.id.tvQuestionsAsked);
        tvCorrect = findViewById(R.id.tvCorrect);
        btnShowSolution = findViewById(R.id.btnShowSolution);
        rvShowQuiz = findViewById(R.id.rvShowQuiz);
        homeJoinQuiz = findViewById(R.id.homeJoinQuiz);

        homeJoinQuiz.setOnClickListener(v -> {
            Intent intent = new Intent(BJoinQuizActivity.this, CreateQuizActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
        });

        int totalQuestions = getIntent().getIntExtra("totalQuestions", 0);
        int correctAnswers = getIntent().getIntExtra("correctAnswers", 0);

        // Get passed question list
        questionList = (ArrayList<BJoinQuiz>) getIntent().getSerializableExtra("questionList");

        tvQuestionsAsked.setText("Questions Asked : " + totalQuestions);
        tvCorrect.setText("Correctly Answered : " + correctAnswers);

        // On button click -> show quiz solution
        btnShowSolution.setOnClickListener(v -> {
            if (questionList != null && !questionList.isEmpty()) {
                rvShowQuiz.setLayoutManager(new LinearLayoutManager(this));
                JoinQuizAdapter adapter = new JoinQuizAdapter(this, questionList);
                rvShowQuiz.setAdapter(adapter);
            } else {
                tvQuestionsAsked.setText("No questions to show!");
            }
        });

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
}
