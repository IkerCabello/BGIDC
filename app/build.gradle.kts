plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    id("com.google.gms.google-services")
}

android {
    namespace = "com.idvkm.bgidc"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.idvkm.bgidc"
        minSdk = 24
        targetSdk = 35
        versionCode = 4
        versionName = "4.2"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        buildConfigField("String", "SENDINBLUE_API_KEY", "\"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\"")
        buildConfigField("String", "SENDINBLUE_API_URL", "\"https://api.sendinblue.com/v3/smtp/email\"")
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

    packaging {
        resources {
            excludes.addAll(listOf("META-INF/NOTICE.md", "META-INF/LICENSE.md"))
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
        buildConfig = true
        viewBinding = true
    }
}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.androidx.lifecycle.livedata.ktx)
    implementation(libs.androidx.lifecycle.viewmodel.ktx)
    implementation (libs.kotlinx.coroutines.android)
    implementation(libs.androidx.navigation.fragment.ktx)
    implementation(libs.androidx.navigation.ui.ktx)
    implementation(libs.androidx.legacy.support.v4)
    implementation(libs.androidx.fragment.ktx)
    implementation(libs.play.services.maps)
    implementation(libs.androidx.activity)
    implementation(libs.okhttp)
    implementation(libs.material)
    implementation(libs.socket.io.client)
    implementation(libs.gson)
    implementation(platform(libs.firebase.bom))
    implementation(libs.google.firebase.analytics)
    implementation(libs.firebase.firestore.ktx)
    implementation(libs.firebase.firestore)
    implementation(libs.firebase.auth)
    implementation(libs.cloudinary.android)
    implementation(libs.glide)
    implementation(libs.circleimageview)
    implementation (libs.okhttp.v491)

    implementation(libs.play.services.auth)
    implementation(libs.play.services.base)
    implementation(libs.firebase.functions.ktx)

    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)

    implementation (libs.gson.v2110)

    // JavaMail para Android
    implementation (libs.android.mail)
    implementation (libs.android.activation)

    // EncryptedSharedPreferences
    implementation (libs.androidx.security.crypto)

}