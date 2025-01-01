plugins {
    kotlin("jvm") version "2.1.0"
    `java-library`
    `maven-publish`
}

group = "org.glfwblocks"
version = "0.0.1"

repositories {
    mavenCentral()
    mavenLocal()
}

dependencies {
    //testImplementation(kotlin("test"))
    implementation("org.lwjgl:lwjgl:3.3.5")
    implementation("org.lwjgl:lwjgl-glfw:3.3.5")
}

tasks.test {
    useJUnitPlatform()
}

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(23)
    }
}

publishing {
    publications {
        create<MavenPublication>("mavenJava") {
            from(components["java"])
            groupId = "org.glfwblocks"
            artifactId = "glfwblocks"
            version = "0.0.1"
        }
    }
    repositories {
        mavenLocal()
    }
}