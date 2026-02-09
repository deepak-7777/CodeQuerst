//package com.vmpk.codequerst.Activity;
//
//import android.content.Intent;
//import android.os.Bundle;
//import android.text.TextUtils;
//import android.widget.EditText;
//import android.widget.RadioGroup;
//import android.widget.TextView;
//import android.widget.Toast;
//
//import androidx.activity.EdgeToEdge;
//import androidx.appcompat.app.AppCompatActivity;
//import androidx.recyclerview.widget.LinearLayoutManager;
//import androidx.recyclerview.widget.RecyclerView;
//
//import com.google.firebase.auth.FirebaseAuth;
//import com.vmpk.codequerst.Adapter.CreateQuizAdapter;
//import com.vmpk.codequerst.Model.ACreateQuiz;
//import com.vmpk.codequerst.Model.BCreateQuiz;
//import com.vmpk.codequerst.R;
//
//import java.util.ArrayList;
//import java.util.List;
//
//public class BCreateQuizActivity extends AppCompatActivity {
//    TextView backBCreateQuiz;
//    private EditText etQuestion, etOption1, etOption2, etOption3, etOption4;
//    private RadioGroup rgCorrectAnswer;
//    private TextView btnAddQuestion, btnSubmit;
//    private RecyclerView rvQuestions;
//    private CreateQuizAdapter adapter;
//    private List<ACreateQuiz> questionList;
//
//    // Metadata from previous activity
//    private String quizTitle, quizTopic, quizDifficulty, quizTime;
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        EdgeToEdge.enable(this);
//        setContentView(R.layout.activity_bcreate_quiz);
//
//        backBCreateQuiz = findViewById(R.id.backBCreateQuiz);
//        backBCreateQuiz.setOnClickListener(v -> finish());
//
//        quizTitle = getIntent().getStringExtra("quizTitle");
//        quizTopic = getIntent().getStringExtra("quizTopic");
//        quizDifficulty = getIntent().getStringExtra("quizDifficulty");
//        quizTime = getIntent().getStringExtra("quizTime");
//
//        etQuestion = findViewById(R.id.etQuestion);
//        etOption1 = findViewById(R.id.etOption1);
//        etOption2 = findViewById(R.id.etOption2);
//        etOption3 = findViewById(R.id.etOption3);
//        etOption4 = findViewById(R.id.etOption4);
//        rgCorrectAnswer = findViewById(R.id.rgCorrectAnswer);
//        btnAddQuestion = findViewById(R.id.btnAddQuestion);
//        btnSubmit = findViewById(R.id.btnCreateQuizSubmit);
//        rvQuestions = findViewById(R.id.rvQuestions);
//
//        rvQuestions.setLayoutManager(new LinearLayoutManager(this));
//        questionList = new ArrayList<>();
//        adapter = new CreateQuizAdapter(questionList);
//        rvQuestions.setAdapter(adapter);
//
//        btnAddQuestion.setOnClickListener(v -> addQuestion());
//        btnSubmit.setOnClickListener(v -> submitQuiz());
//    }
//
//    private void addQuestion() {
//        //  Check if question limit reached
//        if (questionList.size() >= 10) {
//            new android.app.AlertDialog.Builder(this)
//                    .setTitle("Limit Reached")
//                    .setMessage("You can only create up to 10 questions per quiz.")
//                    .setPositiveButton("OK", (dialog, which) -> dialog.dismiss())
//                    .show();
//            return;
//        }
//
//        String qText = etQuestion.getText().toString().trim();
//        String op1 = etOption1.getText().toString().trim();
//        String op2 = etOption2.getText().toString().trim();
//        String op3 = etOption3.getText().toString().trim();
//        String op4 = etOption4.getText().toString().trim();
//        int selectedId = rgCorrectAnswer.getCheckedRadioButtonId();
//
//        if (TextUtils.isEmpty(qText) || TextUtils.isEmpty(op1) || TextUtils.isEmpty(op2) ||
//                TextUtils.isEmpty(op3) || TextUtils.isEmpty(op4) || selectedId == -1) {
//            Toast.makeText(this, "Fill all fields and select correct answer", Toast.LENGTH_SHORT).show();
//            return;
//        }
//
//        int correctIndex = -1;
//        if (selectedId == R.id.rbOption1) correctIndex = 0;
//        else if (selectedId == R.id.rbOption2) correctIndex = 1;
//        else if (selectedId == R.id.rbOption3) correctIndex = 2;
//        else if (selectedId == R.id.rbOption4) correctIndex = 3;
//
//        List<String> options = new ArrayList<>();
//        options.add(op1);
//        options.add(op2);
//        options.add(op3);
//        options.add(op4);
//
//        ACreateQuiz q = new ACreateQuiz(qText, options, correctIndex);
//        questionList.add(q);
//        adapter.notifyItemInserted(questionList.size() - 1);
//
//        etQuestion.setText("");
//        etOption1.setText("");
//        etOption2.setText("");
//        etOption3.setText("");
//        etOption4.setText("");
//        rgCorrectAnswer.clearCheck();
//    }
//
//
//    private void submitQuiz() {
//        if (questionList.isEmpty()) {
//            Toast.makeText(this, "Add at least one question", Toast.LENGTH_SHORT).show();
//            return;
//        }
//
//        String userEmail = FirebaseAuth.getInstance().getCurrentUser().getEmail();
//
//        BCreateQuiz quiz = new BCreateQuiz(
//                quizTitle,
//                quizTopic,
//                quizDifficulty,
//                quizTime,
//                questionList,
//                userEmail //  store creator email
//        );
//
//        Intent intent = new Intent(BCreateQuizActivity.this, FinalCreateQuizActivity.class);
//        intent.putExtra("quizData", quiz);
//        startActivity(intent);
//        finish();
//    }
//}


