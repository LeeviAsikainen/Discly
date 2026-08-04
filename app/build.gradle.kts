plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)      // 🔥 TÄRKEÄ FIX
    alias(libs.plugins.kotlin.compose)
}

android {
    namespace = "com.example.discly"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.discly"
        minSdk = 24
        targetSdk = 34

        // 🔥 CI versionointi (turvallinen fallback)
        val versionCodeCI = (System.getenv("GITHUB_RUN_NUMBER") ?: "1").toInt()

        versionCode = versionCodeCI
        versionName = "1.0.$versionCodeCI"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        getByName("release") {
            isMinifyEnabled = false

            // ⚠️ debug signing jotta CI build toimii
            signingConfig = signingConfigs.getByName("debug")

            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }

        getByName("debug") {
            isMinifyEnabled = false
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
        compose = true
    }

    // 🔥 TÄRKEÄ Compose yhteensopivuus
    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.14"
    }
}

dependencies {
    implementation("androidx.datastore:datastore-preferences:1.0.0")
    implementation("com.google.code.gson:gson:2.10.1")

    // Compose BOM
    implementation(platform(libs.androidx.compose.bom))

    implementation("androidx.compose.material:material-icons-extended")

    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.appcompat)
    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)

    testImplementation(libs.junit)

    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.compose.ui.test.junit4)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(libs.androidx.junit)

    debugImplementation(libs.androidx.compose.ui.tooling)
    debugImplementation(libs.androidx.compose.ui.test.manifest)
}
