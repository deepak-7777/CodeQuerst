package com.vmpk.codequerst.Fragment;


import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.*;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.AppCompatButton;
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
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.vmpk.codequerst.Activity.AboutActivity;
import com.vmpk.codequerst.Activity.CoinsActivity;
import com.vmpk.codequerst.Activity.HomeActivity;
import com.vmpk.codequerst.Activity.LoginActivity;
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
    private FirebaseAuth firebaseAuth;
    private boolean isGuest;
    private GoogleSignInClient gsc;
    private SharedPreferences sharedPreferences;
    private LinearLayout privacyApp, aboutApp, shareApp, feedbackApp, rateApp, logoutApp,coinStore;

    private BroadcastReceiver profileUpdateReceiver;
    private boolean isDataLoaded = false; // load once from cache


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_setting, container, false);

        coinStore = view.findViewById(R.id.coinStore);
        logoutApp = view.findViewById(R.id.logoutApp);
        rateApp = view.findViewById(R.id.rateApp);
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

        firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser user = firebaseAuth.getCurrentUser();
        isGuest = sharedPreferences.getBoolean("isGuest", false);

        // BroadcastReceiver to reload only on actual profile changes
        profileUpdateReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
//                loadProfileFromFirebase();
                loadProfileFromCache();
            }
        };

        coinStore.setOnClickListener(v -> startActivity(new Intent(getActivity(), CoinsActivity.class)));

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
            String playStoreLink =
                    "https://play.google.com/store/apps/details?id="
                            + requireContext().getPackageName();
            String shareMessage =
                    "🚀 Check out this amazing Quiz App!\n" +
                            "🎯 Test your knowledge & challenge friends.\n\n" +
                            "👉 Download now:\n" + playStoreLink;
            shareIntent.putExtra(Intent.EXTRA_TEXT, shareMessage);
            startActivity(Intent.createChooser(shareIntent, "Share via"));
        });


        rateApp.setOnClickListener(v -> {
            try {
                startActivity(new Intent(Intent.ACTION_VIEW,
                        Uri.parse("market://details?id=" + v.getContext().getPackageName())));
            } catch (android.content.ActivityNotFoundException e) {
                startActivity(new Intent(Intent.ACTION_VIEW,
                        Uri.parse("https://play.google.com/store/apps/details?id=" + v.getContext().getPackageName())));
            }
        });

        feedbackApp.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_SENDTO);
            intent.setData(android.net.Uri.parse("mailto:"));
            intent.putExtra(Intent.EXTRA_EMAIL, new String[]{"vmpk77@gmail.com"});
            intent.putExtra(Intent.EXTRA_SUBJECT, "Feedback for Quiz App");
            intent.putExtra(Intent.EXTRA_TEXT, "Hello, I want to give feedback...");
            try {
                startActivity(Intent.createChooser(intent, "Send Feedback"));
            } catch (android.content.ActivityNotFoundException ex) {
                Toast.makeText(getActivity(), "No email app found", Toast.LENGTH_SHORT).show();
            }
        });

        logoutApp.setOnClickListener(v -> showLogoutDialog());
    }

    private void showLogoutDialog() {
        // Create Dialog
        Dialog dialog = new Dialog(requireActivity());
        dialog.setContentView(R.layout.item_logout);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        // Find Buttons
        AppCompatButton btnNo = dialog.findViewById(R.id.logoutDismiss);
        AppCompatButton btnYes = dialog.findViewById(R.id.logoutSignIn);

        // "No" → Just Close Dialog
        btnNo.setOnClickListener(v -> dialog.dismiss());

        // "Yes" → Perform Logout
        btnYes.setOnClickListener(v -> {
            dialog.dismiss(); // Close dialog
            performLogout();  // Logout function
        });

        dialog.show();
    }

    private void performLogout() {
        if (isGuest) {
            // Clear guest data from SharedPreferences
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.remove("name");
            editor.remove("email");
            editor.remove("phone");
            editor.remove("about");
            editor.putBoolean("isGuest", false);
            editor.apply();

            FirebaseAuth.getInstance().signOut();

            // Go to LoginActivity
            Intent intent = new Intent(requireActivity(), LoginActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            requireActivity().finish();
        } else {
            // Firebase + Google Sign Out
            FirebaseAuth.getInstance().signOut();

            // Initialize GoogleSignInClient if null
            if (gsc == null) {
                gsc = GoogleSignIn.getClient(requireActivity(),
                        new com.google.android.gms.auth.api.signin.GoogleSignInOptions.Builder(
                                com.google.android.gms.auth.api.signin.GoogleSignInOptions.DEFAULT_SIGN_IN)
                                .requestEmail()
                                .build());
            }

            gsc.signOut().addOnCompleteListener(task -> {
                Intent intent = new Intent(requireActivity(), LoginActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                requireActivity().finish();
            });
        }
    }



    private void loadProfileFromCache() {
        boolean isGuest = sharedPreferences.getBoolean("isGuest", false);
        String name = sharedPreferences.getString("name", isGuest ? "Guest" : "User");
        String about = sharedPreferences.getString("about",
                isGuest ? "Welcome Guest" : "Welcome Back");
        String imageUrl = sharedPreferences.getString("profileImage", "");

        settingName.setText(name);
        settingAbout.setText(about);

        // 🔥 AVATAR / IMAGE HANDLING
        if (imageUrl.startsWith("avatar_")) {

            int resId = getResources().getIdentifier(
                    imageUrl,
                    "drawable",
                    requireContext().getPackageName()
            );

            if (resId != 0) {
                settingImage.setImageResource(resId);
            } else {
                setDefaultProfileImage(name);
            }

        }
        // 🔥 NORMAL IMAGE URL (Firebase / Gallery)
        else if (!imageUrl.isEmpty() && !isGuest) {

            Glide.with(requireActivity())
                    .load(imageUrl)
                    .placeholder(R.drawable.ic_person)
                    .error(R.drawable.ic_person)
                    .circleCrop()
                    .into(settingImage);

        }
        // 🔥 GUEST / EMPTY CASE
        else {
            setDefaultProfileImage(name);
            Glide.with(requireActivity()).clear(settingImage);
        }
    }



    private void loadProfileFromFirebase() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        boolean isGuest = sharedPreferences.getBoolean("isGuest", false);

        if (isGuest || user == null) {
            // 🔹 Guest mode: skip Firebase load, just show initials
            loadProfileFromCache();
            return;
        }

        DatabaseReference userRef = FirebaseDatabase.getInstance()
                .getReference("users")
                .child(user.getUid());

        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (!isAdded()) return;
                if (!snapshot.exists()) {
                    loadProfileFromCache();
                    return;
                }

                String name = snapshot.child("name").getValue(String.class);
                String about = snapshot.child("about").getValue(String.class);
                String imageUrl = snapshot.child("profileImage").getValue(String.class);

                settingName.setText(name != null ? name : "User");
                settingAbout.setText(about != null ? about : "Welcome Back");

                if (imageUrl != null && !imageUrl.isEmpty()) {
                    Glide.with(requireActivity())
                            .load(imageUrl)
                            .placeholder(R.drawable.ic_person)
                            .error(R.drawable.ic_person)
                            .circleCrop()
                            .into(settingImage);
                } else {
                    setDefaultProfileImage(name);
                }

                // 🔹 Cache update
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString("name", name != null ? name : "User");
                editor.putString("about", about != null ? about : "Welcome Back");
                editor.putString("profileImage", imageUrl != null ? imageUrl : "");
                editor.putBoolean("isGuest", false);
                editor.apply();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                loadProfileFromCache();
            }
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
