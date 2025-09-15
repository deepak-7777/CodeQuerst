package com.example.codequerst.Activity;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.Toolbar;
import com.example.codequerst.R;

public class MainActivity extends AppCompatActivity {

    Toolbar toolbar;
    LinearLayout noConnectionLayout, contentLayout;
    LinearLayout c, cpp, python, java, kotlin, javaScript;
    Button retryButton;
    Button gotoprofile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        toolbar = findViewById(R.id.Toolbar);
        noConnectionLayout = findViewById(R.id.noConnectionLayout);
        contentLayout = findViewById(R.id.contentLayout);
        retryButton = findViewById(R.id.retryButton);
        c = findViewById(R.id.c );
        cpp= findViewById(R.id.cpp);
        python = findViewById(R.id.python);
        java = findViewById(R.id.java);
        kotlin = findViewById(R.id.kotlin);
        javaScript= findViewById(R.id.javaScript);
        gotoprofile = findViewById(R.id.gotoprofile);

        gotoprofile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, ProfileActivity.class));
            }
        });

        ///     status bar fit in toolbar  and status bar color related
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.getDecorView().setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
            window.setStatusBarColor(Color.TRANSPARENT);
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            View decorView = getWindow().getDecorView();
            int flags = decorView.getSystemUiVisibility();

            if (isDarkModeOn()) {
                // Dark Mode → white icons
                decorView.setSystemUiVisibility(flags & ~View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
            } else {
                // Light Mode → black icons
                decorView.setSystemUiVisibility(flags | View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
            }
        }

        c.setOnClickListener(v -> startActivity(new Intent(MainActivity.this, CActivity.class)));
        cpp.setOnClickListener(v -> startActivity(new Intent(MainActivity.this, CppActivity.class)));
        python.setOnClickListener(v -> startActivity(new Intent(MainActivity.this, PythonActivity.class)));
        java.setOnClickListener(v -> startActivity(new Intent(MainActivity.this, JavaActivity.class)));
        kotlin.setOnClickListener(v -> startActivity(new Intent(MainActivity.this, KotlinActivity.class)));
        javaScript.setOnClickListener(v -> startActivity(new Intent(MainActivity.this, JavaScriptActivity.class)));

        ///  toolbar name
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Coding Quiz");
        }

        // Internet check karo
        checkInternet();

        // Retry button click
        retryButton.setOnClickListener(v -> checkInternet());
    }

    // Internet check karne ka method
    private void checkInternet() {
        if (isConnected()) {
            noConnectionLayout.setVisibility(View.GONE);
            contentLayout.setVisibility(View.VISIBLE);
            toolbar.setVisibility(View.VISIBLE);
        } else {
            noConnectionLayout.setVisibility(View.VISIBLE);
            contentLayout.setVisibility(View.GONE);
            toolbar.setVisibility(View.GONE);
        }
    }

    // Connectivity check
    private boolean isConnected() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        return activeNetwork != null && activeNetwork.isConnected();
    }

    // Menu inflate karna
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.my_menu, menu);
        return true;
    }

    // Menu item click handle karna
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.share) {
            Intent shareIntent = new Intent(Intent.ACTION_SEND);
            shareIntent.setType("text/plain");
            String shareMessage = "Check out this amazing Quiz App! \n\nCurrently not on Play Store. Contact me to get the APK.";
            shareIntent.putExtra(Intent.EXTRA_TEXT, shareMessage);

            startActivity(Intent.createChooser(shareIntent, "Share via"));
            return true;
        } else if (id == R.id.feedback) {
            Intent intent = new Intent(Intent.ACTION_SENDTO);
            intent.setData(android.net.Uri.parse("mailto:"));
            intent.putExtra(Intent.EXTRA_EMAIL, new String[]{"deepak0797dk@gmail.com"});
            intent.putExtra(Intent.EXTRA_SUBJECT, "Feedback for Quiz App");
            intent.putExtra(Intent.EXTRA_TEXT, "Hello, I want to give feedback...");

            try {
                startActivity(Intent.createChooser(intent, "Send Feedback"));
            } catch (android.content.ActivityNotFoundException ex) {
                Toast.makeText(this, "No email app found", Toast.LENGTH_SHORT).show();
            }
            return true;
        } else if (id == R.id.setting) {
            startActivity(new Intent(MainActivity.this, SettingActivity.class));
            return true;
        } else if (id == R.id.Mode) {
            // Toggle Mode
            if (isDarkModeOn()) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
            } else {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
            }
            invalidateOptionsMenu(); // menu title refresh
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    // Menu ke title ko update karne ke liye
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        MenuItem modeItem = menu.findItem(R.id.Mode);
        if (isDarkModeOn()) {
            modeItem.setTitle("Light Mode");
        } else {
            modeItem.setTitle("Dark Mode");
        }
        return super.onPrepareOptionsMenu(menu);
    }

    private boolean isDarkModeOn() {
        int nightModeFlags =
                getResources().getConfiguration().uiMode &
                        Configuration.UI_MODE_NIGHT_MASK;
        return nightModeFlags == Configuration.UI_MODE_NIGHT_YES;
    }

}