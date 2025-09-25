package com.vmpk.codequerst.Activity;

import android.os.Bundle;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.vmpk.codequerst.Adapter.QuizQuestionAdapter;
import com.vmpk.codequerst.Model.ACreateQuiz;
import com.vmpk.codequerst.Model.BCreateQuiz;
import com.vmpk.codequerst.R;

import java.util.List;

public class QuizDetailActivity extends AppCompatActivity {

    private RecyclerView rvQuizQuestions;
    private QuizQuestionAdapter adapter;
    private BCreateQuiz quiz;
    private TextView tvQuizTitle, backQuizDetail;
    private String quizId; // Firestore document ID

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_quiz_detail);

        rvQuizQuestions = findViewById(R.id.rvQuizQuestions);
        tvQuizTitle = findViewById(R.id.tvQuizTitle);
        backQuizDetail = findViewById(R.id.backQuizDetail);

        backQuizDetail.setOnClickListener(v -> finish());

        // 🔹 Get data from intent
        quiz = (BCreateQuiz) getIntent().getSerializableExtra("quizData");
        quizId = getIntent().getStringExtra("quizId"); // Firestore document id

        if (quiz == null || quizId == null) {
            finish();
            return;
        }

        tvQuizTitle.setText(quiz.getTitle());

        List<ACreateQuiz> questions = quiz.getQuestions();
        adapter = new QuizQuestionAdapter(questions, quizId); //  Pass quizId to adapter
        rvQuizQuestions.setLayoutManager(new LinearLayoutManager(this));
        rvQuizQuestions.setAdapter(adapter);
    }
}
