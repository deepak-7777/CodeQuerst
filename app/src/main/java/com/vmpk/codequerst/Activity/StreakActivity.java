package com.vmpk.codequerst.Activity;

import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.vmpk.codequerst.R;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class StreakActivity extends AppCompatActivity {
    Button btnContinue;
    TextView backStreak;
    LinearLayout loadingProfile, cardLayout;
    TextView streakNumber, dayStreak;
    TextView day1, day2, day3, day4, day5, day6, day7;
    DatabaseReference userRef;
    String todayDayKey;
    int correctCount = 0, totalCount = 0;
    int totalDaysCompleted = 0;
    int currentStreak = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_streak);

        streakNumber = findViewById(R.id.streak_number);
        dayStreak = findViewById(R.id.dayStreak);
        day1 = findViewById(R.id.day1);
        day2 = findViewById(R.id.day2);
        day3 = findViewById(R.id.day3);
        day4 = findViewById(R.id.day4);
        day5 = findViewById(R.id.day5);
        day6 = findViewById(R.id.day6);
        day7 = findViewById(R.id.day7);
        btnContinue = findViewById(R.id.btn_continue);
        backStreak = findViewById(R.id.backStreak);
        loadingProfile = findViewById(R.id.loadingProfile);
        cardLayout = findViewById(R.id.cardLayout);


        // 🔄 Show loader, hide content initially
        loadingProfile.setVisibility(View.VISIBLE);
        cardLayout.setVisibility(View.GONE);

        // Get intent extras
        todayDayKey = getIntent().getStringExtra("day");
        correctCount = getIntent().getIntExtra("correctCount", 0);
        totalCount = getIntent().getIntExtra("totalCount", 0);

        // ✅ Use user email instead of "user1"
        String userKey = getCurrentUserKey();
        userRef = FirebaseDatabase.getInstance()
                .getReference("user_progress")
                .child(userKey);

        // Load streak data from Firebase
        loadUserStreakData();

        btnContinue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(StreakActivity.this, StreakQuizActivity.class));
            }
        });

        backStreak.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });


        // 🩵 Status bar styling
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

    private void loadUserStreakData() {
        // 🔄 Ensure loader is visible
        loadingProfile.setVisibility(View.VISIBLE);
        cardLayout.setVisibility(View.GONE);

        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                List<Integer> completedDays = new ArrayList<>();

                for (DataSnapshot daySnap : snapshot.getChildren()) {
                    Boolean completed = daySnap.getValue(Boolean.class);
                    if (completed != null && completed) {
                        String key = daySnap.getKey();
                        int dayNum = Integer.parseInt(key.replace("day", ""));
                        completedDays.add(dayNum);
                    }
                }

                if (completedDays.isEmpty()) {
                    totalDaysCompleted = 0;
                    currentStreak = 0;
                } else {
                    Collections.sort(completedDays);
                    totalDaysCompleted = completedDays.size();

                    // Calculate consecutive day streak
                    currentStreak = 1;
                    for (int i = completedDays.size() - 2; i >= 0; i--) {
                        int last = completedDays.get(i + 1);
                        int prev = completedDays.get(i);
                        if (last - prev == 1) currentStreak++;
                        else break;
                    }
                }

                // Update UI
                streakNumber.setText(String.valueOf(totalDaysCompleted));
                dayStreak.setText(currentStreak + " day streak!");
                updateDayCircles(completedDays);

                // ✅ HIDE loader, SHOW content
                loadingProfile.setVisibility(View.GONE);
                cardLayout.setVisibility(View.VISIBLE);
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // ❌ Error case – stop loader
                loadingProfile.setVisibility(View.GONE);
                cardLayout.setVisibility(View.VISIBLE);
            }
        });
    }

    private void updateDayCircles(List<Integer> completedDays) {
        TextView[] days = {day1, day2, day3, day4, day5, day6, day7};
        int maxDay = 7;

        for (int i = 0; i < maxDay; i++) {
            int dayNumber = i + 1;

            if (completedDays.contains(dayNumber)) {
                days[i].setBackgroundColor(Color.parseColor("#4CAF50")); // Green = completed
                days[i].setTextColor(Color.WHITE);
            } else if (dayNumber <= getTodayDayNumber()) {
                days[i].setBackgroundColor(Color.parseColor("#F44336")); // Red = missed
                days[i].setTextColor(Color.WHITE);
            } else {
                days[i].setBackgroundColor(Color.LTGRAY); // Grey = upcoming
                days[i].setTextColor(Color.BLACK);
            }
        }
    }

    private int getTodayDayNumber() {
        try {
            return Integer.parseInt(todayDayKey.replace("day", ""));
        } catch (Exception e) {
            return 1;
        }
    }

    // ✅ Converts current Firebase user's email into a safe Firebase key
    private String getCurrentUserKey() {
        String email = null;

        try {
            email = FirebaseAuth.getInstance().getCurrentUser().getEmail();
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (email == null || email.isEmpty()) {
            return "guest_user";
        }

        // Firebase key-safe version of email
        return email.replace(".", "_")
                .replace("#", "_")
                .replace("$", "_")
                .replace("[", "_")
                .replace("]", "_");
    }
}
