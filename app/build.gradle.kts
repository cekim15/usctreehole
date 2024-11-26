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

    implementation ("androidx.constraintlayout:constraintlayout:2.1.4")

    implementation ("androidx.appcompat:appcompat:1.6.1")
    implementation(libs.conscrypt.android)
    annotationProcessor(libs.compiler)
    testImplementation(libs.junit)
    testImplementation(libs.mockito.core)
    testImplementation(libs.mockito.inline)
    testImplementation(libs.robolectric)
    testImplementation(libs.shadows.framework)
    testImplementation(libs.core)
    testImplementation(libs.powermock.api.mockito2)
    testImplementation(libs.powermock.module.junit4)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
    androidTestImplementation(libs.espresso.intents)
    androidTestImplementation(libs.espresso.contrib) {
        exclude("com.google.protobuf", "protobuf-lite")
    }
    androidTestImplementation(libs.runner)
    androidTestImplementation(libs.rules)
    androidTestImplementation ("androidx.test:core:1.6.1")
    androidTestImplementation ("org.mockito:mockito-android:5.6.0")
    androidTestImplementation ("androidx.test:runner:1.5.2")
    androidTestImplementation ("androidx.test.ext:junit:1.1.5")
    androidTestImplementation ("androidx.test.espresso:espresso-core:3.5.1")


    testImplementation("org.mockito:mockito-core:4.11.0") // Update version as needed
    testImplementation("org.mockito:mockito-inline:4.11.0") // For mocking final classes
    testImplementation("junit:junit:4.13.2") // Ensure JUnit is also included
    testImplementation ("androidx.test:core:1.6.1")
    testImplementation ("org.mockito:mockito-core:5.5.0")

    testImplementation ("org.robolectric:robolectric:4.10.3")
    testImplementation ("org.mockito:mockito-core:5.6.0")

    testImplementation ("org.powermock:powermock-module-junit4:2.0.9")
    testImplementation ("org.powermock:powermock-api-mockito2:2.0.9")
    //implementation(kotlin("script-runtime"))
}