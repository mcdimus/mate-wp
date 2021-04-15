group = "eu.maksimov"
version = "1.0"

plugins {
    application
    kotlin("jvm") version "1.4.32"
}

application {
    mainClassName = "ee.mcdimus.matewp.Main"
}

repositories {
    mavenCentral()
}

java {
    sourceCompatibility = JavaVersion.VERSION_11
    targetCompatibility = JavaVersion.VERSION_11
}

tasks.wrapper {
    gradleVersion = "7.0"
    distributionType = Wrapper.DistributionType.ALL
}

object Version {
    const val junit = "5.3.2"
    const val spek = "1.1.5"
}

dependencies {
    compile(kotlin("stdlib"))
    compile(kotlin("reflect"))
    compile("com.googlecode.json-simple:json-simple:1.1.1")

    testCompile("org.junit.jupiter:junit-jupiter-api:${Version.junit}")
    testRuntime("org.junit.jupiter:junit-jupiter-engine:${Version.junit}")
    testCompile("org.jetbrains.spek:spek-api:${Version.spek}"){
        exclude(group = "org.jetbrains.kotlin")
    }
    testRuntime("org.jetbrains.spek:spek-junit-platform-engine:${Version.spek}") {
        exclude(group = "org.jetbrains.kotlin")
        exclude(group = "org.junit.platform")
    }
}

tasks.test {
    useJUnitPlatform {
        includeEngines("spek")
    }
}
