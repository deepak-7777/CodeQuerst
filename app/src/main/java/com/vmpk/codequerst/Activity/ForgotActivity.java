package com.vmpk.codequerst.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.vmpk.codequerst.R;

public class ForgotActivity extends AppCompatActivity {

    public void backBtn2 (View view) {
        startActivity(new Intent(ForgotActivity.this,  LoginEmailActivity.class));
    }

    private EditText edResetEmail;
    private Button btnResetPassword;
    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_forgot);

        edResetEmail = findViewById(R.id.forgotemail);
        btnResetPassword = findViewById(R.id.btn_reset_password);
        auth = FirebaseAuth.getInstance();

        btnResetPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = edResetEmail.getText().toString().trim();

                if (TextUtils.isEmpty(email)) {
                    Toast.makeText(ForgotActivity.this, "Please enter your email", Toast.LENGTH_SHORT).show();
                    return;
                }

                auth.sendPasswordResetEmail(email)
                        .addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                Toast.makeText(ForgotActivity.this, "Reset link sent to your email", Toast.LENGTH_LONG).show();
                                startActivity(new Intent(getApplicationContext(),  LoginActivity.class));
                            } else {
                                String error = task.getException() != null ? task.getException().getMessage() : "Reset failed";
                                Toast.makeText(ForgotActivity.this, "Error: " + error, Toast.LENGTH_LONG).show();
                            }
                        });
            }
        });
    }

}
