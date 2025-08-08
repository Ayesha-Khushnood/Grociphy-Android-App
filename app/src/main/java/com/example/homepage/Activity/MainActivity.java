package com.example.homepage.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button; // Import Button
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.homepage.Adapters.PopularListAdapter;
import com.example.homepage.Domain.PopularDomain;
import com.example.homepage.Helper.CloudinaryConfig;
import com.example.homepage.R;
import com.google.firebase.FirebaseApp;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private RecyclerView.Adapter adapterPopular;
    private RecyclerView recyclerViewPopular;
    private Button loginButton; // Declare login button

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
//        CloudinaryConfig.initialize(this);
        initRecyclerview();
        initLoginButton();
        bottom_navigation();
        FirebaseApp.initializeApp(this);
        // Initialize Login Button

        // Find the LinearLayout by ID (dbproducts)
        LinearLayout dbproducts = findViewById(R.id.products);

        // Set a click listener on the dbproducts LinearLayout
        dbproducts.setOnClickListener(v -> {
            // Start ProductActivity when dbproducts is clicked
            Intent intent = new Intent(MainActivity.this, ProductActivity.class);
            startActivity(intent);
        });
    }

    private void bottom_navigation() {
        LinearLayout homeBtn = findViewById(R.id.homeBtn);
        LinearLayout cartBtn = findViewById(R.id.cartBtn);
        LinearLayout profileBtn = findViewById(R.id.profilebtn);
        LinearLayout wishlistBtn = findViewById(R.id.wishlistBtn);

        homeBtn.setOnClickListener(v -> startActivity(new Intent(MainActivity.this, MainActivity.class)));
        cartBtn.setOnClickListener(v -> startActivity(new Intent(MainActivity.this, CartActivity.class)));
        profileBtn.setOnClickListener(v -> startActivity(new Intent(MainActivity.this, ProfileActivity.class)));
        wishlistBtn.setOnClickListener(v -> startActivity(new Intent(MainActivity.this, WishlistActivity.class)));
    }

    private void initRecyclerview() {
        ArrayList<PopularDomain> items = new ArrayList<>();
        items.add(new PopularDomain("Grapes", "Fresh juicy grapes", "view_delicious_green_grapes", 15, 20, 12));
        items.add(new PopularDomain("Strawberry", "Fresh juicy strawberries", "strawberry", 15, 20, 12));
        items.add(new PopularDomain("Milk", "fresh milk", "bluemilk", 10, 25, 400));
        items.add(new PopularDomain("Banana", "Ripe yellow bananas", "banana", 1, 5, 40));
        items.add(new PopularDomain("Mango", "Juicy Alphonso mangoes", "mango", 3, 1, 20));
        items.add(new PopularDomain("Tomato", "Fresh red tomatoes", "tomato", 1, 1, 60));
        items.add(new PopularDomain("Potato", "Organic brown potatoes", "potato", 1, 2, 45));

        recyclerViewPopular = findViewById(R.id.view1);
        recyclerViewPopular.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));

        adapterPopular = new PopularListAdapter(items);
        recyclerViewPopular.setAdapter(adapterPopular);
    }

    private void initLoginButton() {
        loginButton = findViewById(R.id.loginbtn); // Find the login button
        loginButton.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, LoginActivity.class);
            startActivity(intent);
        });
    }
}
