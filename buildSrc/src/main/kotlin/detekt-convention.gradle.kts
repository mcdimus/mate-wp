import io.gitlab.arturbosch.detekt.Detekt
import io.gitlab.arturbosch.detekt.DetektCreateBaselineTask

plugins {
  id("io.gitlab.arturbosch.detekt")
}

detekt {
  config.from(files("etc/detekt.yml"))
  source.from(files(projectDir))
}

tasks.withType<Detekt>().configureEach {
  include("**/*.kt")
  exclude("**/*.kts")
  exclude("**/build/**", "**/resources/**")
}

tasks.withType<DetektCreateBaselineTask>().configureEach {
  include("**/*.kt")
  exclude("**/*.kts")
  exclude("**/build/**", "**/resources/**")
}