package com.vmpk.codequerst.Fragment;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.*;
import com.vmpk.codequerst.Activity.LoginActivity;
import com.vmpk.codequerst.Activity.RankingViewActivity;
import com.vmpk.codequerst.Adapter.LeaderboardAdapter;
import com.vmpk.codequerst.Model.UserScore;
import com.vmpk.codequerst.R;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

public class RankingFragment extends Fragment {

    private LinearLayout podiumLayout;
    private ProgressBar progressRanking;
    private RecyclerView recyclerLeaderboard;
    private LeaderboardAdapter adapter;
    private List<UserScore> userList = new ArrayList<>();
    private TextView tvFirstName, tvFirstPoints, tvSecondName, tvSecondPoints, tvThirdName, tvThirdPoints;
    private ImageView imgFirst, imgSecond, imgThird;
    private FirebaseAuth firebaseAuth;
    private SwipeRefreshLayout swipeRanking;
    boolean dialogShown;
    private Button btnWeekly, btnAllTime;
    private boolean showWeekly = true;
    private SharedPreferences sharedPreferences;
    private boolean isGuest;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_ranking, container, false);

        firebaseAuth = FirebaseAuth.getInstance();

        recyclerLeaderboard = view.findViewById(R.id.recyclerLeaderboard);
        recyclerLeaderboard.setLayoutManager(new LinearLayoutManager(getContext()));

        // ✅ Adapter initialization once
        adapter = new LeaderboardAdapter(getContext(), new ArrayList<>());
        recyclerLeaderboard.setAdapter(adapter);

        // Item click listener
        adapter.setOnItemClickListener(user -> {
            int rank = userList.indexOf(user) + 4; // podium 1-3 ke baad
            Intent intent = new Intent(getActivity(), RankingViewActivity.class);
            intent.putExtra("name", user.getName());
            intent.putExtra("image", user.getProfileImage());
            intent.putExtra("points", (showWeekly ? user.getWeeklyPoints() : user.getTotalPoints()) + " pts");
            intent.putExtra("rank", String.valueOf(rank));
            startActivity(intent);
        });

        sharedPreferences = requireContext().getSharedPreferences("user_prefs", Context.MODE_PRIVATE);
        podiumLayout = view.findViewById(R.id.podiumLayout);
        progressRanking = view.findViewById(R.id.progressRanking);
        imgFirst = view.findViewById(R.id.imgFirst);
        tvFirstName = view.findViewById(R.id.tvFirstName);
        tvFirstPoints = view.findViewById(R.id.tvFirstPoints);
        imgSecond = view.findViewById(R.id.imgSecond);
        tvSecondName = view.findViewById(R.id.tvSecondName);
        tvSecondPoints = view.findViewById(R.id.tvSecondPoints);
        imgThird = view.findViewById(R.id.imgThird);
        tvThirdName = view.findViewById(R.id.tvThirdName);
        tvThirdPoints = view.findViewById(R.id.tvThirdPoints);
        swipeRanking = view.findViewById(R.id.swipeRanking);
        btnWeekly = view.findViewById(R.id.BtnWeekly);
        btnAllTime = view.findViewById(R.id.BtnAllTime);

        updateButtonColors();

        // Guest dialog
        isGuest = sharedPreferences.getBoolean("isGuest", false);
        dialogShown = sharedPreferences.getBoolean("rankingDialogShown", false);
        if (isGuest && !dialogShown) {
            showGuestWarningDialog();
        }

        swipeRanking.setOnRefreshListener(() -> {
            loadLeaderboard();
            swipeRanking.setRefreshing(false);
        });

        btnWeekly.setOnClickListener(v -> {
            showWeekly = true;
            updateButtonColors();
            loadLeaderboard();
        });

        btnAllTime.setOnClickListener(v -> {
            showWeekly = false;
            updateButtonColors();
            loadLeaderboard();
        });

        // Show guest warning if anonymous
        if (isGuestUser()) {
            showGuestWarningDialog();
        }

        loadLeaderboard();
        return view;
    }

    private void updateButtonColors() {
        Drawable weeklyDrawable = ContextCompat.getDrawable(getContext(), R.drawable.ic_weekly);
        Drawable allTimeDrawable = ContextCompat.getDrawable(getContext(), R.drawable.ic_weekly);

        if (showWeekly) {
            btnWeekly.setBackground(weeklyDrawable);
            btnAllTime.setBackgroundResource(R.drawable.createquiz_bg);
        } else {
            btnWeekly.setBackgroundResource(R.drawable.createquiz_bg);
            btnAllTime.setBackground(allTimeDrawable);
        }
    }

    private boolean isGuestUser() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        return user != null && user.isAnonymous();
    }

    private void loadLeaderboard() {
        progressRanking.setVisibility(View.VISIBLE);
        recyclerLeaderboard.setVisibility(View.GONE);
        podiumLayout.setVisibility(View.GONE);

        DatabaseReference leaderboardRef = FirebaseDatabase.getInstance().getReference("leaderboard");
        DatabaseReference usersRef = FirebaseDatabase.getInstance().getReference("users");

        leaderboardRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                userList.clear();
                if (!snapshot.exists()) {
                    progressRanking.setVisibility(View.GONE);
                    recyclerLeaderboard.setVisibility(View.VISIBLE);
                    podiumLayout.setVisibility(View.VISIBLE);
                    return;
                }

                List<UserScore> tempList = new ArrayList<>();
                int totalEntries = (int) snapshot.getChildrenCount();
                final int[] processed = {0};
                long oneWeekAgo = System.currentTimeMillis() - 7L * 24 * 60 * 60 * 1000;

                for (DataSnapshot child : snapshot.getChildren()) {
                    String uid = child.getKey();

                    final UserScore scoreFinal = child.getValue(UserScore.class) != null ? child.getValue(UserScore.class) : new UserScore();
                    scoreFinal.setUid(uid);

                    // Load totalPoints from Firebase (important)
                    Integer tp = child.child("totalPoints").getValue(Integer.class);
                    if (tp != null) scoreFinal.setTotalPoints(tp);

                    Integer wp = child.child("weeklyPoints").getValue(Integer.class);
                    if (wp != null) scoreFinal.setWeeklyPoints(wp);

                    // Load user info
                    usersRef.child(uid).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot userSnap) {
                            if (!isAdded()) return;     ///  iski wajah se agar data firebase se load nahi hua hoga tab bhi fragment change karne par app crash nahi hoga
                            if (userSnap.exists()) {
                                scoreFinal.setName(userSnap.child("name").getValue(String.class));
                                scoreFinal.setEmail(userSnap.child("email").getValue(String.class));
                                scoreFinal.setProfileImage(userSnap.child("profileImage").getValue(String.class));
                            }

                            // Weekly points calculation
                            if (showWeekly) {
                                int weeklyPoints = 0;
                                if (scoreFinal.getQuizzes() != null) {
                                    HashSet<String> attemptedTopics = new HashSet<>();
                                    for (Map.Entry<String, UserScore.QuizAttempt> entry : scoreFinal.getQuizzes().entrySet()) {
                                        String topicKey = entry.getKey();
                                        UserScore.QuizAttempt q = entry.getValue();
                                        if (q == null) continue;
                                        if (q.getTimestamp() >= oneWeekAgo && !attemptedTopics.contains(topicKey)) {
                                            weeklyPoints += q.getPoints();
                                            attemptedTopics.add(topicKey);
                                        }
                                    }
                                }
                                scoreFinal.setWeeklyPoints(weeklyPoints);
                            }

                            tempList.add(scoreFinal);
                            processed[0]++;
                            if (processed[0] == totalEntries) displayLeaderboard(tempList);
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            processed[0]++;
                            if (processed[0] == totalEntries) displayLeaderboard(tempList);
                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                progressRanking.setVisibility(View.GONE);
                recyclerLeaderboard.setVisibility(View.VISIBLE);
                podiumLayout.setVisibility(View.VISIBLE);
            }
        });
    }

    private void displayLeaderboard(List<UserScore> list) {
        // Sorting
        if (showWeekly) {
            Collections.sort(list, (u1, u2) -> u2.getWeeklyPoints() - u1.getWeeklyPoints());
        } else {
            Collections.sort(list, (u1, u2) -> u2.getTotalPoints() - u1.getTotalPoints());
        }

        userList = list;

        if (list.size() > 0) setPodium(list.get(0), imgFirst, tvFirstName, tvFirstPoints);
        if (list.size() > 1) setPodium(list.get(1), imgSecond, tvSecondName, tvSecondPoints);
        if (list.size() > 2) setPodium(list.get(2), imgThird, tvThirdName, tvThirdPoints);

        // ✅ Rest list update via adapter.updateList()
        List<UserScore> rest = list.size() > 3 ? list.subList(3, list.size()) : new ArrayList<>();
        adapter.updateList(rest);

        progressRanking.setVisibility(View.GONE);
        recyclerLeaderboard.setVisibility(View.VISIBLE);
        podiumLayout.setVisibility(View.VISIBLE);
    }

