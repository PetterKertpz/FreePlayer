pluginManagement {
    repositories {
        google {
            content {
                includeGroupByRegex("com\\.android.*")
                includeGroupByRegex("com\\.google.*")
                includeGroupByRegex("androidx.*")
            }
        }
        mavenCentral()
        gradlePluginPortal()
    }
}

dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
        // Para algunas librerías de iconos de Compose
        maven { url = uri("https://jitpack.io") }
    }
}

rootProject.name = "FreePlayer" // Cambia esto por el nombre de tu proyecto
include(":app")
