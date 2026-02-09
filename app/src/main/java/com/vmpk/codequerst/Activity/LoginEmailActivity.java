package com.vmpk.codequerst.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.vmpk.codequerst.R;

public class LoginEmailActivity extends AppCompatActivity {

    private EditText edEmail, edPassword;
    private Button btnLogin;
    private TextView tvForgotPassword;
    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login_email);

        edEmail = findViewById(R.id.loginemail);
        edPassword = findViewById(R.id.loginPassword);
        btnLogin = findViewById(R.id.btnLogin);
        tvForgotPassword = findViewById(R.id.tvForgotPassword);
        auth = FirebaseAuth.getInstance();

        // ✅ Already Logged-In? Directly go to HomeActivity
        if (auth.getCurrentUser() != null) {
            startActivity(new Intent(LoginEmailActivity.this, HomeActivity.class));
            finish();
            return;
        }

        // ✅ Forgot Password Click
        tvForgotPassword.setOnClickListener(v -> {
            Intent intent = new Intent(LoginEmailActivity.this, ForgotActivity.class);
            startActivity(intent);
        });

        // ✅ Login Click
        btnLogin.setOnClickListener(v -> {
            String email = edEmail.getText().toString().trim();
            String password = edPassword.getText().toString().trim();

            if (TextUtils.isEmpty(email) || TextUtils.isEmpty(password)) {
                Toast.makeText(LoginEmailActivity.this, "Enter email and password both", Toast.LENGTH_SHORT).show();
            } else {
                loginUser(email, password);
            }
        });
    }

    // ✅ Email + Password Authentication
    private void loginUser(String email, String password) {
        auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(LoginEmailActivity.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(LoginEmailActivity.this, "Login successful", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(getApplicationContext(), HomeActivity.class));
                            finish();
                        } else {
                            String error = task.getException() != null ? task.getException().getMessage() : "Login failed";
                            Toast.makeText(LoginEmailActivity.this, "Error: " + error, Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }

    // ✅ For "Sign Up" Button (XML से onClick में जुड़ा हुआ है)
//    public void signup(View view) {
//        startActivity(new Intent(getApplicationContext(), RegisterActivity.class));
//    }
}
