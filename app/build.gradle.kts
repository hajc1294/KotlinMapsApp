plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("com.google.android.libraries.mapsplatform.secrets-gradle-plugin")
    id("com.google.devtools.ksp")
    id("kotlin-parcelize")
}

android {
    namespace = "com.jeanca.mapsapp"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.jeanca.mapsapp"
        minSdk = 26
        targetSdk = 34
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
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }

    kotlinOptions {
        jvmTarget = "1.8"
    }

    buildFeatures {
        viewBinding = true
    }

    packaging {
        resources.excludes.add("META-INF/*")
    }
}

dependencies {

    // Android
    implementation("androidx.core:core-ktx:1.12.0")
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")

    // Google
    implementation("com.google.android.material:material:1.11.0")
    implementation("com.google.android.gms:play-services-maps:18.2.0")
    implementation("com.google.maps.android:android-maps-utils:3.8.2")

    // Retrofit and gson
    implementation("com.google.code.gson:gson:2.10")
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.retrofit2:converter-gson:2.6.3")

    // Reactive X
    implementation("com.squareup.retrofit2:adapter-rxjava2:2.6.3")
    implementation("io.reactivex.rxjava2:rxandroid:2.1.1")

    // Room
    implementation("androidx.room:room-runtime:2.6.1")
    implementation("androidx.room:room-rxjava2:2.6.1")
    implementation("androidx.test:core-ktx:1.5.0")
    ksp("androidx.room:room-compiler:2.6.1")

    // Recycler view
    implementation("androidx.recyclerview:recyclerview:1.3.2")

    // Test
    testImplementation("junit:junit:4.13.2")
    testImplementation("androidx.arch.core:core-testing:2.2.0")
    testImplementation("com.squareup.okhttp3:mockwebserver:4.12.0")
    testImplementation("org.mockito:mockito-core:2.25.0")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
}