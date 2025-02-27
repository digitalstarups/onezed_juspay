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
        maven {
            url=uri("https://maven.juspay.in/jp-build-packages/hyper-sdk/")
        }
        gradlePluginPortal()


    }

}


dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
        maven(url = "https://jitpack.io")
        maven {
            url=uri("https://maven.juspay.in/jp-build-packages/hyper-sdk/")
        }
    }

}


rootProject.name = "OneZed"
include(":app")
