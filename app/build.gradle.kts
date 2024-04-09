@Suppress("DSL_SCOPE_VIOLATION") // TODO: Remove once KTIJ-19369 is fixed
plugins {
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.kotlinAndroid)
    alias(libs.plugins.hiltAndroid)
    alias(libs.plugins.protobuf)
    kotlin("kapt")
}

android {
    namespace = "com.tangping.androidpractice"
    compileSdk = Configs.compileSdk

    defaultConfig {
        applicationId = "com.tangping.androidpractice"
        minSdk = Configs.minSdk
        targetSdk = Configs.targetSdk
        versionCode = Configs.versionCode
        versionName = Configs.versionName

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
            signingConfig = signingConfigs.getByName("debug")
        }
    }
    compileOptions {
        sourceCompatibility = Configs.sourceCompatibility
        targetCompatibility = Configs.sourceCompatibility
    }
    kotlinOptions {
        jvmTarget = Configs.jvmTarget
    }
    buildFeatures {
        compose = true
    }
    viewBinding {
        enable = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = libs.versions.compose.compiler.get()
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
    protobuf {
        protoc {
            artifact = "com.google.protobuf:protoc:${libs.versions.protobuf.javalite.get()}"
        }
        generateProtoTasks {
            all().forEach { task ->
                task.plugins.create("java") {
                    option("lite")
                }
            }
        }
    }
}

dependencies {
    implementation(project(":components"))
    implementation(libs.core.ktx)
    implementation(libs.lifecycle.runtime.ktx)
    implementation(libs.activity.compose)
    implementation(platform(libs.compose.bom))
    implementation(libs.ui)
    implementation(libs.ui.graphics)
    implementation(libs.ui.tooling.preview)
    implementation(libs.material3)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.test.ext.junit)
    androidTestImplementation(libs.espresso.core)
    androidTestImplementation(platform(libs.compose.bom))
    androidTestImplementation(libs.ui.test.junit4)
    debugImplementation(libs.ui.tooling)
    debugImplementation(libs.ui.test.manifest)
    implementation(libs.navigation.compose)
    implementation(libs.hilt.android)
    kapt(libs.hilt.compiler)
    implementation(libs.constraintlayout.compose)
    implementation(libs.hilt.navigation.compose)
    implementation(libs.gson)
    implementation(libs.datastore.preferences)
    implementation(libs.datastore.proto)
    implementation(libs.protobuf.javalite)
}