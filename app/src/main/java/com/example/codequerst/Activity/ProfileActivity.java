package com.example.codequerst.Activity;

import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.cloudinary.android.MediaManager;
import com.cloudinary.android.callback.ErrorInfo;
import com.cloudinary.android.callback.UploadCallback;
import com.example.codequerst.R;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

public class ProfileActivity extends AppCompatActivity {

    SharedPreferences sharedPreferences;
    boolean isGuest;
    private EditText etFirstName, etEmail, etPhone, etAddress;
    private ImageView profileImage, ivEditImage;
    private FirebaseAuth firebaseAuth;
    private GoogleSignInClient gsc;
    private DatabaseReference usersRef;
    private String uid;
    private TextView backProfile;
    private static final int PICK_IMAGE_REQUEST = 101;
    private Uri selectedImageUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        sharedPreferences = getSharedPreferences("user_prefs", MODE_PRIVATE);
        isGuest = sharedPreferences.getBoolean("isGuest", false);
        etFirstName = findViewById(R.id.etFirstName);
        etEmail = findViewById(R.id.etEmail);
        etPhone = findViewById(R.id.etPhone);
        etAddress = findViewById(R.id.etAddress);
        profileImage = findViewById(R.id.profileImage);
        ivEditImage = findViewById(R.id.ivEditImage);
        backProfile = findViewById(R.id.backBtn);

        backProfile.setOnClickListener(v -> finish());

        firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser user = firebaseAuth.getCurrentUser();

        if (!isGuest && user == null) {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
            return;
        }

        if (!isGuest) {
            uid = user.getUid();
            usersRef = FirebaseDatabase.getInstance().getReference("users").child(uid);
        }

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        gsc = GoogleSignIn.getClient(this, gso);

        try {
            Map config = new HashMap();
            config.put("cloud_name", "dmsbaxkle");
            config.put("api_key", "183187419764851");
            config.put("api_secret", "1N9ZBXDi4r1lxwyHYOELDBlFoX0");
            MediaManager.init(this, config);
        } catch (IllegalStateException e) { }

        ivEditImage.setVisibility(isGuest ? View.GONE : View.VISIBLE);
        loadUserProfile();
        setEditTextListeners();
        ivEditImage.setOnClickListener(v -> openGallery());
        findViewById(R.id.BtnlogOut).setOnClickListener(v -> logout());

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

