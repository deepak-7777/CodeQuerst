//plugins {
//    alias(libs.plugins.android.application)
//    alias(libs.plugins.google.gms.google.services)
//}
//
//android {
//
//    signingConfigs {
//        create("release") {
//            storeFile = file("C:\\Users\\globa\\OneDrive\\Desktop\\CodeQuerst")
//            storePassword = "vijay@maya#"
//            keyPassword = "vijay@maya#"
//            keyAlias = "key0"
//        }
//    }
//
//    namespace = "com.vmpk.codequerst"
//    compileSdk = 36
//
//    defaultConfig {
//        applicationId = "com.vmpk.codequerst"
//        minSdk = 24
//        targetSdk = 36
//        versionCode = 5
//        versionName = "1.4"
//        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
//    }
//
//
//    buildTypes {
//        release {
//            isMinifyEnabled = false
//            proguardFiles(
//                getDefaultProguardFile("proguard-android-optimize.txt"),
//                "proguard-rules.pro"
//            )
//            signingConfig = signingConfigs.getByName("release")
//        }
//    }
//
//    compileOptions {
//        sourceCompatibility = JavaVersion.VERSION_11
//        targetCompatibility = JavaVersion.VERSION_11
//    }
//
//    buildFeatures {
//        prefab = false
//    }
//
//    // 🔥 REQUIRED FOR 16 KB MEMORY PAGE SUPPORT
//    packagingOptions {
//        jniLibs {
//            useLegacyPackaging = false
//        }
//    }
//}
//
//dependencies {
//
//    implementation(libs.appcompat)
//    implementation(libs.material)
//    implementation(libs.constraintlayout)
//    implementation(libs.firebase.auth)
//
//    testImplementation(libs.junit)
//    androidTestImplementation(libs.ext.junit)
//    androidTestImplementation(libs.espresso.core)
//
//    implementation("com.google.firebase:firebase-database:20.3.0")
//    implementation("com.google.code.gson:gson:2.9.1")
//
//    // Firebase BOM
//    implementation(platform("com.google.firebase:firebase-bom:33.16.0"))
//    implementation("com.google.firebase:firebase-auth")
//
//    // Google Sign-In (UPDATED ✅)
//    implementation("androidx.credentials:credentials:1.3.0")
//    implementation("androidx.credentials:credentials-play-services-auth:1.3.0")
//   implementation("com.google.android.libraries.identity.googleid:googleid:1.1.1")
//    implementation("com.google.android.gms:play-services-auth:21.2.0")
//
////    implementation("com.google.android.gms:play-services-auth:22.0.0")
//
//    // Firestore
//    implementation("com.google.firebase:firebase-firestore:24.4.4")
//
//    // Cloudinary (keep for now)
//    implementation("com.cloudinary:cloudinary-android:3.1.1")
//
//    implementation("com.github.bumptech.glide:glide:4.15.1")
//
//    implementation("com.google.android.material:material:1.10.0")
//    implementation("com.github.ismaeldivita:chip-navigation-bar:1.4.0")
//    implementation("androidx.swiperefreshlayout:swiperefreshlayout:1.1.0")
//    implementation("androidx.core:core-splashscreen:1.0.1")
//    implementation("de.hdodenhof:circleimageview:3.1.0")
//}

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.google.gms.google.services)
}

android {

    signingConfigs {
        create("release") {
            storeFile = file("C:\\Users\\globa\\OneDrive\\Desktop\\CodeQuerst")
            storePassword = "vijay@maya#"
            keyPassword = "vijay@maya#"
            keyAlias = "key0"
        }
    }

    namespace = "com.vmpk.codequerst"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.vmpk.codequerst"
        minSdk = 24
        targetSdk = 36

        versionCode = 9
        versionName = "1.8"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        // ✅ Only modern 64-bit ABIs (helps 16 KB issue)
        ndk {
            abiFilters += listOf("arm64-v8a", "x86_64")
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            signingConfig = signingConfigs.getByName("release")
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }

    // ✅ Correct AGP 8+ way for native libs (16 KB memory page support)
    packaging {
        jniLibs {
            useLegacyPackaging = false
        }
    }
}

dependencies {

    // AndroidX
    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.constraintlayout)
    implementation(libs.activity)
    implementation("androidx.swiperefreshlayout:swiperefreshlayout:1.1.0")
    implementation("androidx.core:core-splashscreen:1.0.1")

    // Firebase (BOM handles versions)
    implementation(platform("com.google.firebase:firebase-bom:33.16.0"))
    implementation("com.google.firebase:firebase-auth")
    implementation("com.google.firebase:firebase-database")
    implementation("com.google.firebase:firebase-firestore")

    // Google Sign-In
    implementation("androidx.credentials:credentials:1.3.0")
    implementation("androidx.credentials:credentials-play-services-auth:1.3.0")
    implementation("com.google.android.libraries.identity.googleid:googleid:1.1.1")
    implementation("com.google.android.gms:play-services-auth:21.2.0")

    // Image libraries
    implementation("com.github.bumptech.glide:glide:4.15.1")
//    implementation("com.cloudinary:cloudinary-android:3.1.1")

    // UI
    implementation("de.hdodenhof:circleimageview:3.1.0")
    implementation("com.github.ismaeldivita:chip-navigation-bar:1.4.0")

    // Utils
    implementation("com.google.code.gson:gson:2.9.1")

    // Testing
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)

    ///  for cloundiary
    implementation("com.cloudinary:cloudinary-android:2.2.0") {
        exclude(group = "com.facebook.fresco")
    }

    implementation("com.github.yalantis:ucrop:2.2.8")
}
