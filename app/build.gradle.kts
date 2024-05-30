plugins {
    alias(libs.plugins.androidApplication)
    id("com.google.gms.google-services")
}

android {
    namespace = "com.example.photoeditingapp"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.photoeditingapp"
        minSdk = 21
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"
        multiDexEnabled = true
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        renderscriptTargetApi = 21
        renderscriptSupportModeEnabled = true
    }

    buildFeatures{
        viewBinding = true
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    buildFeatures {
        viewBinding = true
    }
}

dependencies {

    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)
    implementation(libs.firebase.firestore)
    implementation(libs.firebase.database)
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)

    implementation(fileTree(mapOf("dir" to "libs", "include" to listOf("*.jar"))))
    implementation(files("/libs/ds-photo-editor-sdk-v10.aar"))
    implementation("androidx.appcompat:appcompat:1.1.1")
    implementation("io.reactivex.rxjava2:rxjava:2.1.0")
    implementation("io.reactivex.rxjava2:rxandroid:2.0.1")
    implementation("com.github.bumptech.glide:glide:4.14.2")
    annotationProcessor("com.github.bumptech.glide:compiler:4.14.2")

    // firebase
    implementation("com.google.firebase:firebase-database:20.3.1")
    implementation("com.google.firebase:firebase-storage:20.3.0")
    implementation("com.google.firebase:firebase-messaging:24.0.0")
    implementation("com.google.firebase:firebase-firestore:25.0.0")


    implementation("com.intuit.sdp:sdp-android:1.0.6");
    implementation("com.intuit.ssp:ssp-android:1.0.6");
    implementation("com.makeramen:roundedimageview:2.3.0");

    // multi dex
    implementation("androidx.multidex:multidex:2.0.1")
}