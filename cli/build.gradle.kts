import org.jreleaser.gradle.plugin.tasks.JReleaserAssembleTask
import org.jreleaser.model.Active

plugins {
  id("kotlin-conventions")
  application
  alias(libs.plugins.jreleaser)
}

dependencies {
  implementation(project(":core"))
  implementation(libs.logback)

  implementation(libs.kodein)
  implementation(libs.clikt)
}

application {
  mainClass = "eu.maksimov.matewp.cli.MainKt"
}


tasks.withType<JReleaserAssembleTask> {
  dependsOn("build")
}

jreleaser {
  gitRootSearch = true
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
        active = Active.ALWAYS
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
          ignoreMissingDeps = true
          useWildcardInPath = true
          multiRelease = "17"
          // JARs or classes used to limit the module search. Must be defined as paths. Defaults to empty.
          targets.add(File(projectDir, "build/classes/kotlin/main/eu/maksimov/matewp/cli/MainKt.class").absolutePath)
        }
        additionalModuleNames.add("jdk.crypto.ec") // required for SSL
        additionalModuleNames.add("java.naming") // required for logback
        additionalModuleNames.add("java.net.http")

        copyJars = true        // Copy main and input JARs into archive. Defaults to `true`.

        // The executable JAR that contains the application.
        mainJar {
          path = File("${projectDir.absolutePath}/build/libs/cli-{{projectVersion}}.jar")
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
            "eu.maksimov.matewp.cli.MainKt"        // The application's entry point. If left undefined, will use ${project.java.mainClass}.
          multiProject =
            false                          // Identifies the project as being member of a multi-project build.  If left undefined, will use ${project.java.multiProject}.
        }
      }
    }

    jpackage {
      create("app") {
        active = Active.ALWAYS
        exported = true
        verbose = true
        jlink = "app-jlink"
        attachPlatform = true

        applicationPackage {
          appName =
            "mate-wp"     // Name of the application and/or package.  If left undefined, will use `${assembler.name}`.
          vendor = "me"           // Vendor of the application. If left undefined, will use `${project.vendor}`.
          copyright = "Duke 2021" // Copyright for the application. If left undefined, will use `${project.copyright}`.
//          licenseFile = "path/to/LICENSE"
        }

        launcher {
//          arguments.add("update")
          this.javaOptions.add("-Dlogback.debug=false")
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
