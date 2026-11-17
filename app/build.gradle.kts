plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.compose)
    id("com.apollographql.apollo") version "5.2.0"
}

android {
    namespace = "com.example.japim"
    compileSdk {
        version = release(37)
    }

    defaultConfig {
        applicationId = "com.example.japim"
        minSdk = 24
        targetSdk = 37
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            optimization {
                enable = false
            }
        }
    }
    compileOptions {
        isCoreLibraryDesugaringEnabled = true
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    buildFeatures {
        compose = true
    }
}

apollo {
    service("service") {
        packageName.set("com.example.japim.graphql")

        mapScalar("LocalDateTime", "java.time.LocalDateTime", "com.example.japim.apolloClient.LocalDateTimeAdapter")
        mapScalar("Date", "java.time.LocalDate", "com.example.japim.apolloClient.LocalDateAdapter")
        mapScalar("LocalTime", "java.time.LocalTime", "com.example.japim.apolloClient.LocalTimeAdapter")
    }
}

dependencies {
    coreLibraryDesugaring("com.android.tools:desugar_jdk_libs:2.1.4")
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.ui.graphics)
    testImplementation(libs.junit)
    implementation("androidx.compose.material:material-icons-extended")
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.compose.ui.test.junit4)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(libs.androidx.junit)
    debugImplementation(libs.androidx.compose.ui.test.manifest)
    debugImplementation(libs.androidx.compose.ui.tooling)
    implementation("com.apollographql.apollo:apollo-runtime:5.2.0")
    implementation("androidx.navigation:navigation-compose:2.8.5")
}