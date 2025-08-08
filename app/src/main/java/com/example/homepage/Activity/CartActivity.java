package com.example.homepage.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.homepage.Adapters.CartListAdapter;
import com.example.homepage.Domain.PopularDomain;
import com.example.homepage.Helper.ChangeNumberItemsListener;
import com.example.homepage.Helper.ManagmentCart;
import com.example.homepage.Domain.OrderModel;
import com.example.homepage.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.UUID;

public class CartActivity extends AppCompatActivity {
    private RecyclerView.Adapter adapter;
    private RecyclerView recyclerView;
    private ManagmentCart managmentCart;
    private TextView totalFeeTxt, taxTxt, deliveryTxt, totalTxt, emptyTxt;
    private double tax;
    private Button orderBtn;
    private LinearLayout fl;
    private ImageView backBtn;
    private FirebaseFirestore db;
    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);

        managmentCart = new ManagmentCart(this);
        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();

        initView();
        initListeners();
        initList();
        calculateCart();
        bottomNavigation();
    }

    private void initListeners() {
        backBtn.setOnClickListener(v -> finish());

        orderBtn.setOnClickListener(v -> {
            if (!managmentCart.getListCart().isEmpty()) {
                String userId = auth.getCurrentUser() != null ? auth.getCurrentUser().getUid() : "guest";
                String userName = auth.getCurrentUser() != null ? auth.getCurrentUser().getDisplayName() : "Guest";

                OrderModel order = new OrderModel(
                        UUID.randomUUID().toString(),
                        userId,
                        userName,
                        managmentCart.getListCart(),
                        managmentCart.getTotalFee() + tax + 10,
                        "pending"
                );

                db.collection("orders").document(order.getOrderId()).set(order)
                        .addOnSuccessListener(aVoid -> {
                            managmentCart.clearCart();
                            startActivity(new Intent(CartActivity.this, OrderSuccessActivity.class));
                            finish();
                        })
                        .addOnFailureListener(e -> {
                            Toast.makeText(this, "Order failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        });
            } else {
                Toast.makeText(this, "Your cart is empty", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void initView() {
        totalFeeTxt = findViewById(R.id.totalfeetxt);
        taxTxt = findViewById(R.id.taxTxt);
        deliveryTxt = findViewById(R.id.deliveryTxt);
        totalTxt = findViewById(R.id.totalTxt);
        recyclerView = findViewById(R.id.view3);
        backBtn = findViewById(R.id.backcart);
        emptyTxt = findViewById(R.id.emptyTxt);
        fl = findViewById(R.id.fixedLayout);
        orderBtn = findViewById(R.id.orderbtn);
    }

    private void initList() {
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new CartListAdapter(managmentCart.getListCart(), this, () -> calculateCart());
        recyclerView.setAdapter(adapter);

        if (managmentCart.getListCart().isEmpty()) {
            emptyTxt.setVisibility(View.VISIBLE);
            fl.setVisibility(View.GONE);
        } else {
            emptyTxt.setVisibility(View.GONE);
            fl.setVisibility(View.VISIBLE);
        }
    }

    private void calculateCart() {
        double percentTax = 0.02;
        double delivery = 10;
        tax = Math.round(managmentCart.getTotalFee() * percentTax * 100.0) / 100.0;
        double total = Math.round((managmentCart.getTotalFee() + tax + delivery) * 100) / 100.0;
        double itemTotal = Math.round(managmentCart.getTotalFee() * 100) / 100.0;

        totalFeeTxt.setText("$" + itemTotal);
        taxTxt.setText("$" + tax);
        deliveryTxt.setText("$" + delivery);
        totalTxt.setText("$" + total);
    }

    private void bottomNavigation() {
        LinearLayout homeBtn = findViewById(R.id.homeBtn);
        LinearLayout profileBtn = findViewById(R.id.profilebtn);
        LinearLayout wishlistBtn = findViewById(R.id.wishlistBtn);

        homeBtn.setOnClickListener(v -> startActivity(new Intent(this, MainActivity.class)));
        profileBtn.setOnClickListener(v -> startActivity(new Intent(this, ProfileActivity.class)));
        wishlistBtn.setOnClickListener(v -> startActivity(new Intent(this, WishlistActivity.class)));
    }
}