plugins {
    id("java")
    kotlin("jvm") version ("1.9.22")
    id("fabric-loom") version("1.4-SNAPSHOT")
}

group = "git.dragomordor.simpletms"
version = "1.1.0"

repositories {
    mavenLocal()
    mavenCentral()
    maven(url = "https://dl.cloudsmith.io/public/geckolib3/geckolib/maven/")
    maven("https://maven.impactdev.net/repository/development/")
    maven("https://oss.sonatype.org/content/repositories/snapshots")
}

dependencies {
    minecraft("net.minecraft:minecraft:1.20.1")
    mappings("net.fabricmc:yarn:1.20.1+build.8:v2")
    modImplementation("net.fabricmc:fabric-loader:0.14.21")

    modImplementation("net.fabricmc.fabric-api:fabric-api:0.89.3+1.20.1")
    modImplementation("net.fabricmc:fabric-language-kotlin:1.10.17+kotlin.1.9.22")
    modImplementation("com.cobblemon:fabric:1.4.0+")

}

tasks {
    compileKotlin {
        kotlinOptions.jvmTarget = "17"
    }
}

