package com.vmpk.codequerst.Activity;

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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.*;
import com.vmpk.codequerst.Model.Question;
import com.vmpk.codequerst.Model.UserScore;
import com.vmpk.codequerst.R;

import java.util.*;

public class ResultActivity extends AppCompatActivity {

    private Button btnExploreMore;
    private TextView tvCorrectAnswers, tvTotalQuestions, tvName, tvRank;
    private ImageView imgProfile;

    private ArrayList<Question> questionList;
    private HashMap<Integer, Integer> userAnswers;

    private FirebaseAuth firebaseAuth;
    private DatabaseReference usersRef;
    private String currentUid;

    private long quizStartTime, quizEndTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);

        tvCorrectAnswers = findViewById(R.id.tvCorrectAnswers);
        tvTotalQuestions = findViewById(R.id.tvTotalQuestions);
        tvName = findViewById(R.id.tvName);
        tvRank = findViewById(R.id.tvRank);
        imgProfile = findViewById(R.id.imgProfile);
        btnExploreMore = findViewById(R.id.btnExploreMore);

        btnExploreMore.setOnClickListener(v -> {
            startActivity(new android.content.Intent(this, HomeActivity.class));
            finish();
        });

        questionList = (ArrayList<Question>) getIntent().getSerializableExtra("questionList");
        userAnswers = (HashMap<Integer, Integer>) getIntent().getSerializableExtra("userAnswers");
        quizStartTime = getIntent().getLongExtra("quizStartTime", System.currentTimeMillis());
        quizEndTime = getIntent().getLongExtra("quizEndTime", System.currentTimeMillis());

        firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser user = firebaseAuth.getCurrentUser();
        currentUid = user != null ? user.getUid() : null;

        if (questionList != null && userAnswers != null) {
            int correct = calculateAndDisplayResult();
            if (user != null) saveLeaderboard(user, correct);
        }

        if (user != null) {
            usersRef = FirebaseDatabase.getInstance()
                    .getReference("users")
                    .child(user.getUid());
            loadUserProfile();
            loadAndSetRank();
        } else {
            setDefaultProfileImage("U");
        }

        setupStatusBar();
    }

    // ===================================================
    // 🔹 Result Calculation
    // ===================================================
    private int calculateAndDisplayResult() {
        int correct = 0;
        for (int i = 0; i < questionList.size(); i++) {
            if (userAnswers.containsKey(i) &&
                    userAnswers.get(i) == questionList.get(i).getAnswer()) {
                correct++;
            }
        }
        tvCorrectAnswers.setText("Correct Answers " + correct);
        tvTotalQuestions.setText(" / " + questionList.size());
        return correct;
    }

    // ===================================================
    // 🔹 Save Leaderboard (Avatar SAFE)
    // ===================================================
    private void saveLeaderboard(FirebaseUser user, int correctAnswers) {

        String uid = user.getUid();
        long timestamp = System.currentTimeMillis();
        long totalTime = quizEndTime - quizStartTime;

        String language = getIntent().getStringExtra("language");
        String topic = getIntent().getStringExtra("topic");
        String difficulty = getIntent().getStringExtra("difficulty");

        if (language == null || topic == null || difficulty == null) return;

        String topicKey = language + "_" + topic + "_" + difficulty;

        DatabaseReference leaderboardRef =
                FirebaseDatabase.getInstance().getReference("leaderboard").child(uid);

        leaderboardRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snap) {

                UserScore score = snap.exists()
                        ? snap.getValue(UserScore.class)
                        : new UserScore();

                if (score == null) score = new UserScore();

                score.setUid(uid);
                score.setTimestamp(timestamp);
                score.setTotalTime(score.getTotalTime() + totalTime);

                if (score.getTopics() == null)
                    score.setTopics(new HashMap<>());

                if (!score.getTopics().containsKey(topicKey)) {
                    score.getTopics().put(topicKey, correctAnswers);
                    score.setTotalPoints(score.getTotalPoints() + correctAnswers);
                }

                if (score.getQuizzes() == null)
                    score.setQuizzes(new HashMap<>());

                score.getQuizzes().put(
                        topicKey + "_" + timestamp,
                        new UserScore.QuizAttempt(correctAnswers, timestamp)
                );

                // 🔥 Profile image ALWAYS from users node
                UserScore finalScore = score;
                FirebaseDatabase.getInstance()
                        .getReference("users")
                        .child(uid)
                        .addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot u) {
                                finalScore.setName(u.child("name").getValue(String.class));
                                finalScore.setProfileImage(
                                        u.child("profileImage").getValue(String.class));

                                leaderboardRef.setValue(finalScore);
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {}
                        });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });
    }

    // ===================================================
    // 🔹 Load User Profile (Avatar + URL)
    // ===================================================
    private void loadUserProfile() {

        usersRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snap) {

                String name = snap.child("name").getValue(String.class);
                String image = snap.child("profileImage").getValue(String.class);

                tvName.setText(name != null && !name.isEmpty() ? name : "User");

                if (image != null && !image.isEmpty()) {

                    if (image.startsWith("http")) {
                        Glide.with(ResultActivity.this)
                                .load(image)
                                .circleCrop()
                                .into(imgProfile);
                    }
                    else if (image.startsWith("avatar_")) {
                        int resId = getResources()
                                .getIdentifier(image, "drawable", getPackageName());
                        if (resId != 0) imgProfile.setImageResource(resId);
                        else setDefaultProfileImage(name);
                    }
                    else {
                        setDefaultProfileImage(name);
                    }

                } else {
                    setDefaultProfileImage(name);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                setDefaultProfileImage("U");
            }
        });
    }

    // ===================================================
    // 🔹 Rank
    // ===================================================
    private void loadAndSetRank() {
        FirebaseDatabase.getInstance()
                .getReference("leaderboard")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snap) {
                        List<UserScore> list = new ArrayList<>();
                        for (DataSnapshot c : snap.getChildren()) {
                            UserScore u = c.getValue(UserScore.class);
                            if (u != null) {
                                u.setUid(c.getKey());
                                list.add(u);
                            }
                        }

                        list.sort((a, b) -> b.getTotalPoints() - a.getTotalPoints());

                        for (int i = 0; i < list.size(); i++) {
                            if (list.get(i).getUid().equals(currentUid)) {
                                tvRank.setText("Rank " + (i + 1));
                                return;
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

    // ===================================================
    // 🔹 Default Avatar
    // ===================================================
    private void setDefaultProfileImage(String name) {
        if (name == null || name.isEmpty()) name = "U";
        String i = name.substring(0, 1).toUpperCase();

        Bitmap b = Bitmap.createBitmap(120, 120, Bitmap.Config.ARGB_8888);
        Canvas c = new Canvas(b);
        Paint p = new Paint(Paint.ANTI_ALIAS_FLAG);

        p.setColor(0xFF6200EE);
        c.drawCircle(60, 60, 60, p);

        p.setColor(Color.WHITE);
        p.setTextSize(42);
        p.setTextAlign(Paint.Align.CENTER);

        Rect r = new Rect();
        p.getTextBounds(i, 0, 1, r);
        c.drawText(i, 60, 60 - r.centerY(), p);

        imgProfile.setImageBitmap(b);
    }

    // ===================================================
    // 🔹 Status Bar
    // ===================================================
    private void setupStatusBar() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window w = getWindow();
            w.getDecorView().setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
            w.setStatusBarColor(Color.TRANSPARENT);
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            View d = getWindow().getDecorView();
            int f = d.getSystemUiVisibility();
            if (isDarkModeOn())
                d.setSystemUiVisibility(f & ~View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
            else
                d.setSystemUiVisibility(f | View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        }
    }

    private boolean isDarkModeOn() {
        return (getResources().getConfiguration().uiMode
                & Configuration.UI_MODE_NIGHT_MASK)
                == Configuration.UI_MODE_NIGHT_YES;
    }
}
