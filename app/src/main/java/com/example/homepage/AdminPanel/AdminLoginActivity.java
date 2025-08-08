package com.example.homepage.AdminPanel;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.homepage.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class AdminLoginActivity extends AppCompatActivity {

    private EditText adminEmail, adminPassword;
    private Button adminLoginBtn;

    private FirebaseAuth mAuth;
    private final String adminOnlyEmail = "admin@example.com";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_login);

        mAuth = FirebaseAuth.getInstance();

        adminEmail = findViewById(R.id.loginemail);
        adminPassword = findViewById(R.id.loginpass);
        adminLoginBtn = findViewById(R.id.loginbtn);

        adminLoginBtn.setOnClickListener(v -> {
            String email = adminEmail.getText().toString().trim();
            String password = adminPassword.getText().toString().trim();

            if (TextUtils.isEmpty(email) || TextUtils.isEmpty(password)) {
                Toast.makeText(this, "Please enter both email and password", Toast.LENGTH_SHORT).show();
                return;
            }

            if (!email.equals(adminOnlyEmail)) {
                Toast.makeText(this, "You are not authorized as admin", Toast.LENGTH_SHORT).show();
                return;
            }

            mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    FirebaseUser user = mAuth.getCurrentUser();
                    Toast.makeText(this, "Admin login successful", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(this, MainPanel.class));
                    finish();
                } else {
                    Toast.makeText(this, "Login failed: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        });
    }
}
