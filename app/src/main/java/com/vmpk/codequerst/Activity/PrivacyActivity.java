package com.vmpk.codequerst.Activity;


import android.content.res.Configuration;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;

import android.text.Html;
import android.view.View;
import android.view.Window;
import android.widget.TextView;
import com.vmpk.codequerst.R;

public class PrivacyActivity extends AppCompatActivity {
    TextView privacyText, backPrivacy;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_privacy);

        privacyText = findViewById(R.id.privacyText);
        backPrivacy = findViewById(R.id.backPrivacy);

        backPrivacy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        String policy = "<b>This Privacy Policy</b> describes how we collect, use, and protect your information when you use our Quiz App. By using the app, you agree to the terms outlined in this policy.<br><br>" +

                "<b>Information We Collect</b><br><br>" +
                "Personal Information: We do not require you to provide personal details such as your name, email, or phone number unless explicitly requested for features like leaderboard or account creation.<br><br>" +
                "Non-Personal Information: We may collect device-related data such as app usage, performance statistics, and interaction patterns to improve the user experience.<br><br>" +
                "Third-Party Services: If our app integrates with services such as Google Play Services or AdMob, these may collect certain information as governed by their own privacy policies.<br><br>" +

                "<b>How We Use Your Information</b><br><br>" +
                "• Enhance the quiz experience and provide accurate performance tracking.<br>" +
                "• Improve app features and fix technical issues.<br>" +
                "• Display relevant ads (if applicable).<br>" +
                "• Ensure a smooth and personalized user experience.<br><br>" +

                "<b>Data Sharing and Security</b><br><br>" +
                "We do not sell, trade, or rent your information to third parties. Any data collected is used solely for app functionality and improvements. We implement reasonable security measures to protect your data, but please note that no digital platform can guarantee 100% security.<br><br>" +

                "<b>Children’s Privacy</b><br><br>" +
                "Our Quiz App is suitable for general audiences. We do not knowingly collect personal information from children under 13. If you believe we have unintentionally collected such information, please contact us for removal.<br><br>" +

                "<b>Changes to This Policy</b><br><br>" +
                "We may update this Privacy Policy from time to time. Users will be notified of significant changes within the app.";

        privacyText.setText(Html.fromHtml(policy));


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
    }

    ///     status bar fit in toolbar  and status bar color related
    private boolean isDarkModeOn() {
        int nightModeFlags =
                getResources().getConfiguration().uiMode &
                        Configuration.UI_MODE_NIGHT_MASK;
        return nightModeFlags == Configuration.UI_MODE_NIGHT_YES;
    }
}