    private void loadUserProfile() {
        if (isGuest) {
            String name = sharedPreferences.getString("name", "Guest User");
            String email = sharedPreferences.getString("email", "guest@example.com");
            String phone = sharedPreferences.getString("phone", "");
            String about = sharedPreferences.getString("about", "");

            etFirstName.setText(name);
            etEmail.setText(email);
            etPhone.setText(phone);
            etAddress.setText(about);

            setDefaultProfileImage(name);

            findViewById(R.id.loadingLayout).setVisibility(View.GONE);
            findViewById(R.id.scrollView).setVisibility(View.VISIBLE);
        } else {
            FirebaseUser currentUser = firebaseAuth.getCurrentUser();
            if (currentUser == null) return;
            findViewById(R.id.loadingLayout).setVisibility(View.VISIBLE);
            findViewById(R.id.scrollView).setVisibility(View.GONE);

            usersRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot snapshot) {
                    String name = "", email = "", phone = "", about = "", imageUrl = "";

                    if (snapshot.exists()) {
                        name = safeGet(snapshot, "name");
                        email = safeGet(snapshot, "email");
                        phone = safeGet(snapshot, "phone");
                        about = safeGet(snapshot, "about");
                        imageUrl = safeGet(snapshot, "profileImage");
                    } else {
                        email = currentUser.getEmail();
                        name = currentUser.getDisplayName();
                        Map<String, Object> initialData = new HashMap<>();
                        initialData.put("name", name != null ? name : "");
                        initialData.put("email", email != null ? email : "");
                        initialData.put("phone", "");
                        initialData.put("about", "");
                        usersRef.setValue(initialData);
                    }

                    etFirstName.setText(name != null ? name : "");
                    etEmail.setText(email != null ? email : "");
                    etPhone.setText(phone != null ? phone : "");
                    etAddress.setText(about != null ? about : "");

                    if (imageUrl != null && !imageUrl.isEmpty()) {
                        Glide.with(ProfileActivity.this)
                                .load(imageUrl)
                                .circleCrop()
                                .into(profileImage);
                    } else {
                        setDefaultProfileImage(name);
                    }

                    findViewById(R.id.loadingLayout).setVisibility(View.GONE);
                    findViewById(R.id.scrollView).setVisibility(View.VISIBLE);
                }

                @Override
                public void onCancelled(DatabaseError error) {
                    Toast.makeText(ProfileActivity.this, "Failed to load profile: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                    findViewById(R.id.loadingLayout).setVisibility(View.GONE);
                    findViewById(R.id.container).setVisibility(View.VISIBLE);
                }
            });
        }
    }

    private String safeGet(DataSnapshot snapshot, String key) {
        return snapshot.child(key).getValue() != null ? snapshot.child(key).getValue(String.class) : "";
    }

    private void setEditTextListeners() {
        etFirstName.setOnClickListener(v -> showEditDialog("Name", etFirstName, "name"));

        if (isGuest) {
            etEmail.setOnClickListener(v -> showEditDialog("Email", etEmail, "email"));
        } else {
            etEmail.setOnClickListener(v -> Toast.makeText(this, "Email cannot be edited", Toast.LENGTH_SHORT).show());
        }

        etPhone.setOnClickListener(v -> showEditDialog("Phone Number", etPhone, "phone"));
        etAddress.setOnClickListener(v -> showEditDialog("About", etAddress, "about"));
    }


    private void showEditDialog(String title, EditText editText, String field) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_edit_profile, null);
        builder.setView(dialogView);

        AlertDialog dialog = builder.create();

        TextView dialogTitle = dialogView.findViewById(R.id.dialogTitle);
        EditText input = dialogView.findViewById(R.id.dialogEditText);
        Button btnCancel = dialogView.findViewById(R.id.btnCancel);
        Button btnSave = dialogView.findViewById(R.id.btnSave);

        dialogTitle.setText("Edit " + title);
        input.setText(editText.getText().toString());

        btnCancel.setOnClickListener(v -> dialog.dismiss());
        btnSave.setOnClickListener(v -> {
            String newValue = input.getText().toString().trim();
            editText.setText(newValue);
            updateUserProfile(field, newValue);
            if ("name".equals(field)) {
                setDefaultProfileImage(newValue);
            }
            dialog.dismiss();
        });

        dialog.show();
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
    }

    private void updateUserProfile(String field, String value) {
        if (isGuest) {
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString(field, value);
            editor.apply();
        } else {
            Map<String, Object> updateMap = new HashMap<>();
            updateMap.put(field, value);
            usersRef.updateChildren(updateMap)
                    .addOnFailureListener(e -> Toast.makeText(this, "Failed to update " + field, Toast.LENGTH_SHORT).show());
        }
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
        profileImage.setImageBitmap(bitmap);
    }

    private void openGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            selectedImageUri = data.getData();
            profileImage.setImageURI(selectedImageUri);
            uploadImageToCloudinary(selectedImageUri);
        }
    }

    private void uploadImageToCloudinary(Uri imageUri) {
        MediaManager.get().upload(imageUri)
                .unsigned("my_unsigned_upload")
                .callback(new UploadCallback() {
                    @Override public void onStart(String requestId) { }
                    @Override public void onProgress(String requestId, long bytes, long totalBytes) { }
                    @Override public void onSuccess(String requestId, Map resultData) {
                        if (resultData.get("secure_url") != null && !isGuest) {
                            String imageUrl = resultData.get("secure_url").toString();
                            usersRef.child("profileImage").setValue(imageUrl);
                        }
                    }
                    @Override public void onError(String requestId, ErrorInfo error) {
                        Toast.makeText(ProfileActivity.this, "Image upload failed: " + error.getDescription(), Toast.LENGTH_SHORT).show();
                    }
                    @Override public void onReschedule(String requestId, ErrorInfo error) { }
                })
                .dispatch();
    }

    private void logout() {
        if (isGuest) {
            sharedPreferences.edit().clear().apply();
            Intent intent = new Intent(ProfileActivity.this, LoginActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
        } else {
            firebaseAuth.signOut();
            gsc.signOut().addOnCompleteListener(task -> {
                Intent intent = new Intent(ProfileActivity.this, LoginActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                finish();
            });
        }
    }
}
