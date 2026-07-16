plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    // ✅ แนะนำให้ระบุเวอร์ชัน KSP ให้ตรงกับ Kotlin ของคุณ (ตัวอย่างสำหรับ Kotlin 2.0.21)
    id("com.google.devtools.ksp") version "2.0.21-1.0.27"
}

android {
    namespace = "com.example.todolistjetpackcompose"
    compileSdk = 35
    // ✅ รองรับไลบรารีเวอร์ชัน 1.11.0/2.11.0

    defaultConfig {
        applicationId = "com.example.todolistjetpackcompose"
        minSdk = 24
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

    // ✅ เพิ่มส่วนนี้เพื่อแก้ปัญหา JVM Target Inconsistent
    kotlinOptions {
        jvmTarget = "17"
    }

    // ✅ สำหรับ Kotlin 2.0+ แนะนำให้เพิ่มส่วนนี้ด้วยเพื่อให้ KSP รับทราบค่าเดียวกัน
    kotlin {
        compilerOptions {
            jvmTarget.set(org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_17)
        }
    }

    compileOptions {
        // ✅ เปลี่ยนจาก VERSION_11 เป็น VERSION_17
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    // ✅ ย้าย testOptions มาไว้ในบล็อก android { ... }
    testOptions {
        unitTests {
            isIncludeAndroidResources = true
        }
    }
    buildFeatures {
        compose = true
    }
}

dependencies {
    implementation(platform(libs.androidx.compose.bom))
    // --- Core & Compose ---
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)

    // --- Navigation (เลือกตัวใดตัวหนึ่ง แนะนำให้ใช้ตัวนี้เพื่อให้เข้ากับ Compose) ---
    implementation("androidx.navigation:navigation-compose:2.8.5")

    // --- Room Database (ปรับเวอร์ชันให้เสถียร) ---
    val room_version = "2.6.1"
    implementation("androidx.room:room-runtime:$room_version")
    implementation("androidx.room:room-ktx:$room_version")
    ksp("androidx.room:room-compiler:$room_version")
    implementation("androidx.room:room-rxjava3:$room_version")

    // --- Splash Screen & Icons ---
    implementation("androidx.core:core-splashscreen:1.0.1")
    implementation("androidx.compose.material:material-icons-extended")

    // --- RxJava & Coroutines Bridge ---
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-rx3:1.7.3")

    // --- Unit Testing ---
    //    testImplementation("junit:junit:4.13.2")
    testImplementation("io.mockk:mockk:1.13.5")
    testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.7.3")
    testImplementation("androidx.arch.core:core-testing:2.2.0")
    testImplementation("app.cash.turbine:turbine:1.0.0")
    testImplementation("org.robolectric:robolectric:4.11.1") // ปรับเป็นเวอร์ชันใหม่ขึ้น
    testImplementation("androidx.test:core:1.6.1")
    testImplementation("androidx.test:core-ktx:1.6.1")

    // --- UI Testing & Debug ---
    androidTestImplementation(platform(libs.androidx.compose.bom)) // เพิ่มบรรทัดนี้ด้วย
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)
}