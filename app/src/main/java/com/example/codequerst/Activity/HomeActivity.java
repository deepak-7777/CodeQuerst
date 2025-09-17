package com.example.codequerst.Activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.example.codequerst.Fragment.RankingFragment;
import com.example.codequerst.Fragment.SettingFragment;
import com.example.codequerst.R;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.*;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import com.ismaeldivita.chipnavigation.ChipNavigationBar;

import java.util.HashMap;
import java.util.Map;

public class HomeActivity extends AppCompatActivity {

    LinearLayout c, cpp, python, java, kotlin, javaScript;
    ChipNavigationBar chipNavigationBar;
    ScrollView homeScrollView;
    ShapeableImageView profileImage;
    TextView userName;
    SharedPreferences sharedPreferences;
    FirebaseAuth firebaseAuth;
    DatabaseReference usersRef;
    private SharedPreferences.OnSharedPreferenceChangeListener prefListener;

    private SwipeRefreshLayout swipeRefresh;
    private BroadcastReceiver networkReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_home);

        c = findViewById(R.id.c);
        cpp= findViewById(R.id.cpp);
        python = findViewById(R.id.python);
        java = findViewById(R.id.java);
        kotlin = findViewById(R.id.kotlin);
        javaScript= findViewById(R.id.javaScript);
        homeScrollView = findViewById(R.id.homeScrollView);
        chipNavigationBar = findViewById(R.id.buttonNavigation);
//        MainBtn = findViewById(R.id.MainBtn);
        profileImage = findViewById(R.id.imageView2);
        userName = findViewById(R.id.textView5);
        swipeRefresh = findViewById(R.id.swipeRefresh);

        sharedPreferences = getSharedPreferences("user_prefs", MODE_PRIVATE);
        firebaseAuth = FirebaseAuth.getInstance();

        loadUserProfile();
        setupNetworkReceiver();

