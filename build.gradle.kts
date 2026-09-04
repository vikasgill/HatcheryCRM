plugins {
    // trick: for the sake of Gradle module metadata resolution, these plugins must be declared
    // in the root project even though they're only applied in :composeApp
    alias(libs.plugins.androidApplication) apply false
    alias(libs.plugins.kotlinMultiplatform) apply false
    alias(libs.plugins.composeMultiplatform) apply false
    alias(libs.plugins.composeCompiler) apply false
    alias(libs.plugins.sqldelight) apply false
}
