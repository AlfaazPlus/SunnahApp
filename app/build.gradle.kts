plugins {
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.kotlinAndroid)
    alias(libs.plugins.composeCompiler)
    id("com.google.dagger.hilt.android")
    kotlin("kapt")
}

android {
    namespace = "com.alfaazplus.sunnah"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.alfaazplus.sunnah"
        minSdk = 21
        targetSdk = 36
        versionCode = 17
        versionName = "0.1.2"

        setProperty("archivesBaseName", versionName)

        resValue("string", "app_name_generated", "SunnahApp")
        resValue("string", "cleartextTrafficPermitted", "false")

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }

        javaCompileOptions {
            annotationProcessorOptions {
                arguments += mapOf(
                    "room.schemaLocation" to "$projectDir/schemas", "room.incremental" to "true"
                )
            }
        }
    }

    buildTypes {
        debug {
            isDebuggable = true
            isMinifyEnabled = false

            applicationIdSuffix = ".debug"
            versionNameSuffix = "-debug"

            resValue("string", "app_name_generated", "SunnahApp Debug")
            resValue("string", "cleartextTrafficPermitted", "true")
        }

        release {
            isDebuggable = false
            isMinifyEnabled = true
            isShrinkResources = true

            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions {
        jvmTarget = "17"
    }

    composeOptions {
        kotlinCompilerExtensionVersion = "1.4.3"
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }

    buildFeatures {
        viewBinding = true
        compose = true
    }
    buildToolsVersion = "36.0.0"
} // Allow references to generated code
kapt {
    correctErrorTypes = true
}

dependencies {
    implementation(platform(libs.compose.bom))
    implementation(libs.compose.foundation)
    implementation(libs.compose.foundation.layout)
    implementation(libs.compose.ui)
    implementation(libs.compose.ui.tooling)
    implementation(libs.compose.ui.tooling.preview)
    implementation(libs.compose.viewmodel)
    implementation(libs.compose.livedata)
    implementation(libs.activity.compose)
    implementation(libs.ui.graphics)
    implementation(libs.material3)
    implementation(libs.google.fonts)
    implementation(libs.compose.navigation)
    implementation(libs.hilt.navigation.compose)
    debugImplementation(libs.ui.test.manifest)

    implementation(libs.lifecycle.runtime.ktx)
    implementation(libs.core.ktx)
    implementation(libs.appcompat)
    implementation(libs.constraintlayout)
    implementation(libs.navigation.fragment.ktx)
    implementation(libs.navigation.ui.ktx)
    implementation(libs.lifecycle.livedata.ktx)
    implementation(libs.lifecycle.viewmodel.ktx)
    implementation(libs.androidx.room.ktx)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.test.ext.junit)
    androidTestImplementation(libs.espresso.core)
    implementation(libs.asynclayoutinflater)

    implementation(libs.hilt.android)
    kapt(libs.hilt.android.compiler)
    kapt(libs.androidx.hilt.compiler)

    // Room
    implementation(libs.androidx.room.runtime)
    kapt(libs.androidx.room.compiler)

    // Kotlin Extensions and Coroutines support for Room
    implementation(libs.androidx.room.ktx)
    implementation(libs.commons.compress)
    implementation(libs.workManager)
    implementation(libs.dataStore)
    implementation(libs.hiltWork)
    implementation(libs.retrofit)
    implementation(libs.kotlinxSerialization)
    implementation(libs.kotlinxRetrofit)
    implementation(libs.guava)
    implementation(libs.paging)
    implementation(libs.pagingCompose)
    implementation(libs.roomPaging)

    implementation(libs.accompanist.permissions)
}