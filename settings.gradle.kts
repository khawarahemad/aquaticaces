pluginManagement {
    repositories {
        maven { url = uri("https://maven.fabricmc.net/") }
        mavenCentral()
        gradlePluginPortal()
    }
}

dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.PREFER_PROJECT)
    repositories {
        mavenCentral()
        maven { url = uri("https://maven.fabricmc.net/") }
        maven { url = uri("https://maven.terraformersmc.com/") }
        maven { url = uri("https://maven.jitpack.io") }
    }
}

rootProject.name = "AquaticAces"
