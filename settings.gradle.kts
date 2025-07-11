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
        maven {
            name = "GitHubPackages"

            url = uri("https://maven.pkg.github.com/VM-Mobile-SDK/nextgen-adsdk-android-release")

            credentials {
                username = ""
                password = ""
            }
        }
    }
}

rootProject.name = "nextgen-adsdk-issue-demo"
include(":app")
 