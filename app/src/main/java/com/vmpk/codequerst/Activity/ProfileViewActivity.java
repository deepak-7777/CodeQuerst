package com.vmpk.codequerst.Activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.vmpk.codequerst.R;

public class ProfileViewActivity extends AppCompatActivity {
    private ImageView profileImageView;
    TextView backView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_profile_view);

        profileImageView = findViewById(R.id.profileImageView);
        backView = findViewById(R.id.backView);

        // 🔥 Receive imageUri
        String imageUriString = getIntent().getStringExtra("imageUri");

        // 🔥 Receive bitmap
        byte[] byteArray = getIntent().getByteArrayExtra("imageBitmap");

        if (imageUriString != null) {

            Uri imageUri = Uri.parse(imageUriString);

            Glide.with(this)
                    .load(imageUri)
                    .into(profileImageView);

        } else if (byteArray != null) {

            Bitmap bitmap = BitmapFactory.decodeByteArray(
                    byteArray,
                    0,
                    byteArray.length
            );

            profileImageView.setImageBitmap(bitmap);

            backView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    finish();
                }
            });
        }
    }
}
