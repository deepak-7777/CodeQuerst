package com.vmpk.codequerst.Activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.splashscreen.SplashScreen;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.*;

import java.util.concurrent.atomic.AtomicBoolean;

public class SplashActivity extends AppCompatActivity {

    private FirebaseAuth auth;
    private SharedPreferences sharedPreferences;
    private Handler handler = new Handler(Looper.getMainLooper());
    private final long MAX_WAIT_TIME = 3000; // max 3 sec
    private AtomicBoolean proceed = new AtomicBoolean(false);
    private Runnable timeoutRunnable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // ✅ Install SplashScreen before super.onCreate
        SplashScreen splashScreen = SplashScreen.installSplashScreen(this);

        super.onCreate(savedInstanceState);

        auth = FirebaseAuth.getInstance();
        sharedPreferences = getSharedPreferences("user_prefs", MODE_PRIVATE);

        FirebaseUser user = auth.getCurrentUser();
        boolean isGuest = sharedPreferences.getBoolean("isGuest", false);

        if (user != null && !user.isAnonymous()) {
            // Logged in user - wait for Firebase data
            DatabaseReference userRef = FirebaseDatabase.getInstance()
                    .getReference("users")
                    .child(user.getUid());

            timeoutRunnable = () -> {
                if (proceed.compareAndSet(false, true)) {
                    openHome();
                }
            };
            handler.postDelayed(timeoutRunnable, MAX_WAIT_TIME);

            userRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot snapshot) {
                    if (proceed.compareAndSet(false, true)) {
                        String name = snapshot.child("name").getValue(String.class);
                        String imageUrl = snapshot.child("profileImage").getValue(String.class);
                        int points = snapshot.child("points").getValue(Integer.class) != null ?
                                snapshot.child("points").getValue(Integer.class) : 0;

                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        if (name != null) editor.putString("offline_name", name);
                        if (imageUrl != null) editor.putString("offline_image", imageUrl);
                        editor.putInt("offline_points", points);
                        editor.apply();

                        handler.removeCallbacks(timeoutRunnable);
                        openHome();
                    }
                }

                @Override
                public void onCancelled(DatabaseError error) {
                    if (proceed.compareAndSet(false, true)) {
                        handler.removeCallbacks(timeoutRunnable);
                        openHome();
                    }
                }
            });

        } else {
            // Guest or first-time user
            handler.postDelayed(() -> {
                if (proceed.compareAndSet(false, true)) {
                    if (auth.getCurrentUser() != null || isGuest) {
                        openHome();
                    } else {
                        startActivity(new Intent(SplashActivity.this, LoginActivity.class));
                        finish();
                    }
                }
            }, 2000);
        }
    }

    private void openHome() {
        startActivity(new Intent(SplashActivity.this, HomeActivity.class));
        finish();
    }
}
