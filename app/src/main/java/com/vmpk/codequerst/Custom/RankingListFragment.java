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
import android.widget.Button;
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
    private TextView tvFirstName, tvFirstPoints, tvSecondName, tvSecondPoints, tvThirdName, tvThirdPoints;
    private ImageView imgFirst, imgSecond, imgThird, weeklyDialogBox;
    private SwipeRefreshLayout swipeRanking;
    private SharedPreferences sharedPreferences;
    private boolean isGuest;
    private MaterialButton btnTryQuiz;

    private static List<UserScore> cachedLeaderboard = null;
    private static long lastLoadedTime = 0;

    public RankingListFragment(boolean weekly) {
        this.showWeekly = weekly;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_ranking_list, container, false);

        recyclerLeaderboard = view.findViewById(R.id.recyclerLeaderboard);
        recyclerLeaderboard.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new LeaderboardAdapter(getContext(), new ArrayList<>());
        recyclerLeaderboard.setAdapter(adapter);

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

        emptyStateLayout = view.findViewById(R.id.emptyStateLayout);
        btnTryQuiz = view.findViewById(R.id.btnTryQuiz);

        sharedPreferences = requireContext().getSharedPreferences("user_prefs", Context.MODE_PRIVATE);
        isGuest = sharedPreferences.getBoolean("isGuest", false);

        // ✅ Show Guest Dialog only once after install or logout
        if (isGuest) {
            showGuestDialogOnce();
        }

        btnTryQuiz.setOnClickListener(v -> {
            Intent intent = new Intent(getContext(), HomeActivity.class);
            startActivity(intent);
            requireActivity().finish();
        });

        swipeRanking.setOnRefreshListener(() -> loadLeaderboard(true));
        adapter.setOnItemClickListener(this::openUserDetail);

        if (cachedLeaderboard != null && !cachedLeaderboard.isEmpty()) {
            displayLeaderboard(cachedLeaderboard);
        } else {
            loadLeaderboard(false);
        }

        weeklyDialogBox = view.findViewById(R.id.weeklyDialogBox);
        if (weeklyDialogBox != null) {
            weeklyDialogBox.setVisibility(showWeekly ? View.VISIBLE : View.GONE);
            weeklyDialogBox.setOnClickListener(v -> {
                AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
                builder.setTitle("Weekly Leaderboard Info")
                        .setMessage("This leaderboard shows weekly points from Monday to Sunday. Scores reset every Monday.")
                        .setPositiveButton("OK", (dialog, which) -> dialog.dismiss())
                        .show();
            });
        }


        return view;
    }

    // ===========================================================
    // 🔹 Guest Dialog (Shown only once)
    // ===========================================================
    private void showGuestDialogOnce() {
        SharedPreferences prefs = requireContext().getSharedPreferences("AppPrefs", Context.MODE_PRIVATE);
        boolean isDialogShown = prefs.getBoolean("isGuestDialogShown", false);

        if (!isDialogShown) {
            Dialog dialog = new Dialog(requireContext());
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setContentView(R.layout.guest_dialog);
            dialog.setCancelable(false);
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

            Button btnDismiss = dialog.findViewById(R.id.btnDismiss);
            Button btnSignIn = dialog.findViewById(R.id.btnSignIn);

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

                    Intent intent = new Intent(requireActivity(), LoginActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                    requireActivity().finish();
                }
            });

            dialog.show();

            // ✅ Mark dialog as shown once
            prefs.edit().putBoolean("isGuestDialogShown", true).apply();
        }
    }

    // ===========================================================
    // 🔹 Load Leaderboard Data
    // ===========================================================
    private void loadLeaderboard(boolean fromSwipe) {
        if (!fromSwipe) progressRanking.setVisibility(View.VISIBLE);

        recyclerLeaderboard.setVisibility(View.GONE);
        podiumLayout.setVisibility(View.GONE);
        emptyStateLayout.setVisibility(View.GONE);

        DatabaseReference leaderboardRef = FirebaseDatabase.getInstance().getReference("leaderboard");
        DatabaseReference usersRef = FirebaseDatabase.getInstance().getReference("users");

        // ✅ Weekly calculation setup
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);

        long weekStartMillis = calendar.getTimeInMillis();
        int currentWeek = calendar.get(Calendar.WEEK_OF_YEAR);

        // ✅ Auto-reset weekly scores once every Monday
        SharedPreferences prefs = requireContext().getSharedPreferences("AppPrefs", Context.MODE_PRIVATE);
        int lastWeek = prefs.getInt("lastWeek", -1);
        if (lastWeek != currentWeek) {
            leaderboardRef.get().addOnSuccessListener(snapshot -> {
                for (DataSnapshot child : snapshot.getChildren()) {
                    child.getRef().child("weeklyPoints").setValue(0);
                }
            });
            prefs.edit().putInt("lastWeek", currentWeek).apply();
        }

        leaderboardRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                List<UserScore> tempList = new ArrayList<>();
                if (!snapshot.exists()) {
                    showEmptyState();
                    swipeRanking.setRefreshing(false);
                    return;
                }

                int totalEntries = (int) snapshot.getChildrenCount();
                final int[] processed = {0};

                for (DataSnapshot child : snapshot.getChildren()) {
                    String uid = child.getKey();
                    UserScore scoreFinal = child.getValue(UserScore.class);
                    if (scoreFinal == null) scoreFinal = new UserScore();
                    scoreFinal.setUid(uid);

                    Integer tp = child.child("totalPoints").getValue(Integer.class);
                    if (tp != null) scoreFinal.setTotalPoints(tp);

                    UserScore finalScoreFinal = scoreFinal;
                    usersRef.child(uid).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot userSnap) {
                            if (!isAdded()) return;
                            if (userSnap.exists()) {
                                finalScoreFinal.setName(userSnap.child("name").getValue(String.class));
                                finalScoreFinal.setEmail(userSnap.child("email").getValue(String.class));
                                finalScoreFinal.setProfileImage(userSnap.child("profileImage").getValue(String.class));
                            }

                            // ✅ Weekly points logic (Monday → Sunday)
                            if (showWeekly) {
                                int weeklyPoints = 0;
                                if (finalScoreFinal.getQuizzes() != null) {
                                    for (Map.Entry<String, UserScore.QuizAttempt> entry :
                                            finalScoreFinal.getQuizzes().entrySet()) {
                                        UserScore.QuizAttempt q = entry.getValue();
                                        if (q != null && q.getTimestamp() >= weekStartMillis) {
                                            weeklyPoints += q.getPoints();
                                        }
                                    }
                                }
                                finalScoreFinal.setWeeklyPoints(weeklyPoints);
                            }

                            tempList.add(finalScoreFinal);
                            processed[0]++;
                            if (processed[0] == totalEntries) {
                                if (tempList.isEmpty()) showEmptyState();
                                else displayLeaderboard(tempList);
                                swipeRanking.setRefreshing(false);
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            processed[0]++;
                            if (processed[0] == totalEntries) {
                                if (tempList.isEmpty()) showEmptyState();
                                else displayLeaderboard(tempList);
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

    // ===========================================================
    // 🔹 Empty State
    // ===========================================================
    private void showEmptyState() {
        progressRanking.setVisibility(View.GONE);
        recyclerLeaderboard.setVisibility(View.GONE);
        podiumLayout.setVisibility(View.GONE);
        emptyStateLayout.setVisibility(View.VISIBLE);
    }

    // ===========================================================
    // 🔹 Display Leaderboard + Cache
    // ===========================================================
    private void displayLeaderboard(List<UserScore> list) {
        cachedLeaderboard = new ArrayList<>(list);
        lastLoadedTime = System.currentTimeMillis();

        if (showWeekly) list.sort((u1, u2) -> u2.getWeeklyPoints() - u1.getWeeklyPoints());
        else list.sort((u1, u2) -> u2.getTotalPoints() - u1.getTotalPoints());

        if (list.isEmpty()) {
            showEmptyState();
            return;
        }

        if (list.size() > 0) setPodium(list.get(0), imgFirst, tvFirstName, tvFirstPoints);
        if (list.size() > 1) setPodium(list.get(1), imgSecond, tvSecondName, tvSecondPoints);
        if (list.size() > 2) setPodium(list.get(2), imgThird, tvThirdName, tvThirdPoints);

        List<UserScore> rest = list.size() > 3 ? list.subList(3, list.size()) : new ArrayList<>();
        adapter.updateList(rest);

        progressRanking.setVisibility(View.GONE);
        recyclerLeaderboard.setVisibility(View.VISIBLE);
        podiumLayout.setVisibility(View.VISIBLE);
        emptyStateLayout.setVisibility(View.GONE);
    }

    // ===========================================================
    // 🔹 Podium setup
    // ===========================================================
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

    // ===========================================================
    // 🔹 Open user detail activity
    // ===========================================================
    private void openUserDetail(UserScore user) {
        int rank = cachedLeaderboard.indexOf(user) + 1;
        Intent intent = new Intent(getActivity(), RankingViewActivity.class);
        intent.putExtra("name", user.getName());
        intent.putExtra("image", user.getProfileImage());
        intent.putExtra("points", (showWeekly ? user.getWeeklyPoints() : user.getTotalPoints()) + " pts");
        intent.putExtra("rank", String.valueOf(rank));
        startActivity(intent);
    }

    // ===========================================================
    // 🔹 Default initials for profile
    // ===========================================================
    private void setDefaultProfileImage(String fullName, ImageView imageView) {
        if (fullName == null || fullName.isEmpty()) fullName = "U";
        String[] words = fullName.trim().split(" ");
        String initials = "";
        for (String w : words)
            if (!w.isEmpty() && initials.length() < 2)
                initials += w.charAt(0);
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
}
