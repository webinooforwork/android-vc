plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("org.jetbrains.kotlin.plugin.compose")
    id("com.google.devtools.ksp")
}
android {
    namespace = "io.twoyi"
    compileSdk = 35
    defaultConfig {
        applicationId = "com.virtualos.container"
        minSdk = 27
        targetSdk = 35
        versionCode = 1
        versionName = "0.2.0"
        ndk { abiFilters += "arm64-v8a" }
    }
    buildFeatures { compose = true }
    compileOptions { sourceCompatibility = JavaVersion.VERSION_17; targetCompatibility = JavaVersion.VERSION_17 }
    kotlinOptions { jvmTarget = "17" }
}
dependencies {
    val composeBom = platform("androidx.compose:compose-bom:2025.10.00")
    implementation(composeBom)
    implementation("androidx.activity:activity-compose:1.11.0")
    implementation("androidx.compose.material3:material3")
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.ui:ui-tooling-preview")
    debugImplementation("androidx.compose.ui:ui-tooling")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.9.4")
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.9.4")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.10.2")
    implementation("androidx.room:room-runtime:2.8.2")
    implementation("androidx.room:room-ktx:2.8.2")
    ksp("androidx.room:room-compiler:2.8.2")
    implementation("com.squareup.okhttp3:okhttp:5.1.0")
    implementation("androidx.work:work-runtime-ktx:2.10.5")
    implementation("org.apache.commons:commons-compress:1.28.0")

    implementation("androidx.appcompat:appcompat:1.7.1")
    implementation("com.google.android.material:material:1.13.0")
    implementation("com.github.tiann:FreeReflection:3.1.0")
    implementation("com.github.topjohnwu.libsu:core:3.2.1")
    implementation("com.afollestad.material-dialogs:core:0.9.6.0")
    implementation("de.psdev.licensesdialog:licensesdialog:1.9.0")
    implementation("org.jdeferred:jdeferred-android-aar:1.2.4")
    implementation("com.github.clans:fab:1.6.4")
    implementation("com.github.bumptech.glide:glide:4.9.0")
    annotationProcessor("com.github.bumptech.glide:compiler:4.9.0")
    implementation("org.apache.commons:commons-compress:1.20")
    implementation("org.tukaani:xz:1.5")
    implementation("com.hzy:libp7zip:1.7.0")
    implementation("com.cleveroad:androidmanimation:0.9.1")
}
