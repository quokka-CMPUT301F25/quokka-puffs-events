plugins {
    alias(libs.plugins.android.application)
    id("com.google.gms.google-services")
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
}

dependencies {

    implementation(platform("com.google.firebase:firebase-bom:34.3.0"))
    implementation(libs.ext.junit)
    implementation(libs.espresso.core)
    // These were changed from implementation to androidTestImplementation
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.runner)
    androidTestImplementation(libs.espresso.core)
    androidTestImplementation("androidx.fragment:fragment-testing:1.6.2")


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
}