//        MainBtn.setOnClickListener(v -> startActivity(new Intent(HomeActivity.this, MainActivity.class)));

        bottomNavigation();

        profileImage.setOnClickListener(v -> startActivity(new Intent(HomeActivity.this, ProfileActivity.class)));

        swipeRefresh.setOnRefreshListener(() -> {
            loadUserProfile();
            swipeRefresh.setRefreshing(false);
        });

        c.setOnClickListener(v -> startActivity(new Intent(HomeActivity.this, CActivity.class)));
        cpp.setOnClickListener(v -> startActivity(new Intent(HomeActivity.this, CppActivity.class)));
        python.setOnClickListener(v -> startActivity(new Intent(HomeActivity.this, PythonActivity.class)));
        java.setOnClickListener(v -> startActivity(new Intent(HomeActivity.this, JavaActivity.class)));
        kotlin.setOnClickListener(v -> startActivity(new Intent(HomeActivity.this, KotlinActivity.class)));
        javaScript.setOnClickListener(v -> startActivity(new Intent(HomeActivity.this, JavaScriptActivity.class)));

    }

    private void setupNetworkReceiver() {
        networkReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (isNetworkAvailable()) {
                    syncPendingUpdates();
                }
            }
        };
        IntentFilter filter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        registerReceiver(networkReceiver, filter);
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo network = cm.getActiveNetworkInfo();
        return network != null && network.isConnected();
    }

    private void syncPendingUpdates() {
        boolean isGuest = sharedPreferences.getBoolean("isGuest", false);
        if (!isGuest && usersRef != null) {
            Map<String, Object> pendingUpdates = new HashMap<>();
            if (sharedPreferences.contains("offline_name")) {
                pendingUpdates.put("name", sharedPreferences.getString("offline_name", ""));
            }
            if (!pendingUpdates.isEmpty()) {
                usersRef.updateChildren(pendingUpdates).addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.remove("offline_name");
                        editor.apply();
                        // Reload profile after syncing
                        loadUserProfile();
                    }
                });
            }
        }
    }

    private void loadUserProfile() {
        boolean isGuest = sharedPreferences.getBoolean("isGuest", false);
        TextView welcomeText = findViewById(R.id.textView4);

        if (isGuest) {
            String name = sharedPreferences.getString("name", "Guest User");
            userName.setText(name);
            setDefaultProfileImage(name);

            // Guest hamesha Welcome
            welcomeText.setText("Welcome");

            prefListener = (prefs, key) -> {
                if (key.equals("name")) {
                    String newName = prefs.getString("name", "Guest User");
                    userName.setText(newName);
                    setDefaultProfileImage(newName);
                    welcomeText.setText("Welcome");
                }
            };
            sharedPreferences.registerOnSharedPreferenceChangeListener(prefListener);

        } else {
            FirebaseUser currentUser = firebaseAuth.getCurrentUser();
            if (currentUser != null) {
                String uid = currentUser.getUid();
                usersRef = FirebaseDatabase.getInstance().getReference("users").child(uid);

                usersRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot snapshot) {
                        String name = "User";
                        if (snapshot.exists()) {
                            name = snapshot.child("name").getValue(String.class);
                            String imageUrl = snapshot.child("profileImage").getValue(String.class);

                            if (name != null) {
                                if (sharedPreferences.contains("offline_name")) {
                                    name = sharedPreferences.getString("offline_name", name);
                                }
                                userName.setText(name);
                                setDefaultProfileImage(name);
                            }

                            if (imageUrl != null && !imageUrl.isEmpty()) {
                                Glide.with(HomeActivity.this)
                                        .load(imageUrl)
                                        .circleCrop()
                                        .into(profileImage);
                            } else {
                                setDefaultProfileImage(name);
                            }
                        }

                        // Google login welcome logic
                        boolean isFirstTime = sharedPreferences.getBoolean("google_first_time", true);
                        if (isFirstTime) {
                            welcomeText.setText("Welcome");
                            SharedPreferences.Editor editor = sharedPreferences.edit();
                            editor.putBoolean("google_first_time", false);
                            editor.apply();
                        } else {
                            welcomeText.setText("Welcome Back");
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError error) { }
                });
            }
        }
    }



    private void setDefaultProfileImage(String fullName) {
        if (fullName == null || fullName.isEmpty()) fullName = "U";
        String[] words = fullName.trim().split(" ");
        String initials = "";
        for (String w : words) {
            if (!w.isEmpty() && initials.length() < 2) initials += w.charAt(0);
        }
        initials = initials.toUpperCase();

        int size = 90;
        Bitmap bitmap = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        Paint paint = new Paint();
        paint.setAntiAlias(true);
        paint.setColor(0xFF6200EE);
        canvas.drawCircle(size / 2f, size / 2f, size / 2f, paint);

        paint.setColor(0xFFFFFFFF);
        paint.setTextSize(36f);
        paint.setTextAlign(Paint.Align.CENTER);
        Rect bounds = new Rect();
        paint.getTextBounds(initials, 0, initials.length(), bounds);
        float x = size / 2f;
        float y = size / 2f - (bounds.top + bounds.bottom) / 2f;
        canvas.drawText(initials, x, y, paint);

        profileImage.setImageBitmap(bitmap);
    }

    private void bottomNavigation() {
        chipNavigationBar.setItemSelected(R.id.home, true);

        chipNavigationBar.setOnItemSelectedListener(id -> {
            Fragment selectedFragment = null;

            if (id == R.id.ranking) {
                selectedFragment = new RankingFragment();
                homeScrollView.setVisibility(View.GONE);
                findViewById(R.id.profileLayout).setVisibility(View.GONE);
                findViewById(R.id.fragmentContainer).setVisibility(View.VISIBLE);
            } else if (id == R.id.setting) {
                selectedFragment = new SettingFragment();
                homeScrollView.setVisibility(View.GONE);
                findViewById(R.id.profileLayout).setVisibility(View.GONE);
                findViewById(R.id.fragmentContainer).setVisibility(View.VISIBLE);
            } else if (id == R.id.home) {
                homeScrollView.setVisibility(View.VISIBLE);
                findViewById(R.id.profileLayout).setVisibility(View.VISIBLE);
                findViewById(R.id.fragmentContainer).setVisibility(View.GONE);
            }

            if (selectedFragment != null) {
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragmentContainer, selectedFragment)
                        .commit();
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (sharedPreferences != null && prefListener != null) {
            sharedPreferences.unregisterOnSharedPreferenceChangeListener(prefListener);
        }
        if (networkReceiver != null) {
            unregisterReceiver(networkReceiver);
        }
    }

    @Override
    public void onBackPressed() {
        new AlertDialog.Builder(this)
                .setTitle("Exit App")
                .setMessage("Are you sure you want to exit?")
                .setCancelable(false)
                .setPositiveButton("Yes", (dialog, which) -> finishAffinity())
                .setNegativeButton("No", (dialog, which) -> dialog.dismiss())
                .show();
    }
}
