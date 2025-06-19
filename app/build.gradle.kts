plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
}

android {
    namespace = "com.example.motolicznik"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.motolicznik"
        minSdk = 24
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }
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
    kotlinOptions {
        jvmTarget = "11"
    }
    buildFeatures {
        viewBinding = true
        dataBinding = true
        compose = true // OK, można zostawić
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
    buildToolsVersion = "35.0.0"
}


dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material) // To jest biblioteka Material Components, nie Compose Material
    implementation(libs.androidx.constraintlayout)
    implementation(platform(libs.androidx.compose.bom)) // Poprawnie
    androidTestImplementation(platform(libs.androidx.compose.bom)) // Poprawnie
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview) // Użyj aliasu, który zdefiniowałeś w TOML (np. libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)          // Użyj aliasu (np. libs.androidx.material3)

    // Activity Compose - użyj aliasu, wersja jest zdefiniowana w TOML
    implementation(libs.androidx.activity.compose)

    // --- Testy ---
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)          // Użyj aliasu (np. libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core) // Użyj aliasu (np. libs.androidx.espresso.core)

    // testy UI dla Compose - użyj aliasów
    androidTestImplementation(libs.androidx.ui.test.junit4) // Użyj aliasu (np. libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)         // Użyj aliasu (np. libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)   // Użyj aliasu (np. libs.androidx.ui.test.manifest)
}