plugins {
  alias(libs.plugins.android.application)
  alias(libs.plugins.kotlin.android)
  alias(libs.plugins.kotlin.compose)
  alias(libs.plugins.hilt.android)
  alias(libs.plugins.ksp)
  alias(libs.plugins.google.gms.google.services)
}

android {
  namespace = "com.pmk.FreePlayer" // Cambia esto por tu namespace
  compileSdk = 36

  defaultConfig {
    applicationId = "com.pmk.FreePlayer" // Cambia esto por tu applicationId
    minSdk = 26 // Android 8.0 - Buen balance para apps de música modernas
    targetSdk = 36
    versionCode = 1
    versionName = "1.0.0"

    testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

    // Configuración para Room - esquema de exportación
    //noinspection WrongGradleMethod
    ksp {
      arg("room.schemaLocation", "$projectDir/schemas")
      arg("room.incremental", "true")
      arg("room.generateKotlin", "true")
    }

    vectorDrawables { useSupportLibrary = true }
  }

  buildTypes {
    debug {
      isMinifyEnabled = false
      isDebuggable = true
      applicationIdSuffix = ".debug"
      versionNameSuffix = "-debug"
    }

    release {
      isMinifyEnabled = true
      isShrinkResources = true
      proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
      // Firma de release - configura tu keystore
      // signingConfig = signingConfigs.getByName("release")
    }
  }

  compileOptions {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
    // Habilitar desugaring para APIs de Java 8+ en versiones antiguas de Android
    isCoreLibraryDesugaringEnabled = true
  }

  buildFeatures {
    compose = true
    buildConfig = true
  }

  packaging {
    resources {
      excludes +=
          listOf(
              "/META-INF/{AL2.0,LGPL2.1}",
              "META-INF/DEPENDENCIES",
              "META-INF/LICENSE",
              "META-INF/LICENSE.txt",
              "META-INF/NOTICE",
              "META-INF/NOTICE.txt",
          )
    }
  }

  // Configuración para tests
  testOptions {
    unitTests {
      isIncludeAndroidResources = true
      isReturnDefaultValues = true
    }
  }

  // Habilitar el soporte de lint
  lint {
    abortOnError = false
    checkReleaseBuilds = true
    warningsAsErrors = false
  }
  buildToolsVersion = "36.1.0"
}

kotlin {
  compilerOptions {
    jvmTarget.set(org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_17)
    freeCompilerArgs.addAll(
        "-opt-in=kotlin.RequiresOptIn",
        "-opt-in=kotlinx.coroutines.ExperimentalCoroutinesApi",
        "-opt-in=androidx.compose.material3.ExperimentalMaterial3Api",
        "-opt-in=androidx.compose.animation.ExperimentalAnimationApi",
        "-opt-in=androidx.compose.foundation.ExperimentalFoundationApi",
    )
  }
}

dependencies {

  // CORE LIBRARY DESUGARING
  coreLibraryDesugaring(libs.desugar.jdk.libs)

  // CORE ANDROID
  implementation(libs.androidx.core.ktx)
  implementation(libs.androidx.lifecycle.runtime.ktx)
  implementation(libs.androidx.lifecycle.runtime.compose)
  implementation(libs.androidx.activity.compose)
  implementation(libs.androidx.datastore.preferences)

  // COMPOSE UI - Usando BOM para versiones consistentes
  implementation(platform(libs.androidx.compose.bom))
  implementation(libs.androidx.ui)
  implementation(libs.androidx.ui.graphics)
  implementation(libs.androidx.ui.text)
  implementation(libs.androidx.material3)
  implementation(libs.androidx.compose.animation)
  implementation(libs.androidx.compose.foundation)
  implementation(libs.androidx.compose.foundation.layout)
  implementation(libs.androidx.constraintlayout.compose)
  implementation(libs.androidx.navigation.compose)

  // Tooling para Preview (solo debug)
  debugImplementation(libs.androidx.ui.tooling)
  implementation(libs.androidx.ui.tooling.preview)

  // ICONOS
  implementation(libs.androidx.material.icons.extended)
  implementation(libs.composeIcons.feather)
  implementation(libs.composeIcons.fontAwesome)

  // HILT - Inyección de Dependencias
  implementation(libs.hilt.android)
  ksp(libs.hilt.compiler)
  implementation(libs.androidx.hilt.navigation.compose)
  implementation(libs.androidx.hilt.work)
  ksp(libs.androidx.hilt.compiler)

  // ROOM - Base de Datos Local
  implementation(libs.androidx.room.runtime)
  implementation(libs.androidx.room.ktx)
  ksp(libs.androidx.room.compiler)

  // WORKMANAGER - Tareas en segundo plano
  implementation(libs.androidx.work.runtime.ktx)

  // MEDIA3 (ExoPlayer) - Reproducción de Audio ⭐ ESENCIAL
  implementation(libs.androidx.media3.exoplayer)
  implementation(libs.androidx.media3.session) // MediaSession para notificaciones
  implementation(libs.androidx.media3.ui) // Controles de UI

  // FIREBASE & GOOGLE AUTH
  implementation(platform(libs.firebase.bom))
  implementation(libs.firebase.analytics)
  implementation(libs.firebase.auth)
  implementation(libs.play.services.auth)
  implementation(libs.androidx.credentials)
  implementation(libs.androidx.credentials.play.services.auth)
  implementation(libs.google.id)
  implementation(libs.kotlinx.coroutines.guava)

  // NETWORK - Retrofit + OkHttp + Moshi
  implementation(libs.retrofit.core)
  implementation(libs.retrofit.converter.moshi)
  implementation(libs.moshi.kotlin)
  implementation(libs.okhttp.logging.interceptor)

  // UTILIDADES
  implementation(libs.gson)
  implementation(libs.coil.compose) // Carga de imágenes (carátulas de álbumes)
  implementation(libs.jsoup) // Scraping de letras (si lo necesitas)
  implementation(libs.bcrypt) // Encriptación
  implementation(libs.accompanist.permissions) // Permisos en Compose
  implementation(libs.androidx.palette.ktx) // Extraer colores de carátulas ⭐

  // TESTING
  testImplementation(libs.junit)
  testImplementation(libs.mockk)
  testImplementation(libs.kotlinx.coroutines.test)

  androidTestImplementation(libs.androidx.junit)
  androidTestImplementation(libs.androidx.espresso.core)
  androidTestImplementation(platform(libs.androidx.compose.bom))
  androidTestImplementation(libs.androidx.ui.test.junit4)

  debugImplementation(libs.androidx.ui.test.manifest)
}
