package com.example.codequerst.Activity;

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

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.codequerst.Model.Question;
import com.example.codequerst.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;

public class ResultActivity extends AppCompatActivity {

    private Button btnExploreMore;
    private TextView tvCorrectAnswers, tvTotalQuestions, tvName;
    private ImageView imgProfile;

    private ArrayList<Question> questionList;
    private HashMap<Integer, Integer> userAnswers;

    private FirebaseAuth firebaseAuth;
    private DatabaseReference usersRef;

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

        btnExploreMore.setOnClickListener(v -> {
            startActivity(new android.content.Intent(ResultActivity.this, HomeActivity.class));
            finish();
        });

        // Get quiz data from QuizActivity
        questionList = (ArrayList<Question>) getIntent().getSerializableExtra("questionList");
        userAnswers = (HashMap<Integer, Integer>) getIntent().getSerializableExtra("userAnswers");

        if (questionList != null && userAnswers != null) {
            calculateAndDisplayResult();
        }

        // Load profile info
        firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser user = firebaseAuth.getCurrentUser();
        if (user != null) {
            String uid = user.getUid();
            usersRef = FirebaseDatabase.getInstance().getReference("users").child(uid);
            loadUserProfile();
        } else {
            setDefaultProfileImage("U"); // fallback
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

    private void calculateAndDisplayResult() {
        int totalQ = questionList.size();
        int correct = 0;

        for (int i = 0; i < questionList.size(); i++) {
            Question q = questionList.get(i);
            if (userAnswers.containsKey(i)) {
                int userAns = userAnswers.get(i);
                if (userAns == q.getAnswer()) { // use getAnswer() instead of getCorrectAns()
                    correct++;
                }
            }
        }

        tvCorrectAnswers.setText("Correct Answer " + correct);
        tvTotalQuestions.setText(" / " + totalQ);
    }


    private void loadUserProfile() {
        usersRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                String name = "";
                String imageUrl = "";

                if (snapshot.exists()) {
                    name = snapshot.child("name").getValue(String.class);
                    imageUrl = snapshot.child("profileImage").getValue(String.class);
                }

                if (name != null && !name.isEmpty()) {
                    tvName.setText(name);
                } else {
                    tvName.setText("User");
                }

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
            public void onCancelled(DatabaseError error) {
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
        paint.setColor(0xFF6200EE); // background color
        canvas.drawCircle(size / 2f, size / 2f, size / 2f, paint);

        paint.setColor(Color.WHITE); // text color
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
        int nightModeFlags =
                getResources().getConfiguration().uiMode &
                        Configuration.UI_MODE_NIGHT_MASK;
        return nightModeFlags == Configuration.UI_MODE_NIGHT_YES;
    }
}
