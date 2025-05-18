import java.io.FileInputStream
import java.util.Properties

// Define a variable for the keystore.properties file
val keystorePropertiesFile = rootProject.file("keystore.properties")
// Create a new Properties object
val keystoreProperties = Properties()

// Load the properties if the file exists
if (keystorePropertiesFile.exists()) {
    FileInputStream(keystorePropertiesFile).use { stream ->
        keystoreProperties.load(stream)
    }
}

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.ksp)
    alias(libs.plugins.hilt)
}

android {
    namespace = "com.example.lupath"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.lupath"
        minSdk = 28
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        setProperty("archivesBaseName", "LuPath-$versionName")
    }

    signingConfigs {
        getByName("debug") {
            // Android Studio manages the debug keystore by default.
        }
        create("release") {
            if (keystorePropertiesFile.exists() &&
                keystoreProperties.containsKey("storeFile") &&
                keystoreProperties.containsKey("storePassword") &&
                keystoreProperties.containsKey("keyAlias") &&
                keystoreProperties.containsKey("keyPassword")) {

                storeFile = file(keystoreProperties.getProperty("storeFile"))
                storePassword = keystoreProperties.getProperty("storePassword")
                keyAlias = keystoreProperties.getProperty("keyAlias")
                keyPassword = keystoreProperties.getProperty("keyPassword")
            } else {
                println("Warning: keystore.properties not found or missing required entries (storeFile, storePassword, keyAlias, keyPassword). Release build will not be signed with a release key.")
                // Optionally, throw an exception to fail the build if properties are missing:
                // throw GradleException("keystore.properties is missing or incomplete for the release build. Required keys: storeFile, storePassword, keyAlias, keyPassword.")
            }
        }
    }

    buildTypes {
        getByName("release") {
            isMinifyEnabled = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            signingConfig = signingConfigs.getByName("release")
        }
        getByName("debug") {
            // signingConfig = signingConfigs.getByName("debug") // This is usually default
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
}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    implementation(libs.androidx.foundation)
    implementation(libs.androidx.lifecycle.viewmodel.ktx)
    implementation(libs.androidx.lifecycle.viewmodel.compose)
    implementation(libs.androidx.navigation.compose)
    implementation(libs.accompanist.pager)
    implementation(libs.accompanist.pager.indicators)
    implementation(libs.kotlinx.coroutines.android)
//    implementation(libs.androidx.room.common.jvm)
//    implementation(libs.androidx.room.runtime.android)
    implementation(libs.androidx.room.runtime) // Uses the new alias
    implementation(libs.androidx.room.ktx)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)
    implementation(libs.hilt.android)
    implementation(libs.hilt.navigation.compose)
    ksp(libs.hilt.compiler)
    ksp(libs.androidx.room.compiler)
}
