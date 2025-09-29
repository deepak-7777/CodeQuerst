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

public class ChooseJavaScriptActivity extends AppCompatActivity {

    TextView backChooseJavaScript;
    LinearLayout easyJavaScript, mediumJavaScript, hardJavaScript;

    String language, topic;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_java_script);

        backChooseJavaScript = findViewById(R.id.backChooseJavaScript);
        easyJavaScript = findViewById(R.id.EasyJavaScript);
        mediumJavaScript = findViewById(R.id.MediumJavaScript);
        hardJavaScript = findViewById(R.id.HardJavaScript);

        // Get language & topic from intent
        language = getIntent().getStringExtra("language");
        topic = getIntent().getStringExtra("topic");

        backChooseJavaScript.setOnClickListener(v -> finish());

        // Click listeners for difficulty
        easyJavaScript.setOnClickListener(v -> openQuizActivity("Easy"));
        mediumJavaScript.setOnClickListener(v -> openQuizActivity("Medium"));
        hardJavaScript.setOnClickListener(v -> openQuizActivity("Hard"));

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
        Intent intent = new Intent(ChooseJavaScriptActivity.this, QuizActivity.class);
        intent.putExtra("language", language);
        intent.putExtra("topic", topic);
        intent.putExtra("difficulty", difficulty);
        startActivity(intent);
    }
}
