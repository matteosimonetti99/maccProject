import com.android.build.api.dsl.Packaging

plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id ("kotlin-android")
    id ("kotlin-kapt")
}

android {
    compileSdk=34

    defaultConfig {
        applicationId="com.example.jetpackcomposedemo"
        minSdk=26
        targetSdk=33
        versionCode=1
        versionName="1.0"

        testInstrumentationRunner="androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary=true
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
                targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions {
        jvmTarget = "17"
    }
    buildFeatures {
        compose = true
        viewBinding = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.2"
    }
    fun Packaging.() {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
    namespace = "com.example.jetpackcomposedemo"
}




dependencies {
    implementation("androidx.core:core-ktx:1.12.0")
    implementation ("androidx.compose.ui:ui-graphics:1.5.4")
    implementation("androidx.compose.ui:ui:1.5.4")
    implementation("androidx.compose.material:material:1.5.4")
    implementation("androidx.compose.material:material-icons-extended:1.5.4")
    implementation("androidx.compose.ui:ui-tooling-preview:1.5.4")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.6.2")
    implementation("androidx.activity:activity-compose:1.8.1")
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.6.2")
    implementation ("com.google.android.material:material:1.10.0")
    implementation ("com.google.maps.android:maps-compose:4.3.0")
    implementation("com.google.maps.android:maps-ktx:5.0.0")
    implementation ("com.google.android.gms:play-services-maps:18.2.0")
    implementation ("com.google.android.gms:play-services-location:21.0.1")
    implementation("com.google.android.gms:play-services-cast-framework:21.4.0")
    implementation("com.google.accompanist:accompanist-insets:0.17.0")
    implementation("com.google.accompanist:accompanist-systemuicontroller:0.17.0")
    implementation("io.coil-kt:coil-compose:2.5.0")

    implementation("androidx.compose.ui:ui:1.6.0-beta02")
    implementation("androidx.compose.material3:material3:1.2.0-alpha12")
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.7.0-rc01")
    implementation("org.jetbrains.kotlinx:kotlinx-datetime:0.5.0")
    implementation("com.google.android.gms:play-services-vision-common:19.1.3")
    implementation("com.google.android.gms:play-services-mlkit-barcode-scanning:18.3.0")
    implementation("androidx.camera:camera-view:1.3.0")


    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
    androidTestImplementation("androidx.compose.ui:ui-test-junit4:1.5.4")
    debugImplementation("androidx.compose.ui:ui-tooling:1.5.4")
    implementation("androidx.navigation:navigation-compose:2.7.5")

    //Navigation
    implementation("androidx.navigation:navigation-compose:2.7.5")
    implementation("com.squareup.okhttp3:okhttp:4.9.1")

    implementation("io.coil-kt:coil-compose:2.5.0")

    //QR code
    implementation("com.google.zxing:core:3.5.2")

    implementation("androidx.camera:camera-camera2:1.3.0")
    implementation("androidx.camera:camera-lifecycle:1.3.0")
    implementation("androidx.camera:camera-core:1.4.0-alpha02")
    implementation("androidx.camera:camera-camera2:1.4.0-alpha02")
    implementation("com.google.mlkit:barcode-scanning:17.2.0")


}