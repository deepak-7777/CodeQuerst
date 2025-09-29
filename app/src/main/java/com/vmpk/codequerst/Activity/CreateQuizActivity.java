package com.vmpk.codequerst.Activity;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.vmpk.codequerst.Adapter.QuizAdapter;
import com.vmpk.codequerst.Model.BCreateQuiz;
import com.vmpk.codequerst.R;

import java.util.ArrayList;
import java.util.List;

public class CreateQuizActivity extends AppCompatActivity {
    private Button btnCreateQuiz, btnJoinQuiz;
    private TextView backCreateQuiz;
    private RecyclerView rvShowQuiz;
    private SwipeRefreshLayout swipeRefresh;
    private ProgressBar progressBar;
    private EditText etSearch;
    private QuizAdapter adapter;
    private List<BCreateQuiz> quizList;
    private List<BCreateQuiz> filteredList; //  Filtered list for search
    private List<String> quizIds;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_create_quiz);

        rvShowQuiz = findViewById(R.id.rvShowQuiz);
        progressBar = findViewById(R.id.CreateQuizProgressBar);
        btnCreateQuiz = findViewById(R.id.btnCreateQuiz);
        btnJoinQuiz = findViewById(R.id.btnJoinQuiz);
        backCreateQuiz = findViewById(R.id.backCreateQuiz);
        etSearch = findViewById(R.id.etSearch);
        swipeRefresh = findViewById(R.id.swipeRefresh);

        swipeRefresh.setOnRefreshListener(() -> {
            loadQuizzes(); // refresh RecyclerView items
        });

        rvShowQuiz.setLayoutManager(new LinearLayoutManager(this));

        quizList = new ArrayList<>();
        filteredList = new ArrayList<>();
        quizIds = new ArrayList<>();
        adapter = new QuizAdapter(filteredList, quizIds); //  Adapter uses filtered list
        rvShowQuiz.setAdapter(adapter);

        db = FirebaseFirestore.getInstance();
        loadQuizzes();

        backCreateQuiz.setOnClickListener(v -> finish());
        btnCreateQuiz.setOnClickListener(v -> startActivity(new Intent(CreateQuizActivity.this, ACreateQuizActivity.class)));
//        btnJoinQuiz.setOnClickListener(v -> startActivity(new Intent(CreateQuizActivity.this, JoinQuizActivity.class)));
        btnJoinQuiz.setOnClickListener(v -> showJoinQuizDialog());
        setupSearch();
    }

    private void showJoinQuizDialog() {
        Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.item_joinquiz);
        dialog.setCancelable(true);

        EditText etJoinQuizCode = dialog.findViewById(R.id.joinQuizText);
        AppCompatButton joinQuizDismiss = dialog.findViewById(R.id.joinQuizDismiss);
        AppCompatButton joinQuizStart = dialog.findViewById(R.id.joinQuizStart);
        ProgressBar joinProgressBar = new ProgressBar(this);
        joinProgressBar.setVisibility(View.GONE);

        // Dismiss button
        joinQuizDismiss.setOnClickListener(v -> dialog.dismiss());

        // Join button
        joinQuizStart.setOnClickListener(v -> {
            String codeInput = etJoinQuizCode.getText().toString().trim();

            if (codeInput.isEmpty()) {
                Toast.makeText(this, "Please enter quiz code", Toast.LENGTH_SHORT).show();
                return;
            }

            joinProgressBar.setVisibility(View.VISIBLE);

            FirebaseFirestore db = FirebaseFirestore.getInstance();
            db.collection("quizzes").document(codeInput).get()
                    .addOnSuccessListener(doc -> {
                        joinProgressBar.setVisibility(View.GONE);

                        if (doc.exists()) {
                            String creatorEmail = doc.getString("creatorEmail");
                            String currentUserEmail = FirebaseAuth.getInstance().getCurrentUser().getEmail();

                            // ✅ Yahan check karenge
                            if (creatorEmail != null && creatorEmail.equals(currentUserEmail)) {
                                // Agar user apna hi quiz join kar raha hai
                                new android.app.AlertDialog.Builder(this)
                                        .setTitle("Alert")
                                        .setMessage("This quiz was created by you. Instead of joining, share the quiz code with your friends so they can join!")
                                        .setPositiveButton("OK", (d, which) -> d.dismiss())
                                        .show();
                            } else {
                                // Quiz exists → start AJoinQuizActivity
                                Intent intent = new Intent(CreateQuizActivity.this, AJoinQuizActivity.class);
                                intent.putExtra("quizId", doc.getId());
                                intent.putExtra("quizTitle", doc.getString("title"));
                                startActivity(intent);
                                dialog.dismiss();
                            }
                        } else {
                            Toast.makeText(this, "Invalid quiz code! Please try again.", Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnFailureListener(e -> {
                        joinProgressBar.setVisibility(View.GONE);
                        Toast.makeText(this, "Error checking code. Try again.", Toast.LENGTH_SHORT).show();
                    });
        });

        dialog.show();
    }



    private void loadQuizzes() {
        if (!swipeRefresh.isRefreshing()) {
            progressBar.setVisibility(View.VISIBLE);
        }

        String userEmail = FirebaseAuth.getInstance().getCurrentUser().getEmail();

        db.collection("quizzes")
                .whereEqualTo("creatorEmail", userEmail)
                .get()
                .addOnCompleteListener(task -> {
                    progressBar.setVisibility(View.GONE);
                    swipeRefresh.setRefreshing(false); // Stop refresh animation

                    if (task.isSuccessful() && task.getResult() != null) {
                        quizList.clear();
                        quizIds.clear();
                        for (QueryDocumentSnapshot doc : task.getResult()) {
                            BCreateQuiz quiz = doc.toObject(BCreateQuiz.class);
                            quiz.setId(doc.getId());
                            quizList.add(quiz);
                            quizIds.add(doc.getId());
                        }
                        filteredList.clear();
                        filteredList.addAll(quizList);
                        adapter.notifyDataSetChanged();
                    }
                });
    }


    private void setupSearch() {
        etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filterQuizzes(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) { }
        });
    }

    private void filterQuizzes(String query) {
        filteredList.clear();
        if (query.isEmpty()) {
            filteredList.addAll(quizList);
        } else {
            String lowerCaseQuery = query.toLowerCase();
            for (BCreateQuiz quiz : quizList) {
                if (quiz.getTitle().toLowerCase().contains(lowerCaseQuery)) {
                    filteredList.add(quiz);
                }
            }
        }
        adapter.notifyDataSetChanged();
    }
}

