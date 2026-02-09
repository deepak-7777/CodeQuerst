package com.vmpk.codequerst.Activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.vmpk.codequerst.Fragment.LevelFragment;
import com.vmpk.codequerst.Fragment.RankingFragment;
import com.vmpk.codequerst.Fragment.SettingFragment;
import com.vmpk.codequerst.R;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.*;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import com.ismaeldivita.chipnavigation.ChipNavigationBar;

import java.util.HashMap;
import java.util.Map;

public class HomeActivity extends AppCompatActivity {

    private BroadcastReceiver profileReceiver;
    private LinearLayout c, cpp, python, java, kotlin, javaScript, createQuiz, weeklyStreak, playFriend;
   private ChipNavigationBar chipNavigationBar;
   private ScrollView homeScrollView;
   private ShapeableImageView profileImage;
   private TextView userName, tvPoints, rankPointHome;
   private SharedPreferences sharedPreferences;
   private FirebaseAuth firebaseAuth;
   private DatabaseReference usersRef;
    private SharedPreferences.OnSharedPreferenceChangeListener prefListener;
    private SwipeRefreshLayout swipeRefresh;
    private BroadcastReceiver networkReceiver;
    private ValueEventListener profileListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_home);

        playFriend = findViewById(R.id.playFriend);
        weeklyStreak  = findViewById(R.id.weeklyStreak);
        rankPointHome = findViewById(R.id.rankPointHome);
        c = findViewById(R.id.c);
        cpp = findViewById(R.id.cpp);
        python = findViewById(R.id.python);
        java = findViewById(R.id.java);
        kotlin = findViewById(R.id.kotlin);
        javaScript = findViewById(R.id.javaScript);
        homeScrollView = findViewById(R.id.homeScrollView);
        chipNavigationBar = findViewById(R.id.buttonNavigation);
        profileImage = findViewById(R.id.imageView2);
        userName = findViewById(R.id.textView5);
        swipeRefresh = findViewById(R.id.swipeRefresh);
        tvPoints = findViewById(R.id.rankPointHome);
        createQuiz = findViewById(R.id.createQuiz);

        sharedPreferences = getSharedPreferences("user_prefs", MODE_PRIVATE);
        firebaseAuth = FirebaseAuth.getInstance();

        loadUserProfile();
        loadUserPoints(); //  Points load karna

        setupNetworkReceiver();
        bottomNavigation();

        profileImage.setOnClickListener(v -> startActivity(new Intent(HomeActivity.this, ProfileActivity.class)));
        rankPointHome.setOnClickListener(v -> startActivity(new Intent(HomeActivity.this, PointsActivity.class)));
        weeklyStreak.setOnClickListener(v -> startActivity(new Intent(HomeActivity.this, StreakActivity.class)));
        playFriend.setOnClickListener(v -> startActivity(new Intent(HomeActivity.this, FriendActivity.class)));

        swipeRefresh.setOnRefreshListener(() -> {
            loadUserProfile();
            loadUserPoints(); // Refresh par points bhi reload
            swipeRefresh.setRefreshing(false);
        });

        c.setOnClickListener(v -> startActivity(new Intent(HomeActivity.this, CActivity.class)));
        cpp.setOnClickListener(v -> startActivity(new Intent(HomeActivity.this, CppActivity.class)));
        python.setOnClickListener(v -> startActivity(new Intent(HomeActivity.this, PythonActivity.class)));
        java.setOnClickListener(v -> startActivity(new Intent(HomeActivity.this, JavaActivity.class)));
        kotlin.setOnClickListener(v -> startActivity(new Intent(HomeActivity.this, KotlinActivity.class)));
        javaScript.setOnClickListener(v -> startActivity(new Intent(HomeActivity.this, JavaScriptActivity.class)));

        createQuiz.setOnClickListener(v -> {
            boolean isGuest = sharedPreferences.getBoolean("isGuest", false);
            if (isGuest) {
                // Custom dialog for guest users
                AlertDialog.Builder builder = new AlertDialog.Builder(HomeActivity.this);
                View dialogView = getLayoutInflater().inflate(R.layout.item_createquizguest, null);
                builder.setView(dialogView);
                AlertDialog dialog = builder.create();
                // Buttons
                androidx.appcompat.widget.AppCompatButton btnDismiss = dialogView.findViewById(R.id.btnCreateQuizDismiss);
                androidx.appcompat.widget.AppCompatButton btnSignIn = dialogView.findViewById(R.id.btnCreateQuizSignIn);
                // Dismiss button
                btnDismiss.setOnClickListener(view -> dialog.dismiss());
                // Sign in button → redirect to LoginActivity
                btnSignIn.setOnClickListener(view -> {
                    if (isGuest) {
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.remove("name");
                        editor.remove("email");
                        editor.remove("phone");
                        editor.remove("about");
                        editor.putBoolean("isGuest", false);
                        editor.apply();

                        FirebaseAuth.getInstance().signOut();

                        Intent intent = new Intent(HomeActivity.this, LoginActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                        finish();
                    }
                });

                dialog.show();
            } else {
                // Normal flow for logged-in users
                startActivity(new Intent(HomeActivity.this, CreateQuizActivity.class));
            }
        });

        profileReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                loadUserProfile();
            }
        };

    }

    @Override
    protected void onResume() {
        super.onResume();
        IntentFilter filter = new IntentFilter("PROFILE_UPDATED");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            registerReceiver(profileReceiver, filter, Context.RECEIVER_NOT_EXPORTED);
        } else {
            registerReceiver(profileReceiver, filter);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (profileReceiver != null) {
            unregisterReceiver(profileReceiver);
        }
    }


    private void loadUserPoints() {
        boolean isGuest = sharedPreferences.getBoolean("isGuest", false);
        if (isGuest) {
            // Guest ke liye kuch bhi nahi dikhana
            tvPoints.setVisibility(View.GONE);
            return;
        }

        FirebaseUser currentUser = firebaseAuth.getCurrentUser();
        if (currentUser != null && !currentUser.isAnonymous()) {
            String uid = currentUser.getUid();
            DatabaseReference leaderboardRef = FirebaseDatabase.getInstance().getReference("leaderboard").child(uid);

            leaderboardRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot snapshot) {
                    if (snapshot.exists()) {
                        if (isFinishing() || isDestroyed()) return;         ///       this line was very helpful for app crash
                        Integer correctAnswers = snapshot.child("totalPoints").getValue(Integer.class);
                        if (correctAnswers != null) {
                            tvPoints.setText(correctAnswers + " pts");
                            tvPoints.setVisibility(View.VISIBLE);
                        } else {
                            tvPoints.setText("0 pts");
                            tvPoints.setVisibility(View.VISIBLE);
                        }
                    } else {
                        tvPoints.setText("0 pts");
                        tvPoints.setVisibility(View.VISIBLE);
                    }
                }

                @Override
                public void onCancelled(DatabaseError error) {
                    tvPoints.setText("0 pts");
                    tvPoints.setVisibility(View.VISIBLE);
                }
            });
        } else {
            tvPoints.setVisibility(View.GONE);
        }
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

            // 🔹 Guest user
            String name = sharedPreferences.getString("name", "Guest User");
            userName.setText(name);
            setDefaultProfileImage(name);
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
            if (currentUser == null) return;

            String uid = currentUser.getUid();
            usersRef = FirebaseDatabase.getInstance()
                    .getReference("users")
                    .child(uid);

            // 🔹 STEP 1: Offline cache
            String cachedName = sharedPreferences.getString("name", "User");
            String cachedImage = sharedPreferences.getString("profileImage", "");

            userName.setText(cachedName);

            if (cachedImage.startsWith("avatar_")) {

                int resId = getResources().getIdentifier(
                        cachedImage, "drawable", getPackageName());

                if (resId != 0) {
                    profileImage.setImageResource(resId);
                } else {
                    setDefaultProfileImage(cachedName);
                }

            } else if (!cachedImage.isEmpty()) {

                if (profileImage != null && profileImage.getContext() != null) {
                    Glide.with(profileImage)
                            .load(cachedImage)
                            .circleCrop()
                            .placeholder(R.drawable.ic_person)
                            .error(R.drawable.ic_person)
                            .into(profileImage);
                }

            } else {
                setDefaultProfileImage(cachedName);
            }

            // 🔹 STEP 2: Firebase refresh
            if (isNetworkAvailable()) {

                // ❗ IMPORTANT: remove old listener first
                if (profileListener != null) {
                    usersRef.removeEventListener(profileListener);
                }

                profileListener = new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                        // 🛡️ Activity already dead → STOP
                        if (isFinishing() || isDestroyed()) return;
                        if (!snapshot.exists()) return;

                        String name = snapshot.child("name").getValue(String.class);
                        String imageValue = snapshot.child("profileImage").getValue(String.class);

                        if (name != null) {
                            userName.setText(name);
                            welcomeText.setText("Welcome Back");
                        }

                        // 🔹 AVATAR / IMAGE HANDLE
                        if (imageValue != null && imageValue.startsWith("avatar_")) {

                            int resId = getResources().getIdentifier(
                                    imageValue, "drawable", getPackageName());

                            if (resId != 0) {
                                profileImage.setImageResource(resId);
                            } else if (name != null) {
                                setDefaultProfileImage(name);
                            }

                        } else if (imageValue != null && !imageValue.isEmpty()) {

                            if (profileImage != null && profileImage.getContext() != null) {
                                Glide.with(profileImage)
                                        .load(imageValue)
                                        .circleCrop()
                                        .placeholder(R.drawable.ic_person)
                                        .error(R.drawable.ic_person)
                                        .into(profileImage);
                            }

                        } else if (name != null) {
                            setDefaultProfileImage(name);
                        }

                        // 🔹 Cache update
                        cacheProfileToPrefs(snapshot);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {}
                };

                usersRef.addValueEventListener(profileListener);

            } else {
                welcomeText.setText("Welcome Back");
            }
        }
    }



    private void cacheProfileToPrefs(DataSnapshot snapshot) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        if (snapshot.hasChild("name")) {
            editor.putString("name", snapshot.child("name").getValue(String.class));
        }
        if (snapshot.hasChild("profileImage")) {
            editor.putString("profileImage", snapshot.child("profileImage").getValue(String.class));
        }
        if (snapshot.hasChild("email")) {
            editor.putString("email", snapshot.child("email").getValue(String.class));
        }
        if (snapshot.hasChild("about")) {
            editor.putString("about", snapshot.child("about").getValue(String.class));
        }
        editor.apply();
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
            } else if (id == R.id.level) {
                selectedFragment = new LevelFragment();
                homeScrollView.setVisibility(View.GONE);
                findViewById(R.id.profileLayout).setVisibility(View.GONE);
                findViewById(R.id.fragmentContainer).setVisibility(View.VISIBLE);
            }else if (id == R.id.home) {
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

        if (profileListener != null && usersRef != null) {
            usersRef.removeEventListener(profileListener);
        }

        if (prefListener != null) {
            sharedPreferences.unregisterOnSharedPreferenceChangeListener(prefListener);
        }
    }


    @Override
    public void onBackPressed() {
        new AlertDialog.Builder(this)
                .setTitle("Exit App")
                .setMessage("Are you sure you want to exit app?")
                .setCancelable(false)
                .setPositiveButton("Yes", (dialog, which) -> finishAffinity())
                .setNegativeButton("No", (dialog, which) -> dialog.dismiss())
                .show();
    }
}
