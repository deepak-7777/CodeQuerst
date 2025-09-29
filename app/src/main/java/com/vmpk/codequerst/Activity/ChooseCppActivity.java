package com.vmpk.codequerst.Activity;

import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.LinearLayout;
import androidx.appcompat.app.AppCompatActivity;
import com.vmpk.codequerst.R;

public class ChooseCppActivity extends AppCompatActivity {

    LinearLayout easyCpp, mediumCpp, hardCpp;
    String language, topic;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_cpp);

        // Receive language and topic from CppActivity
        language = getIntent().getStringExtra("language");
        topic = getIntent().getStringExtra("topic");

        easyCpp = findViewById(R.id.EasyCpp);
        mediumCpp = findViewById(R.id.MediumCpp);
        hardCpp = findViewById(R.id.HardCpp);

        // Easy button click
        easyCpp.setOnClickListener(v -> openQuizActivity("Easy"));

        // Medium button click
        mediumCpp.setOnClickListener(v -> openQuizActivity("Medium"));

        // Hard button click
        hardCpp.setOnClickListener(v -> openQuizActivity("Hard"));

        // Back button
        findViewById(R.id.backChooseCpp).setOnClickListener(v -> finish());

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

    private void openQuizActivity(String difficulty){
        Intent i = new Intent(ChooseCppActivity.this, QuizActivity.class);
        i.putExtra("language", language);
        i.putExtra("topic", topic);
        i.putExtra("difficulty", difficulty); // ✅ Pass difficulty
        startActivity(i);
    }
}
