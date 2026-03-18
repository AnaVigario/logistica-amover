import java.util.Properties

android.buildFeatures.buildConfig = true

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)

    id("com.google.devtools.ksp")
    id("com.google.gms.google-services")
    id ("kotlin-kapt")

    id("androidx.room")

    id("com.google.android.libraries.mapsplatform.secrets-gradle-plugin")

    id("org.jetbrains.kotlin.plugin.serialization")
}


val secretsPropertiesFile = rootProject.file("secrets.properties")
val secretsProperties = Properties().apply {
    if (secretsPropertiesFile.exists()) {
        load(secretsPropertiesFile.inputStream())
    } else {
        println("⚠️ Ficheiro secrets.properties não encontrado!")
    }
}


android {
    namespace = "com.example.myamover"
    compileSdk = 36



    defaultConfig {
        applicationId = "com.example.myamover"
        minSdk = 24
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        // ...
        buildConfigField(
            "String",
            "SUPABASE_URL",
            "\"${secretsProperties.getProperty("SUPABASE_URL")}\""
        )
        buildConfigField(
            "String",
            "SUPABASE_ANON_KEY",
            "\"${secretsProperties.getProperty("SUPABASE_ANON_KEY")}\""
        )
        // googlemaps
        manifestPlaceholders["MAPS_API_KEY"] = project.properties["MAPS_API_KEY"] ?: ""

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
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions {
        jvmTarget = "17"
    }
    buildFeatures {
        compose = true
        buildConfig = true
    }
}

room {
    schemaDirectory("$projectDir/schemas")
}

dependencies {

    // --- AndroidX Core ---
    implementation(libs.androidx.core.ktx)

    // --- Lifecycle & ViewModel ---
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.lifecycle.viewmodel.compose)
    implementation(libs.androidx.lifecycle.viewmodel.ktx)
    implementation(libs.androidx.lifecycle.viewmodel.savedstate)

    // --- Coroutines ---
    implementation(libs.kotlinx.coroutines.android)

    // --- Compose ---
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.runtime)

    // Material 3
    implementation(libs.androidx.material3)
    implementation(libs.androidx.material3.window.size.class1)
    implementation(libs.androidx.material.icons.extended)

    // Activity Compose
    implementation(libs.androidx.activity.compose)

    // Navigation Compose
    implementation(libs.androidx.navigation.compose)
    // Se usar Hilt para injeção de dependência
    // implementation(libs.androidx.navigation.compose.hilt)
    // implementation(libs.androidx.navigation.compose.hilt.navigation)

    // --- Lottie ---
    implementation(libs.lottie.compose)

    // --- Firebase ---
    implementation(platform(libs.firebase.bom))
    implementation(libs.firebase.analytics)
    implementation(libs.firebase.messaging)
    implementation("com.google.accompanist:accompanist-permissions:0.36.0")




    implementation(libs.androidx.localbroadcastmanager)

    // --- Google Play Services ---
    implementation(libs.play.services.cast.framework)
    implementation(libs.androidx.compose.material3.material3)
    implementation(libs.runtime)

    // --- Room ---
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.room.runtime)
    implementation(libs.androidx.room.ktx)
    implementation(libs.androidx.foundation.layout)
    implementation(libs.androidx.compose.ui.ui)
    implementation(libs.firebase.inappmessaging.display)
    implementation(libs.androidx.compose.ui.ui2)
    ksp(libs.androidx.room.compiler)
    annotationProcessor(libs.androidx.room.compiler)

    // --- DataStore ---
    implementation(libs.androidx.datastore.preferences)

    // --- Testes ---
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)

    // --- Debug ---
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)

    //--- Maps ---
    // Maps SDK for Android
    implementation("com.google.android.gms:play-services-maps:20.0.0")
    implementation(libs.maps.compose)
    implementation("com.google.code.gson:gson:2.10.1")
    implementation("com.google.android.gms:play-services-location:21.3.0")


    // Cliente Supabase
    implementation(libs.supabase.kt)

    // (opcionais mas normalmente úteis)
    implementation("io.github.jan-tennert.supabase:postgrest-kt:3.2.5")  // CRUD nas tabelas
    implementation("io.github.jan-tennert.supabase:auth-kt:3.2.5") // Autenticação de utilizadores
    implementation(libs.realtime.kt)    // Subscrições em tempo real

    implementation("io.ktor:ktor-client-android:3.3.1")

    //Retrofit
    implementation(libs.retrofit)
    implementation(libs.logging.interceptor)
    implementation("com.squareup.retrofit2:converter-scalars:3.0.0")
    implementation("com.squareup.okhttp3:okhttp:5.3.2")
    implementation("com.squareup.okhttp3:logging-interceptor:5.3.2")
    implementation("com.squareup.retrofit2:converter-gson:2.11.0")

    // Converter para Kotlinx Serialization (como já usas @Serializable)
    implementation(libs.kotlinx.serialization.json)
    implementation("org.json:json:20251224")
    implementation(libs.retrofit2.kotlinx.serialization.converter)

    implementation("com.google.android.gms:play-services-location:21.3.0")


}


//google maps
secrets {

    propertiesFileName = "secrets.properties"
    defaultPropertiesFileName = "local.defaults.properties"
}
