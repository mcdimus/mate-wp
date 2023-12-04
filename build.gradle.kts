import org.gradle.api.tasks.testing.logging.TestExceptionFormat.FULL
import org.gradle.api.tasks.testing.logging.TestLogEvent.FAILED
import org.gradle.api.tasks.testing.logging.TestLogEvent.PASSED
import org.gradle.api.tasks.testing.logging.TestLogEvent.SKIPPED
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import org.jreleaser.gradle.plugin.tasks.JReleaserAssembleTask
import org.jreleaser.model.Active.ALWAYS

plugins {
  application
  alias(libs.plugins.kotlin.jvm)
  alias(libs.plugins.kotlin.serialization)
  alias(libs.plugins.detekt)
  alias(libs.plugins.kover)
  alias(libs.plugins.versions)
  alias(libs.plugins.versions.catalogUpdate)
  alias(libs.plugins.jreleaser)
}

group = "eu.maksimov"
version = "1.0.0"

application {
  mainClass = "ee.mcdimus.matewp.MainKt"
}

repositories {
  mavenCentral()
}

java {
  toolchain {
    languageVersion = JavaLanguageVersion.of(17)
  }
}

tasks.withType<KotlinCompile>().configureEach {
  kotlinOptions.languageVersion = "1.9"
}

dependencies {
  implementation(kotlin("stdlib"))
  implementation(kotlin("reflect"))
  implementation(libs.kodein)
  implementation(libs.kotlinx.serialization.json)
  implementation(libs.kotlinx.serialization.properties)
  implementation(libs.kotlinx.datetime)
  implementation(libs.logback)
  implementation(libs.resilience.kotlin)
  implementation(libs.resilience.retry)

  // for jlink
  implementation("com.google.code.findbugs:jsr305:3.0.2")
  implementation("io.github.resilience4j:resilience4j-all:2.0.2")

  testImplementation(libs.bundles.kotest)
  testImplementation(libs.mockk)
  testImplementation(libs.assertj)
  testImplementation(libs.junit5.api)
  testRuntimeOnly(libs.junit5.engine)
}

versionCatalogUpdate {
  sortByKey = false
}

tasks.withType<Test>().configureEach {
  useJUnitPlatform()
  testLogging {
    events(PASSED, SKIPPED, FAILED)
    exceptionFormat = FULL
  }
}

detekt {
  config.from(files("etc/detekt.yml"))
}

tasks.withType<JReleaserAssembleTask> {
  dependsOn("build")
}

jreleaser {
  project {
    vendor = "Dmitri Maksimov"
    authors = listOf("Dmitri Maksimov")
    description = "Photo Of the Day from Bing"
    license = "Apache-2.0"
    inceptionYear = "2014"
    links {
      homepage = "https://github.com/mcdimus/mate-wp"
    }
    java {

    }
  }
  assemble {
    jlink {
      create("app-jlink") {
        active = ALWAYS
        exported = false
        executable = "mate-wp" // Name of the executable launcher. If left undefined, will use ${assembler.name}.

        val compiler = javaToolchains.compilerFor {
          languageVersion = JavaLanguageVersion.of(17)
        }

        // List of JDKs for generating cross-platform images.
        targetJdk {
          platform = "linux-x86_64"
          path = compiler.get().metadata.installationPath.asFile
        }

        // The JDK to use. If undefined, will find a matching JDK.
        jdk {
          platform = "linux-x86_64"
          path = compiler.get().metadata.installationPath.asFile
        }

        jdeps {
          ignoreMissingDeps = false
          useWildcardInPath = true
          multiRelease = "17"
          // JARs or classes used to limit the module search. Must be defined as paths. Defaults to empty.
          targets.add(File(projectDir, "build/classes/kotlin/main/ee/mcdimus/matewp/MainKt.class").absolutePath)
        }
        additionalModuleNames.add("jdk.crypto.ec") // required for SSL
        additionalModuleNames.add("java.naming") // required for logback

        copyJars = true        // Copy main and input JARs into archive. Defaults to `true`.

        // The executable JAR that contains the application.
        mainJar {
          path = File("build/libs/mate-wp-{{projectVersion}}.jar")
        }

        configurations.runtimeClasspath.get().filter { it.name.endsWith("jar") }.forEach {
          jars {
            setDirectory(it.parent)
            pattern = "*.jar"
          }
        }

        java {
//        groupId.set("${project.java.groupId}")        // Maven coordinates = groupId. If left undefined, will use ${project.java.groupId}.
//        artifactId.set("${project.java.artifactId}")  // Maven coordinates = artifactId. If left undefined, will use ${project.java.artifactId}.
          version =
            "17"                                // The minimum Java version required by consumers to run the application. If left undefined, will use ${project.java.version}
          mainClass =
            "ee.mcdimus.matewp.MainKt"        // The application's entry point. If left undefined, will use ${project.java.mainClass}.
          multiProject =
            false                          // Identifies the project as being member of a multi-project build.  If left undefined, will use ${project.java.multiProject}.
        }
      }
    }

    jpackage {
      create("app") {
        active = ALWAYS
        exported = true
        verbose = true
        jlink = "app-jlink"
        attachPlatform = true

        applicationPackage {
          appName =
            "mate-wp"     // Name of the application and/or package.  If left undefined, will use `${assembler.name}`.
          vendor = "me"           // Vendor of the application. If left undefined, will use `${project.vendor}`.
          copyright = "Duke 2021" // Copyright for the application. If left undefined, will use `${project.copyright}`.
          licenseFile = "path/to/LICENSE"
        }

        launcher {
          arguments.add("update")
        }

        linux {
          types.add("deb")
          installDir = "/opt/"           // Absolute path of the installation directory of the application.
          packageName = "mate-wp"        // Name for Linux package. If undefined, will use the assembler's name.
          maintainer = "Dmitri Maksimov" // Maintainer for .deb package.
          menuGroup = "apps"             // Menu group this application is placed in.
          license =
            "MIT"                // Type of the license ("License: " of the RPM .spec). If undefined, will use `${project.license}`
          appRelease =
            "1"               // Release value of the RPM .spec file or Debian revision value of the DEB control file.
          appCategory = "devel"          // Group value of the RPM .spec file or Section value of DEB control file.
          shortcut = false               // Creates a shortcut for the application
          jdk {
            val compiler = javaToolchains.compilerFor {
              languageVersion = JavaLanguageVersion.of(17)
            }
            path = compiler.get().metadata.installationPath.asFile
          }
          resourceDir = "${projectDir.absolutePath}/etc/jpackage/resources"
        }

        fileSet {
          input = "${projectDir.absolutePath}/etc/jpackage/files"
        }
      }
    }
  }
}

tasks.wrapper {
  gradleVersion = "8.5"
  distributionType = Wrapper.DistributionType.ALL
}
