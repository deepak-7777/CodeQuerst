package com.vmpk.codequerst.Custom;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import com.google.firebase.database.*;
import com.vmpk.codequerst.Activity.RankingViewActivity;
import com.vmpk.codequerst.Adapter.LeaderboardAdapter;
import com.vmpk.codequerst.Model.UserScore;
import com.vmpk.codequerst.R;

import java.util.*;

public class RankingListFragment extends Fragment {
    private boolean showWeekly;
    private RecyclerView recyclerLeaderboard;
    private LeaderboardAdapter adapter;
    private LinearLayout podiumLayout;
    private ProgressBar progressRanking;
    private TextView tvFirstName, tvFirstPoints, tvSecondName, tvSecondPoints, tvThirdName, tvThirdPoints;
    private ImageView imgFirst, imgSecond, imgThird;
    private SwipeRefreshLayout swipeRanking;
    private SharedPreferences sharedPreferences;
    private boolean isGuest;
    // ✅ Cache static variables
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

        sharedPreferences = requireContext().getSharedPreferences("user_prefs", Context.MODE_PRIVATE);
        isGuest = sharedPreferences.getBoolean("isGuest", false);

        // ✅ Swipe-to-refresh: always reload from Firebase
        swipeRanking.setOnRefreshListener(() -> {
            loadLeaderboard(true); // Force reload
        });

        adapter.setOnItemClickListener(user -> openUserDetail(user));

        // ✅ Use cache if available, otherwise load Firebase
        if (cachedLeaderboard != null && !cachedLeaderboard.isEmpty()) {
            displayLeaderboard(cachedLeaderboard);
        } else {
            loadLeaderboard(false);
        }

        return view;
    }

    // ===========================================================
    // 🔹 Load Leaderboard Data (Firebase)
    // ===========================================================
    private void loadLeaderboard(boolean fromSwipe) {
        if (!fromSwipe) {
            progressRanking.setVisibility(View.VISIBLE);
        }

        recyclerLeaderboard.setVisibility(View.GONE);
        podiumLayout.setVisibility(View.GONE);

        DatabaseReference leaderboardRef = FirebaseDatabase.getInstance().getReference("leaderboard");
        DatabaseReference usersRef = FirebaseDatabase.getInstance().getReference("users");

        leaderboardRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                List<UserScore> tempList = new ArrayList<>();
                if (!snapshot.exists()) {
                    progressRanking.setVisibility(View.GONE);
                    recyclerLeaderboard.setVisibility(View.VISIBLE);
                    podiumLayout.setVisibility(View.VISIBLE);
                    swipeRanking.setRefreshing(false);
                    return;
                }

                int totalEntries = (int) snapshot.getChildrenCount();
                final int[] processed = {0};
                long oneWeekAgo = System.currentTimeMillis() - 7L * 24 * 60 * 60 * 1000;

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

                            if (showWeekly) {
                                int weeklyPoints = 0;
                                if (finalScoreFinal.getQuizzes() != null) {
                                    HashSet<String> attemptedTopics = new HashSet<>();
                                    for (Map.Entry<String, UserScore.QuizAttempt> entry : finalScoreFinal.getQuizzes().entrySet()) {
                                        UserScore.QuizAttempt q = entry.getValue();
                                        if (q != null && q.getTimestamp() >= oneWeekAgo &&
                                                !attemptedTopics.contains(entry.getKey())) {
                                            weeklyPoints += q.getPoints();
                                            attemptedTopics.add(entry.getKey());
                                        }
                                    }
                                }
                                finalScoreFinal.setWeeklyPoints(weeklyPoints);
                            }

                            tempList.add(finalScoreFinal);
                            processed[0]++;
                            if (processed[0] == totalEntries) {
                                displayLeaderboard(tempList);
                                swipeRanking.setRefreshing(false);
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            processed[0]++;
                            if (processed[0] == totalEntries) {
                                displayLeaderboard(tempList);
                                swipeRanking.setRefreshing(false);
                            }
                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                progressRanking.setVisibility(View.GONE);
                recyclerLeaderboard.setVisibility(View.VISIBLE);
                podiumLayout.setVisibility(View.VISIBLE);
                swipeRanking.setRefreshing(false);
            }
        });
    }

    // ===========================================================
    // 🔹 Display Leaderboard + Cache
    // ===========================================================
    private void displayLeaderboard(List<UserScore> list) {
        cachedLeaderboard = new ArrayList<>(list);
        lastLoadedTime = System.currentTimeMillis();

        if (showWeekly) {
            list.sort((u1, u2) -> u2.getWeeklyPoints() - u1.getWeeklyPoints());
        } else {
            list.sort((u1, u2) -> u2.getTotalPoints() - u1.getTotalPoints());
        }

        if (list.size() > 0) setPodium(list.get(0), imgFirst, tvFirstName, tvFirstPoints);
        if (list.size() > 1) setPodium(list.get(1), imgSecond, tvSecondName, tvSecondPoints);
        if (list.size() > 2) setPodium(list.get(2), imgThird, tvThirdName, tvThirdPoints);

        List<UserScore> rest = list.size() > 3 ? list.subList(3, list.size()) : new ArrayList<>();
        adapter.updateList(rest);

        progressRanking.setVisibility(View.GONE);
        recyclerLeaderboard.setVisibility(View.VISIBLE);
        podiumLayout.setVisibility(View.VISIBLE);
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
    // 🔹 Generate default profile initials
    // ===========================================================
    private void setDefaultProfileImage(String fullName, ImageView imageView) {
        if (fullName == null || fullName.isEmpty()) fullName = "U";
        String[] words = fullName.trim().split(" ");
        String initials = "";
        for (String w : words) if (!w.isEmpty() && initials.length() < 2) initials += w.charAt(0);
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
