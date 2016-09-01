package ee.mcdimus.matewp

import ee.mcdimus.matewp.command.CommandFactory
import java.util.*

/**
 * @author Dmitri Maksimov
 */
object Main {

  private const val CONFIG_FILENAME = "config.properties"

  // Constants for the MATE
  private const val GSETTINGS = "gsettings"
  private const val SET_CMD = "set"
  private const val GET_CMD = "get"
  private const val SCHEMA = "org.mate.background"
  private const val KEY = "picture-filename"

  private val props: Properties? = null

  @JvmStatic
  fun main(args: Array<String>) {
    if (args.isEmpty()) {
      printUsage()
      return
    }

    val command = CommandFactory.getCommand(args[0], *args.drop(1).toTypedArray())
    command.execute()
  }

  private fun printUsage() {
    println("Usage:")
    println("\t[x] 'mate-wp update': will download and set as wallpaper the current photo of the day.")
    println("\t[x] 'mate-wp save <name>': will save the current wallpaper with <name> so that it will be possible to restore it.")
    println("\t[x] 'mate-wp restore <name>': will set as wallpaper the photo which was saved with <name>.")
  }

  private fun saveCurrentWPconfig(id: String) {
//    val homeDir = File((System.getenv() as java.util.Map<String, String>).getOrDefault("HOME", "./"))
//    val imagesDir = File(homeDir, "Pictures/mate-wp")
//    val configDir = File(imagesDir, "configs")
//    try {
//      if (!configDir.exists()) {
//        if (!configDir.mkdirs()) {
//          System.err.println("Could not create directory: " + configDir.absolutePath)
//          System.exit(2)
//        }
//      }
//      val properties = Properties()
//      val result = execCommand(GSETTINGS, GET_CMD, SCHEMA, KEY)
//      properties.setProperty("matewp.system.background", result)
//      FileWriter(configDir + File.separator + id + ".properties").use({ writer -> properties.store(writer, null) })
//    } catch (ex1: IOException) {
//      System.err.println("Failed to create properties file at '" + configDir + File.separator + id + ".properties" + "'. Shutting down...")
//      System.exit(1)
//    } catch (ex1: InterruptedException) {
//      System.err.println("Failed to create properties file at '" + configDir + File.separator + id + ".properties" + "'. Shutting down...")
//      System.exit(1)
//    }
//
  }

  private fun restoreWPconfig(arg1: String) {
//    val homeDir = File((System.getenv() as java.util.Map<String, String>).getOrDefault("HOME", "./"))
//    val imagesDir = File(homeDir, "Pictures/mate-wp")
//    val configDir = File(imagesDir, "configs")
//
//    val config = File(configDir, arg1 + ".properties")
//    val properties = Properties()
//    try {
//      properties.load(FileInputStream(config))
//    } catch (e: IOException) {
//      e.printStackTrace()
//    }
//
//    val imageFile = File(properties.getProperty("matewp.system.background").replace("'", ""))
//    // change wallpaper
//    try {
//      // execute command 'gsettings set org.mate.background picture-filename '/home/dmitri/Pictures/mate-wp/2014-08-27.jpg''
//      execCommand(GSETTINGS, SET_CMD, SCHEMA, KEY, String.format("'%s'", imageFile.getAbsoluteFile()))
//    } catch (ex: IOException) {
//      System.err.println(ex.message)
//    } catch (ex: InterruptedException) {
//      System.err.println(ex.message)
//    }
//
  }

}
