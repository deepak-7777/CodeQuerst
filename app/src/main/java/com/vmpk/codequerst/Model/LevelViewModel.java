package com.vmpk.codequerst.Model;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.*;

import java.util.HashMap;
import java.util.Map;

public class LevelViewModel extends AndroidViewModel {

    private static final int TOTAL_LEVELS = 100;

    private final MutableLiveData<Map<Integer, Boolean>> levelStatusLive = new MutableLiveData<>();
    private final MutableLiveData<Map<Integer, Integer>> levelStarsLive = new MutableLiveData<>();
    private boolean loaded = false;
    private DatabaseReference userRef;
    private ValueEventListener progressListener;
    private final SharedPreferences prefs;

    public LevelViewModel(@NonNull Application application) {
        super(application);
        prefs = application.getSharedPreferences("levelCache", Context.MODE_PRIVATE);
    }

    public LiveData<Map<Integer, Boolean>> getLevelStatus() {
        if (!loaded) loadLevels();
        return levelStatusLive;
    }

    public LiveData<Map<Integer, Integer>> getLevelStars() {
        if (!loaded) loadLevels();
        return levelStarsLive;
    }

    private void loadLevels() {
        Map<Integer, Boolean> cacheStatus = new HashMap<>();
        Map<Integer, Integer> cacheStars = new HashMap<>();

        for (int i = 1; i <= TOTAL_LEVELS; i++) {
            boolean unlocked = prefs.getBoolean("level_" + i, i == 1);
            int star = prefs.getInt("level_" + i + "_stars", 0);
            cacheStatus.put(i, unlocked);
            cacheStars.put(i, star);
        }

        levelStatusLive.setValue(cacheStatus);
        levelStarsLive.setValue(cacheStars);

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            userRef = FirebaseDatabase.getInstance().getReference("userProgress").child(user.getUid());
            userRef.keepSynced(true);

            progressListener = new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot snapshot) {
                    Map<Integer, Boolean> statusMap = new HashMap<>();
                    Map<Integer, Integer> starsMap = new HashMap<>();
                    SharedPreferences.Editor editor = prefs.edit();

                    for (int i = 1; i <= TOTAL_LEVELS; i++) {
                        Boolean completed = snapshot.child("level_" + i).child("complete").getValue(Boolean.class);
                        Integer star = snapshot.child("level_" + i).child("stars").getValue(Integer.class);

                        boolean val = completed != null && completed;
                        int starVal = (star != null ? star : 0);

                        statusMap.put(i, val);
                        starsMap.put(i, starVal);

                        editor.putBoolean("level_" + i, val);
                        editor.putInt("level_" + i + "_stars", starVal);
                    }
                    editor.apply();

                    levelStatusLive.setValue(statusMap);
                    levelStarsLive.setValue(starsMap);
                }

                @Override
                public void onCancelled(DatabaseError error) {}
            };

            userRef.addValueEventListener(progressListener);
        }

        loaded = true;
    }

    // ✅ Mark level complete and save star only if not already saved
    public void markLevelComplete(int levelNumber, int newStars) {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) return;

        DatabaseReference ref = FirebaseDatabase.getInstance()
                .getReference("userProgress")
                .child(user.getUid())
                .child("level_" + levelNumber);

        ref.child("stars").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Integer existingStars = snapshot.getValue(Integer.class);
                if (existingStars == null) existingStars = 0;

                if (existingStars == 0) {
                    Map<String, Object> updates = new HashMap<>();
                    updates.put("complete", true);
                    updates.put("stars", newStars);
                    updates.put("email", user.getEmail());
                    ref.updateChildren(updates);
                }

                // 🔹 Update LiveData and SharedPreferences
                Map<Integer, Boolean> statusMap = levelStatusLive.getValue();
                if (statusMap == null) statusMap = new HashMap<>();
                statusMap.put(levelNumber, true);
                levelStatusLive.setValue(statusMap);
                prefs.edit().putBoolean("level_" + levelNumber, true).apply();

                Map<Integer, Integer> starsMap = levelStarsLive.getValue();
                if (starsMap == null) starsMap = new HashMap<>();
                starsMap.put(levelNumber, existingStars == 0 ? newStars : existingStars);
                levelStarsLive.setValue(starsMap);
                prefs.edit().putInt("level_" + levelNumber + "_stars", starsMap.get(levelNumber)).apply();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        if (userRef != null && progressListener != null) {
            userRef.removeEventListener(progressListener);
        }
    }
}
