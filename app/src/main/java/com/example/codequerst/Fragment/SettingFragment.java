package com.example.codequerst.Fragment;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
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
import com.example.codequerst.Activity.AboutActivity;
import com.example.codequerst.Activity.HomeActivity;
import com.example.codequerst.Activity.PrivacyActivity;
import com.example.codequerst.Activity.ProfileActivity;
import com.example.codequerst.R;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.*;

public class SettingFragment extends Fragment {

    private ShapeableImageView settingImage;
    private TextView settingName, settingAbout, backSetting, reportText;
    private Switch reportSwitch;
    private SharedPreferences sharedPreferences;
    LinearLayout privacyApp, aboutApp, shareApp, feedbackApp;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_setting, container, false);

        // Initialize views
        settingImage = view.findViewById(R.id.settingImage);
        settingName = view.findViewById(R.id.settingName);
        settingAbout = view.findViewById(R.id.settingAbout);
        backSetting = view.findViewById(R.id.backSetting);
        reportSwitch = view.findViewById(R.id.reportSwitch);
        reportText = view.findViewById(R.id.reportText);
        aboutApp = view.findViewById(R.id.aboutApp);
        privacyApp = view.findViewById(R.id.privacyApp);
        shareApp = view.findViewById(R.id.shareApp);
        feedbackApp = view.findViewById(R.id.feedbackApp);

        settingImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), ProfileActivity.class);
                startActivity(intent);
            }
        });

        privacyApp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), PrivacyActivity.class);
                startActivity(intent);
            }
        });

        aboutApp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), AboutActivity.class);
                startActivity(intent);
            }
        });

        shareApp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent shareIntent = new Intent(Intent.ACTION_SEND);
                shareIntent.setType("text/plain");
                String shareMessage = "Check out this amazing Quiz App! \n\nCurrently not on Play Store. Contact me to get the APK.";
                shareIntent.putExtra(Intent.EXTRA_TEXT, shareMessage);

                startActivity(Intent.createChooser(shareIntent, "Share via"));
            }
        });

        feedbackApp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_SENDTO);
                intent.setData(android.net.Uri.parse("mailto:"));
                intent.putExtra(Intent.EXTRA_EMAIL, new String[]{"deepak0797dk@gmail.com"});
                intent.putExtra(Intent.EXTRA_SUBJECT, "Feedback for Quiz App");
                intent.putExtra(Intent.EXTRA_TEXT, "Hello, I want to give feedback...");

                try {
                    startActivity(Intent.createChooser(intent, "Send Feedback"));
                } catch (android.content.ActivityNotFoundException ex) {
                    Toast.makeText(getActivity(), "No email app found", Toast.LENGTH_SHORT).show();
                }
            }
        });

        sharedPreferences = getActivity().getSharedPreferences("user_prefs", Context.MODE_PRIVATE);
        boolean isGuest = sharedPreferences.getBoolean("isGuest", false);

        // Load user data
        if (isGuest) {
            String name = sharedPreferences.getString("name", "Guest User");
            String about = sharedPreferences.getString("about", "Welcome Back");
            String imageUri = sharedPreferences.getString("profileImage", null);

            settingName.setText(name);
            settingAbout.setText(about);

            if (imageUri != null && !imageUri.isEmpty()) {
                Glide.with(getActivity())
                        .load(imageUri)
                        .circleCrop()
                        .into(settingImage);
            } else {
                setDefaultProfileImage(name);
            }
        } else {
            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
            if (user != null) {
                DatabaseReference usersRef = FirebaseDatabase.getInstance().getReference("users").child(user.getUid());
                usersRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot snapshot) {
                        String name = snapshot.child("name").getValue(String.class);
                        String about = snapshot.child("about").getValue(String.class);
                        String imageUrl = snapshot.child("profileImage").getValue(String.class);

                        settingName.setText(name != null ? name : "User");
                        settingAbout.setText(about != null ? about : "Welcome Back");

                        if (imageUrl != null && !imageUrl.isEmpty()) {
                            Glide.with(getActivity())
                                    .load(imageUrl)
                                    .circleCrop()
                                    .into(settingImage);
                        } else {
                            setDefaultProfileImage(name);
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError error) { }
                });
            }
        }

        // Set switch and text according to current mode
        int currentNightMode = getResources().getConfiguration().uiMode
                & android.content.res.Configuration.UI_MODE_NIGHT_MASK;

        if(currentNightMode == android.content.res.Configuration.UI_MODE_NIGHT_YES){
            reportSwitch.setChecked(true);
            reportText.setText("Dark Mode");
        } else {
            reportSwitch.setChecked(false);
            reportText.setText("Light Mode");
        }

        // Switch listener for changing theme
        reportSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                    reportText.setText("Dark Mode");
                } else {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                    reportText.setText("Light Mode");
                }

                // Restart HomeActivity with smooth fade animation
                if(getActivity() != null){
                    getActivity().finish(); // Finish current activity
                    Intent intent = new Intent(getActivity(), HomeActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                    getActivity().overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                }
            }
        });

        // Back button
        backSetting.setOnClickListener(v -> {
            if(getActivity() != null) {
                Intent intent = new Intent(getActivity(), HomeActivity.class);
                startActivity(intent);
                getActivity().finish();
            }
        });

        return view;
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
}
