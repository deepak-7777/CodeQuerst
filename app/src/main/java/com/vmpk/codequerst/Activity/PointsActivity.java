package com.vmpk.codequerst.Activity;

import android.content.res.Configuration;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.vmpk.codequerst.Adapter.PointsAdapter;
import com.vmpk.codequerst.Model.UserScore;
import com.vmpk.codequerst.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class PointsActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private PointsAdapter adapter;
    private List<UserScore.QuizAttemptItem> quizItemList = new ArrayList<>();
    private TextView tvEmpty, backPoints;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_points);

        recyclerView = findViewById(R.id.recyclerView);
        tvEmpty = findViewById(R.id.tvEmpty);
        backPoints = findViewById(R.id.backPoints);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new PointsAdapter(quizItemList);
        recyclerView.setAdapter(adapter);

        backPoints.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        fetchPointsFromFirebase();

        // 7️ Status bar styling
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

    private void fetchPointsFromFirebase() {
        String uid = com.google.firebase.auth.FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("leaderboard").child(uid);

        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                quizItemList.clear();
                if (snapshot.exists()) {
                    UserScore userScore = snapshot.getValue(UserScore.class);
                    if (userScore != null && userScore.getQuizzes() != null) {

                        Set<String> seenKeys = new HashSet<>();

                        for (Map.Entry<String, UserScore.QuizAttempt> entry : userScore.getQuizzes().entrySet()) {
                            String quizKey = entry.getKey(); // language_topic_difficulty
                            UserScore.QuizAttempt attempt = entry.getValue();

                            String[] parts = quizKey.split("_");
                            if (parts.length >= 3) {
                                String language = parts[0];
                                String topic = parts[1];
                                String difficulty = parts[2];

                                // check if already added
                                String keyCheck = language + "_" + topic + "_" + difficulty;
                                if (!seenKeys.contains(keyCheck)) {
                                    quizItemList.add(new UserScore.QuizAttemptItem(language, topic, difficulty, attempt.getPoints()));
                                    seenKeys.add(keyCheck);
                                }
                            }
                        }
                    }
                }

                if (quizItemList.isEmpty()) {
                    tvEmpty.setVisibility(View.VISIBLE);
                } else {
                    tvEmpty.setVisibility(View.GONE);
                    adapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                tvEmpty.setVisibility(View.VISIBLE);
            }
        });
    }
}



               ///      iska MODEL      USERSCORE MODEL mai hai