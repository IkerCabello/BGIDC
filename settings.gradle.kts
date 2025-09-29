pluginManagement {
    repositories {
        gradlePluginPortal()
        google()
        mavenCentral()
    }
    resolutionStrategy {
        eachPlugin {
            when (requested.id.id) {
                "org.jetbrains.kotlin.multiplatform",
                "org.jetbrains.kotlin.jvm",
                "org.jetbrains.kotlin.plugin.serialization" -> {
                    useVersion("1.9.20") // ajusta si usas otra Kotlin
                }
                "com.android.application",
                "com.android.library" -> {
                    // Android plugin viene de google()
                }
                "com.google.gms.google-services" -> {
                    useVersion("4.4.0") // versión del plugin google-services (ajusta si quieres)
                }
            }
        }
    }
}

dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.PREFER_SETTINGS)
    repositories {
        google()
        mavenCentral()
    }
}

rootProject.name = "BGIDC"
include(":app")