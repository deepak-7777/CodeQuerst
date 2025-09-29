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

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.vmpk.codequerst.R;

public class ChooseKotlinActivity extends AppCompatActivity {

    TextView backKotlin;
    LinearLayout EasyKotlin, MediumKotlin, HardKotlin;
    String topicName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_choose_kotlin);

        backKotlin = findViewById(R.id.backKotlin);
        EasyKotlin = findViewById(R.id.EasyKotlin);
        MediumKotlin = findViewById(R.id.MediumKotlin);
        HardKotlin = findViewById(R.id.HardKotlin);

        topicName = getIntent().getStringExtra("topic");

        backKotlin.setOnClickListener(v -> finish());

        EasyKotlin.setOnClickListener(v -> openQuiz("Easy"));
        MediumKotlin.setOnClickListener(v -> openQuiz("Medium"));
        HardKotlin.setOnClickListener(v -> openQuiz("Hard"));

        // 7️⃣ Status bar styling
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

    private void openQuiz(String difficulty) {
        Intent i = new Intent(ChooseKotlinActivity.this, QuizActivity.class);
        i.putExtra("language", "Kotlin");
        i.putExtra("topic", topicName);
        i.putExtra("difficulty", difficulty);
        startActivity(i);
    }
}
