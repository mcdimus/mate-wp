import org.gradle.api.tasks.testing.logging.TestExceptionFormat.FULL
import org.gradle.api.tasks.testing.logging.TestLogEvent.FAILED
import org.gradle.api.tasks.testing.logging.TestLogEvent.PASSED
import org.gradle.api.tasks.testing.logging.TestLogEvent.SKIPPED
import org.jetbrains.kotlin.gradle.dsl.KotlinVersion
import org.jetbrains.kotlin.gradle.tasks.KotlinJvmCompile

plugins {
  id("org.jetbrains.kotlin.jvm")
  id("io.gitlab.arturbosch.detekt")
  id("org.jetbrains.kotlinx.kover")
}

group = rootProject.group
version = rootProject.version

kotlin {
  jvmToolchain(17)
}

tasks.withType<KotlinJvmCompile>().configureEach {
  compilerOptions.languageVersion = KotlinVersion.KOTLIN_2_0
}

repositories {
  mavenCentral()
}

val libs = versionCatalogs.named("libs")
dependencies {
  implementation(kotlin("stdlib"))
  implementation(kotlin("reflect"))

  testImplementation(libs.findBundle("kotest").get())
  testImplementation(libs.findLibrary("mockk").get())
  testImplementation(libs.findLibrary("assertj").get())
  testImplementation(libs.findLibrary("junit5.api").get())
  testRuntimeOnly(libs.findLibrary("junit5.engine").get())
}

detekt {
  config.from(files("../etc/detekt.yml"))
}

koverReport {
  defaults {
    log {
      onCheck = true
    }
  }
}

tasks.withType<Test>().configureEach {
  useJUnitPlatform()
  testLogging {
    events(PASSED, SKIPPED, FAILED)
    exceptionFormat = FULL
  }
}