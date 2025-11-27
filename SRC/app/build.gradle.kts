plugins {
    alias(libs.plugins.android.application)
    id("com.google.gms.google-services")   // REQUIRED for Firebase
    alias(libs.plugins.google.android.libraries.mapsplatform.secrets.gradle.plugin)
}

android {
    namespace = "com.example.quokkapuffevents"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.example.quokkapuffevents"
        minSdk = 33
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            // OPTIONAL – disable for debugging
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

    buildFeatures {
        viewBinding = true
    }
}

dependencies {

    // ➤ Only ONE Firebase BOM – controls the versions automatically
    implementation(platform("com.google.firebase:firebase-bom:34.3.0"))

    // ➤ Core Firebase dependencies – SO important for notifications!
    implementation("com.google.firebase:firebase-messaging")      // FCM
    implementation("com.google.firebase:firebase-firestore")      // Firestore DB
    implementation("com.google.firebase:firebase-analytics")      // Analytics (OK to keep)
    implementation("com.google.firebase:firebase-storage")        // Storage (OK to keep)
    implementation("com.google.firebase:firebase-database")       // Realtime DB (if used)

    implementation("com.google.android.gms:play-services-base:18.4.0")
    implementation("com.google.android.gms:play-services-auth:20.7.0")  // only if using Google Sign-In


    // AndroidX Core Dependencies
    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)

    // Maps API (SAFE TO KEEP)
    implementation(libs.play.services.maps)

    // QR Code Scanner (SAFE TO KEEP)
    implementation("com.journeyapps:zxing-android-embedded:4.1.0")

    // For unit testing
    testImplementation(libs.junit)

    // For instrumented testing
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
    androidTestImplementation(libs.runner)
    androidTestImplementation("androidx.fragment:fragment-testing:1.6.2")
}
