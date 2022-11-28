import org.gradle.api.tasks.testing.logging.TestExceptionFormat.FULL
import org.gradle.api.tasks.testing.logging.TestLogEvent.FAILED
import org.gradle.api.tasks.testing.logging.TestLogEvent.PASSED
import org.gradle.api.tasks.testing.logging.TestLogEvent.SKIPPED
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import org.jreleaser.gradle.plugin.tasks.JReleaserAssembleTask
import org.jreleaser.model.Active.ALWAYS

@Suppress("DSL_SCOPE_VIOLATION") // suppressed until https://github.com/gradle/gradle/issues/22797 is fixed
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
  mainClass.set("ee.mcdimus.matewp.MainKt")
}

repositories {
  mavenCentral()
}

java {
  toolchain {
    languageVersion.set(JavaLanguageVersion.of(17))
  }
}

tasks.withType<KotlinCompile>().configureEach {
  kotlinOptions.languageVersion = "1.8"
}

dependencies {
  implementation(kotlin("stdlib"))
  implementation(kotlin("reflect"))
  implementation(libs.kodein)
  implementation(libs.kotlinx.serialization.json)
  implementation(libs.kotlinx.serialization.properties)
  implementation(libs.kotlinx.datetime)
  implementation(libs.logback)

  testImplementation(libs.bundles.kotest)
  testImplementation(libs.mockk)
  testImplementation(libs.assertj)
  testImplementation(libs.junit5.api)
  testRuntimeOnly(libs.junit5.engine)
}

versionCatalogUpdate {
  sortByKey.set(false)
}

tasks.withType<Test>().configureEach {
  useJUnitPlatform()
  testLogging {
    events(PASSED, SKIPPED, FAILED)
    exceptionFormat = FULL
  }
}

detekt {
  config = files("etc/detekt.yml")
}

tasks.withType<JReleaserAssembleTask> {
  dependsOn("build")
}

jreleaser {
  project {
    vendor.set("Dmitri Maksimov")
    website.set("https://github.com/mcdimus/mate-wp")
    authors.set(listOf("Dmitri Maksimov"))
    description.set("Photo Of the Day from Bing")
    license.set("Apache-2.0")
    extraProperties.put("inceptionYear", "2014")
    java {

    }
  }
  assemble {
    jlink {
      create("app-jlink") {
        active.set(ALWAYS)
        exported.set(false)
        executable.set("mate-wp")        // Name of the executable launcher. If left undefined, will use ${assembler.name}.

        val compiler = javaToolchains.compilerFor {
          languageVersion.set(JavaLanguageVersion.of(17))
        }

        // List of JDKs for generating cross-platform images.
        targetJdk {
          platform.set("linux-x86_64")
          path.set(compiler.get().metadata.installationPath.asFile)
        }

        // The JDK to use. If undefined, will find a matching JDK.
        jdk {
          platform.set("linux-x86_64")
          path.set(compiler.get().metadata.installationPath.asFile)
        }

        jdeps {
          ignoreMissingDeps.set(false)
          useWildcardInPath.set(true)
          multiRelease.set("17")
          // JARs or classes used to limit the module search. Must be defined as paths. Defaults to empty.
          targets.add(File(projectDir, "build/classes/kotlin/main/ee/mcdimus/matewp/MainKt.class").absolutePath)
        }

        copyJars.set(true)        // Copy main and input JARs into archive. Defaults to `true`.

        // The executable JAR that contains the application.
        mainJar {
          path.set(File("build/libs/mate-wp-{{projectVersion}}.jar"))
        }

        configurations.runtimeClasspath.get().filter { it.name.endsWith("jar") }.forEach {
          jars {
            setDirectory(it.parent)
            pattern.set("*.jar")
          }
        }

        java {
//            groupId.set("${project.java.groupId}")           // Maven coordinates = groupId. If left undefined, will use ${project.java.groupId}.
//            artifactId.set("${project.java.artifactId}")           // Maven coordinates = artifactId. If left undefined, will use ${project.java.artifactId}.
          version.set("17")      // The minimum Java version required by consumers to run the application. If left undefined, will use ${project.java.version}
          mainClass.set("ee.mcdimus.matewp.MainKt")       // The application's entry point. If left undefined, will use ${project.java.mainClass}.
          multiProject.set(false)           // Identifies the project as being member of a multi-project build.  If left undefined, will use ${project.java.multiProject}.
        }
      }
    }

    jpackage {
      create("app") {
        active.set(ALWAYS)
        exported.set(true)
        verbose.set(true)
        jlink.set("app-jlink")
        attachPlatform.set(true)

        applicationPackage {
          appName.set("mate-wp")     // Name of the application and/or package.  If left undefined, will use `${assembler.name}`.
          vendor.set("me")           // Vendor of the application. If left undefined, will use `${project.vendor}`.
          copyright.set("Duke 2021") // Copyright for the application. If left undefined, will use `${project.copyright}`.
          licenseFile.set("path/to/LICENSE")
        }

        launcher {
          arguments.add("update")
        }

        //
        linux {
          installDir.set("/opt/")    // Absolute path of the installation directory of the application.
          types.add("deb")
          packageName.set("mate-wp") // Name for Linux package. If undefined, will use the assembler's name.
          maintainer.set("Dmitri Maksimov")       // Maintainer for .deb package.
          menuGroup.set("apps")      // Menu group this application is placed in.
          license.set("MIT")         // Type of the license ("License: " of the RPM .spec). If undefined, will use `${project.license}`
          appRelease.set("1")        // Release value of the RPM .spec file or Debian revision value of the DEB control file.
          appCategory.set("devel")   // Group value of the RPM .spec file or Section value of DEB control file.
          shortcut.set(false)        // Creates a shortcut for the application
          jdk {
            val compiler = javaToolchains.compilerFor {
              languageVersion.set(JavaLanguageVersion.of(17))
            }
            path.set(compiler.get().metadata.installationPath.asFile)
          }
          resourceDir.set("${projectDir.absolutePath}/etc/jpackage/resources")
        }

        fileSet {
          input.set("${projectDir.absolutePath}/etc/jpackage/files")
        }
      }
    }
  }
}

tasks.wrapper {
  gradleVersion = "7.6"
  distributionType = Wrapper.DistributionType.ALL
}
