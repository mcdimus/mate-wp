plugins {
  base
  alias(libs.plugins.versions)
  alias(libs.plugins.versions.catalogUpdate)
  id("org.jetbrains.kotlinx.kover")
  id("detekt-convention")
  id("test-report-aggregation")
}

group = "eu.maksimov.matewp"
version = "1.0.0"

versionCatalogUpdate {
  sortByKey = false
}

dependencies {
  kover(project(":core"))
  kover(project(":cli"))

  testReportAggregation(project(":core"))
  testReportAggregation(project(":cli"))
}

reporting {
  reports {
    val testAggregateTestReport by creating(AggregateTestReport::class) {
      testType = TestSuiteType.UNIT_TEST
    }
  }
}

tasks.check {
  dependsOn(tasks.named<TestReport>("testAggregateTestReport"))
}

tasks.wrapper {
  gradleVersion = "8.14.3"
  distributionType = Wrapper.DistributionType.ALL
}
