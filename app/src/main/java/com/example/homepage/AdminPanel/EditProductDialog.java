package com.example.homepage.AdminPanel;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.annotation.NonNull;

import com.bumptech.glide.Glide;
import com.cloudinary.android.MediaManager;
import com.cloudinary.android.callback.ErrorInfo;
import com.cloudinary.android.callback.UploadCallback;
import com.example.homepage.Domain.PopularDomain;
import com.example.homepage.R;

import java.util.Map;

public class EditProductDialog extends Dialog {

    private final PopularDomain product;
    private final UpdateListener updateListener;
    private final ActivityResultLauncher<String> imagePickerLauncher;

    private ImageView productImage;
    private EditText etTitle, etPrice, etDescription, etRating, etReviews;
    private Button btnUpdate, btnChangeImage;
    private Uri newImageUri;

    public EditProductDialog(@NonNull Context context,
                             PopularDomain product,
                             UpdateListener updateListener,
                             ActivityResultLauncher<String> imagePickerLauncher) {
        super(context);
        this.product = product;
        this.updateListener = updateListener;
        this.imagePickerLauncher = imagePickerLauncher;
        setContentView(R.layout.dialog_edit_product);

        Window window = getWindow();
        if (window != null) {
            window.setLayout(
                    WindowManager.LayoutParams.MATCH_PARENT,
                    WindowManager.LayoutParams.WRAP_CONTENT
            );
            window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }

        initializeViews();
        populateFields();
    }

    private void initializeViews() {
        productImage = findViewById(R.id.editProductImage);
        etTitle = findViewById(R.id.EditItemName);
        etPrice = findViewById(R.id.EditItemPrice);
        etDescription = findViewById(R.id.EditDescription);
        etRating = findViewById(R.id.EditRating);
        etReviews = findViewById(R.id.EditReviews);
        btnUpdate = findViewById(R.id.UpdateItembtn);
        btnChangeImage = findViewById(R.id.EditChooseImage);

        btnChangeImage.setOnClickListener(v -> openImageChooser());
        btnUpdate.setOnClickListener(v -> validateAndUpdate());
    }

    private void populateFields() {
        etTitle.setText(product.getTitle());
        etPrice.setText(String.valueOf(product.getPrice()));
        etDescription.setText(product.getDescription());
        etRating.setText(String.valueOf(product.getScore()));
        etReviews.setText(String.valueOf(product.getReview()));

        Glide.with(getContext())
                .load(product.getPicUrl())
                .placeholder(R.drawable.ic_baseline_person_24)
                .error(R.drawable.ic_baseline_person_24)
                .into(productImage);
    }

    private void openImageChooser() {
        imagePickerLauncher.launch("image/*");
    }

    // ✅ This method allows AdminViewAllActivity to send selected image URI back to this dialog
    public void handleImageSelection(Uri imageUri) {
        newImageUri = imageUri;
        Glide.with(getContext())
                .load(imageUri)
                .into(productImage);
    }

    private void validateAndUpdate() {
        if (!validateInputs()) return;

        if (newImageUri != null) {
            uploadNewImage();
        } else {
            updateProductData(product.getPicUrl());
        }
    }

    private boolean validateInputs() {
        try {
            Double.parseDouble(etPrice.getText().toString());
            Double.parseDouble(etRating.getText().toString());
            Integer.parseInt(etReviews.getText().toString());
            return true;
        } catch (NumberFormatException e) {
            Toast.makeText(getContext(), "Invalid number format", Toast.LENGTH_SHORT).show();
            return false;
        }
    }

    private void uploadNewImage() {
        MediaManager.get().upload(newImageUri)
                .callback(new UploadCallback() {
                    @Override
                    public void onStart(String requestId) {
                        showToast("Uploading image...");
                    }

                    @Override
                    public void onProgress(String requestId, long bytes, long totalBytes) {
                        // Optional: progress handling
                    }

                    @Override
                    public void onSuccess(String requestId, Map resultData) {
                        String newUrl = (String) resultData.get("secure_url");
                        updateProductData(newUrl);
                    }

                    @Override
                    public void onError(String requestId, ErrorInfo error) {
                        showToast("Upload failed: " + error.getDescription());
                    }

                    @Override
                    public void onReschedule(String requestId, ErrorInfo error) {
                        showToast("Upload rescheduled");
                    }
                })
                .dispatch();
    }

    private void updateProductData(String imageUrl) {
        try {
            PopularDomain updatedProduct = new PopularDomain(
                    product.getId(),
                    etTitle.getText().toString().trim(),
                    etDescription.getText().toString().trim(),
                    imageUrl,
                    Integer.parseInt(etRating.getText().toString().trim()),
                    Integer.parseInt(etReviews.getText().toString().trim()),
                    Double.parseDouble(etPrice.getText().toString().trim())
            );

            updateListener.onUpdate(updatedProduct);
            dismiss();
        } catch (NumberFormatException e) {
            showToast("Invalid number format");
        }
    }

    private void showToast(String message) {
        Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
    }

    public interface UpdateListener {
        void onUpdate(PopularDomain updatedProduct);
    }
}
