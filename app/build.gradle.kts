import java.util.Properties
import org.gradle.api.GradleException


plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.google.gms.google.services)
}

val localProps = Properties()
val localPropsFile = rootProject.file("local.properties")
if (localPropsFile.exists()) {
    localProps.load(localPropsFile.inputStream())
}


android {
    namespace = "edu.northeastern.group2"
    compileSdk = 35

    defaultConfig {
        applicationId = "edu.northeastern.group2"
        minSdk = 27
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        val npsKey: String = localProps.getProperty("npsApiKey") ?: ""
        val weatherKey: String = localProps.getProperty("weatherApiKey")
            ?: throw GradleException("weatherApiKey not found in local.properties")

        buildConfigField(
            "String",
            "NPS_API_KEY",
            "\"$npsKey\""
        )
        
        buildConfigField(
            "String",
            "WEATHER_API_KEY",
            "\"$weatherKey\""
        )

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
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    buildFeatures {
        buildConfig = true
    }

}

dependencies {

    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)
    implementation(libs.firebase.analytics)
    implementation(libs.firebase.database)
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
}