private void setPodium(UserScore user, ImageView img, TextView tvName, TextView tvPoints) {
        tvName.setText(user.getName() != null ? user.getName() : "User");

        if (user.getProfileImage() != null && !user.getProfileImage().isEmpty()) {
            Glide.with(this).load(user.getProfileImage()).circleCrop().into(img);
        } else {
            setDefaultProfileImage(tvName.getText().toString(), img);
        }

        tvPoints.setText((showWeekly ? user.getWeeklyPoints() : user.getTotalPoints()) + " pts");

        img.setOnClickListener(v -> openUserDetail(user));
        tvName.setOnClickListener(v -> openUserDetail(user));
        tvPoints.setOnClickListener(v -> openUserDetail(user));
    }

    private void openUserDetail(UserScore user) {
        int rank = userList.indexOf(user) + 1;
        Intent intent = new Intent(getActivity(), RankingViewActivity.class);
        intent.putExtra("name", user.getName());
        intent.putExtra("image", user.getProfileImage());
        intent.putExtra("points", (showWeekly ? user.getWeeklyPoints() : user.getTotalPoints()) + " pts");
        intent.putExtra("rank", String.valueOf(rank));
        startActivity(intent);
    }

    private void setDefaultProfileImage(String fullName, ImageView imageView) {
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
        paint.setColor(0xFFFFFFFF);
        paint.setTextSize(42f);
        paint.setTextAlign(Paint.Align.CENTER);
        Rect bounds = new Rect();
        paint.getTextBounds(initials, 0, initials.length(), bounds);
        float x = size / 2f;
        float y = size / 2f - (bounds.top + bounds.bottom) / 2f;
        canvas.drawText(initials, x, y, paint);
        imageView.setImageBitmap(bitmap);
    }

    private void showGuestWarningDialog() {
        View dialogView = getLayoutInflater().inflate(R.layout.guest_dialog, null);

        androidx.appcompat.app.AlertDialog dialog = new androidx.appcompat.app.AlertDialog.Builder(getContext())
                .setView(dialogView)
                .setCancelable(false)
                .create();

        Button btnDismiss = dialogView.findViewById(R.id.btnDismiss);
        Button btnSignIn = dialogView.findViewById(R.id.btnSignIn);

        btnDismiss.setOnClickListener(v -> dialog.dismiss());

        btnSignIn.setOnClickListener(v -> {
            if (isGuest) {
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.remove("name");
                editor.remove("email");
                editor.remove("phone");
                editor.remove("about");
                editor.putBoolean("isGuest", false);
                editor.apply();

                FirebaseAuth.getInstance().signOut();

                Intent intent = new Intent(requireContext(), com.vmpk.codequerst.Activity.LoginActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                requireActivity().finish();
            }
        });

        dialog.show();
    }
}
