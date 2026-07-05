// Используется только для Docker-сборки веб-версии клиента (wasmJs).
// В отличие от settings.gradle.kts, НЕ подключает :androidApp — веб-таргету
// он не нужен, а конфигурация Android Gradle Plugin потребовала бы Android SDK
// внутри контейнера.
//
// Запуск вручную (без докера): ./gradlew -c settings.docker.gradle.kts :webApp:wasmJsBrowserDistribution

pluginManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
}

dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.PREFER_SETTINGS)
    repositories {
        google()
        mavenCentral()
        ivy("https://nodejs.org/dist/") {
            name = "Node.js Distributions"
            patternLayout {
                artifact("v[revision]/[artifact](-v[revision]-[classifier]).[ext]")
            }
            metadataSources {
                artifact()
            }
        }
        ivy("https://github.com/yarnpkg/yarn/releases/download/") {
            name = "Yarn Distributions"
            patternLayout {
                artifact("v[revision]/[artifact]-v[revision].[ext]")
            }
            metadataSources {
                artifact()
            }
        }
        ivy("https://github.com/WebAssembly/binaryen/releases/download/") {
            name = "Binaryen Distributions"
            patternLayout {
                artifact("version_[revision]/[artifact]-version_[revision]-[classifier].[ext]")
            }
            metadataSources {
                artifact()
            }
        }
    }
}

rootProject.name = "volna-client"

include(":shared")
include(":webApp")
