plugins {
    alias(libs.plugins.android.application)
    id("com.google.gms.google-services")
    alias(libs.plugins.google.android.libraries.mapsplatform.secrets.gradle.plugin)
}

android {
    namespace = "com.example.quokkapuffevents"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.example.quokkapuffevents"
        minSdk = 24
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
    }
}

dependencies {

    implementation(platform("com.google.firebase:firebase-bom:34.3.0"))
    implementation("com.google.firebase:firebase-storage")
    implementation("com.opencsv:opencsv:5.9")
    implementation(libs.ext.junit)
    implementation(libs.espresso.core)
    implementation(libs.firebase.database)
    implementation(libs.espresso.intents)
    implementation(libs.firebase.storage)
    implementation(libs.firebase.messaging)
    implementation(libs.play.services.maps)
    // These were changed from implementation to androidTestImplementation
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.runner)
    androidTestImplementation(libs.espresso.core)
    androidTestImplementation("androidx.fragment:fragment-testing:1.6.2")
    implementation("com.google.firebase:firebase-messaging:23.1.1")
    implementation("com.squareup.okhttp3:okhttp:3.14.9")



    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)
    implementation(libs.firebase.firestore)

    // For local unit tests (in src/test/java)
    testImplementation(libs.junit)

    // For instrumented tests (in src/androidTest/java)
    // You might have duplicates after the change, you can keep just one set
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)

    //For QR Codes
    implementation("com.journeyapps:zxing-android-embedded:4.1.0")
}
