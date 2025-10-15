package com.vmpk.codequerst.Fragment;

import android.content.res.Configuration;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.vmpk.codequerst.Custom.RankingListFragment;
import com.vmpk.codequerst.R;

public class RankingFragment extends Fragment {

    private TabLayout tabLayout;
    private ViewPager2 viewPager;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_ranking, container, false);

        tabLayout = view.findViewById(R.id.tabLayout);
        viewPager = view.findViewById(R.id.viewPagerRanking);

        // Adapter
        viewPager.setAdapter(new RankingPagerAdapter(requireActivity()));

        // TabLayout + ViewPager2 linking
        new TabLayoutMediator(tabLayout, viewPager, (tab, position) -> {
            tab.setText(position == 0 ? "All Time" : "Weekly");

            // Set drawable background for each tab
            if (position == 0) {
                tab.view.setBackgroundResource(R.drawable.tab_alltime_bg);
            } else {
                tab.view.setBackgroundResource(R.drawable.tab_weekly_bg);
            }
        }).attach();

        // Tab selection listener to handle drawable state changes
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                updateTabBackgrounds(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                updateTabBackgrounds(tabLayout.getSelectedTabPosition());
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {}
        });

        // Night mode background
        int nightModeFlags = getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK;
        if (nightModeFlags == Configuration.UI_MODE_NIGHT_YES) view.setBackgroundResource(R.drawable.black);
        else view.setBackgroundResource(R.drawable.whitequiz);

        return view;
    }

    private void updateTabBackgrounds(int selectedPosition) {
        for (int i = 0; i < tabLayout.getTabCount(); i++) {
            if (i == selectedPosition) continue; // selected tab already has drawable
            if (i == 0) tabLayout.getTabAt(i).view.setBackgroundResource(R.drawable.tab_alltime_bg);
            else tabLayout.getTabAt(i).view.setBackgroundResource(R.drawable.tab_weekly_bg);
        }
    }

    private static class RankingPagerAdapter extends androidx.viewpager2.adapter.FragmentStateAdapter {

        public RankingPagerAdapter(@NonNull androidx.fragment.app.FragmentActivity fa) { super(fa); }
        public RankingPagerAdapter(@NonNull Fragment fragment) { super(fragment); }

        @NonNull
        @Override
        public Fragment createFragment(int position) {
            // weekly = true for position 1
            return new RankingListFragment(position == 1);
        }

        @Override
        public int getItemCount() { return 2; }
    }
}
