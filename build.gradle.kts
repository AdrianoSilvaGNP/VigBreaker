import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.4.10"
    application
}
group = "me.lawliet"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}
dependencies {
    testImplementation(kotlin("test-junit"))
    implementation("com.github.shiguruikai:combinatoricskt:1.6.0")
}
tasks.withType<KotlinCompile>() {
    kotlinOptions.jvmTarget = "1.8"
}
application {
    mainClassName = "MainKt"
}