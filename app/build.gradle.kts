plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.google.gms.google.services)
}

android {
    namespace = "com.example.codequerst"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.example.codequerst"
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

    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)
    implementation(libs.firebase.auth)
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
    implementation ("com.google.firebase:firebase-database:20.3.0")
    //  for email
    implementation("com.google.code.gson:gson:2.9.1")
/// for google
    implementation(platform("com.google.firebase:firebase-bom:33.16.0"))
    implementation("com.google.firebase:firebase-auth")
    implementation("androidx.credentials:credentials:1.3.0")
    implementation("androidx.credentials:credentials-play-services-auth:1.3.0")
    implementation("com.google.android.libraries.identity.googleid:googleid:1.1.1")
    implementation ("com.google.android.gms:play-services-auth:20.6.0")

    /// firestore
    implementation("com.google.firebase:firebase-firestore:24.4.4")

    ///   Cloudinary
//    implementation("com.cloudinary:cloudinary-android:3.1.1")
//    implementation("com.github.bumptech.glide:glide:4.15.1")

    implementation("com.cloudinary:cloudinary-android:3.1.1")
    implementation("com.github.bumptech.glide:glide:4.15.1")

             ///    profile image ko circle shape mai lane ke liye
    implementation("com.google.android.material:material:1.10.0")
}