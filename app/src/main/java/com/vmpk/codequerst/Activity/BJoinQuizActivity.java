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

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.vmpk.codequerst.Adapter.JoinQuizAdapter;
import com.vmpk.codequerst.Model.BJoinQuiz;
import com.vmpk.codequerst.R;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class BJoinQuizActivity extends AppCompatActivity {

    LinearLayout homeJoinQuiz;
    private TextView tvQuestionsAsked, tvCorrect;
    private TextView creatorName, creatorRank, creatorPoints;
    private AppCompatButton btnShowSolution;
    private RecyclerView rvShowQuiz;
    private ArrayList<BJoinQuiz> questionList;

    private String creatorUid;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bjoin_quiz);

        // --- Views ---
        tvQuestionsAsked = findViewById(R.id.tvQuestionsAsked);
        tvCorrect = findViewById(R.id.tvCorrect);
        btnShowSolution = findViewById(R.id.btnShowSolution);
        rvShowQuiz = findViewById(R.id.rvShowQuiz);
        homeJoinQuiz = findViewById(R.id.homeJoinQuiz);

        creatorName = findViewById(R.id.creatorName);
        creatorRank = findViewById(R.id.creatorRank);
        creatorPoints = findViewById(R.id.creatorPoints);

        homeJoinQuiz.setOnClickListener(v -> {
            Intent intent = new Intent(BJoinQuizActivity.this, CreateQuizActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
        });

        int totalQuestions = getIntent().getIntExtra("totalQuestions", 0);
        int correctAnswers = getIntent().getIntExtra("correctAnswers", 0);
        questionList =
                (ArrayList<BJoinQuiz>) getIntent().getSerializableExtra("questionList");

        creatorUid = getIntent().getStringExtra("creatorUid");

        tvQuestionsAsked.setText("Questions Asked : " + totalQuestions);
        tvCorrect.setText("Correctly Answered : " + correctAnswers);

        btnShowSolution.setOnClickListener(v -> {
            if (questionList != null && !questionList.isEmpty()) {
                rvShowQuiz.setLayoutManager(new LinearLayoutManager(this));
                JoinQuizAdapter adapter = new JoinQuizAdapter(this, questionList);
                rvShowQuiz.setAdapter(adapter);
            } else {
                tvQuestionsAsked.setText("No questions to show!");
            }
        });

        // Status bar styling (UNCHANGED)
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

        // 🔥 FETCH CREATOR INFO (UID BASED)
        if (creatorUid != null && !creatorUid.isEmpty()) {
            fetchCreatorInfoByUid(creatorUid);
        } else {
            creatorName.setText("User");
            creatorRank.setText("-");
            creatorPoints.setText("0");
        }

    }

    private void fetchCreatorInfoByUid(String creatorUid) {

        DatabaseReference userRef =
                FirebaseDatabase.getInstance()
                        .getReference("users")
                        .child(creatorUid);

        DatabaseReference leaderboardRoot =
                FirebaseDatabase.getInstance()
                        .getReference("leaderboard");

        // 🔹 Fetch creator name
        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String name = snapshot.child("name").getValue(String.class);
                creatorName.setText("Name : " + (name != null ? name : "User"));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                creatorName.setText("User");
            }
        });

        // 🔹 Fetch points & rank
        leaderboardRoot.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                int myPoints = 0;
                List<Integer> allPoints = new ArrayList<>();

                for (DataSnapshot ds : snapshot.getChildren()) {
                    Integer p = ds.child("totalPoints").getValue(Integer.class);
                    if (p != null) {
                        allPoints.add(p);
                        if (ds.getKey().equals(creatorUid)) {
                            myPoints = p;
                        }
                    }
                }

                Collections.sort(allPoints, Collections.reverseOrder());
                int rank = allPoints.indexOf(myPoints) + 1;

                creatorPoints.setText("Points : " + myPoints);
                creatorRank.setText("Rank : " + rank);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                creatorPoints.setText("0");
                creatorRank.setText("-");
            }
        });
    }

    private boolean isDarkModeOn() {
        int nightModeFlags =
                getResources().getConfiguration().uiMode &
                        Configuration.UI_MODE_NIGHT_MASK;
        return nightModeFlags == Configuration.UI_MODE_NIGHT_YES;
    }
}
