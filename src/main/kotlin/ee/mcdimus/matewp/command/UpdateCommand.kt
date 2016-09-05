package ee.mcdimus.matewp.command

import ee.mcdimus.matewp.service.BingPhotoOfTheDayService
import ee.mcdimus.matewp.service.FileSystemService
import java.io.BufferedReader
import java.io.File
import java.io.IOException
import java.io.InputStreamReader
import java.net.URL
import java.nio.file.Files
import javax.imageio.ImageIO

class UpdateCommand : Command {

  companion object {
    private const val GSETTINGS = "gsettings"
    private const val SET_CMD = "set"
    private const val GET_CMD = "get"
    private const val SCHEMA = "org.mate.background"
    private const val KEY = "picture-filename"
  }

  private val bingPhotoOfTheDayService: BingPhotoOfTheDayService by lazy { BingPhotoOfTheDayService() }
  private val fileSystemService: FileSystemService by lazy { FileSystemService() }

  override fun execute() {
    // get image data
    val imageData = bingPhotoOfTheDayService.getData()
    println(imageData)

    val imagesDir = fileSystemService.getImagesDirectory()
    val imagePropertiesPath = imagesDir.resolve("${imageData.startDate}.properties")

    if (Files.notExists(imagePropertiesPath)) {
      SaveCommand("previous").execute()

      fileSystemService.saveProperties(imagePropertiesPath, linkedMapOf(
          Pair("startDate", imageData.startDate),
          Pair("urlBase", imageData.urlBase),
          Pair("copyright", imageData.copyright)
      ))

      //  download image
      var imageFile: File? = null
      try {
        println("Download URL: " + imageData.downloadURL)
        val url = URL(imageData.downloadURL)
        val urlConnection = url.openConnection()
        val contentLength = urlConnection.contentLengthLong
        println("\t [-] image size: $contentLength bytes")
        val image = ImageIO.read(url)

        imageFile = File(imagesDir.toFile(), imageData.filename)
        ImageIO.write(image, "jpg", imageFile)
      } catch (e: IOException) {
        System.err.println(if ("Could not create image file: " + imageFile!! != null) imageFile.absolutePath else e.message)
        System.exit(4)
      }

      // change wallpaper
      try {
        // execute command 'gsettings set org.mate.background picture-filename '/home/dmitri/Pictures/mate-wp/2014-08-27.jpg''
        execCommand(GSETTINGS, SET_CMD, SCHEMA, KEY, String.format("'%s'", imageFile!!.absoluteFile))
      } catch (ex: IOException) {
        System.err.println(ex.message)
      } catch (ex: InterruptedException) {
        System.err.println(ex.message)
      }

    } else {
      // change wallpaper
      try {
        // execute command 'gsettings set org.mate.background picture-filename '/home/dmitri/Pictures/mate-wp/2014-08-27.jpg''
        val imageFile = File(imagesDir.toFile(), imageData.filename)
        execCommand(GSETTINGS, SET_CMD, SCHEMA, KEY, String.format("'%s'", imageFile.absoluteFile))
      } catch (ex: IOException) {
        System.err.println(ex.message)
      } catch (ex: InterruptedException) {
        System.err.println(ex.message)
      }

    }
  }

  @Throws(IOException::class, InterruptedException::class)
  private fun execCommand(vararg args: String): String? {
    val processBuilder = ProcessBuilder(*args)
    processBuilder.redirectErrorStream(true)
    val process = processBuilder.start()
    var value: String? = null
    BufferedReader(InputStreamReader(process.inputStream)).use { `in` ->
      value = `in`.readLine()
      //        while ((line = in.readLine()) != null) {
      //          value += line + "\n";
      //        }
    }
    process.waitFor()
    return value
  }

}
