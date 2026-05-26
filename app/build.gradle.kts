import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.ksp)
    alias(libs.plugins.hiltAndroid)
    alias(libs.plugins.google.gms.google.services)
}

android {
    namespace = "com.kevinfreyap.jetspending"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.kevinfreyap.jetspending"
        minSdk = 25
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
        isCoreLibraryDesugaringEnabled = true
    }

    buildFeatures {
        compose = true
    }
}

kotlin{
    compilerOptions {
        jvmTarget.set(JvmTarget.JVM_11)
    }
}

dependencies {
    implementation(project(":data"))
    implementation(project(":domain"))

    implementation(libs.androidx.biometric.ktx)

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    implementation(libs.androidx.navigation.compose)
    implementation(libs.androidx.splash.screen)

    implementation(libs.androidx.paging.compose)

    implementation(libs.coil.compose)
    implementation(libs.coil.network.okhttp)

    implementation(libs.hilt.android)
    ksp(libs.hilt.android.compiler)
    ksp(libs.kotlinx.metadata.jvm)

    implementation(libs.vico.compose)
    implementation(libs.vico.compose.m3)

    coreLibraryDesugaring(libs.android.desugarJdkLibs)

    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)
}