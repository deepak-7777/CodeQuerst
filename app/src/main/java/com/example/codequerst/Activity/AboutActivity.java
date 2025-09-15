package com.example.codequerst.Activity;

import android.content.res.Configuration;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.view.Window;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import com.example.codequerst.R;

public class AboutActivity extends AppCompatActivity {
    TextView aboutText, backAbout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_about);

        backAbout = findViewById(R.id.backAbout);

        backAbout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        aboutText = findViewById(R.id.aboutText);

        String about =          //  "<b>About Our Coding Quiz App</b><br><br>" +
                "Our Coding Quiz App is a <b>multiple-choice question (MCQ)</b> based learning platform designed to test and improve your coding knowledge.<br><br>" +
                        "The app includes a wide range of quizzes on programming languages such as <b>C, C++, Java, Python</b>, and important computer science concepts like <b>Data Structures, Algorithms, OOPs,</b> and <b>Databases</b>.<br><br>" +
                        "With each quiz, users can challenge themselves, track their scores, and strengthen their problem-solving skills. The interactive MCQ format makes learning simple, effective, and fun.<br><br>" +
                        "Whether you are a <b>student preparing for exams</b>, an <b>aspiring programmer preparing for interviews</b>, or just someone who enjoys coding challenges, this app provides the right set of quizzes to sharpen your skills.<br><br>" +
                        "Our goal is to make coding education engaging and accessible for everyone.<br><br>" +
                        "<b>Learn. Practice. Grow.</b> with our Coding Quiz App!<br><br>" +

                        "<b>Why Choose This App?</b><br><br>" +
                        "✔ Covers both <b>beginner</b> and <b>advanced</b> coding concepts.<br>" +
                        "✔ Simple and user-friendly interface designed for smooth learning.<br>" +
                        "✔ Regular updates with fresh questions to match current trends.<br>" +
                        "✔ Helpful for competitive exams, coding interviews, and self-assessment.<br><br>" +

                        "We believe that coding is not just about syntax, but about building logic and problem-solving skills. Through consistent practice with our quizzes, you will gain confidence in tackling programming challenges of any level. " +
                        "This app is built for learners who want to grow step by step, from basics to advanced topics, while enjoying the process. " +
                        "Your feedback is valuable to us, and we aim to keep improving the experience with every update.";


        aboutText.setText(Html.fromHtml(about));


        ///     status bar fit in toolbar  and status bar color related
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
                // Dark Mode → white icons
                decorView.setSystemUiVisibility(flags & ~View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
            } else {
                // Light Mode → black icons
                decorView.setSystemUiVisibility(flags | View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
            }
        }
    }

    ///     status bar fit in toolbar  and status bar color related
    private boolean isDarkModeOn() {
        int nightModeFlags =
                getResources().getConfiguration().uiMode &
                        Configuration.UI_MODE_NIGHT_MASK;
        return nightModeFlags == Configuration.UI_MODE_NIGHT_YES;
    }
}