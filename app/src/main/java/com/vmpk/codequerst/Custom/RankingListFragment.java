package com.vmpk.codequerst.Custom;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.bumptech.glide.Glide;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.*;
import com.vmpk.codequerst.Activity.HomeActivity;
import com.vmpk.codequerst.Activity.LoginActivity;
import com.vmpk.codequerst.Activity.RankingViewActivity;
import com.vmpk.codequerst.Adapter.LeaderboardAdapter;
import com.vmpk.codequerst.Model.UserScore;
import com.vmpk.codequerst.R;

import java.util.*;

public class RankingListFragment extends Fragment {

    private boolean showWeekly;
    private RecyclerView recyclerLeaderboard;
    private LeaderboardAdapter adapter;
    private LinearLayout podiumLayout, emptyStateLayout;
    private ProgressBar progressRanking;
    private SwipeRefreshLayout swipeRanking;
    private ImageView imgFirst, imgSecond, imgThird;
    private TextView tvFirstName, tvFirstPoints,
            tvSecondName, tvSecondPoints,
            tvThirdName, tvThirdPoints;
    private GoogleSignInClient gsc;
    private ImageView weeklyDialogBox;
    private MaterialButton btnTryQuiz;
    private SharedPreferences sharedPreferences;
    private boolean isGuest;
    private final List<UserScore> currentSortedList = new ArrayList<>();
    private static List<UserScore> cachedLeaderboard = null;
    public RankingListFragment(boolean weekly) {
        this.showWeekly = weekly;
    }

    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_ranking_list, container, false);

        recyclerLeaderboard = view.findViewById(R.id.recyclerLeaderboard);
        recyclerLeaderboard.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new LeaderboardAdapter(getContext(), new ArrayList<>());
        recyclerLeaderboard.setAdapter(adapter);

        podiumLayout = view.findViewById(R.id.podiumLayout);
        emptyStateLayout = view.findViewById(R.id.emptyStateLayout);
        progressRanking = view.findViewById(R.id.progressRanking);
        swipeRanking = view.findViewById(R.id.swipeRanking);

        imgFirst = view.findViewById(R.id.imgFirst);
        imgSecond = view.findViewById(R.id.imgSecond);
        imgThird = view.findViewById(R.id.imgThird);

        tvFirstName = view.findViewById(R.id.tvFirstName);
        tvFirstPoints = view.findViewById(R.id.tvFirstPoints);
        tvSecondName = view.findViewById(R.id.tvSecondName);
        tvSecondPoints = view.findViewById(R.id.tvSecondPoints);
        tvThirdName = view.findViewById(R.id.tvThirdName);
        tvThirdPoints = view.findViewById(R.id.tvThirdPoints);

        weeklyDialogBox = view.findViewById(R.id.weeklyDialogBox);
        btnTryQuiz = view.findViewById(R.id.btnTryQuiz);

        sharedPreferences = requireContext()
                .getSharedPreferences("user_prefs", Context.MODE_PRIVATE);
        isGuest = sharedPreferences.getBoolean("isGuest", false);

        if (isGuest) showGuestDialogOnce();

        btnTryQuiz.setOnClickListener(v -> {
            startActivity(new Intent(getContext(), HomeActivity.class));
            requireActivity().finish();
        });

        adapter.setOnItemClickListener(this::openUserDetail);

        // 🔥 Swipe refresh = ONLY time reload happens
        swipeRanking.setOnRefreshListener(() -> {
            cachedLeaderboard = null;   // force reload
            loadLeaderboard(true);
        });

        setupWeeklyInfoIcon();

        // 🔥 USE CACHE (NO AUTO RELOAD)
        if (cachedLeaderboard != null && !cachedLeaderboard.isEmpty()) {
            displayLeaderboard(new ArrayList<>(cachedLeaderboard));
        } else {
            loadLeaderboard(false);
        }

        return view;
    }

    // ===================================================
    // 🔹 Load Leaderboard (Firebase)
    // ===================================================
    private void loadLeaderboard(boolean fromSwipe) {

        if (!fromSwipe) progressRanking.setVisibility(View.VISIBLE);

        recyclerLeaderboard.setVisibility(View.GONE);
        podiumLayout.setVisibility(View.GONE);
        emptyStateLayout.setVisibility(View.GONE);

        DatabaseReference leaderboardRef =
                FirebaseDatabase.getInstance().getReference("leaderboard");
        DatabaseReference usersRef =
                FirebaseDatabase.getInstance().getReference("users");

        leaderboardRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if (!snapshot.exists()) {
                    showEmptyState();
                    swipeRanking.setRefreshing(false);
                    return;
                }

                List<UserScore> list = new ArrayList<>();
                int total = (int) snapshot.getChildrenCount();
                int[] processed = {0};

                for (DataSnapshot child : snapshot.getChildren()) {

                    String uid = child.getKey();
                    UserScore score = child.getValue(UserScore.class);
                    if (score == null) score = new UserScore();
                    score.setUid(uid);

                    UserScore finalScore = score;

                    usersRef.child(uid)
                            .addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot u) {

                                    if (!isAdded()) return;

                                    finalScore.setName(u.child("name").getValue(String.class));
                                    finalScore.setEmail(u.child("email").getValue(String.class));
                                    finalScore.setProfileImage(
                                            u.child("profileImage").getValue(String.class));

                                    // 🔥 WEEKLY POINTS CALCULATION + SAVE (ONLY WEEKLY)
                                    if (showWeekly) {

                                        Calendar cal = Calendar.getInstance();
                                        int day = cal.get(Calendar.DAY_OF_WEEK);
                                        int diff = (day == Calendar.SUNDAY)
                                                ? 6
                                                : day - Calendar.MONDAY;
                                        cal.add(Calendar.DAY_OF_MONTH, -diff);

                                        cal.set(Calendar.HOUR_OF_DAY, 0);
                                        cal.set(Calendar.MINUTE, 0);
                                        cal.set(Calendar.SECOND, 0);
                                        cal.set(Calendar.MILLISECOND, 0);

                                        long weekStartMillis = cal.getTimeInMillis();
                                        int weeklyPoints = 0;

                                        if (finalScore.getQuizzes() != null) {
                                            for (UserScore.QuizAttempt q :
                                                    finalScore.getQuizzes().values()) {
                                                if (q != null &&
                                                        q.getTimestamp() >= weekStartMillis) {
                                                    weeklyPoints += q.getPoints();
                                                }
                                            }
                                        }

                                        // set in object
                                        finalScore.setWeeklyPoints(weeklyPoints);

                                        // 🔥 SAVE TO FIREBASE (MAIN FIX)
                                        leaderboardRef
                                                .child(finalScore.getUid())
                                                .child("weeklyPoints")
                                                .setValue(weeklyPoints);
                                    }

                                    list.add(finalScore);
                                    processed[0]++;

                                    if (processed[0] == total) {
                                        displayLeaderboard(list);
                                        swipeRanking.setRefreshing(false);
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {
                                    processed[0]++;
                                    if (processed[0] == total) {
                                        displayLeaderboard(list);
                                        swipeRanking.setRefreshing(false);
                                    }
                                }
                            });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                showEmptyState();
                swipeRanking.setRefreshing(false);
            }
        });
    }


    // ===================================================
    // 🔹 Display Leaderboard + SAVE CACHE
    // ===================================================
    private void displayLeaderboard(List<UserScore> list) {

        if (list.isEmpty()) {
            showEmptyState();
            return;
        }

        if (showWeekly)
            list.sort((a, b) -> b.getWeeklyPoints() - a.getWeeklyPoints());
        else
            list.sort((a, b) -> b.getTotalPoints() - a.getTotalPoints());

        currentSortedList.clear();
        currentSortedList.addAll(list);

        // 🔥 SAVE CACHE (KEY)
        cachedLeaderboard = new ArrayList<>(list);

        if (list.size() > 0) setPodium(list.get(0), imgFirst, tvFirstName, tvFirstPoints);
        if (list.size() > 1) setPodium(list.get(1), imgSecond, tvSecondName, tvSecondPoints);
        if (list.size() > 2) setPodium(list.get(2), imgThird, tvThirdName, tvThirdPoints);

        List<UserScore> rest =
                list.size() > 3 ? list.subList(3, list.size()) : new ArrayList<>();

        adapter.setShowWeekly(showWeekly);
        adapter.updateList(rest);

        progressRanking.setVisibility(View.GONE);
        recyclerLeaderboard.setVisibility(View.VISIBLE);
        podiumLayout.setVisibility(View.VISIBLE);
        emptyStateLayout.setVisibility(View.GONE);
    }

    // ===================================================
    // 🔹 Podium
    // ===================================================
    private void setPodium(UserScore user, ImageView img, TextView name, TextView points) {

        name.setText(user.getName() != null ? user.getName() : "User");

        String image = user.getProfileImage();

        if (image != null && !image.isEmpty()) {
            if (image.startsWith("http")) {
                Glide.with(this).load(image).circleCrop().into(img);
            } else if (image.startsWith("avatar_")) {
                int resId = requireContext().getResources()
                        .getIdentifier(image, "drawable",
                                requireContext().getPackageName());
                if (resId != 0) img.setImageResource(resId);
                else setDefaultProfileImage(name.getText().toString(), img);
            } else {
                setDefaultProfileImage(name.getText().toString(), img);
            }
        } else {
            setDefaultProfileImage(name.getText().toString(), img);
        }

        points.setText(
                (showWeekly ? user.getWeeklyPoints() : user.getTotalPoints()) + " pts");

        img.setOnClickListener(v -> openUserDetail(user));
        name.setOnClickListener(v -> openUserDetail(user));
        points.setOnClickListener(v -> openUserDetail(user));
    }

    private void openUserDetail(UserScore user) {
        int rank = currentSortedList.indexOf(user) + 1;

        Intent i = new Intent(getActivity(), RankingViewActivity.class);
        i.putExtra("name", user.getName());
        i.putExtra("image", user.getProfileImage());
        i.putExtra("points",
                String.valueOf(showWeekly ? user.getWeeklyPoints() : user.getTotalPoints()));
        i.putExtra("rank", String.valueOf(rank));
        i.putExtra("about", user.getEmail());

        startActivity(i);
    }

    private void showEmptyState() {
        progressRanking.setVisibility(View.GONE);
        recyclerLeaderboard.setVisibility(View.GONE);
        podiumLayout.setVisibility(View.GONE);
        emptyStateLayout.setVisibility(View.VISIBLE);
    }

    private void setDefaultProfileImage(String name, ImageView img) {
        if (name == null || name.isEmpty()) name = "U";
        String initial = name.substring(0, 1).toUpperCase();

        Bitmap b = Bitmap.createBitmap(120, 120, Bitmap.Config.ARGB_8888);
        Canvas c = new Canvas(b);
        Paint p = new Paint(Paint.ANTI_ALIAS_FLAG);

        p.setColor(0xFF6200EE);
        c.drawCircle(60, 60, 60, p);

        p.setColor(Color.WHITE);
        p.setTextSize(42);
        p.setTextAlign(Paint.Align.CENTER);

        Rect r = new Rect();
        p.getTextBounds(initial, 0, 1, r);
        c.drawText(initial, 60, 60 - r.centerY(), p);

        img.setImageBitmap(b);
    }

    private void setupWeeklyInfoIcon() {
        if (weeklyDialogBox == null) return;

        if (showWeekly) {
            weeklyDialogBox.setVisibility(View.VISIBLE);
            weeklyDialogBox.setOnClickListener(v -> {
                new AlertDialog.Builder(requireContext())
                        .setTitle("Weekly Leaderboard Info")
                        .setMessage(
                                "Weekly points are calculated from all quiz attempts\n" +
                                        "between Monday 12:00 AM and Sunday 11:59 PM.\n\n" +
                                        "• Same quiz can be played multiple times\n" +
                                        "• Points reset every Sunday night"
                        )
                        .setPositiveButton("OK", null)
                        .show();
            });
        } else {
            weeklyDialogBox.setVisibility(View.GONE);
            weeklyDialogBox.setOnClickListener(null);
        }
    }

    private void showGuestDialogOnce() {
        SharedPreferences prefs =
                requireContext().getSharedPreferences("AppPrefs", Context.MODE_PRIVATE);
        if (prefs.getBoolean("isGuestDialogShown", false)) return;

        Dialog d = new Dialog(requireContext());
        d.requestWindowFeature(Window.FEATURE_NO_TITLE);
        d.setContentView(R.layout.guest_dialog);
        d.setCancelable(false);
        d.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        d.findViewById(R.id.btnDismiss).setOnClickListener(v -> d.dismiss());
        d.findViewById(R.id.btnSignIn).setOnClickListener(v -> {
            FirebaseAuth.getInstance().signOut();
            performLogout();
            startActivity(new Intent(getActivity(), LoginActivity.class));
            requireActivity().finish();
        });

        d.show();
        prefs.edit().putBoolean("isGuestDialogShown", true).apply();
    }
    private void performLogout() {
        if (isGuest) {
            // Clear guest data from SharedPreferences
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.remove("name");
            editor.remove("email");
            editor.remove("phone");
            editor.remove("about");
            editor.putBoolean("isGuest", false);
            editor.apply();

            FirebaseAuth.getInstance().signOut();

            // Go to LoginActivity
            Intent intent = new Intent(requireActivity(), LoginActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            requireActivity().finish();
        } else {
            // Firebase + Google Sign Out
            FirebaseAuth.getInstance().signOut();

            // Initialize GoogleSignInClient if null
            if (gsc == null) {
                gsc = GoogleSignIn.getClient(requireActivity(),
                        new com.google.android.gms.auth.api.signin.GoogleSignInOptions.Builder(
                                com.google.android.gms.auth.api.signin.GoogleSignInOptions.DEFAULT_SIGN_IN)
                                .requestEmail()
                                .build());
            }

            gsc.signOut().addOnCompleteListener(task -> {
                Intent intent = new Intent(requireActivity(), LoginActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                requireActivity().finish();
            });
        }
    }


}

