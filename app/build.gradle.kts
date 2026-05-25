import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.kotlinAndroid)
    alias(libs.plugins.composeCompiler)
    alias(libs.plugins.kotlinSerialization)
    id("com.google.dagger.hilt.android")
    kotlin("kapt")
    alias(libs.plugins.protobuf)
}

android {
    namespace = "com.alfaazplus.sunnah"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.alfaazplus.sunnah"
        minSdk = 24
        targetSdk = 36
        versionCode = 19
        versionName = "0.1.4"

        resValue("string", "app_name_generated", "SunnahApp")
        resValue("string", "cleartextTrafficPermitted", "false")

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }

        javaCompileOptions {
            annotationProcessorOptions {
                arguments += mapOf(
                    "room.schemaLocation" to "$projectDir/schemas",
                    "room.incremental" to "true",
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
        buildConfig = true
    }

    buildToolsVersion = "36.0.0"

    sourceSets.named("main") {
        java.srcDir(
            layout.buildDirectory
                .dir("generated/java/generateDebugProto/java")
                .get().asFile
        )
    }
}

base {
    archivesName = android.defaultConfig.versionName
}

kapt {
    correctErrorTypes = true

    javacOptions {
        option("-Adagger.hilt.android.internal.disableAndroidSuperclassValidation=true")
    }
}

protobuf {
    protoc {
        artifact = "com.google.protobuf:protoc:${libs.versions.protobuf.get()}"
    }

    generateProtoTasks {
        all().forEach { task ->
            task.builtins {
                create("java") {}
            }
        }
    }
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
    implementation(libs.compose.material3.windowSizeClass)
    implementation(libs.compose.material3.adaptive)
    implementation(libs.androidx.activityCompose)
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
    implementation(libs.protobuf.java)
}

tasks
    .withType<KotlinCompile>()
    .configureEach {
        if (name.contains("Debug", ignoreCase = true)) {
            dependsOn("generateDebugProto")
        } else if (name.contains("Release", ignoreCase = true)) {
            dependsOn("generateReleaseProto")
        }
    }
