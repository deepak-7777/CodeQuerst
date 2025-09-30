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
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.vmpk.codequerst.Adapter.JoinQuizAdapter;
import com.vmpk.codequerst.Model.BJoinQuiz;
import com.vmpk.codequerst.R;

import java.util.ArrayList;

public class BJoinQuizActivity extends AppCompatActivity {
    LinearLayout homeJoinQuiz;
    private TextView tvQuestionsAsked, tvCorrect;
    private AppCompatButton btnShowSolution;
    private RecyclerView rvShowQuiz;
    private ArrayList<BJoinQuiz> questionList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bjoin_quiz);

        tvQuestionsAsked = findViewById(R.id.tvQuestionsAsked);
        tvCorrect = findViewById(R.id.tvCorrect);
        btnShowSolution = findViewById(R.id.btnShowSolution);
        rvShowQuiz = findViewById(R.id.rvShowQuiz);
        homeJoinQuiz = findViewById(R.id.homeJoinQuiz);

        homeJoinQuiz.setOnClickListener(v -> {
            Intent intent = new Intent(BJoinQuizActivity.this, CreateQuizActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
        });

        int totalQuestions = getIntent().getIntExtra("totalQuestions", 0);
        int correctAnswers = getIntent().getIntExtra("correctAnswers", 0);

        // Get passed question list
        questionList = (ArrayList<BJoinQuiz>) getIntent().getSerializableExtra("questionList");

        tvQuestionsAsked.setText("Questions Asked : " + totalQuestions);
        tvCorrect.setText("Correctly Answered : " + correctAnswers);

        // On button click -> show quiz solution
        btnShowSolution.setOnClickListener(v -> {
            if (questionList != null && !questionList.isEmpty()) {
                rvShowQuiz.setLayoutManager(new LinearLayoutManager(this));
                JoinQuizAdapter adapter = new JoinQuizAdapter(this, questionList);
                rvShowQuiz.setAdapter(adapter);
            } else {
                tvQuestionsAsked.setText("No questions to show!");
            }
        });

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


//package com.vmpk.codequerst.Activity;
//
//import android.content.Intent;
//import android.content.res.Configuration;
//import android.graphics.Color;
//import android.os.Build;
//import android.os.Bundle;
//import android.view.View;
//import android.view.Window;
//import android.widget.LinearLayout;
//import android.widget.TextView;
//
//import androidx.annotation.NonNull;
//import androidx.appcompat.app.AppCompatActivity;
//import androidx.appcompat.widget.AppCompatButton;
//import androidx.recyclerview.widget.LinearLayoutManager;
//import androidx.recyclerview.widget.RecyclerView;
//
//import com.vmpk.codequerst.Adapter.JoinQuizAdapter;
//import com.vmpk.codequerst.Model.BJoinQuiz;
//import com.vmpk.codequerst.R;
//import com.google.firebase.database.*;
//
//import java.util.ArrayList;
//import java.util.Collections;
//import java.util.List;
//
//public class BJoinQuizActivity extends AppCompatActivity {
//    LinearLayout homeJoinQuiz;
//    private TextView tvQuestionsAsked, tvCorrect;
//    private TextView creatorName, creatorRank, creatorPoints;
//    private AppCompatButton btnShowSolution;
//    private RecyclerView rvShowQuiz;
//    private ArrayList<BJoinQuiz> questionList;
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_bjoin_quiz);
//
//        // --- Views ---
//        tvQuestionsAsked = findViewById(R.id.tvQuestionsAsked);
//        tvCorrect = findViewById(R.id.tvCorrect);
//        btnShowSolution = findViewById(R.id.btnShowSolution);
//        rvShowQuiz = findViewById(R.id.rvShowQuiz);
//        homeJoinQuiz = findViewById(R.id.homeJoinQuiz);
//
//        creatorName = findViewById(R.id.creatorName);
//        creatorRank = findViewById(R.id.creatorRank);
//        creatorPoints = findViewById(R.id.creatorPoints);
//
//        homeJoinQuiz.setOnClickListener(v -> {
//            Intent intent = new Intent(BJoinQuizActivity.this, CreateQuizActivity.class);
//            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
//            startActivity(intent);
//            finish();
//        });
//
//        int totalQuestions = getIntent().getIntExtra("totalQuestions", 0);
//        int correctAnswers = getIntent().getIntExtra("correctAnswers", 0);
//        questionList = (ArrayList<BJoinQuiz>) getIntent().getSerializableExtra("questionList");
//        String creatorUid = getIntent().getStringExtra("creatorUid"); // Creator UID from intent
//
//        tvQuestionsAsked.setText("Questions Asked : " + totalQuestions);
//        tvCorrect.setText("Correctly Answered : " + correctAnswers);
//
//        btnShowSolution.setOnClickListener(v -> {
//            if (questionList != null && !questionList.isEmpty()) {
//                rvShowQuiz.setLayoutManager(new LinearLayoutManager(this));
//                JoinQuizAdapter adapter = new JoinQuizAdapter(this, questionList);
//                rvShowQuiz.setAdapter(adapter);
//            } else {
//                tvQuestionsAsked.setText("No questions to show!");
//            }
//        });
//
//        // Status bar styling
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//            Window window = getWindow();
//            window.getDecorView().setSystemUiVisibility(
//                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
//            window.setStatusBarColor(Color.TRANSPARENT);
//        }
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//            View decorView = getWindow().getDecorView();
//            int flags = decorView.getSystemUiVisibility();
//            if (isDarkModeOn()) {
//                decorView.setSystemUiVisibility(flags & ~View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
//            } else {
//                decorView.setSystemUiVisibility(flags | View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
//            }
//        }
//
//        // --- Fetch creator info from Firebase ---
//        if (creatorUid != null) {
//            fetchCreatorInfo(creatorUid);
//        }
//    }
//
//    private void fetchCreatorInfo(String creatorUid) {
//        DatabaseReference usersRef = FirebaseDatabase.getInstance().getReference("users");
//        DatabaseReference leaderboardRef = FirebaseDatabase.getInstance().getReference("leaderboard");
//
//        usersRef.child(creatorUid).addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot snapshot) {
//                if(snapshot.exists()){
//                    String name = snapshot.child("name").getValue(String.class);
//                    creatorName.setText("Name : " + (name != null ? name : "User"));
//
//                    leaderboardRef.child(creatorUid).addListenerForSingleValueEvent(new ValueEventListener() {
//                        @Override
//                        public void onDataChange(@NonNull DataSnapshot leaderSnap) {
//                            if(leaderSnap.exists()){
//                                Integer totalPoints = leaderSnap.child("totalPoints").getValue(Integer.class);
//                                int points = totalPoints != null ? totalPoints : 0;
//                                creatorPoints.setText("Points : " + points);
//
//                                // Calculate rank
//                                leaderboardRef.addListenerForSingleValueEvent(new ValueEventListener() {
//                                    @Override
//                                    public void onDataChange(@NonNull DataSnapshot allSnap) {
//                                        List<Integer> pointsList = new ArrayList<>();
//                                        for(DataSnapshot ds : allSnap.getChildren()){
//                                            Integer p = ds.child("totalPoints").getValue(Integer.class);
//                                            if(p != null) pointsList.add(p);
//                                        }
//                                        Collections.sort(pointsList, Collections.reverseOrder());
//                                        int rank = pointsList.indexOf(points) + 1;
//                                        creatorRank.setText("Rank : " + rank);
//                                    }
//                                    @Override public void onCancelled(@NonNull DatabaseError error) {}
//                                });
//                            }
//                        }
//                        @Override public void onCancelled(@NonNull DatabaseError error) {}
//                    });
//                }
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError error) {}
//        });
//    }
//
//    private boolean isDarkModeOn() {
//        int nightModeFlags =
//                getResources().getConfiguration().uiMode &
//                        Configuration.UI_MODE_NIGHT_MASK;
//        return nightModeFlags == Configuration.UI_MODE_NIGHT_YES;
//    }
//}
