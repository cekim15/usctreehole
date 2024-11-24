plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.google.gms.google.services)
}

android {
    namespace = "com.example.usctreehole"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.usctreehole"
        minSdk = 24
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
}

dependencies {

    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)
    implementation(libs.firebase.database)
    implementation(platform(libs.firebase.bom))
    implementation(libs.firebase.auth)
    implementation(libs.firebase.firestore)
    implementation(libs.firebase.storage)
    implementation(libs.glide)
    implementation(libs.recyclerview)
    implementation(libs.core.ktx)
    implementation(libs.protobuf.javalite)
    annotationProcessor(libs.compiler)
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
    androidTestImplementation(libs.espresso.intents)
    androidTestImplementation(libs.espresso.contrib) {
        exclude("com.google.protobuf", "protobuf-lite")
    }
    androidTestImplementation(libs.runner)
    androidTestImplementation(libs.rules)
    androidTestImplementation ("androidx.test:core:1.6.1")
    testImplementation("org.mockito:mockito-core:4.11.0") // Update version as needed
    testImplementation("org.mockito:mockito-inline:4.11.0") // For mocking final classes
    testImplementation("junit:junit:4.13.2") // Ensure JUnit is also included
    testImplementation ("androidx.test:core:1.6.1")
}