package com.vmpk.codequerst.Activity;

import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.vmpk.codequerst.Model.Question;
import com.vmpk.codequerst.Model.UserScore;
import com.vmpk.codequerst.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ResultActivity extends AppCompatActivity {

    private Button btnExploreMore;
    private TextView tvCorrectAnswers, tvTotalQuestions, tvName, tvRank;
    private ImageView imgProfile;
    private ArrayList<Question> questionList;
    private HashMap<Integer, Integer> userAnswers;
    private FirebaseAuth firebaseAuth;
    private DatabaseReference usersRef;
    private long quizStartTime;
    private long quizEndTime;
    private String currentUid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);

        // Initialize views
        tvCorrectAnswers = findViewById(R.id.tvCorrectAnswers);
        tvTotalQuestions = findViewById(R.id.tvTotalQuestions);
        imgProfile = findViewById(R.id.imgProfile);
        btnExploreMore = findViewById(R.id.btnExploreMore);
        tvName = findViewById(R.id.tvName);
        tvRank = findViewById(R.id.tvRank);

        btnExploreMore.setOnClickListener(v -> {
            startActivity(new android.content.Intent(ResultActivity.this, HomeActivity.class));
            finish();
        });

        questionList = (ArrayList<Question>) getIntent().getSerializableExtra("questionList");
        userAnswers = (HashMap<Integer, Integer>) getIntent().getSerializableExtra("userAnswers");
        quizStartTime = getIntent().getLongExtra("quizStartTime", System.currentTimeMillis());
        quizEndTime = getIntent().getLongExtra("quizEndTime", System.currentTimeMillis());

        firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser user = firebaseAuth.getCurrentUser();
        currentUid = (user != null) ? user.getUid() : null;

        if (questionList != null && userAnswers != null) {
            int correct = calculateAndDisplayResult();
            if (user != null && user.getEmail() != null) {
                saveLeaderboard(user, correct);
            }
        } else {
            setDefaultProfileImage("U");
        }

        if (user != null && user.getEmail() != null) {
            usersRef = FirebaseDatabase.getInstance().getReference("users").child(user.getUid());
            loadUserProfile();
            loadAndSetRank();
        } else {
            setDefaultProfileImage("U");
        }

        // Status bar styling
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

    private int calculateAndDisplayResult() {
        int totalQ = questionList.size();
        int correct = 0;
        for (int i = 0; i < questionList.size(); i++) {
            Question q = questionList.get(i);
            if (userAnswers.containsKey(i)) {
                int userAns = userAnswers.get(i);
                if (userAns == q.getAnswer()) correct++;
            }
        }
        tvCorrectAnswers.setText("Correct Answers " + correct);
        tvTotalQuestions.setText(" / " + totalQ);
        return correct;
    }

    private void saveLeaderboard(FirebaseUser user, int correctAnswers) {
        String uid = user.getUid();
        String name = (user.getDisplayName() != null) ? user.getDisplayName() : "User";
        String profileImage = (user.getPhotoUrl() != null) ? user.getPhotoUrl().toString() : "";
        long totalTime = quizEndTime - quizStartTime;
        long timestamp = System.currentTimeMillis();

        String language = getIntent().getStringExtra("language");
        String topic = getIntent().getStringExtra("topic");
        String difficulty = getIntent().getStringExtra("difficulty");

        if (language == null || topic == null || difficulty == null) {
            Toast.makeText(this, "Cannot save score: missing quiz info", Toast.LENGTH_SHORT).show();
            return;
        }

        String topicKey = language + "_" + topic + "_" + difficulty;
        DatabaseReference leaderboardRef = FirebaseDatabase.getInstance().getReference("leaderboard").child(uid);

        leaderboardRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                UserScore userScore;
                if (snapshot.exists()) {
                    userScore = snapshot.getValue(UserScore.class);
                    if (userScore == null) userScore = new UserScore();
                } else {
                    userScore = new UserScore();
                }

                userScore.setName(name);
                userScore.setProfileImage(profileImage);
                userScore.setTimestamp(timestamp);
                userScore.setTotalTime(userScore.getTotalTime() + totalTime);

                // Topics update
                if (userScore.getTopics() == null) userScore.setTopics(new HashMap<>());
                Map<String, Integer> topicsMap = userScore.getTopics();
                int pointsToAdd = topicsMap.containsKey(topicKey) ? 0 : correctAnswers;
                topicsMap.put(topicKey, correctAnswers);
                userScore.setTopics(topicsMap);

                // Total points (all-time)
                userScore.setTotalPoints(userScore.getTotalPoints() + pointsToAdd);

                // Quizzes map for weekly
                if (userScore.getQuizzes() == null) userScore.setQuizzes(new HashMap<>());
                Map<String, UserScore.QuizAttempt> quizMap = userScore.getQuizzes();
                String quizId = topicKey + "_" + timestamp;
                quizMap.put(quizId, new UserScore.QuizAttempt(correctAnswers, timestamp));
                userScore.setQuizzes(quizMap);

                // Weekly points calculation (last 7 days)
                long oneWeekAgo = System.currentTimeMillis() - 7L * 24 * 60 * 60 * 1000;
                int weeklySum = 0;
                for (UserScore.QuizAttempt attempt : quizMap.values()) {
                    if (attempt.getTimestamp() >= oneWeekAgo) {
                        weeklySum += attempt.getPoints();
                    }
                }
                userScore.setWeeklyPoints(weeklySum);

                leaderboardRef.setValue(userScore).addOnCompleteListener(task -> {
                    if (!task.isSuccessful()) {
                        Toast.makeText(ResultActivity.this, "Leaderboard update failed", Toast.LENGTH_SHORT).show();
                    } else {
                        loadAndSetRank(); // existing code
                        // Launch PointsActivity after saving leaderboard
//                        Intent intent = new Intent(ResultActivity.this, PointsActivity.class);
//                        intent.putExtra("uid", currentUid); // Pass UID to fetch user-specific points
//                        startActivity(intent);
                    }
                });

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(ResultActivity.this, "Leaderboard update failed", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadAndSetRank() {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("leaderboard");
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                List<UserScore> allUsers = new ArrayList<>();
                for (DataSnapshot child : snapshot.getChildren()) {
                    UserScore u = child.getValue(UserScore.class);
                    if (u != null) {
                        u.setUid(child.getKey());
                        allUsers.add(u);
                    }
                }

                // Sort by totalPoints
                Collections.sort(allUsers, (u1, u2) -> {
                    if (u2.getTotalPoints() != u1.getTotalPoints()) {
                        return u2.getTotalPoints() - u1.getTotalPoints();
                    } else if (u1.getTotalTime() != u2.getTotalTime()) {
                        return Long.compare(u1.getTotalTime(), u2.getTotalTime());
                    } else {
                        return Long.compare(u1.getTimestamp(), u2.getTimestamp());
                    }
                });

                if (currentUid != null) {
                    for (int i = 0; i < allUsers.size(); i++) {
                        if (allUsers.get(i).getUid().equals(currentUid)) {
                            int rank = i + 1;
                            tvRank.setText("Rank " + rank);
                            return;
                        }
                    }
                }
                tvRank.setText("Rank -");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                tvRank.setText("Rank -");
            }
        });
    }

    private void loadUserProfile() {
        usersRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String name = "";
                String imageUrl = "";
                if (snapshot.exists()) {
                    name = snapshot.child("name").getValue(String.class);
                    imageUrl = snapshot.child("profileImage").getValue(String.class);
                }

                tvName.setText((name != null && !name.isEmpty()) ? name : "User");

                if (imageUrl != null && !imageUrl.isEmpty()) {
                    Glide.with(ResultActivity.this)
                            .load(imageUrl)
                            .circleCrop()
                            .into(imgProfile);
                } else {
                    setDefaultProfileImage(name);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                tvName.setText("User");
                setDefaultProfileImage("U");
            }
        });
    }

    private void setDefaultProfileImage(String fullName) {
        if (fullName == null || fullName.isEmpty()) fullName = "U";
        String[] words = fullName.trim().split(" ");
        String initials = "";
        for (String w : words) {
            if (!w.isEmpty() && initials.length() < 2) initials += w.charAt(0);
        }
        initials = initials.toUpperCase();

        int size = 120;
        Bitmap bitmap = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);

        Paint paint = new Paint();
        paint.setAntiAlias(true);
        paint.setColor(0xFF6200EE);
        canvas.drawCircle(size / 2f, size / 2f, size / 2f, paint);

        paint.setColor(Color.WHITE);
        paint.setTextSize(48f);
        paint.setTextAlign(Paint.Align.CENTER);
        Rect bounds = new Rect();
        paint.getTextBounds(initials, 0, initials.length(), bounds);
        float x = size / 2f;
        float y = size / 2f - (bounds.top + bounds.bottom) / 2f;
        canvas.drawText(initials, x, y, paint);

        imgProfile.setImageBitmap(bitmap);
    }

    private boolean isDarkModeOn() {
        int nightModeFlags = getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK;
        return nightModeFlags == Configuration.UI_MODE_NIGHT_YES;
    }
}
