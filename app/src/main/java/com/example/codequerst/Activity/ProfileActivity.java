package com.example.codequerst.Activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
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
import com.google.firebase.database.*;

import java.util.HashMap;
import java.util.Map;

public class ProfileActivity extends AppCompatActivity {

    private SharedPreferences sharedPreferences;
    private boolean isGuest;
    private EditText etFirstName, etEmail, etPhone, etAddress;
    private ImageView profileImage, ivEditImage;
    private FirebaseAuth firebaseAuth;
    private GoogleSignInClient gsc;
    private DatabaseReference usersRef, leaderboardRef;
    private String uid;
    private TextView backProfile;
    private static final int PICK_IMAGE_REQUEST = 101;
    private Uri selectedImageUri;

    private SharedPreferences.Editor sharedPrefsEditor;
    private BroadcastReceiver networkReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        sharedPreferences = getSharedPreferences("user_prefs", MODE_PRIVATE);
        sharedPrefsEditor = sharedPreferences.edit();
        isGuest = sharedPreferences.getBoolean("isGuest", false);

        etFirstName = findViewById(R.id.etFirstName);
        etEmail = findViewById(R.id.etEmail);
        etPhone = findViewById(R.id.etPhone);
        etAddress = findViewById(R.id.etAddress);
        profileImage = findViewById(R.id.profileImage);
        ivEditImage = findViewById(R.id.ivEditImage);
        backProfile = findViewById(R.id.backBtn);

        backProfile.setOnClickListener(v -> finish());

