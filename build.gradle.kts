group = "eu.maksimov"
version = "1.0"

plugins {
  application
  kotlin("jvm") version "1.6.20"
  id("org.jetbrains.kotlinx.kover") version "0.5.0"
}

application {
  mainClass.set("ee.mcdimus.matewp.Main")
}

repositories {
  mavenCentral()
}

java {
  sourceCompatibility = JavaVersion.VERSION_11
  targetCompatibility = JavaVersion.VERSION_11
}

tasks.wrapper {
  gradleVersion = "7.4.2"
  distributionType = Wrapper.DistributionType.ALL
}

dependencies {
  implementation(kotlin("stdlib"))
  implementation(kotlin("reflect"))
  implementation("com.googlecode.json-simple:json-simple:1.1.1")

  testImplementation("org.junit.jupiter:junit-jupiter-api:5.8.2")
  testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.8.2")
  testImplementation("org.spekframework.spek2:spek-dsl-jvm:2.0.18")
  testRuntimeOnly("org.spekframework.spek2:spek-runner-junit5:2.0.18")
}

tasks.test {
  useJUnitPlatform {
    includeEngines("spek2")
  }
}
