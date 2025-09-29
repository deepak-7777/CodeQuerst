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

import com.vmpk.codequerst.R;

public class ChooseJavaActivity extends AppCompatActivity {
    TextView backChooseJava;
    LinearLayout easyJava, mediumJava, hardJava;
    String language, topic;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_java);

        backChooseJava = findViewById(R.id.backChooseJava);
        easyJava = findViewById(R.id.EasyJava);
        mediumJava = findViewById(R.id.MediumJava);
        hardJava = findViewById(R.id.HardJava);

        // Get language & topic from intent
        language = getIntent().getStringExtra("language");
        topic = getIntent().getStringExtra("topic");

        backChooseJava.setOnClickListener(v -> finish());

        // Click listeners for difficulty
        easyJava.setOnClickListener(v -> openQuizActivity("Easy"));
        mediumJava.setOnClickListener(v -> openQuizActivity("Medium"));
        hardJava.setOnClickListener(v -> openQuizActivity("Hard"));

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

    private void openQuizActivity(String difficulty) {
        Intent intent = new Intent(ChooseJavaActivity.this, QuizActivity.class);
        intent.putExtra("language", language);
        intent.putExtra("topic", topic);
        intent.putExtra("difficulty", difficulty);
        startActivity(intent);
    }
}
