plugins {
    alias(libs.plugins.android.application)
}

android {
    namespace = "com.radach.gpstrackerreal"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.radach.gpstrackerreal"
        minSdk = 28
        targetSdk = 35
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
    implementation(libs.play.services.location)
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)

    implementation(libs.appcompat.v171)
    implementation(libs.material.v190)
    implementation(libs.constraintlayout.v221)

    // Google Play Services for Location
    implementation(libs.play.services.location.v2000)

    // HTTP client for API calls
    implementation(libs.okhttp)
    implementation(libs.logging.interceptor)
}