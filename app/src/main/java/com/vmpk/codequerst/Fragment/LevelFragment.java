package com.vmpk.codequerst.Fragment;

import android.content.res.Configuration;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.vmpk.codequerst.Adapter.LevelAdapter;
import com.vmpk.codequerst.Model.LevelViewModel;
import com.vmpk.codequerst.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class LevelFragment extends Fragment {

    private RecyclerView recyclerView;
    private LevelAdapter adapter;
    private List<Integer> levelList = new ArrayList<>();
    private LevelViewModel viewModel;

    private static final int TOTAL_LEVELS = 100;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_level, container, false);

        recyclerView = view.findViewById(R.id.rvLevels);
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 3));

        for (int i = 1; i <= TOTAL_LEVELS; i++) levelList.add(i);

        adapter = new LevelAdapter(getContext(), levelList, new HashMap<>());
        recyclerView.setAdapter(adapter);

        viewModel = new ViewModelProvider(requireActivity()).get(LevelViewModel.class);

        // Observe level completion
        viewModel.getLevelStatus().observe(getViewLifecycleOwner(), levelStatus -> {
            adapter.updateStatus(levelStatus);
        });

        // Observe stars
        viewModel.getLevelStars().observe(getViewLifecycleOwner(), levelStars -> {
            adapter.setLevelStars(levelStars);
        });

        // Night mode background
        int nightModeFlags = getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK;
        if (nightModeFlags == Configuration.UI_MODE_NIGHT_YES)
            view.setBackgroundResource(R.drawable.black);
        else
            view.setBackgroundResource(R.drawable.whitequiz);

        return view;
    }
}
