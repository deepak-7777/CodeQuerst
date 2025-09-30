package com.vmpk.codequerst.Fragment;

import android.content.*;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.vmpk.codequerst.Activity.AboutActivity;
import com.vmpk.codequerst.Activity.HomeActivity;
import com.vmpk.codequerst.Activity.PrivacyActivity;
import com.vmpk.codequerst.Activity.ProfileActivity;
import com.vmpk.codequerst.R;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.*;

public class SettingFragment extends Fragment {

    private ShapeableImageView settingImage;
    private TextView settingName, settingAbout, reportText;
    private Switch reportSwitch;
    private SharedPreferences sharedPreferences;
    LinearLayout privacyApp, aboutApp, shareApp, feedbackApp;

    private BroadcastReceiver profileUpdateReceiver;
    private boolean isDataLoaded = false; // load once from cache

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_setting, container, false);

        // Init views
        settingImage = view.findViewById(R.id.settingImage);
        settingName = view.findViewById(R.id.settingName);
        settingAbout = view.findViewById(R.id.settingAbout);
        reportSwitch = view.findViewById(R.id.reportSwitch);
        reportText = view.findViewById(R.id.reportText);
        aboutApp = view.findViewById(R.id.aboutApp);
        privacyApp = view.findViewById(R.id.privacyApp);
        shareApp = view.findViewById(R.id.shareApp);
        feedbackApp = view.findViewById(R.id.feedbackApp);

        sharedPreferences = getActivity().getSharedPreferences("user_prefs", Context.MODE_PRIVATE);

        setupThemeSwitch();
        setupClickListeners();

        // Load cached profile first (no flicker)
        if (!isDataLoaded) {
            loadProfileFromCache();
            isDataLoaded = true;
        }

        // BroadcastReceiver to reload only on actual profile changes
        profileUpdateReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                loadProfileFromFirebase();
            }
        };

        return view;
    }

    private void setupThemeSwitch() {
        int currentNightMode = getResources().getConfiguration().uiMode
                & android.content.res.Configuration.UI_MODE_NIGHT_MASK;

        if(currentNightMode == android.content.res.Configuration.UI_MODE_NIGHT_YES){
            reportSwitch.setChecked(true);
            reportText.setText("Dark Mode");
        } else {
            reportSwitch.setChecked(false);
            reportText.setText("Light Mode");
        }

        reportSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if(isChecked){
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                reportText.setText("Dark Mode");
            } else {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                reportText.setText("Light Mode");
            }

            if(getActivity() != null){
                getActivity().finish();
                startActivity(new Intent(getActivity(), HomeActivity.class)
                        .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK));
            }
        });
    }

    private void setupClickListeners() {


        settingImage.setOnClickListener(v -> startActivity(new Intent(getActivity(), ProfileActivity.class)));

        privacyApp.setOnClickListener(v -> startActivity(new Intent(getActivity(), PrivacyActivity.class)));
        aboutApp.setOnClickListener(v -> startActivity(new Intent(getActivity(), AboutActivity.class)));

        shareApp.setOnClickListener(v -> {
            Intent shareIntent = new Intent(Intent.ACTION_SEND);
            shareIntent.setType("text/plain");
            shareIntent.putExtra(Intent.EXTRA_TEXT, "Check out this amazing Quiz App! Contact me to get the APK.");
            startActivity(Intent.createChooser(shareIntent, "Share via"));
        });

        feedbackApp.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_SENDTO);
            intent.setData(android.net.Uri.parse("mailto:"));
            intent.putExtra(Intent.EXTRA_EMAIL, new String[]{"globalxwar0@gmail.com"});
            intent.putExtra(Intent.EXTRA_SUBJECT, "Feedback for Quiz App");
            intent.putExtra(Intent.EXTRA_TEXT, "Hello, I want to give feedback...");
            try {
                startActivity(Intent.createChooser(intent, "Send Feedback"));
            } catch (android.content.ActivityNotFoundException ex) {
                Toast.makeText(getActivity(), "No email app found", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadProfileFromCache() {
        String name = sharedPreferences.getString("name", "User");
        String about = sharedPreferences.getString("about", "Welcome Back");
        String imageUrl = sharedPreferences.getString("profileImage", "");

        settingName.setText(name);
        settingAbout.setText(about);

        if (!imageUrl.isEmpty()) {
            Glide.with(getActivity())
                    .load(imageUrl)
                    .circleCrop()
                    .into(settingImage);
        } else {
            setDefaultProfileImage(name);
        }
    }

    private void loadProfileFromFirebase() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null || !isNetworkAvailable()) return;

        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("users").child(user.getUid());
        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                if (!isAdded()) return;
                if (!snapshot.exists()) return;

                String name = snapshot.child("name").getValue(String.class);
                String about = snapshot.child("about").getValue(String.class);
                String imageUrl = snapshot.child("profileImage").getValue(String.class);

                if (name != null) settingName.setText(name);
                if (about != null) settingAbout.setText(about);

                if (imageUrl != null && !imageUrl.isEmpty()) {
                    Glide.with(getActivity())
                            .load(imageUrl)
                            .circleCrop()
                            .into(settingImage);
                } else if (name != null) {
                    setDefaultProfileImage(name);
                }

                // Update cache
                SharedPreferences.Editor editor = sharedPreferences.edit();
                if (name != null) editor.putString("name", name);
                if (about != null) editor.putString("about", about);
                if (imageUrl != null) editor.putString("profileImage", imageUrl);
                editor.apply();
            }

            @Override
            public void onCancelled(DatabaseError error) {}
        });
    }

    private void setDefaultProfileImage(String fullName) {
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
        paint.setTextSize(48f);
        paint.setTextAlign(Paint.Align.CENTER);
        Rect bounds = new Rect();
        paint.getTextBounds(initials, 0, initials.length(), bounds);
        float x = size / 2f;
        float y = size / 2f - (bounds.top + bounds.bottom) / 2f;
        canvas.drawText(initials, x, y, paint);

        settingImage.setImageBitmap(bitmap);
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager cm = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo network = cm.getActiveNetworkInfo();
        return network != null && network.isConnected();
    }

//    @Override
//    public void onResume() {
//        super.onResume();
//        if (getActivity() != null) {
//            getActivity().registerReceiver(profileUpdateReceiver, new IntentFilter("PROFILE_UPDATED"));
//        }
//    }

    @Override
    public void onResume() {
        super.onResume();
        if (getActivity() != null) {
            IntentFilter filter = new IntentFilter("PROFILE_UPDATED");
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.S) {
                getActivity().registerReceiver(profileUpdateReceiver, filter, Context.RECEIVER_NOT_EXPORTED);
            } else {
                getActivity().registerReceiver(profileUpdateReceiver, filter);
            }
        }
    }


    @Override
    public void onPause() {
        super.onPause();
        if (getActivity() != null) {
            getActivity().unregisterReceiver(profileUpdateReceiver);
        }
    }
}
