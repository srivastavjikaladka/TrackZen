import com.android.build.api.dsl.Packaging
import org.jetbrains.kotlin.gradle.plugin.KaptExtension

plugins {
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.kotlinAndroid)
    alias(libs.plugins.kotlinKapt)
    alias(libs.plugins.androidxNavigationSafeArgs)
    alias(libs.plugins.hiltAndroid)




}


android {
    namespace = "com.example.trackzen"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.trackzen"
        minSdk = 25
        targetSdk = 35
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
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
    buildFeatures {
        compose = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.1"
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
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
    implementation(libs.material3)
    implementation(libs.androidx.runtime.livedata)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)
    implementation(libs.material.icons.extended)
    implementation(libs.compose.ui)
    implementation(libs.material3)
    implementation(libs.foundation)


    // Room
    implementation(libs.androidx.room.runtime) // If defined in version catalog
    implementation(libs.androidx.room.ktx)
    kapt(libs.androidx.room.compiler)

    kapt(libs.glide.compiler)

    // Coroutines
    implementation(libs.kotlinx.coroutines.core)
    implementation(libs.kotlinx.coroutines.android)

    implementation(libs.navigation.compose) // navigation

    implementation(libs.accompanist.permissions)
    implementation(libs.accompanist.systemuicontroller)




    // Google Play Services (Maps & Location)
    implementation(libs.play.services.maps)
    implementation(libs.play.services.location)
    implementation(libs.play.services.base)
    implementation(libs.mapsCompose)

    // Location and lifecycle services
    implementation("androidx.lifecycle:lifecycle-service:2.6.2")


    // Dagger (DI) hilt core
    implementation(libs.hilt.android)
    kapt(libs.hilt.android.compiler)
    implementation(libs.androidx.hilt.navigation.compose)

    // Timber (logging)
    implementation("com.jakewharton.timber:timber:4.7.1")

    // EasyPermissions
    implementation("pub.devrel:easypermissions:3.0.0")

    // Glide (image loading)
    implementation("com.github.bumptech.glide:glide:4.11.0")
    kapt("com.github.bumptech.glide:compiler:4.11.0")

    // MPAndroidChart
    implementation("com.github.PhilJay:MPAndroidChart:v3.1.0")

}
kapt {
    correctErrorTypes = true
}
