package com.example.homepage.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.airbnb.lottie.LottieAnimationView;
import com.example.homepage.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class SplashScreen extends AppCompatActivity {

    LottieAnimationView loadAnimation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_splash_screen);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        loadAnimation = findViewById(R.id.loadanim);
        loadAnimation.setAnimation(R.raw.anim2);
        loadAnimation.loop(true);
        loadAnimation.playAnimation();

        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();

            if (currentUser != null) {
                // User is already logged in
                startActivity(new Intent(SplashScreen.this, MainActivity.class));
            } else {
                // User is not logged in
                startActivity(new Intent(SplashScreen.this, LoginActivity.class));
            }

            finish();
        }, 4000); // 4 seconds delay
    }
}
