package com.example.homepage.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import androidx.appcompat.app.AppCompatActivity;

import com.airbnb.lottie.LottieAnimationView;
import com.example.homepage.R;

public class OrderSuccessActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_success);

        LottieAnimationView lottieAnimationView = findViewById(R.id.lottieAnimationView);
        lottieAnimationView.playAnimation();

        // After animation completes, go to MainActivity
        new Handler().postDelayed(() -> {
            startActivity(new Intent(OrderSuccessActivity.this, MainActivity.class));
            finish();
        }, 2000);
    }
}