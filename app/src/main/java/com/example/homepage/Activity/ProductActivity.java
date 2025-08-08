package com.example.homepage.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.homepage.Adapters.PopularListAdapter;
import com.example.homepage.Adapters.ProductAdapter;
import com.example.homepage.Domain.PopularDomain;
import com.example.homepage.R;
import com.google.firebase.FirebaseApp;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;

public class ProductActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private ProductAdapter adapter;
    private FirebaseFirestore db;
    private EditText searchEditText;
    private ImageView backArrow;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product);

        // Firebase initialization
        if (FirebaseApp.getApps(this).isEmpty()) {
            FirebaseApp.initializeApp(this);
        }
        db = FirebaseFirestore.getInstance();

        initializeViews();
        setupRecyclerView();
        setupSearch();
        fetchProducts();
    }

    private void initializeViews() {
        recyclerView = findViewById(R.id.recyclerViewProducts);
        searchEditText = findViewById(R.id.editTextText);
        backArrow = findViewById(R.id.backArrow);
        backArrow.setOnClickListener(v -> startActivity(new Intent(ProductActivity.this, MainActivity.class)));
    }

    private void setupRecyclerView() {
        adapter = new ProductAdapter(new ArrayList<>());
        recyclerView.setLayoutManager(new GridLayoutManager(this, 2));
        recyclerView.setAdapter(adapter);

    }

    private void setupSearch() {
        searchEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                adapter.getFilter().filter(s);
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });
    }

    private void fetchProducts() {
        db.collection("products")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        ArrayList<PopularDomain> products = new ArrayList<>();
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            PopularDomain product = new PopularDomain(
                                    document.getString("title"),
                                    document.getString("description"),
                                    document.getString("picUrl"),
                                    document.getLong("review").intValue(),
                                    document.getLong("score").intValue(),
                                    document.getDouble("price")
                            );
                            products.add(product);
                        }
                        adapter.updateProducts(products);
                    } else {
                        showError("Error fetching products: " + task.getException());
                    }
                });
    }

    private void showError(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}

