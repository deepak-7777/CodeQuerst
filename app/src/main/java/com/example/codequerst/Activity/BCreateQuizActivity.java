package com.example.codequerst.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.example.codequerst.R;

public class BCreateQuizActivity extends AppCompatActivity {
Button btnQuizSubmit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_bcreate_quiz);

        btnQuizSubmit = findViewById(R.id.btnQuizSubmit);
        btnQuizSubmit.setOnClickListener(v -> {
            startActivity(new Intent(BCreateQuizActivity.this, FinalCreateQuizActivity.class));
        });
    }
}