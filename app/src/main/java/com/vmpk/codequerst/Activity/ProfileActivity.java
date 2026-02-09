package com.vmpk.codequerst.Activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.res.Configuration;
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

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.cloudinary.android.MediaManager;
import com.cloudinary.android.callback.ErrorInfo;
import com.cloudinary.android.callback.UploadCallback;
import com.vmpk.codequerst.R;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.*;
import com.yalantis.ucrop.UCrop;

import java.util.HashMap;
import java.util.Map;

public class ProfileActivity extends AppCompatActivity {

    private SharedPreferences sharedPreferences;
    private boolean isGuest;
    private EditText etFirstName, etEmail, etPhone, etAddress;
    private ImageView profileImage, ivEditImage;

    // ✅ AVATAR VIEWS ADDED
    private ImageView avatar1, avatar2, avatar3, avatar4, avatar5, avatar6,
            avatar7, avatar8, avatar9, avatar10, avatar11, avatar12, avatar13,
            avatar14, avatar15, avatar16, avatar17;
    private boolean isProfileImageSet = false;

    private FirebaseAuth firebaseAuth;
    private GoogleSignInClient gsc;
    private DatabaseReference usersRef, leaderboardRef;
    private String uid;
    private TextView backProfile;
    private static final int PICK_IMAGE_REQUEST = 101;
    private Uri selectedImageUri;
    private static final int CAMERA_PERMISSION_REQUEST = 201;
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

        // ✅ AVATAR INIT
        avatar1 = findViewById(R.id.avatar1);
        avatar2 = findViewById(R.id.avatar2);
        avatar3 = findViewById(R.id.avatar3);
        avatar4 = findViewById(R.id.avatar4);
        avatar5 = findViewById(R.id.avatar5);
        avatar6 = findViewById(R.id.avatar6);
        avatar7 = findViewById(R.id.avatar7);
        avatar8 = findViewById(R.id.avatar8);
        avatar9 = findViewById(R.id.avatar9);
        avatar10 = findViewById(R.id.avatar10);
        avatar11 = findViewById(R.id.avatar11);
        avatar12 = findViewById(R.id.avatar12);
        avatar13 = findViewById(R.id.avatar13);
        avatar14 = findViewById(R.id.avatar14);
        avatar15 = findViewById(R.id.avatar15);
        avatar16 = findViewById(R.id.avatar16);
        avatar17 = findViewById(R.id.avatar17);

        setupAvatarClicks(); // ✅ CALL

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
        ivEditImage.setOnClickListener(v -> openImageChooser());
        findViewById(R.id.BtnlogOut).setOnClickListener(v -> logout());

        setupNetworkReceiver();

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

    // ================== AVATAR SECTION (FROM FIRST CODE) ==================

    private void setupAvatarClicks() {
        avatar1.setOnClickListener(v -> saveAvatar("avatar_1", R.drawable.avatar_1));
        avatar2.setOnClickListener(v -> saveAvatar("avatar_2", R.drawable.avatar_2));
        avatar3.setOnClickListener(v -> saveAvatar("avatar_3", R.drawable.avatar_3));
        avatar4.setOnClickListener(v -> saveAvatar("avatar_4", R.drawable.avatar_4));
        avatar5.setOnClickListener(v -> saveAvatar("avatar_5", R.drawable.avatar_5));
        avatar6.setOnClickListener(v -> saveAvatar("avatar_6", R.drawable.avatar_6));
        avatar7.setOnClickListener(v -> saveAvatar("avatar_7", R.drawable.avatar_7));
        avatar8.setOnClickListener(v -> saveAvatar("avatar_8", R.drawable.avatar_8));
        avatar9.setOnClickListener(v -> saveAvatar("avatar_9", R.drawable.avatar_9));
        avatar10.setOnClickListener(v -> saveAvatar("avatar_10", R.drawable.avatar_10));
        avatar11.setOnClickListener(v -> saveAvatar("avatar_11", R.drawable.avatar_11));
        avatar12.setOnClickListener(v -> saveAvatar("avatar_12", R.drawable.avatar_12));
        avatar13.setOnClickListener(v -> saveAvatar("avatar_13", R.drawable.avatar_13));
        avatar14.setOnClickListener(v -> saveAvatar("avatar_14", R.drawable.avatar_14));
        avatar15.setOnClickListener(v -> saveAvatar("avatar_15", R.drawable.avatar_15));
        avatar17.setOnClickListener(v -> saveAvatar("avatar_17", R.drawable.avatar_17));
        avatar16.setOnClickListener(v -> setGoogleProfileImage());
    }

