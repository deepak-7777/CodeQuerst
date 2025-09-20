package com.example.codequerst.Fragment;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.codequerst.Activity.HomeActivity;
import com.example.codequerst.Adapter.LeaderboardAdapter;
import com.example.codequerst.Model.UserScore;
import com.example.codequerst.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class RankingFragment extends Fragment {

    private RecyclerView recyclerLeaderboard;
    private LeaderboardAdapter adapter;
    private List<UserScore> userList = new ArrayList<>();

    private TextView tvFirstName, tvFirstPoints, tvSecondName, tvSecondPoints, tvThirdName, tvThirdPoints;
    private ImageView imgFirst, imgSecond, imgThird;

    private FirebaseAuth firebaseAuth;
    private BroadcastReceiver profileUpdateReceiver;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_ranking, container, false);

        firebaseAuth = FirebaseAuth.getInstance();

        recyclerLeaderboard = view.findViewById(R.id.recyclerLeaderboard);
        recyclerLeaderboard.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new LeaderboardAdapter(getContext(), new ArrayList<>());
        recyclerLeaderboard.setAdapter(adapter);

        imgFirst = view.findViewById(R.id.imgFirst);
        tvFirstName = view.findViewById(R.id.tvFirstName);
        tvFirstPoints = view.findViewById(R.id.tvFirstPoints);

        imgSecond = view.findViewById(R.id.imgSecond);
        tvSecondName = view.findViewById(R.id.tvSecondName);
        tvSecondPoints = view.findViewById(R.id.tvSecondPoints);

        imgThird = view.findViewById(R.id.imgThird);
        tvThirdName = view.findViewById(R.id.tvThirdName);
        tvThirdPoints = view.findViewById(R.id.tvThirdPoints);

        loadLeaderboard();

        // Broadcast receiver for profile updates
        profileUpdateReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                loadLeaderboard(); // refresh leaderboard & podium
            }
        };
        ContextCompat.registerReceiver(requireActivity(), profileUpdateReceiver, new IntentFilter("PROFILE_UPDATED"), ContextCompat.RECEIVER_NOT_EXPORTED);

        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (profileUpdateReceiver != null) {
            requireActivity().unregisterReceiver(profileUpdateReceiver);
        }
    }

    private void loadLeaderboard() {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("leaderboard");

        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                userList.clear();
                for (DataSnapshot child : snapshot.getChildren()) {
                    UserScore user = child.getValue(UserScore.class);
                    if (user != null) {
                        user.setUid(child.getKey()); // make sure you have setUid() in UserScore
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

                SharedPreferences prefs = requireContext().getSharedPreferences("user_prefs", Context.MODE_PRIVATE);
                String offlineName = prefs.getString("name", null);
                String offlineImage = prefs.getString("profileImage", null);
                String currentUid = firebaseAuth.getCurrentUser() != null ? firebaseAuth.getCurrentUser().getUid() : "";

                // podium
                if (userList.size() > 0) setPodium(userList.get(0), imgFirst, tvFirstName, tvFirstPoints, offlineName, offlineImage, currentUid);
                if (userList.size() > 1) setPodium(userList.get(1), imgSecond, tvSecondName, tvSecondPoints, offlineName, offlineImage, currentUid);
                if (userList.size() > 2) setPodium(userList.get(2), imgThird, tvThirdName, tvThirdPoints, offlineName, offlineImage, currentUid);

                List<UserScore> rest = userList.size() > 3 ? userList.subList(3, userList.size()) : new ArrayList<>();
                adapter = new LeaderboardAdapter(getContext(), rest);
                recyclerLeaderboard.setAdapter(adapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) { }
        });
    }

    private void setPodium(UserScore user, ImageView img, TextView tvName, TextView tvPoints,
                           String offlineName, String offlineImage, String currentUid) {
        if (offlineName != null && user.getUid().equals(currentUid)) {
            tvName.setText(offlineName);
        } else {
            tvName.setText(user.getName());
        }

        if (offlineImage != null && user.getUid().equals(currentUid)) {
            Glide.with(this).load(offlineImage).circleCrop().into(img);
        } else if (user.getProfileImage() != null && !user.getProfileImage().isEmpty()) {
            Glide.with(this).load(user.getProfileImage()).circleCrop().into(img);
        } else {
            img.setImageResource(R.drawable.ic_person);
        }

        tvPoints.setText(user.getCorrectAnswers() + " pts");
    }
}
