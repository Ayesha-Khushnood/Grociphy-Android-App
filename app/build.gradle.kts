plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.google.gms.google.services)
}

android {
    namespace = "com.example.homepage"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.homepage"
        minSdk = 25
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
}

dependencies {
    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)
    implementation(libs.filament.android)
    implementation(libs.firebase.auth)
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)

    // Image Loading Libraries
    implementation("com.github.bumptech.glide:glide:4.12.0")
    annotationProcessor("com.github.bumptech.glide:compiler:4.12.0")
    implementation("com.squareup.picasso:picasso:2.71828")

    // Firebase Dependencies
    implementation(platform("com.google.firebase:firebase-bom:33.12.0"))
    implementation("com.google.firebase:firebase-auth")
    implementation("com.google.firebase:firebase-database")
    implementation("com.google.firebase:firebase-firestore:24.10.0")
    implementation("com.google.firebase:firebase-analytics")

    // Other Libraries
    implementation("com.airbnb.android:lottie:6.1.0")
    implementation("com.google.code.gson:gson:2.8.9")
    implementation("com.cloudinary:cloudinary-android:2.3.1")
    implementation("androidx.credentials:credentials:1.3.0")
    implementation("androidx.credentials:credentials-play-services-auth:1.3.0")
    implementation("com.google.android.libraries.identity.googleid:googleid:1.1.1")
    implementation("com.google.android.gms:play-services-auth:20.7.0")
    implementation ("androidx.activity:activity-ktx:1.6.0") // Use the latest version
    implementation ("androidx.activity:activity:1.6.0")  // Use the latest version
    implementation ("com.github.PhilJay:MPAndroidChart:v3.1.0")
    implementation ("com.itextpdf:itextg:5.5.10")
    implementation ("androidx.core:core:1.6.0")

    // Picasso or Glide (only keep one)
    // implementation("com.squareup.picasso:picasso:2.71828") // You can choose either Picasso or Glide
}