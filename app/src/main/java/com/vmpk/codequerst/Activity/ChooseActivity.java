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
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.vmpk.codequerst.R;

public class ChooseActivity extends AppCompatActivity {
      TextView backChoose;
    LinearLayout easyLayout, mediumLayout, hardLayout;
    String language, topic;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_choose);

        language = getIntent().getStringExtra("language");
        topic = getIntent().getStringExtra("topic");

        easyLayout = findViewById(R.id.Easy);
        mediumLayout = findViewById(R.id.Medium);
        hardLayout = findViewById(R.id.Hard);
        backChoose = findViewById(R.id.backChoose);

        backChoose.setOnClickListener(v -> finish());

        easyLayout.setOnClickListener(v -> startQuiz("Easy"));
        mediumLayout.setOnClickListener(v -> startQuiz("Medium"));
        hardLayout.setOnClickListener(v -> startQuiz("Hard"));

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

    private void startQuiz(String difficulty){
        Intent intent = new Intent(ChooseActivity.this, QuizActivity.class);
        intent.putExtra("language", language);
        intent.putExtra("topic", topic);
        intent.putExtra("difficulty", difficulty);
        startActivity(intent);
        finish();
    }
}
