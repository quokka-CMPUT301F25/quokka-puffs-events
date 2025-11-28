plugins {
    alias(libs.plugins.android.application)
    id("com.google.gms.google-services")
    alias(libs.plugins.map.secret)
}

android {
    namespace = "com.example.quokkapuffevents"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.example.quokkapuffevents"
        minSdk = 26
        targetSdk = 36
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

    buildFeatures {
        viewBinding = true
        buildConfig = true
    }
}

dependencies {

    implementation(platform("com.google.firebase:firebase-bom:34.3.0"))
    implementation("com.google.firebase:firebase-storage")
    implementation("com.google.firebase:firebase-messaging")      // FCM
    implementation("com.google.firebase:firebase-firestore")      // Firestore DB
    implementation("com.google.firebase:firebase-analytics")      // Analytics (OK to keep)
    implementation("com.google.firebase:firebase-storage")        // Storage (OK to keep)
    implementation("com.google.firebase:firebase-database")       // Realtime DB (if used)
    implementation("com.opencsv:opencsv:5.9")
    implementation(libs.ext.junit)
    implementation(libs.espresso.core)
    implementation(libs.firebase.database)
    implementation(libs.espresso.intents)
    implementation(libs.firebase.storage)
    implementation(libs.firebase.messaging)
    implementation(libs.play.services.maps)

    // Location
    implementation(libs.google.maps)

    // These were changed from implementation to androidTestImplementation
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.runner)
    androidTestImplementation(libs.espresso.core)
    androidTestImplementation("androidx.fragment:fragment-testing:1.6.2")
    implementation("com.squareup.okhttp3:okhttp:3.14.9")

    implementation("com.google.android.gms:play-services-base:18.4.0")
    implementation("com.google.android.gms:play-services-auth:20.7.0")  // only if using Google Sign-In


    // AndroidX Core Dependencies
    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)
    implementation(libs.firebase.firestore)

    // QR Code Scanner (SAFE TO KEEP)
    implementation("com.journeyapps:zxing-android-embedded:4.1.0")

    // For unit testing
    testImplementation(libs.junit)

    // For instrumented testing
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
    androidTestImplementation(libs.runner)
}
