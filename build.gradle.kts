plugins {
  alias(libs.plugins.versions)
  alias(libs.plugins.versions.catalogUpdate)
  id("org.jetbrains.kotlinx.kover")
}

group = "eu.maksimov.matewp"
version = "1.0.0"

versionCatalogUpdate {
  sortByKey = false
}

dependencies {
  kover(project(":core"))
  kover(project(":cli"))
}

tasks.wrapper {
  gradleVersion = "8.8"
  distributionType = Wrapper.DistributionType.ALL
}
