package com.vmpk.codequerst.Fragment;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
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
import com.vmpk.codequerst.Activity.HomeActivity;
import com.vmpk.codequerst.Adapter.LeaderboardAdapter;
import com.vmpk.codequerst.Model.UserScore;
import com.vmpk.codequerst.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class RankingFragment extends Fragment {
    private LinearLayout podiumLayout;
    private ProgressBar progressRanking;
    boolean dialogShown;
    private boolean isGuest;
    private RecyclerView recyclerLeaderboard;
    private LeaderboardAdapter adapter;
    private List<UserScore> userList = new ArrayList<>();
    private TextView tvFirstName, tvFirstPoints, tvSecondName, tvSecondPoints, tvThirdName, tvThirdPoints, backRanking;
    private ImageView imgFirst, imgSecond, imgThird;
    private FirebaseAuth firebaseAuth;
    private BroadcastReceiver profileUpdateReceiver;
    private SharedPreferences sharedPreferences;
    private SwipeRefreshLayout swipeRanking;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_ranking, container, false);

        firebaseAuth = FirebaseAuth.getInstance();
        sharedPreferences = requireContext().getSharedPreferences("user_prefs", Context.MODE_PRIVATE);

        backRanking = view.findViewById(R.id.backRanking);
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

        ///    for open dialog-box
        isGuest = sharedPreferences.getBoolean("isGuest", false);
        dialogShown = sharedPreferences.getBoolean("rankingDialogShown", false);
        if (isGuest && !dialogShown) {
            showGuestDialog();
        }

        // Setup swipe-to-refresh
        swipeRanking.setOnRefreshListener(() -> {
            loadLeaderboard();  // refresh leaderboard
            swipeRanking.setRefreshing(false); // stop the spinner after refresh
        });

        loadLeaderboard();

        // Broadcast receiver for profile updates
        profileUpdateReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                loadLeaderboard(); // refresh leaderboard & podium
            }
        };
        ContextCompat.registerReceiver(requireActivity(),
                profileUpdateReceiver,
                new IntentFilter("PROFILE_UPDATED"),
                ContextCompat.RECEIVER_NOT_EXPORTED);

        ///    move to HomeActivity
        backRanking.setOnClickListener(v -> {
            if(getActivity() != null) {
                Intent intent = new Intent(getActivity(), HomeActivity.class);
                startActivity(intent);
                getActivity().finish();
            }
        });

        return view;
    }

    /// //   for open dialog
    private void showGuestDialog() {
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(requireContext());
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.guest_dialog, null);
        builder.setView(dialogView);

        // Get views
        Button btnDismiss = dialogView.findViewById(R.id.btnDismiss);
        Button btnSignIn = dialogView.findViewById(R.id.btnSignIn);

        android.app.AlertDialog dialog = builder.create();

        btnDismiss.setOnClickListener(v -> {
            dialog.dismiss();
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putBoolean("rankingDialogShown", true);
            editor.apply();
        });

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




    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (profileUpdateReceiver != null) {
            requireActivity().unregisterReceiver(profileUpdateReceiver);
        }
    }

    private void loadLeaderboard() {
        progressRanking.setVisibility(View.VISIBLE);
        recyclerLeaderboard.setVisibility(View.GONE);
        podiumLayout.setVisibility(View.GONE);
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("leaderboard");

        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                userList.clear();
                for (DataSnapshot child : snapshot.getChildren()) {
                    UserScore user = child.getValue(UserScore.class);
                    if (user != null) {
                        user.setUid(child.getKey());
                        userList.add(user);
                    }
                }

                Collections.sort(userList, (u1, u2) -> {
                    if (u2.getCorrectAnswers() != u1.getCorrectAnswers()) {
                        return u2.getCorrectAnswers() - u1.getCorrectAnswers();
                    } else if (u1.getTotalTime() != u2.getTotalTime()) {
                        return Long.compare(u1.getTotalTime(), u2.getTotalTime());
                    } else {
                        return Long.compare(u1.getTimestamp(), u2.getTimestamp());
                    }
                });

                boolean isGuest = sharedPreferences.getBoolean("isGuest", false);
                String offlineName = sharedPreferences.getString("name", null);
                String offlineImage = sharedPreferences.getString("profileImage", null);
                String currentUid = firebaseAuth.getCurrentUser() != null ? firebaseAuth.getCurrentUser().getUid() : "";

                // podium
                if (userList.size() > 0)
                    setPodium(userList.get(0), imgFirst, tvFirstName, tvFirstPoints, offlineName, offlineImage, currentUid, isGuest);
                if (userList.size() > 1)
                    setPodium(userList.get(1), imgSecond, tvSecondName, tvSecondPoints, offlineName, offlineImage, currentUid, isGuest);
                if (userList.size() > 2)
                    setPodium(userList.get(2), imgThird, tvThirdName, tvThirdPoints, offlineName, offlineImage, currentUid, isGuest);

                List<UserScore> rest = userList.size() > 3 ? userList.subList(3, userList.size()) : new ArrayList<>();
                adapter = new LeaderboardAdapter(getContext(), rest);
                recyclerLeaderboard.setAdapter(adapter);

                progressRanking.setVisibility(View.GONE);   // hide progress bar
                recyclerLeaderboard.setVisibility(View.VISIBLE); // show list
                podiumLayout.setVisibility(View.VISIBLE);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                progressRanking.setVisibility(View.GONE);   // hide progress bar on error
                recyclerLeaderboard.setVisibility(View.VISIBLE);
                podiumLayout.setVisibility(View.VISIBLE);
            }
        });
    }

    private void setPodium(UserScore user, ImageView img, TextView tvName, TextView tvPoints,
                           String offlineName, String offlineImage, String currentUid, boolean isGuest) {

        if (isGuest && offlineName != null && user.getUid().equals(currentUid)) {
            tvName.setText(offlineName);
        } else if (user.getName() != null) {
            tvName.setText(user.getName());
        } else {
            tvName.setText("User");
        }

        if (isGuest && offlineImage != null && user.getUid().equals(currentUid)) {
            Glide.with(this).load(offlineImage).circleCrop().into(img);
        } else if (user.getProfileImage() != null && !user.getProfileImage().isEmpty()) {
            Glide.with(this).load(user.getProfileImage()).circleCrop().into(img);
        } else {
            setDefaultProfileImage(tvName.getText().toString(), img);
        }

        tvPoints.setText(user.getCorrectAnswers() + " pts");
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
}
