package com.example.homepage.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.airbnb.lottie.LottieAnimationView;
import com.example.homepage.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class RegistrationActivity extends AppCompatActivity {
    Button signup_btn;
    EditText name, password, email;
    TextView signin_txt;
    LottieAnimationView loginAnimation;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();

        // Check if user is already logged in
//        FirebaseUser currentUser = mAuth.getCurrentUser();
//        if(currentUser != null){
//            startActivity(new Intent(this, MainActivity.class));
//            finish();
//        }

        signup_btn = findViewById(R.id.login_btn);
        name = findViewById(R.id.name);
        password = findViewById(R.id.password);
        email = findViewById(R.id.email);
        signin_txt = findViewById(R.id.sign_in);
        signin_txt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(RegistrationActivity.this, LoginActivity.class));

            }
        });
        loginAnimation = findViewById(R.id.login);

        // Set animation properties
        loginAnimation.setAnimation(R.raw.login);
        loginAnimation.loop(true);
        loginAnimation.playAnimation();



        signup_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Get user input
                String userEmail = email.getText().toString().trim();
                String userPassword = password.getText().toString().trim();
                String userName = name.getText().toString().trim();

                // Validate inputs
                if (TextUtils.isEmpty(userEmail)) {
                    email.setError("Email is required");
                    return;
                }

                if (TextUtils.isEmpty(userPassword)) {
                    password.setError("Password is required");
                    return;
                }

                if (userPassword.length() < 6) {
                    password.setError("Password must be ≥6 characters");
                    return;
                }

                // Show animation while processing
                loginAnimation.setSpeed(2f); // Speed up animation during registration

                // Create user with Firebase
                mAuth.createUserWithEmailAndPassword(userEmail, userPassword)
                        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                loginAnimation.setSpeed(1f); // Reset animation speed

                                if (task.isSuccessful()) {
                                    // Registration success
                                    Toast.makeText(RegistrationActivity.this,
                                            "Registration successful!", Toast.LENGTH_SHORT).show();

                                    // You can save the user's name to Firebase Realtime Database here if needed

                                    // Proceed to main activity
                                    startActivity(new Intent(RegistrationActivity.this, MainActivity.class));
                                    finish();
                                } else {
                                    // Registration failed
                                    Toast.makeText(RegistrationActivity.this,
                                            "Registration failed: " + task.getException().getMessage(),
                                            Toast.LENGTH_LONG).show();
                                }
                            }
                        });
            }
        });
    }
}


//package com.example.homepage.Activity;
//
//import android.content.Intent;
//import android.os.Bundle;
//import android.view.View;
//import android.widget.Button;
//import android.widget.EditText;
//import android.widget.TextView;
//import androidx.appcompat.app.AppCompatActivity;
//import com.airbnb.lottie.LottieAnimationView;
//import com.example.homepage.R;
//
//public class RegistrationActivity extends AppCompatActivity {
//    Button signup_btn;
//    EditText name, password, email;
//    TextView signin_txt;
//    LottieAnimationView loginAnimation;
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_registration);
//
//        signup_btn = findViewById(R.id.login_btn);
//        name = findViewById(R.id.name);
//        password = findViewById(R.id.password);
//        email = findViewById(R.id.email);
//        signin_txt = findViewById(R.id.sign_in);
//        loginAnimation = findViewById(R.id.login); // Initialize Lottie animation view
//
//        // Set animation properties (optional, in case you want to modify)
//        loginAnimation.setAnimation(R.raw.login);
//        loginAnimation.loop(true);
//        loginAnimation.playAnimation();
//
//        signin_txt.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                startActivity(new Intent(RegistrationActivity.this, LoginActivity.class));
//            }
//        });
//        signup_btn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                startActivity(new Intent(RegistrationActivity.this, MainActivity.class));
//            }
//        });
//    }
//}
