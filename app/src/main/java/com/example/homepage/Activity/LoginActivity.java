package com.example.homepage.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.homepage.AdminPanel.AdminLoginActivity;
import com.example.homepage.R;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class LoginActivity extends AppCompatActivity {

    private static final String TAG = "LoginActivity";
    private static final int RC_SIGN_IN = 9001;

    private EditText email, password;
    private Button login_btn, btn_google_login;
    private TextView signup_text;
    private FirebaseAuth mAuth;
    private FirebaseDatabase database;
    private GoogleSignInClient googleSignInClient;
    private Button loginAsAdminBtn;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);

        // Initialize Firebase components
        mAuth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();

        // Configure Google Sign In
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.client_id))
                .requestEmail()
                .build();
        googleSignInClient = GoogleSignIn.getClient(this, gso);

        // Initialize UI components
        initializeViews();
        setupClickListeners();
    }

    private void initializeViews() {
        email = findViewById(R.id.loginemail);
        password = findViewById(R.id.loginpass);
        login_btn = findViewById(R.id.loginbtn);
        signup_text = findViewById(R.id.l_signup);
        btn_google_login = findViewById(R.id.btn_google_login);
        loginAsAdminBtn = findViewById(R.id.loginasadmin);

    }

    private void setupClickListeners() {
        signup_text.setOnClickListener(v -> {
            startActivity(new Intent(LoginActivity.this, RegistrationActivity.class));
        });

        login_btn.setOnClickListener(v -> handleEmailPasswordLogin());

        btn_google_login.setOnClickListener(v -> signInWithGoogle());
        loginAsAdminBtn.setOnClickListener(v -> { // NEW
            Intent intent = new Intent(LoginActivity.this, AdminLoginActivity.class);
            startActivity(intent);
        });
    }

    private void handleEmailPasswordLogin() {
        String userEmail = email.getText().toString().trim();
        String userPassword = password.getText().toString().trim();

        if (TextUtils.isEmpty(userEmail)) {
            email.setError("Email is required");
            return;
        }

        if (TextUtils.isEmpty(userPassword)) {
            password.setError("Password is required");
            return;
        }

        mAuth.signInWithEmailAndPassword(userEmail, userPassword)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        handleLoginSuccess();
                    } else {
                        handleLoginFailure(task.getException());
                    }
                });
    }

    private void signInWithGoogle() {
        Intent signInIntent = googleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                GoogleSignInAccount account = task.getResult(ApiException.class);
                firebaseAuthWithGoogle(account.getIdToken());
            } catch (ApiException e) {
                Log.w(TAG, "Google sign in failed", e);
                Toast.makeText(this, "Google sign in failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void firebaseAuthWithGoogle(String idToken) {
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser user = mAuth.getCurrentUser();
                        saveUserDataToDatabase(user);
                        handleLoginSuccess();
                    } else {
                        handleLoginFailure(task.getException());
                    }
                });
    }

    private void saveUserDataToDatabase(FirebaseUser user) {
        HashMap<String, Object> userData = new HashMap<>();
        userData.put("id", user.getUid());
        userData.put("name", user.getDisplayName());
        userData.put("email", user.getEmail());
        if (user.getPhotoUrl() != null) {
            userData.put("profile", user.getPhotoUrl().toString());
        }

        database.getReference().child("users").child(user.getUid()).setValue(userData)
                .addOnFailureListener(e -> Log.w(TAG, "Failed to save user data", e));
    }

    private void handleLoginSuccess() {
        Toast.makeText(LoginActivity.this, "Login successful!", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }

    private void handleLoginFailure(Exception exception) {
        Log.w(TAG, "Login failed", exception);
        String errorMessage = exception != null ? exception.getMessage() : "Unknown error occurred";
        Toast.makeText(LoginActivity.this, "Login failed: " + errorMessage, Toast.LENGTH_LONG).show();
    }

    @Override
    protected void onStart() {
        super.onStart();
        // Only redirect if not coming from another activity
        if (isTaskRoot()) {
            FirebaseUser currentUser = mAuth.getCurrentUser();
            if (currentUser != null) {
                startActivity(new Intent(this, MainActivity.class));
                finish();
            }
        }
    }
}
//package com.example.homepage.Activity;
//
//import android.content.Intent;
//import android.os.Bundle;
//import android.text.TextUtils;
//import android.view.View;
//import android.widget.Button;
//import android.widget.EditText;
//import android.widget.TextView;
//import android.widget.Toast;
//
//import androidx.activity.EdgeToEdge;
//import androidx.annotation.NonNull;
//import androidx.appcompat.app.AppCompatActivity;
//import androidx.core.graphics.Insets;
//import androidx.core.view.ViewCompat;
//import androidx.core.view.WindowInsetsCompat;
//
//import com.example.homepage.R;
//import com.google.android.gms.tasks.OnCompleteListener;
//import com.google.android.gms.tasks.Task;
//import com.google.firebase.auth.AuthResult;
//import com.google.firebase.auth.FirebaseAuth;
//import com.google.firebase.auth.FirebaseUser;
//
//public class LoginActivity extends AppCompatActivity {
//
//    EditText email, password;
//    Button login_btn;
//    TextView signup_text;
//    private FirebaseAuth mAuth;
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        EdgeToEdge.enable(this);
//        setContentView(R.layout.activity_login);
//
//        // Initialize Firebase Auth
//        mAuth = FirebaseAuth.getInstance();
//
////        // Check if user is already logged in
////        FirebaseUser currentUser = mAuth.getCurrentUser();
////        if(currentUser != null){
////            startActivity(new Intent(this, MainActivity.class));
////            finish();
////        }
//
//        email = findViewById(R.id.loginemail);
//        password = findViewById(R.id.loginpass);
//        login_btn = findViewById(R.id.loginbtn);
//        signup_text = findViewById(R.id.l_signup);
//
//        signup_text.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                startActivity(new Intent(LoginActivity.this, RegistrationActivity.class));
//            }
//        });
//
//        login_btn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                String userEmail = email.getText().toString().trim();
//                String userPassword = password.getText().toString().trim();
//
//                if (TextUtils.isEmpty(userEmail)) {
//                    email.setError("Email is required");
//                    return;
//                }
//
//                if (TextUtils.isEmpty(userPassword)) {
//                    password.setError("Password is required");
//                    return;
//                }
//
//
//                // Authenticate user
//                mAuth.signInWithEmailAndPassword(userEmail, userPassword)
//                        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
//                            @Override
//                            public void onComplete(@NonNull Task<AuthResult> task) {
//                                if (task.isSuccessful()) {
//                                    // Login success
//                                    Toast.makeText(LoginActivity.this,
//                                            "Login successful!", Toast.LENGTH_SHORT).show();
//                                    startActivity(new Intent(LoginActivity.this, MainActivity.class));
//                                    finish();
//                                } else {
//                                    // Login failed
//                                    Toast.makeText(LoginActivity.this,
//                                            "Login failed: " + task.getException().getMessage(),
//                                            Toast.LENGTH_LONG).show();
//                                }
//                            }
//                        });
//            }
//        });
//
//
//    }
//}
//
////package com.example.homepage.Activity;
////
////import android.content.Intent;
////import android.os.Bundle;
////import android.view.View;
////import android.widget.Button;
////import android.widget.EditText;
////import android.widget.TextView;
////
////import androidx.activity.EdgeToEdge;
////import androidx.appcompat.app.AppCompatActivity;
////import androidx.core.graphics.Insets;
////import androidx.core.view.ViewCompat;
////import androidx.core.view.WindowInsetsCompat;
////
////import com.example.homepage.R;
////
////public class LoginActivity extends AppCompatActivity {
////
////    EditText email,password;
////    Button login_btn;
////    TextView signup_text;
////
////
////
////    @Override
////    protected void onCreate(Bundle savedInstanceState) {
////        super.onCreate(savedInstanceState);
////        EdgeToEdge.enable(this);
////        setContentView(R.layout.activity_login);
////
////        email=findViewById(R.id.loginemail);
////        password=findViewById(R.id.loginpass);
////        login_btn=findViewById(R.id.loginbtn);
////        signup_text=findViewById(R.id.l_signup);
////
////        signup_text.setOnClickListener(new View.OnClickListener() {
////            @Override
////            public void onClick(View v) {
////                startActivity(new Intent(LoginActivity.this, RegistrationActivity.class));
////            }
////        });
////        login_btn.setOnClickListener(new View.OnClickListener() {
////            @Override
////            public void onClick(View v) {
////                startActivity(new Intent(LoginActivity.this, MainActivity.class));
////            }
////        });
////    }
////
////}