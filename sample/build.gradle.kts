plugins {
    kotlin("jvm")
    id("com.google.devtools.ksp") version "1.7.0-1.0.6"
}

repositories {
    mavenCentral()
}

kotlin {
    sourceSets.main {
        kotlin.srcDir("build/generated/ksp/main/kotlin")
    }
    sourceSets.test {
        kotlin.srcDir("build/generated/ksp/test/kotlin")
    }
}

dependencies {
    implementation(kotlin("stdlib-jdk8"))
    implementation(project(":processor"))
    ksp(project(":processor"))
}