package com.vmpk.codequerst.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.vmpk.codequerst.R;

public class ACreateQuizActivity extends AppCompatActivity {
     TextView backACreateQuiz;
    private EditText etQuizTitle, etQuizTopic;
    private RadioGroup rgDifficulty;
    private RadioButton rbEasy, rbMedium, rbHard;
    private Spinner spinnerTime;
    private Button btnNext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_acreate_quiz);

        // Bind views
        etQuizTitle = findViewById(R.id.etQuizTitle);
        etQuizTopic = findViewById(R.id.etQuizTopic);
        rgDifficulty = findViewById(R.id.rgDifficulty);
        rbEasy = findViewById(R.id.rbEasy);
        rbMedium = findViewById(R.id.rbMedium);
        rbHard = findViewById(R.id.rbHard);
        spinnerTime = findViewById(R.id.spinnerTime);
        btnNext = findViewById(R.id.btnNextStep);
        backACreateQuiz = findViewById(R.id.backACreateQuiz);

        // Back button click
        backACreateQuiz.setOnClickListener(v -> finish());

        // Next button click
        btnNext.setOnClickListener(v -> {
            String title = etQuizTitle.getText().toString().trim();
            String topic = etQuizTopic.getText().toString().trim();
            String time = spinnerTime.getSelectedItem().toString();

            // Validation
            if (TextUtils.isEmpty(title)) {
                etQuizTitle.setError("Enter quiz title");
                return;
            }
            if (TextUtils.isEmpty(topic)) {
                etQuizTopic.setError("Enter quiz topic");
                return;
            }

            // Difficulty selection
            int selectedId = rgDifficulty.getCheckedRadioButtonId();
            if (selectedId == -1) {
                Toast.makeText(this, "Please select difficulty", Toast.LENGTH_SHORT).show();
                return;
            }
            RadioButton selectedDifficulty = findViewById(selectedId);
            String difficulty = selectedDifficulty.getText().toString();

            // Pass data to next activity
            Intent intent = new Intent(ACreateQuizActivity.this, BCreateQuizActivity.class);
            intent.putExtra("quizTitle", title);
            intent.putExtra("quizTopic", topic);
            intent.putExtra("quizDifficulty", difficulty);
            intent.putExtra("quizTime", time);
            startActivity(intent);
        });
    }
}
