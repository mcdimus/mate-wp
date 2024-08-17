plugins {
  id("kotlin-conventions")
  alias(libs.plugins.kotlin.serialization)
}

dependencies {
  api(libs.resilience.retry)
  implementation(libs.kotlinx.serialization.json)
  implementation(libs.kotlinx.serialization.properties)
  implementation(libs.kotlinx.datetime)
  implementation(libs.resilience.kotlin)
  implementation(libs.slf4j.api)
}