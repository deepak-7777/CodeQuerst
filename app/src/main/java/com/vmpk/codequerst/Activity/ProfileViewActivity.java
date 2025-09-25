package com.vmpk.codequerst.Activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import com.vmpk.codequerst.R;
import com.google.android.material.imageview.ShapeableImageView;

public class ProfileViewActivity extends AppCompatActivity {
    ShapeableImageView profileViewImage;
    TextView backProfileView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_profile_view);

        backProfileView = findViewById(R.id.backProfileView);
        profileViewImage = findViewById(R.id.ProfileImageView);

        ///     received img from ProfileActivity
        Intent intent = getIntent();
        if (intent.hasExtra("imageUri")) {
            Uri imageUri = Uri.parse(intent.getStringExtra("imageUri"));
            profileViewImage.setImageURI(imageUri);
        } else if (intent.hasExtra("imageBitmap")) {
            byte[] byteArray = intent.getByteArrayExtra("imageBitmap");
            Bitmap bitmap = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length);
            profileViewImage.setImageBitmap(bitmap);
        }

        backProfileView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
}