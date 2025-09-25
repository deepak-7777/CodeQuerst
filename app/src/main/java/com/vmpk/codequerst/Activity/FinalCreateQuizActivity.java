package com.vmpk.codequerst.Activity;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import android.widget.TextView;

import com.google.firebase.firestore.FirebaseFirestore;
import com.vmpk.codequerst.Model.BCreateQuiz;
import com.vmpk.codequerst.R;

import java.util.UUID;

public class FinalCreateQuizActivity extends AppCompatActivity {

    private TextView tvQuizTitle, tvQuizTopic, tvQuizDifficulty, tvQuestionsCount, tvQuizCode;
    private AppCompatButton btnCopyCode, btnShareCode, btnGoHome;

    private FirebaseFirestore db;
    private String quizId;
    private BCreateQuiz quiz;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_final_create_quiz);

        tvQuizTitle = findViewById(R.id.tvQuizTitle);
        tvQuizTopic = findViewById(R.id.tvQuizTopic);
        tvQuizDifficulty = findViewById(R.id.tvQuizDifficulty);
        tvQuestionsCount = findViewById(R.id.tvQuestionsCount);
        tvQuizCode = findViewById(R.id.tvQuizCode);

        btnCopyCode = findViewById(R.id.btnCopyCode);
        btnShareCode = findViewById(R.id.btnShareCode);
        btnGoHome = findViewById(R.id.btnGoHome);

        db = FirebaseFirestore.getInstance();

        // Receive quiz object from previous activity
        quiz = (BCreateQuiz) getIntent().getSerializableExtra("quizData");
        if (quiz == null) {
            Toast.makeText(this, "No quiz data found!", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Set quiz summary
        tvQuizTitle.setText("Title: " + quiz.getTitle());
        tvQuizTopic.setText("Topic: " + quiz.getTopic());
        tvQuizDifficulty.setText("Difficulty: " + quiz.getDifficulty());
        tvQuestionsCount.setText("Questions: " + quiz.getQuestions().size());

        // Generate unique 6-character code
        quizId = UUID.randomUUID().toString().substring(0, 6).toUpperCase();
        tvQuizCode.setText(quizId);

        // Save to Firestore using the code as document ID
        db.collection("quizzes").document(quizId).set(quiz)
                .addOnSuccessListener(aVoid -> {
                })
                .addOnFailureListener(e -> Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show());

        // Copy quiz code
        btnCopyCode.setOnClickListener(v -> {
            ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
            ClipData clip = ClipData.newPlainText("Quiz Code", quizId);
            clipboard.setPrimaryClip(clip);
            Toast.makeText(this, "Code copied to clipboard", Toast.LENGTH_SHORT).show();
        });

        // Share quiz code
        btnShareCode.setOnClickListener(v -> {
            Intent shareIntent = new Intent(Intent.ACTION_SEND);
            shareIntent.setType("text/plain");
            shareIntent.putExtra(Intent.EXTRA_SUBJECT, "Play My Quiz!");
            shareIntent.putExtra(Intent.EXTRA_TEXT, "Join my quiz using this code: " + quizId);
            startActivity(Intent.createChooser(shareIntent, "Share Quiz Code"));
        });

        // Go to home
        btnGoHome.setOnClickListener(v -> {
            Intent intent = new Intent(FinalCreateQuizActivity.this, CreateQuizActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
        });
    }
}
