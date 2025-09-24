package com.example.codequerst.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.example.codequerst.R;

public class CreateQuizActivity extends AppCompatActivity {
Button btnCreateQuiz;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_create_quiz);

        btnCreateQuiz = findViewById(R.id.btnCreateQuiz);

        btnCreateQuiz.setOnClickListener(v -> startActivity(new Intent(CreateQuizActivity.this, ACreateQuizActivity.class)));
    }
}