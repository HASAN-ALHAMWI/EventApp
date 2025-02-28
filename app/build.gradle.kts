plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.hilt)
    alias(libs.plugins.kotlin.ksp)
    alias(libs.plugins.androidx.navigation.safeargs)
}

android {
    namespace = "com.hasan.eventapp"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.hasan.eventapp"
        minSdk = 26
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        ksp {
            arg("room.schemaLocation", "$projectDir/schemas")
        }
    }

    signingConfigs {
        create("release") {
            storeFile  = file("..\\EventKey")
            storePassword = "Event@123"
            keyAlias = "key0"
            keyPassword = "Key@123"
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = true
            isShrinkResources = true

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
    kotlinOptions {
        jvmTarget = "11"
    }
    buildFeatures {
        viewBinding = true
    }
}

dependencies {

    // ----------------- CORE DEPENDENCIES ----------------- //
    implementation(libs.androidx.core.ktx)

    // ----------------- UI DEPENDENCIES ----------------- //
    implementation(libs.bundles.ui)

    // ----------------- ARCHITECTURE COMPONENTS ----------------- //
    implementation(libs.bundles.lifecycle)

    // ----------------- NAVIGATION ----------------- //
    implementation(libs.bundles.navigation)

    // ----------------- NETWORKING ----------------- //
    implementation(libs.bundles.networking)

    // ----------------- DEPENDENCY INJECTION ----------------- //
    implementation(libs.bundles.hilt)
    ksp(libs.hilt.compiler)
    ksp(libs.androidx.hilt.compiler)

    // ----------------- DATA STORAGE ----------------- //
    implementation(libs.bundles.storage)
    ksp(libs.room.compiler)

    // ----------------- CONCURRENCY ----------------- //
    implementation(libs.bundles.coroutines)

    // ----------------- SECURITY ----------------- //
    implementation(libs.security.crypto)

    // ----------------- IMAGE LOADING ----------------- //
    implementation(libs.glide)

    // ----------------- TESTING ----------------- //
    testImplementation(libs.bundles.unit.testing)
    androidTestImplementation(libs.bundles.android.testing)
}