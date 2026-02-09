package com.vmpk.codequerst.Activity;

import android.content.res.Configuration;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.vmpk.codequerst.R;

public class CoinsActivity extends AppCompatActivity {

    private LinearLayout coinA, coinB, coinC;
    TextView backPoints;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_coins);

        // Initialize coin pack layouts
        coinA = findViewById(R.id.coinA);
        coinB = findViewById(R.id.coinB);
        coinC = findViewById(R.id.coinC);
        backPoints = findViewById(R.id.backPoints);

        backPoints.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        // Click listeners for coin packs
        coinA.setOnClickListener(v -> startPayment("Small Pack", 50, 80));
        coinB.setOnClickListener(v -> startPayment("Medium Pack", 500, 700));
        coinC.setOnClickListener(v -> startPayment("Large Pack", 700, 1200));

        // 7️ Status bar styling
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
                decorView.setSystemUiVisibility(flags & ~View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
            } else {
                decorView.setSystemUiVisibility(flags | View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
            }
        }
    }

    private boolean isDarkModeOn() {
        int nightModeFlags =
                getResources().getConfiguration().uiMode &
                        Configuration.UI_MODE_NIGHT_MASK;
        return nightModeFlags == Configuration.UI_MODE_NIGHT_YES;
    }

    /**
     * Start payment flow for selected pack
     * @param packName Name of the pack
     * @param price Price in rupees
     * @param coins Coins to add
     */
    private void startPayment(String packName, int price, int coins) {
        // Here you integrate your payment gateway (e.g., Google Play Billing, Razorpay, etc.)
        // For demo, we just show a Toast
//        Toast.makeText(this, "Payment started for " + packName + " (" + price + " ₹)", Toast.LENGTH_SHORT).show();
        Toast.makeText(this, "Currently this plan is not available.", Toast.LENGTH_SHORT).show();

        // After successful payment, add coins to user
        addCoinsToUser(coins);
    }

    /**
     * Add coins to user in Firebase (demo skeleton)
     * @param coins Number of coins to add
     */
    private void addCoinsToUser(int coins) {


//        Toast.makeText(this, coins + " coins added to your account!", Toast.LENGTH_SHORT).show();
    }
}