    private void saveAvatar(String key, int drawable) {
        profileImage.setImageResource(drawable);
        selectedImageUri = null;

        sharedPrefsEditor.putString("profileImage", key).apply();

        if (!isGuest && isNetworkAvailable()) {
            usersRef.child("profileImage").setValue(key);
            leaderboardRef.child("profileImage").setValue(key);
        }

        sendBroadcast(new Intent("PROFILE_UPDATED"));
    }

    private void setGoogleProfileImage() {

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        if (user == null || user.getPhotoUrl() == null) {
            setDefaultProfileImage(etFirstName.getText().toString());
            return;
        }

        String photoUrl = user.getPhotoUrl().toString();

        Glide.with(profileImage)
                .load(photoUrl)
                .circleCrop()
                .placeholder(R.drawable.ic_person)
                .error(R.drawable.ic_person)
                .into(profileImage);

        Glide.with(avatar16)
                .load(photoUrl)
                .circleCrop()
                .placeholder(R.drawable.ic_person)
                .error(R.drawable.ic_person)
                .into(avatar16);

        sharedPrefsEditor.putString("profileImage", photoUrl).apply();

        if (!isGuest && isNetworkAvailable()) {
            usersRef.child("profileImage").setValue(photoUrl);
            leaderboardRef.child("profileImage").setValue(photoUrl);
        }

        sendBroadcast(new Intent("PROFILE_UPDATED"));
    }

    // ================== REST SECOND CODE (UNCHANGED) ==================
    // ⚠️ Neeche ka code tumhara same second code hai
    // Maine koi line delete nahi ki

    private boolean isNetworkAvailable() {
        ConnectivityManager cm =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo net = cm.getActiveNetworkInfo();
        return net != null && net.isConnected();
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

            String name = sharedPreferences.getString("name", "Guest User");
            String email = sharedPreferences.getString("email", "guest@example.com");
            String phone = sharedPreferences.getString("phone", "");
            String about = sharedPreferences.getString("about", "");

            etFirstName.setText(name);
            etEmail.setText(email);
            etPhone.setText(phone);
            etAddress.setText(about);

            // ✅ Default only if no profile image
            if (!isProfileImageSet) {
                setDefaultProfileImage(name);
            }

            loadingLayout.setVisibility(View.GONE);
            scrollView.setVisibility(View.VISIBLE);
            return;
        }

        FirebaseUser currentUser = firebaseAuth.getCurrentUser();
        if (currentUser == null) return;

        // ================= OFFLINE =================
        if (!isNetworkAvailable()) {

            String name = sharedPreferences.getString("offline_name",
                    currentUser.getDisplayName() != null
                            ? currentUser.getDisplayName()
                            : "User");

            String email = sharedPreferences.getString("offline_email",
                    currentUser.getEmail() != null
                            ? currentUser.getEmail()
                            : "");

            String phone = sharedPreferences.getString("offline_phone", "");
            String about = sharedPreferences.getString("offline_about", "");

            etFirstName.setText(name);
            etEmail.setText(email);
            etPhone.setText(phone);
            etAddress.setText(about);

            if (!isProfileImageSet) {
                setDefaultProfileImage(name);
            }

            String offlineImage =
                    sharedPreferences.getString("offline_profileImage", null);

            if (offlineImage != null && !offlineImage.isEmpty()) {

                isProfileImageSet = true;   // 🔥 IMPORTANT

                Glide.with(ProfileActivity.this)
                        .load(offlineImage)
                        .circleCrop()
                        .into(profileImage);
            }

            loadingLayout.setVisibility(View.GONE);
            scrollView.setVisibility(View.VISIBLE);
            return;
        }

