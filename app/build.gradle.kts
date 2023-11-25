import com.android.build.api.dsl.Packaging

plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
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

    implementation("androidx.core:core-ktx:1.10.1")
    implementation ("androidx.compose.ui:ui-graphics:1.1.0")
    implementation("androidx.compose.ui:ui:1.4.3")
    implementation("androidx.compose.material:material:1.4.3")
    implementation("androidx.compose.material:material-icons-extended:1.4.3")
    implementation("androidx.compose.ui:ui-tooling-preview:1.4.3")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.6.1")
    implementation("androidx.activity:activity-compose:1.7.2")
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.6.2")
    implementation ("com.google.android.material:material:1.10.0")
    implementation ("com.google.maps.android:maps-compose:2.2.0")
    implementation ("com.google.android.gms:play-services-maps:18.2.0")



    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
    androidTestImplementation("androidx.compose.ui:ui-test-junit4:1.4.3")
    debugImplementation("androidx.compose.ui:ui-tooling:1.4.3")
    implementation("androidx.navigation:navigation-compose:2.7.5")

    //Navigation
    implementation("androidx.navigation:navigation-compose:2.7.0-rc01")
    implementation("com.squareup.okhttp3:okhttp:4.9.1")
}