group = "eu.maksimov"
version = "1.0"

plugins {
  application
  alias(libs.plugins.kotlin.jvm)
  alias(libs.plugins.kotlin.serialization)
  alias(libs.plugins.detekt)
  alias(libs.plugins.kover)
  alias(libs.plugins.versions)
  alias(libs.plugins.versions.catalogUpdate)
}

application {
  mainClass.set("ee.mcdimus.matewp.MainKt")
}

repositories {
  mavenCentral()
}

java {
  sourceCompatibility = JavaVersion.VERSION_11
  targetCompatibility = JavaVersion.VERSION_11
}

dependencies {
  implementation(kotlin("stdlib"))
  implementation(kotlin("reflect"))
  implementation(libs.kodein)
  implementation(libs.kotlinx.serialization.json)

  testImplementation(libs.bundles.kotest)
  testImplementation(libs.junit5.api)
  testRuntimeOnly(libs.junit5.engine)
}

versionCatalogUpdate {
  sortByKey.set(false)
}

tasks.withType<Test>().configureEach {
  useJUnitPlatform()
}

detekt {
  config = files("etc/detekt.yml")
}

tasks.wrapper {
  gradleVersion = "7.4.2"
  distributionType = Wrapper.DistributionType.ALL
}
