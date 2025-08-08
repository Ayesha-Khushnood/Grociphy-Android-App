package com.example.homepage.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.homepage.Adapters.WishlistAdapter;
import com.example.homepage.Domain.PopularDomain;
import com.example.homepage.Helper.WishlistHelper;
import com.example.homepage.R;

import java.util.List;

public class WishlistActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private WishlistAdapter adapter;
    private ImageView ba;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wishlist);

        recyclerView = findViewById(R.id.wishlistRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // In your WishlistActivity.java
        List<PopularDomain> wishlistItems = WishlistHelper.getWishlist(this);
        WishlistAdapter adapter = new WishlistAdapter(this, wishlistItems);
        recyclerView.setAdapter(adapter);
        bottom_navigation();
    }
    private void bottom_navigation() {
        LinearLayout homeBtn = findViewById(R.id.homeBtn);
        LinearLayout cartBtn = findViewById(R.id.cartBtn);
        LinearLayout profileBtn = findViewById(R.id.profilebtn);
        LinearLayout wishlistBtn = findViewById(R.id.wishlistBtn);

        homeBtn.setOnClickListener(v -> startActivity(new Intent(WishlistActivity.this, MainActivity.class)));
        cartBtn.setOnClickListener(v -> startActivity(new Intent(WishlistActivity.this, CartActivity.class)));
        profileBtn.setOnClickListener(v -> startActivity(new Intent(WishlistActivity.this,ProfileActivity.class)));
        wishlistBtn.setOnClickListener(v -> startActivity(new Intent(WishlistActivity.this,WishlistActivity.class)));
    }
}