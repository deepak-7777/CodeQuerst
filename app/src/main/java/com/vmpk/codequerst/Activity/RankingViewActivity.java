package com.vmpk.codequerst.Activity;

import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.bumptech.glide.Glide;
import com.google.android.material.imageview.ShapeableImageView;
import com.vmpk.codequerst.R;

public class RankingViewActivity extends AppCompatActivity {

    private ShapeableImageView profileImage;
    private TextView tvName, tvPoints, tvRank, tvAbout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ranking_view);

        profileImage = findViewById(R.id.rankImage);
        tvName = findViewById(R.id.rankFirstName);
        tvPoints = findViewById(R.id.rankPoint);
        tvRank = findViewById(R.id.rank);
        tvAbout = findViewById(R.id.rankAbout);

        // Get data from intent
        String name = getIntent().getStringExtra("name");
        String points = getIntent().getStringExtra("points");
        String rank = getIntent().getStringExtra("rank");
        String about = getIntent().getStringExtra("about");
        String imageUrl = getIntent().getStringExtra("image");

        tvName.setText(name != null ? name : "User");
        tvPoints.setText(points != null ? points : "0 pts");
        tvRank.setText(rank != null ? rank : "-");
        tvAbout.setText(about != null ? about : "About not available");

        if (imageUrl != null && !imageUrl.isEmpty()) {
            Glide.with(this).load(imageUrl).circleCrop().into(profileImage);
        } else {
            setDefaultProfileImage(name, profileImage);
        }

        // Back button
        findViewById(R.id.backRank).setOnClickListener(v -> finish());

        // status bar styling
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

    private void setDefaultProfileImage(String fullName, ShapeableImageView imageView) {
        if (fullName == null || fullName.isEmpty()) fullName = "U";
        String[] words = fullName.trim().split(" ");
        String initials = "";
        for (String w : words) {
            if (!w.isEmpty() && initials.length() < 2) initials += w.charAt(0);
        }
        initials = initials.toUpperCase();

        int size = 220; // same as your XML frame
        Bitmap bitmap = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        Paint paint = new Paint();
        paint.setAntiAlias(true);
        paint.setColor(0xFF6200EE);
        canvas.drawCircle(size / 2f, size / 2f, size / 2f, paint);
        paint.setColor(0xFFFFFFFF);
        paint.setTextSize(80f);
        paint.setTextAlign(Paint.Align.CENTER);
        Rect bounds = new Rect();
        paint.getTextBounds(initials, 0, initials.length(), bounds);
        float x = size / 2f;
        float y = size / 2f - (bounds.top + bounds.bottom) / 2f;
        canvas.drawText(initials, x, y, paint);

        imageView.setImageBitmap(bitmap);
    }
}
