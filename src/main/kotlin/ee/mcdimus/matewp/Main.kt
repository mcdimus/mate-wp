package ee.mcdimus.matewp

import ee.mcdimus.matewp.command.CommandFactory

/**
 * @author Dmitri Maksimov
 */
object Main {

  @JvmStatic
  fun main(args: Array<String>) {
    if (args.isEmpty()) {
      printUsage()
      return
    }

    val command = CommandFactory.get(
        commandId = args[0],
        commandArgs = *args.drop(1).toTypedArray()
    )
    command.execute()
  }

  private fun printUsage() {
    println("Usage:")
    println("\t[x] 'mate-wp update': will download and set as wallpaper the current photo of the day.")
    println("\t[x] 'mate-wp save <name>': will save the current wallpaper with <name> so that it will be possible to restore it.")
    println("\t[x] 'mate-wp restore <name>': will set as wallpaper the photo which was saved with <name>.")
    println("\t[x] 'mate-wp list': will list all saved wallpapers.")
  }

}
