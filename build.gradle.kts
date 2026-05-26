// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    alias(libs.plugins.androidApplication) apply false
    alias(libs.plugins.kotlinAndroid) apply false
    alias(libs.plugins.composeCompiler) apply false
    alias(libs.plugins.kotlinSerialization) apply false
    alias(libs.plugins.daggerHilt) apply false
    alias(libs.plugins.protobuf) apply false
    id("com.google.devtools.ksp") version "2.3.4" apply false
    alias(libs.plugins.androidx.room3) apply false
}
