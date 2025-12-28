import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import java.io.FileInputStream
import java.util.Properties

plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.ksp)
    alias(libs.plugins.hiltAndroid)
}

android {
    namespace = "com.kevinfreyap.data"
    compileSdk = 36

    defaultConfig {
        minSdk = 25

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        consumerProguardFiles("consumer-rules.pro")

        val localProperties = Properties()
        val localPropertiesFile = rootProject.file("local.properties")
        if (localPropertiesFile.exists()){
            localProperties.load(FileInputStream(localPropertiesFile))
        }

        buildConfigField("String", "CURRENCY_URL", "\"https://%s.currency-api.pages.dev/v1/\"")
        buildConfigField("String", "WEB_CLIENT_ID", localProperties.getProperty("GOOGLE_WEB_CLIENT_ID") ?: "")
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
        buildConfig = true
    }
}

kotlin {
    compilerOptions {
        jvmTarget.set(JvmTarget.JVM_11)
    }
}

dependencies {
    implementation(project(":domain"))

    api(libs.androidx.credentials)
    api(libs.androidx.credentials.play.services.auth)
    api(platform(libs.firebase.bom))
    implementation(libs.googleid)
    implementation(libs.firebase.firestore)
    implementation(libs.firebase.fireauth)
    implementation(libs.coroutine.play.services)

    implementation(libs.androidx.datastore)
    implementation(libs.androidx.datastore.preference)

    implementation(libs.hilt.android)
    ksp(libs.hilt.android.compiler)
    api(libs.hilt.navigation)

    implementation(libs.androidx.room.runtime)
    implementation(libs.androidx.room.ktx)
    ksp(libs.androidx.room.compiler)

    implementation(libs.androidx.paging.runtime)
    implementation(libs.retrofit)
    implementation(libs.converter.gson)
    implementation(libs.logging.interceptor)

    coreLibraryDesugaring(libs.android.desugarJdkLibs)

    testImplementation(libs.junit)
    testImplementation(libs.mockk)
    testImplementation(libs.kotlin.coroutine.test)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
}