package com.vmpk.codequerst.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.vmpk.codequerst.Adapter.CreateQuizAdapter;
import com.vmpk.codequerst.Model.ACreateQuiz;
import com.vmpk.codequerst.Model.BCreateQuiz;
import com.vmpk.codequerst.R;

import java.util.ArrayList;
import java.util.List;

public class BCreateQuizActivity extends AppCompatActivity {

    TextView backBCreateQuiz;
    private EditText etQuestion, etOption1, etOption2, etOption3, etOption4;
    private RadioGroup rgCorrectAnswer;
    private TextView btnAddQuestion, btnSubmit;
    private RecyclerView rvQuestions;
    private CreateQuizAdapter adapter;
    private List<ACreateQuiz> questionList;

    // Metadata from previous activity
    private String quizTitle, quizTopic, quizDifficulty, quizTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_bcreate_quiz);

        backBCreateQuiz = findViewById(R.id.backBCreateQuiz);
        backBCreateQuiz.setOnClickListener(v -> finish());

        quizTitle = getIntent().getStringExtra("quizTitle");
        quizTopic = getIntent().getStringExtra("quizTopic");
        quizDifficulty = getIntent().getStringExtra("quizDifficulty");
        quizTime = getIntent().getStringExtra("quizTime");

        etQuestion = findViewById(R.id.etQuestion);
        etOption1 = findViewById(R.id.etOption1);
        etOption2 = findViewById(R.id.etOption2);
        etOption3 = findViewById(R.id.etOption3);
        etOption4 = findViewById(R.id.etOption4);
        rgCorrectAnswer = findViewById(R.id.rgCorrectAnswer);
        btnAddQuestion = findViewById(R.id.btnAddQuestion);
        btnSubmit = findViewById(R.id.btnCreateQuizSubmit);
        rvQuestions = findViewById(R.id.rvQuestions);

        rvQuestions.setLayoutManager(new LinearLayoutManager(this));
        questionList = new ArrayList<>();
        adapter = new CreateQuizAdapter(questionList);
        rvQuestions.setAdapter(adapter);

        btnAddQuestion.setOnClickListener(v -> addQuestion());
        btnSubmit.setOnClickListener(v -> submitQuiz());
    }

    private void addQuestion() {

        if (questionList.size() >= 10) {
            new android.app.AlertDialog.Builder(this)
                    .setTitle("Limit Reached")
                    .setMessage("You can only create up to 10 questions per quiz.")
                    .setPositiveButton("OK", (dialog, which) -> dialog.dismiss())
                    .show();
            return;
        }

        String qText = etQuestion.getText().toString().trim();
        String op1 = etOption1.getText().toString().trim();
        String op2 = etOption2.getText().toString().trim();
        String op3 = etOption3.getText().toString().trim();
        String op4 = etOption4.getText().toString().trim();
        int selectedId = rgCorrectAnswer.getCheckedRadioButtonId();

        if (TextUtils.isEmpty(qText) || TextUtils.isEmpty(op1) ||
                TextUtils.isEmpty(op2) || TextUtils.isEmpty(op3) ||
                TextUtils.isEmpty(op4) || selectedId == -1) {

            Toast.makeText(this,
                    "Fill all fields and select correct answer",
                    Toast.LENGTH_SHORT).show();
            return;
        }

        int correctIndex = -1;
        if (selectedId == R.id.rbOption1) correctIndex = 0;
        else if (selectedId == R.id.rbOption2) correctIndex = 1;
        else if (selectedId == R.id.rbOption3) correctIndex = 2;
        else if (selectedId == R.id.rbOption4) correctIndex = 3;

        List<String> options = new ArrayList<>();
        options.add(op1);
        options.add(op2);
        options.add(op3);
        options.add(op4);

        ACreateQuiz q = new ACreateQuiz(qText, options, correctIndex);
        questionList.add(q);
        adapter.notifyItemInserted(questionList.size() - 1);

        etQuestion.setText("");
        etOption1.setText("");
        etOption2.setText("");
        etOption3.setText("");
        etOption4.setText("");
        rgCorrectAnswer.clearCheck();
    }

    private void submitQuiz() {

        if (questionList.isEmpty()) {
            Toast.makeText(this,
                    "Add at least one question",
                    Toast.LENGTH_SHORT).show();
            return;
        }

        // 🔥 CREATOR DETAILS
        String creatorEmail =
                FirebaseAuth.getInstance().getCurrentUser().getEmail();

        String creatorUid =
                FirebaseAuth.getInstance().getUid(); // 🔥 MAIN FIX

        // Create quiz object
        BCreateQuiz quiz = new BCreateQuiz(
                quizTitle,
                quizTopic,
                quizDifficulty,
                quizTime,
                questionList,
                creatorEmail
        );

        // 🔥 ADD CREATOR UID TO QUIZ
        quiz.setCreatorUid(creatorUid);

        Intent intent =
                new Intent(BCreateQuizActivity.this,
                        FinalCreateQuizActivity.class);

        intent.putExtra("quizData", quiz);
        startActivity(intent);
        finish();
    }
}
