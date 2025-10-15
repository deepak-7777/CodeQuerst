package com.vmpk.codequerst.Fragment;

import android.content.res.Configuration;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ScrollView;
import androidx.fragment.app.Fragment;
import com.vmpk.codequerst.R;

public class LevelFragment extends Fragment {

    public static LevelFragment newInstance() {
        return new LevelFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_level, container, false);

        View root = view.findViewById(R.id.levelRoot);

        int nightModeFlags =
                getResources().getConfiguration().uiMode &
                        Configuration.UI_MODE_NIGHT_MASK;

        if (nightModeFlags == Configuration.UI_MODE_NIGHT_YES) {
            root.setBackgroundResource(R.drawable.black);
        } else {
            root.setBackgroundResource(R.drawable.whitequiz);
        }

        // 🟢 Scroll automatically to bottom (Level 1 visible first)
        ScrollView scrollView = view.findViewById(R.id.scrollViewLevels);
        if (scrollView != null) {
            scrollView.post(() -> scrollView.fullScroll(View.FOCUS_DOWN));
        }

        return view;
    }
}
