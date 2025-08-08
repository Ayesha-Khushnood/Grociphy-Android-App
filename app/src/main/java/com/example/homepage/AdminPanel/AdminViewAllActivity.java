package com.example.homepage.AdminPanel;

import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.homepage.Adapters.AdminProductAdapter;
import com.example.homepage.Domain.PopularDomain;
import com.example.homepage.Helper.CloudinaryConfig;
import com.example.homepage.R;
import com.cloudinary.android.MediaManager;
import com.cloudinary.android.callback.ErrorInfo;
import com.cloudinary.android.callback.UploadCallback;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class AdminViewAllActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private AdminProductAdapter adapter;
    private FirebaseFirestore db;
    private ProgressBar progressBar;
    private ActivityResultLauncher<String> imagePickerLauncher;
    private EditProductDialog currentDialog;
    private String oldImageUrl; // To store previous image URL during updates

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        CloudinaryConfig.initialize(this);
        setContentView(R.layout.activity_admin_view_all);

        db = FirebaseFirestore.getInstance();
        progressBar = findViewById(R.id.progressBar);
        recyclerView = findViewById(R.id.recyclerViewAdmin);

        imagePickerLauncher = registerForActivityResult(
                new ActivityResultContracts.GetContent(),
                uri -> {
                    if (uri != null && currentDialog != null) {
                        currentDialog.handleImageSelection(uri);
                    }
                }
        );

        setupRecyclerView();
        loadProducts();
    }

    private void setupRecyclerView() {
        adapter = new AdminProductAdapter(new ArrayList<>(), this::showEditDialog, this::deleteProduct);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);
    }

    private void loadProducts() {
        progressBar.setVisibility(View.VISIBLE);
        db.collection("products")
                .get()
                .addOnCompleteListener(task -> {
                    progressBar.setVisibility(View.GONE);
                    if (task.isSuccessful()) {
                        ArrayList<PopularDomain> products = new ArrayList<>();
                        for (DocumentSnapshot document : task.getResult().getDocuments()) {
                            products.add(new PopularDomain(
                                    document.getId(),
                                    document.getString("title"),
                                    document.getString("description"),
                                    document.getString("picUrl"),
                                    document.getLong("score").intValue(),
                                    document.getLong("review").intValue(),
                                    document.getDouble("price")
                            ));
                        }
                        adapter.updateProducts(products);
                    } else {
                        Toast.makeText(this, "Error loading products", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void showEditDialog(PopularDomain product) {
        // Store the old image URL before any potential updates
        oldImageUrl = product.getPicUrl();

        currentDialog = new EditProductDialog(
                this,
                product,
                updatedProduct -> {
                    // Check if image was changed
                    if (!updatedProduct.getPicUrl().equals(oldImageUrl)) {
                        // Upload new image first, then update product
                        uploadNewImageAndUpdateProduct(updatedProduct);
                    } else {
                        // No image change, just update product
                        updateProductInFirestore(updatedProduct);
                    }
                    adapter.updateProduct(updatedProduct);
                    currentDialog = null;
                },
                imagePickerLauncher
        );
        currentDialog.show();
    }

    private void uploadNewImageAndUpdateProduct(PopularDomain product) {
        // First delete old image if it exists and is from Cloudinary
        if (oldImageUrl != null && oldImageUrl.contains("res.cloudinary.com")) {
            deleteImageFromCloudinary(oldImageUrl, () -> {
                // After old image is deleted, update product in Firestore
                updateProductInFirestore(product);
            });
        } else {
            // No old image to delete, just update
            updateProductInFirestore(product);
        }
    }

    private void deleteProduct(String productId) {
        // First get the product to get the image URL before deleting
        db.collection("products").document(productId)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        String picUrl = documentSnapshot.getString("picUrl");

                        // Delete from Firestore
                        db.collection("products").document(productId)
                                .delete()
                                .addOnSuccessListener(aVoid -> {
                                    adapter.removeProduct(productId);
                                    Toast.makeText(this, "Product deleted", Toast.LENGTH_SHORT).show();

                                    // Delete image from Cloudinary if URL is valid
                                    if (picUrl != null && picUrl.contains("res.cloudinary.com")) {
                                        deleteImageFromCloudinary(picUrl, null);
                                    }
                                })
                                .addOnFailureListener(e ->
                                        Toast.makeText(this, "Delete failed", Toast.LENGTH_SHORT).show());
                    }
                })
                .addOnFailureListener(e ->
                        Toast.makeText(this, "Error getting product for deletion", Toast.LENGTH_SHORT).show());
    }

    private void deleteImageFromCloudinary(String imageUrl, Runnable onSuccess) {
        new Thread(() -> {
            try {
                // Extract publicId from the image URL
                String[] parts = imageUrl.split("/");
                String publicIdWithExtension = parts[parts.length - 1];
                String publicId = publicIdWithExtension.split("\\.")[0];

                // Delete image from Cloudinary
                Map result = MediaManager.get().getCloudinary().uploader().destroy(publicId, new HashMap());

                runOnUiThread(() -> {
                    Toast.makeText(AdminViewAllActivity.this,
                            "Old image deleted from Cloudinary",
                            Toast.LENGTH_SHORT).show();
                    if (onSuccess != null) {
                        onSuccess.run();
                    }
                });

            } catch (Exception e) {
                runOnUiThread(() ->
                        Toast.makeText(AdminViewAllActivity.this,
                                "Failed to delete old image: " + e.getMessage(),
                                Toast.LENGTH_SHORT).show());
            }
        }).start();
    }

    private void updateProductInFirestore(PopularDomain product) {
        Map<String, Object> updateData = new HashMap<>();
        updateData.put("title", product.getTitle());
        updateData.put("description", product.getDescription());
        updateData.put("price", product.getPrice());
        updateData.put("score", product.getScore());
        updateData.put("review", product.getReview());
        updateData.put("picUrl", product.getPicUrl());

        db.collection("products").document(product.getId())
                .update(updateData)
                .addOnSuccessListener(aVoid ->
                        Toast.makeText(this, "Product updated", Toast.LENGTH_SHORT).show())
                .addOnFailureListener(e ->
                        Toast.makeText(this, "Update failed", Toast.LENGTH_SHORT).show());
    }
}