        // ================= FIREBASE =================
        usersRef.addListenerForSingleValueEvent(
                new ValueEventListener() {

                    @Override
                    public void onDataChange(DataSnapshot snapshot) {

                        // ---------- NAME ----------
                        String firebaseName = safeGet(snapshot, "name");
                        String finalName;

                        if (firebaseName != null && !firebaseName.isEmpty()) {
                            finalName = firebaseName;
                        } else {
                            finalName = currentUser.getDisplayName() != null
                                    ? currentUser.getDisplayName()
                                    : "User";
                        }

                        etFirstName.setText(finalName);

                        // ---------- 🔥 STEP 1: LOCAL IMAGE FIRST ----------
                        String localImage =
                                sharedPreferences.getString("profileImage", null);

                        if (localImage != null && !localImage.isEmpty()) {
                            setProfileImageFromValue(localImage);
                        } else {
                            setDefaultProfileImage(finalName);
                        }

                        // ---------- OTHER FIELDS ----------
                        etEmail.setText(safeGet(snapshot, "email"));
                        etPhone.setText(safeGet(snapshot, "phone"));
                        etAddress.setText(safeGet(snapshot, "about"));

                        // ---------- 🔥 STEP 2: FIREBASE IMAGE (SYNC) ----------
                        String imageUrl = safeGet(snapshot, "profileImage");

                        if (imageUrl != null && !imageUrl.isEmpty()) {

                            // Firebase image overwrite only if exists
                            setProfileImageFromValue(imageUrl);

                            // 🔥 Firebase image ko local me sync kar do
                            sharedPrefsEditor
                                    .putString("profileImage", imageUrl)
                                    .apply();
                        }

                        loadingLayout.setVisibility(View.GONE);
                        scrollView.setVisibility(View.VISIBLE);
                    }


                    @Override
                    public void onCancelled(DatabaseError error) {

                        Toast.makeText(ProfileActivity.this,
                                "Failed to load profile",
                                Toast.LENGTH_SHORT).show();

                        loadingLayout.setVisibility(View.GONE);
                        scrollView.setVisibility(View.VISIBLE);
                    }
                });
    }

    private void openImageChooser() {

        // Gallery intent (no permission needed)
        Intent galleryIntent = new Intent(Intent.ACTION_PICK);
        galleryIntent.setType("image/*");

        // Camera intent
        Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);

        // 🔥 Camera permission check
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(android.Manifest.permission.CAMERA)
                    != android.content.pm.PackageManager.PERMISSION_GRANTED) {

                requestPermissions(
                        new String[]{android.Manifest.permission.CAMERA},
                        CAMERA_PERMISSION_REQUEST
                );
                return; // 👈 wait for permission result
            }
        }

        // Chooser
        Intent chooser = Intent.createChooser(galleryIntent, "Select Profile Picture");
        chooser.putExtra(Intent.EXTRA_INITIAL_INTENTS, new Intent[]{cameraIntent});

        startActivityForResult(chooser, PICK_IMAGE_REQUEST);
    }


    private void saveProfileField(String field, String value) {
        if (isGuest) {
            // Guest user → direct shared prefs me save
            sharedPrefsEditor.putString(field, value).apply();
            return;
        }

        // Agar user ne name change kiya to offline_name me bhi save
        if (field.equals("name")) {
            sharedPrefsEditor.putString("offline_name", value).apply();
        }

        // ✅ Network available → Firebase update
        if (isNetworkAvailable()) {
            Map<String, Object> updateMap = new HashMap<>();
            updateMap.put(field, value);

            usersRef.updateChildren(updateMap)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            // ⭐ Firebase me update success → SharedPreferences me bhi save
                            sharedPrefsEditor.putString(field, value).apply();

                            // Leaderboard update only when user changes name or profile image
                            if (field.equals("name") || field.equals("profileImage")) {
                                leaderboardRef.child(field).setValue(value);
                            }

                            sendBroadcast(new Intent("PROFILE_UPDATED"));
                        }
                    })
                    .addOnFailureListener(e ->
                            Toast.makeText(this, "Update failed", Toast.LENGTH_SHORT).show());
        } else {
            // Offline → offline_field save karo
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
            etEmail.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                }
            });
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

    private void startCrop(Uri sourceUri) {

        Uri destinationUri = Uri.fromFile(
                new java.io.File(getCacheDir(), "cropped_profile.jpg")
        );

        UCrop.Options options = new UCrop.Options();

        // 🔵 UI related
        options.setCircleDimmedLayer(true);        // 👈 CIRCLE OVERLAY
        options.setShowCropGrid(false);            // grid off
        options.setShowCropFrame(false);           // frame off
        options.setHideBottomControls(true);        // clean UI

        // 🎨 Colors (optional)
        options.setToolbarColor(Color.BLACK);
        options.setStatusBarColor(Color.BLACK);
        options.setToolbarWidgetColor(Color.WHITE);

        // 🖼 Quality
        options.setCompressionQuality(90);
        options.setCompressionFormat(Bitmap.CompressFormat.JPEG);

        UCrop.of(sourceUri, destinationUri)
                .withAspectRatio(1, 1)              // 1:1 for DP
                .withMaxResultSize(600, 600)
                .withOptions(options)
                .start(this);
    }

    @Override
    public void onRequestPermissionsResult(
            int requestCode,
            @NonNull String[] permissions,
            @NonNull int[] grantResults) {

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == CAMERA_PERMISSION_REQUEST) {

            if (grantResults.length > 0
                    && grantResults[0] == android.content.pm.PackageManager.PERMISSION_GRANTED) {

                // ✅ Permission granted → reopen chooser
                openImageChooser();

            } else {
                // ❌ Permission denied
                Toast.makeText(
                        this,
                        "Camera permission required to take photo",
                        Toast.LENGTH_SHORT
                ).show();
            }
        }
    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != RESULT_OK || data == null) return;
        try {
            // ================= IMAGE PICK (Chooser) =================
            if (requestCode == PICK_IMAGE_REQUEST) {
                Uri imageUri = data.getData();

                if (imageUri != null) {
                    // 📂 Gallery / Photos / Files
                    startCrop(imageUri);
                }
                else if (data.getExtras() != null) {
                    // 📷 Camera capture
                    Bitmap photo = (Bitmap) data.getExtras().get("data");

                    if (photo != null) {
                        Uri tempUri = getImageUri(this, photo);
                        startCrop(tempUri);
                    }
                }
            }
            // ================= UCROP RESULT =================
            else if (requestCode == UCrop.REQUEST_CROP) {
                Uri resultUri = UCrop.getOutput(data);
                if (resultUri != null) {
                    // 🔥 IMPORTANT FLAG
                    isProfileImageSet = true;
                    selectedImageUri = resultUri;
                    Glide.with(this)
                            .load(resultUri)
                            .circleCrop()
                            .skipMemoryCache(true)
                            .diskCacheStrategy(
                                    com.bumptech.glide.load.engine.DiskCacheStrategy.NONE
                            )
                            .into(profileImage);
                    // Temp save
                    sharedPrefsEditor
                            .putString("tempProfileImage", resultUri.toString())
                            .apply();

                    uploadImageToCloudinary(resultUri);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "Image processing failed", Toast.LENGTH_SHORT).show();
        }
    }


    private Uri getImageUri(Context context, Bitmap bitmap) {

        java.io.ByteArrayOutputStream bytes =
                new java.io.ByteArrayOutputStream();

        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bytes);

        String path = android.provider.MediaStore.Images.Media.insertImage(
                context.getContentResolver(),
                bitmap,
                "ProfileTemp",
                null
        );

        return Uri.parse(path);
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

    private boolean isDarkModeOn() {
        int nightModeFlags =
                getResources().getConfiguration().uiMode &
                        Configuration.UI_MODE_NIGHT_MASK;
        return nightModeFlags == Configuration.UI_MODE_NIGHT_YES;
    }

    private void setProfileImageFromValue(String value) {

        if (value == null || value.isEmpty()) {
            setDefaultProfileImage(etFirstName.getText().toString());
            return;
        }

        // ================= AVATAR =================
        if (value.startsWith("avatar_")) {

            int resId = getResources().getIdentifier(
                    value,
                    "drawable",
                    getPackageName()
            );

            if (resId != 0) {
                profileImage.setImageResource(resId);
            } else {
                setDefaultProfileImage(etFirstName.getText().toString());
            }

            return;
        }

        // ================= URL / LOCAL =================
        try {

            Glide.with(this)
                    .load(value)
                    .circleCrop()
                    .skipMemoryCache(true)   // 🔥 FIX
                    .diskCacheStrategy(
                            com.bumptech.glide.load.engine.DiskCacheStrategy.NONE
                    )
                    .into(profileImage);

        } catch (Exception e) {

            setDefaultProfileImage(etFirstName.getText().toString());
        }
    }



}
