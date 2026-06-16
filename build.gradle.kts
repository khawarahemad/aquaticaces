import proguard.gradle.ProGuardTask

buildscript {
    repositories {
        maven { url = uri("https://maven.fabricmc.net/") }
        mavenCentral()
        gradlePluginPortal()
    }
    dependencies {
        classpath("com.guardsquare:proguard-gradle:7.5.0")
    }
}

plugins {
    kotlin("jvm") version "2.0.0"
    kotlin("plugin.serialization") version "2.0.0"
    id("fabric-loom") version "1.10.5"
}

version = "1.4.1"
group = "com.aquaticaces"

base {
    archivesName.set("aquaticaces")
}

val minecraftVersion: String by project
val loaderVersion: String by project
val fabricApiVersion: String by project
val fabricLanguageKotlinVersion: String by project
val serializationVersion: String by project
val coroutinesVersion: String by project
val lwjglVersion: String by project

repositories {
    mavenCentral()
    maven { url = uri("https://maven.fabricmc.net/") }
    maven { url = uri("https://maven.terraformersmc.com/") }
    maven { url = uri("https://maven.jitpack.io") }
}

dependencies {
    // Minecraft & Mappings
    minecraft("com.mojang:minecraft:$minecraftVersion")
    mappings(loom.officialMojangMappings())

    // Fabric Loader & API
    modImplementation("net.fabricmc:fabric-loader:$loaderVersion")
    modImplementation("net.fabricmc.fabric-api:fabric-api:$fabricApiVersion")
    modImplementation("net.fabricmc:fabric-language-kotlin:$fabricLanguageKotlinVersion")

    // Kotlin Serialization & Coroutines
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:$serializationVersion")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:$coroutinesVersion")

    // NanoVG — ship Java bindings + natives inside the mod jar (Minecraft does not include nanovg)
    modImplementation("org.lwjgl:lwjgl-nanovg:$lwjglVersion")
    include("org.lwjgl:lwjgl-nanovg:$lwjglVersion")

    listOf(
        "windows",
        "windows-arm64",
        "linux",
        "linux-arm64",
        "macos",
        "macos-arm64",
    ).forEach { platform ->
        modImplementation("org.lwjgl:lwjgl-nanovg:$lwjglVersion:natives-$platform")
        include("org.lwjgl:lwjgl-nanovg:$lwjglVersion:natives-$platform")
    }
}

tasks.processResources {
    inputs.property("version", project.version)
    filesMatching("fabric.mod.json") {
        expand("version" to project.version)
    }
}

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile>().configureEach {
    compilerOptions {
        jvmTarget.set(org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_21)
        freeCompilerArgs.addAll(listOf("-opt-in=kotlin.RequiresOptIn"))
    }
}

java {
    sourceCompatibility = JavaVersion.VERSION_21
    targetCompatibility = JavaVersion.VERSION_21
}

// ProGuard obfuscation task integration
tasks.register<ProGuardTask>("proguard") {
    dependsOn("remapJar")

    val remapJarTask = tasks.named<net.fabricmc.loom.task.RemapJarTask>("remapJar").get()
    injars(remapJarTask.archiveFile)
    outjars(layout.buildDirectory.file("libs/aquaticaces-${project.version}-obfuscated.jar"))

    // Library dependencies
    val javaHome = System.getProperty("java.home")
    libraryjars("$javaHome/jmods")
    libraryjars(configurations.compileClasspath.get().files)

    val rules = """
        -dontshrink
        -dontwarn
        -optimizationpasses 5
        -allowaccessmodification
        -mergeinterfacesaggressively
        -overloadaggressively
        -repackageclasses 'com.aquaticaces.internal'

        -keep public class com.aquaticaces.AquaticAces {
            public static void onInitializeClient();
        }
        -keep public class * implements net.fabricmc.api.ClientModInitializer { *; }
        -keep class com.aquaticaces.mixin.** { *; }
        -keep class com.aquaticaces.accessor.** { *; }
        -keep class com.aquaticaces.ui.** { *; }
        -keep class com.aquaticaces.event.** { *; }
        -keep class com.aquaticaces.module.impl.ghost.SelfDestruct { *; }
        -keepclassmembers class * {
            @org.spongepowered.asm.mixin.Shadow <fields>;
            @org.spongepowered.asm.mixin.Shadow <methods>;
            @org.spongepowered.asm.mixin.injection.Inject <methods>;
        }
        -keepattributes *Annotation*,Signature,InnerClasses,EnclosingMethod
        -keep @kotlinx.serialization.Serializable class com.aquaticaces.config.** { *; }
    """.trimIndent()

    val rulesFile = layout.buildDirectory.file("proguard-rules.pro").get().asFile
    rulesFile.parentFile.mkdirs()
    rulesFile.writeText(rules)
    
    configuration(rulesFile)
}

tasks.register("buildObfuscated") {
    group = "build"
    description = "Builds the ProGuard-obfuscated release jar"
    dependsOn("proguard")
}