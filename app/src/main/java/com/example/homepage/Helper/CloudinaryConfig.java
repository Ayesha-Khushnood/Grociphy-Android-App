package com.example.homepage.Helper;

import android.content.Context;
import com.cloudinary.android.MediaManager;
import java.util.HashMap;
import java.util.Map;

public class CloudinaryConfig {

    private static boolean isInitialized = false;  // Flag to track initialization

    public static void initialize(Context context) {
        // Only initialize if it hasn't been initialized yet
        if (!isInitialized) {
            Map<String, String> config = new HashMap<>();
            config.put("cloud_name", "dqqyanyk6");
            config.put("api_key", "811689271942421"); // API Key
            config.put("api_secret", "uwTeVaflSS_LSJ7EgiiiGNBfR90");

            MediaManager.init(context, config);  // Initialize Cloudinary
            isInitialized = true;  // Set the flag to true after initialization
        }
    }
}
