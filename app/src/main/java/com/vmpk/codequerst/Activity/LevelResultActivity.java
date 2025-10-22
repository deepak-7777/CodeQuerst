package com.vmpk.codequerst.Activity;

import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.vmpk.codequerst.Fragment.LevelFragment;
import com.vmpk.codequerst.Model.LevelViewModel;
import com.vmpk.codequerst.R;

public class LevelResultActivity extends AppCompatActivity {

    private ImageView imgResultBanner;
    private TextView tvCongratulation, tvQuestionStats, tvMotivation;
    private ImageView star1, star2, star3;
    private Button btnNext, btnExit;

    private int levelNumber, totalQuestions, correctAnswers;
    private LevelViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_level_result);

        // 🔹 Initialize views
        imgResultBanner = findViewById(R.id.imgResultBanner);
        tvCongratulation = findViewById(R.id.tvCongratulation);
        tvQuestionStats = findViewById(R.id.tvQuestionStats);
        tvMotivation = findViewById(R.id.tvMotivation);
        star1 = findViewById(R.id.star1);
        star2 = findViewById(R.id.star2);
        star3 = findViewById(R.id.star3);
        btnNext = findViewById(R.id.btnNext);
        btnExit = findViewById(R.id.btnExit);

        // 🔹 Get data from Intent
        levelNumber = getIntent().getIntExtra("levelNumber", 1);
        totalQuestions = getIntent().getIntExtra("totalQuestions", 0);
        correctAnswers = getIntent().getIntExtra("correctAnswers", 0);

        tvQuestionStats.setText("Questions: " + totalQuestions + " | Correct: " + correctAnswers);

        viewModel = new ViewModelProvider(this).get(LevelViewModel.class);

        // 🔹 Show first-time earned stars
        viewModel.getLevelStars().observe(this, starsMap -> {
            int stars = starsMap.getOrDefault(levelNumber, 0);

            int filled = R.drawable.ic_star_yellow;
            int empty = R.drawable.ic_star_border;

            star1.setImageResource(stars >= 1 ? filled : empty);
            star2.setImageResource(stars >= 2 ? filled : empty);
            star3.setImageResource(stars >= 3 ? filled : empty);
        });

        // 🔹 Next level button
        btnNext.setOnClickListener(v -> {
            int nextLevel = levelNumber + 1;

            // Check if next level unlocked
            if (viewModel.getLevelStatus().getValue().getOrDefault(nextLevel - 1, false)) {
                Intent intent = new Intent(LevelResultActivity.this, LevelQuizActivity.class);
                intent.putExtra("levelNumber", nextLevel);
                startActivity(intent);
            }
            finish();
        });

        // 🔹 Exit button
        btnExit.setOnClickListener(v -> {
            Intent intent = new Intent(LevelResultActivity.this, com.vmpk.codequerst.Activity.HomeActivity.class);
            startActivity(intent);
            finish();
        });


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

}
