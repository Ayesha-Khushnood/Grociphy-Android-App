package com.example.homepage.Activity;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.airbnb.lottie.LottieAnimationView;
import com.bumptech.glide.Glide;
import com.example.homepage.Domain.PopularDomain;
import com.example.homepage.Helper.ManagmentCart;
import com.example.homepage.Helper.WishlistHelper;
import com.example.homepage.R;

public class DetailActivity extends AppCompatActivity {
    private Button addToCartBtn;
    private TextView titleTxt, feeTxt, descriptionTxt, reviewTxt, scoreTxt;
    private ImageView picItem, backArrow, bookmarkBtn;
    private LottieAnimationView cartAnimation;
    private PopularDomain object;
    private int numberOrder = 1;
    private ManagmentCart managmentCart;
    private boolean isBookmarked = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        managmentCart = new ManagmentCart(this);

        initView();
        getBundle();
        setupBookmarkButton();
    }

    private void initView() {
        addToCartBtn = findViewById(R.id.addToCartbtn);
        feeTxt = findViewById(R.id.priceTxt);
        titleTxt = findViewById(R.id.titleTxt);
        descriptionTxt = findViewById(R.id.descriptionTxt);
        picItem = findViewById(R.id.itemPic);
        reviewTxt = findViewById(R.id.reviewTxt);
        scoreTxt = findViewById(R.id.scoreTxt);
        backArrow = findViewById(R.id.imageView3);
        cartAnimation = findViewById(R.id.cartAnimation);
        bookmarkBtn = findViewById(R.id.imageView6);
    }

    private void getBundle() {
        object = (PopularDomain) getIntent().getSerializableExtra("object");
        if (object != null) {
            // Updated image loading logic
            String imageUrl = object.getPicUrl();
            if (imageUrl != null && (imageUrl.startsWith("http") || imageUrl.startsWith("https"))) {
                // Load from URL (e.g., Cloudinary)
                Glide.with(this)
                        .load(imageUrl)
                        .placeholder(R.drawable.ic_baseline_person_24)
                        .error(R.drawable.ic_baseline_person_24)
                        .into(picItem);
            } else {
                // Load from drawable
                int drawableResourceId = this.getResources().getIdentifier(imageUrl, "drawable", this.getPackageName());
                Glide.with(this)
                        .load(drawableResourceId)
                        .placeholder(R.drawable.ic_baseline_person_24)
                        .error(R.drawable.ic_baseline_person_24)
                        .into(picItem);
            }

            titleTxt.setText(object.getTitle());
            feeTxt.setText("$" + object.getPrice());
            descriptionTxt.setText(object.getDescription());
            reviewTxt.setText(String.valueOf(object.getReview()));
            scoreTxt.setText(String.valueOf(object.getScore()));

            // Check if item is already in wishlist
            isBookmarked = WishlistHelper.isInWishlist(this, object.getTitle());
            updateBookmarkIcon();
        }

        addToCartBtn.setOnClickListener(v -> {
            object.setNumberinCart(numberOrder);
            managmentCart.insertFood(object);
            cartAnimation.playAnimation();
            showAddToCartDialog();
        });

        backArrow.setOnClickListener(v -> startActivity(new Intent(DetailActivity.this, MainActivity.class)));
    }

    private void setupBookmarkButton() {
        bookmarkBtn.setOnClickListener(v -> {
            isBookmarked = !isBookmarked;
            updateBookmarkIcon();

            if (isBookmarked) {
                addToWishlist();
            } else {
                removeFromWishlist();
            }
        });
    }

    private void updateBookmarkIcon() {
        if (isBookmarked) {
            bookmarkBtn.setImageResource(R.drawable.bookmarkfilled);
            bookmarkBtn.setScaleX(1.1f);
            bookmarkBtn.setScaleY(1.1f);
        } else {
            bookmarkBtn.setImageResource(R.drawable.bookmark);
        }
    }

    private void addToWishlist() {
        if (object != null) {
            WishlistHelper.addToWishlist(this, object);
            Toast.makeText(this, "Added to wishlist", Toast.LENGTH_SHORT).show();
        }
    }

    private void removeFromWishlist() {
        if (object != null) {
            WishlistHelper.removeFromWishlist(this, object.getTitle());
            Toast.makeText(this, "Removed from wishlist", Toast.LENGTH_SHORT).show();
        }
    }

    private void showAddToCartDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Item Added")
                .setMessage("Your item has been added to the cart.")
                .setPositiveButton("OK", (dialog, which) -> dialog.dismiss())
                .show();
    }
}