        profileImage.setOnClickListener(v -> {
            Intent intent = new Intent(ProfileActivity.this, ProfileViewActivity.class);

            if (selectedImageUri != null) {
                intent.putExtra("imageUri", selectedImageUri.toString());
            } else {
                profileImage.setDrawingCacheEnabled(true);
                Bitmap bitmap = profileImage.getDrawingCache();
                java.io.ByteArrayOutputStream stream = new java.io.ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
                byte[] byteArray = stream.toByteArray();
                intent.putExtra("imageBitmap", byteArray);
                profileImage.setDrawingCacheEnabled(false);
            }
            startActivity(intent);
        });

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
            leaderboardRef = FirebaseDatabase.getInstance().getReference("leaderboard").child(uid);
        }

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        gsc = GoogleSignIn.getClient(this, gso);

        try {
            Map<String, String> config = new HashMap<>();
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

        setupNetworkReceiver();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.getDecorView().setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
            window.setStatusBarColor(Color.TRANSPARENT);
        }
    }

    private void setupNetworkReceiver() {
        networkReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (isNetworkAvailable()) {
                    syncPendingUpdates();
                }
            }
        };
        IntentFilter filter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        registerReceiver(networkReceiver, filter);
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo network = cm.getActiveNetworkInfo();
        return network != null && network.isConnected();
    }

    private void syncPendingUpdates() {
        if (!isGuest && usersRef != null) {
            Map<String, Object> pendingUpdates = new HashMap<>();
            if (sharedPreferences.contains("offline_name")) {
                pendingUpdates.put("name", sharedPreferences.getString("offline_name", ""));
            }
            if (sharedPreferences.contains("offline_email")) {
                pendingUpdates.put("email", sharedPreferences.getString("offline_email", ""));
            }
            if (sharedPreferences.contains("offline_phone")) {
                pendingUpdates.put("phone", sharedPreferences.getString("offline_phone", ""));
            }
            if (sharedPreferences.contains("offline_about")) {
                pendingUpdates.put("about", sharedPreferences.getString("offline_about", ""));
            }
            if (!pendingUpdates.isEmpty()) {
                usersRef.updateChildren(pendingUpdates).addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        sharedPrefsEditor.remove("offline_name")
                                .remove("offline_email")
                                .remove("offline_phone")
                                .remove("offline_about")
                                .apply();
                    }
                });
            }
        }
    }

    private void loadUserProfile() {
        LinearLayout loadingLayout = findViewById(R.id.loadingProfile);
        ScrollView scrollView = findViewById(R.id.scrollView);

        loadingLayout.setVisibility(View.VISIBLE);
        scrollView.setVisibility(View.GONE);

        if (isGuest) {
            // Guest user
            String name = sharedPreferences.getString("name", "Guest User");
            String email = sharedPreferences.getString("email", "guest@example.com");
            String phone = sharedPreferences.getString("phone", "");
            String about = sharedPreferences.getString("about", "");

            etFirstName.setText(name);
            etEmail.setText(email);
            etPhone.setText(phone);
            etAddress.setText(about);

            setDefaultProfileImage(name);

            loadingLayout.setVisibility(View.GONE);
            scrollView.setVisibility(View.VISIBLE);
            return;
        }

        FirebaseUser currentUser = firebaseAuth.getCurrentUser();
        if (currentUser == null) return;

        // 1️⃣ Check offline_name first (user ne pehle edit kiya)
        String offlineName = sharedPreferences.getString("offline_name", null);
        if (offlineName != null && !offlineName.isEmpty()) {
            etFirstName.setText(offlineName);
            setDefaultProfileImage(offlineName);

            // Load remaining fields from Firebase, ignore name
            usersRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot snapshot) {
                    etEmail.setText(safeGet(snapshot, "email"));
                    etPhone.setText(safeGet(snapshot, "phone"));
                    etAddress.setText(safeGet(snapshot, "about"));

                    String imageUrl = safeGet(snapshot, "profileImage");
                    if (imageUrl != null && !imageUrl.isEmpty()) {
                        Glide.with(ProfileActivity.this)
                                .load(imageUrl)
                                .circleCrop()
                                .into(profileImage);
                    }

                    loadingLayout.setVisibility(View.GONE);
                    scrollView.setVisibility(View.VISIBLE);
                }

                @Override
                public void onCancelled(DatabaseError error) {
                    Toast.makeText(ProfileActivity.this, "Failed to load profile", Toast.LENGTH_SHORT).show();
                }
            });
            return; // Offline name exist, Firebase name ignore
        }

        // 2️⃣ First-time login: offline_name null → use Firebase name or Google name
        usersRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                String firebaseName = safeGet(snapshot, "name");
                String finalName;

                if (firebaseName != null && !firebaseName.isEmpty()) {
                    finalName = firebaseName; // Firebase saved name
                } else {
                    finalName = currentUser.getDisplayName() != null ? currentUser.getDisplayName() : "User"; // Google name
                }

                etFirstName.setText(finalName);
                setDefaultProfileImage(finalName);

                etEmail.setText(safeGet(snapshot, "email"));
                etPhone.setText(safeGet(snapshot, "phone"));
                etAddress.setText(safeGet(snapshot, "about"));

                String imageUrl = safeGet(snapshot, "profileImage");
                if (imageUrl != null && !imageUrl.isEmpty()) {
                    Glide.with(ProfileActivity.this)
                            .load(imageUrl)
                            .circleCrop()
                            .into(profileImage);
                }

                loadingLayout.setVisibility(View.GONE);
                scrollView.setVisibility(View.VISIBLE);
            }

            @Override
            public void onCancelled(DatabaseError error) {
                Toast.makeText(ProfileActivity.this, "Failed to load profile", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void saveProfileField(String field, String value) {
        if (isGuest) {
            sharedPrefsEditor.putString(field, value).apply();
            return;
        }

        // User edit kare → offline_name me save
        if (field.equals("name")) {
            sharedPrefsEditor.putString("offline_name", value).apply();
        }

        // Network available → Firebase update
        if (isNetworkAvailable()) {
            Map<String, Object> updateMap = new HashMap<>();
            updateMap.put(field, value);

            usersRef.updateChildren(updateMap)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            // Leaderboard update only when user changes name or profile image
                            if (field.equals("name") || field.equals("profileImage")) {
                                leaderboardRef.child(field).setValue(value);
                            }
                            sendBroadcast(new Intent("PROFILE_UPDATED"));
                        }
                    })
                    .addOnFailureListener(e -> Toast.makeText(this, "Update failed", Toast.LENGTH_SHORT).show());
        } else {
            // Offline → offline_field save
            sharedPrefsEditor.putString("offline_" + field, value).apply();
            sendBroadcast(new Intent("PROFILE_UPDATED"));
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
            etEmail.setOnClickListener(v -> Toast.makeText(this, "Email cannot be change", Toast.LENGTH_SHORT).show());
        }
        etPhone.setOnClickListener(v -> showEditDialog("Phone", etPhone, "phone"));
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
            saveProfileField(field, newValue);
            if ("name".equals(field)) {
                setDefaultProfileImage(newValue);
            }
            dialog.dismiss();
        });

        dialog.show();
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
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
        if (!isGuest && !isNetworkAvailable()) {
            Toast.makeText(ProfileActivity.this, "Please check your network connection", Toast.LENGTH_SHORT).show();
            return;
        }

        MediaManager.get().upload(imageUri)
                .unsigned("my_unsigned_upload")
                .callback(new UploadCallback() {
                    @Override public void onStart(String requestId) { }
                    @Override public void onProgress(String requestId, long bytes, long totalBytes) { }

                    @Override public void onSuccess(String requestId, Map resultData) {
                        if (resultData.get("secure_url") != null && !isGuest) {
                            String imageUrl = resultData.get("secure_url").toString();

                            if (isNetworkAvailable()) {
                                usersRef.child("profileImage").setValue(imageUrl)
                                        .addOnCompleteListener(task -> {
                                            leaderboardRef.child("profileImage").setValue(imageUrl);
                                            Intent intent = new Intent("PROFILE_UPDATED");
                                            sendBroadcast(intent);
                                        });
                            } else {
                                sharedPrefsEditor.putString("offline_profileImage", imageUrl).apply();
                                Intent intent = new Intent("PROFILE_UPDATED");
                                sendBroadcast(intent);
                            }
                        }
                    }

                    @Override public void onError(String requestId, ErrorInfo error) {
                        Toast.makeText(ProfileActivity.this, "Image upload failed: " + error.getDescription(), Toast.LENGTH_SHORT).show();
                    }

                    @Override public void onReschedule(String requestId, ErrorInfo error) { }
                }).dispatch();
    }

    private void logout() {
        if (isGuest) {
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.remove("name");
            editor.remove("email");
            editor.remove("phone");
            editor.remove("about");
            editor.putBoolean("isGuest", false);
            editor.apply();

            FirebaseAuth.getInstance().signOut();

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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (networkReceiver != null) {
            unregisterReceiver(networkReceiver);
        }
    }
}
