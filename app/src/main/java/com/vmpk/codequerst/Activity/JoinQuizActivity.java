package com.vmpk.codequerst.Activity;

import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.vmpk.codequerst.R;

public class JoinQuizActivity extends AppCompatActivity {

    private EditText etJoinQuizCode;
    private ProgressBar joinProgressBar;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_join_quiz);

        etJoinQuizCode = findViewById(R.id.etJoinQuizCode);
        joinProgressBar = findViewById(R.id.joinProgressBar);

        db = FirebaseFirestore.getInstance();

        findViewById(R.id.btnJoinQuiz).setOnClickListener(v -> {
            String codeInput = etJoinQuizCode.getText().toString().trim();

            if (codeInput.isEmpty()) {
                Toast.makeText(this, "Please enter quiz code", Toast.LENGTH_SHORT).show();
                return;
            }

            joinProgressBar.setVisibility(View.VISIBLE);

            // Firestore: check if document exists with given code (document ID)
            db.collection("quizzes").document(codeInput).get()
                    .addOnSuccessListener(doc -> {
                        joinProgressBar.setVisibility(View.GONE);

                        if (doc.exists()) {
                            // Quiz exists → open AJoinActivity
                            Intent intent = new Intent(JoinQuizActivity.this, AJoinQuizActivity.class);
                            intent.putExtra("quizId", doc.getId());
                            intent.putExtra("quizTitle", doc.getString("title"));
                            startActivity(intent);
                        } else {
                            // Invalid code
                            Toast.makeText(this, "Invalid quiz code! Please try again.", Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnFailureListener(e -> {
                        joinProgressBar.setVisibility(View.GONE);
                        Toast.makeText(this, "Error checking code. Try again.", Toast.LENGTH_SHORT).show();
                    });
        });

        // Back button
        findViewById(R.id.backJoinQuiz).setOnClickListener(v -> finish());

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
