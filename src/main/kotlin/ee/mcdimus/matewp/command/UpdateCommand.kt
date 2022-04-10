package ee.mcdimus.matewp.command

import ee.mcdimus.matewp.service.BingPhotoOfTheDayService
import ee.mcdimus.matewp.service.FileSystemService
import ee.mcdimus.matewp.service.ImageService
import ee.mcdimus.matewp.service.OpSysService
import ee.mcdimus.matewp.service.OpSysServiceFactory
import java.io.File
import java.io.IOException
import java.nio.file.Files
import javax.imageio.ImageIO
import kotlin.system.exitProcess

class UpdateCommand : Command {

  private val bingPhotoOfTheDayService: BingPhotoOfTheDayService by lazy { BingPhotoOfTheDayService() }
  private val fileSystemService: FileSystemService by lazy { FileSystemService() }
  private val opSystemService: OpSysService by lazy { OpSysServiceFactory.get() }
  private val imageService: ImageService by lazy { ImageService() }

  override fun execute() {
    // get image data
    val imageData = bingPhotoOfTheDayService.getData()
    println(imageData)

    val imagesDir = fileSystemService.getImagesDirectory()
    val imagePropertiesPath = imagesDir.resolve("${imageData.startDate}.properties")

    if (Files.notExists(imagePropertiesPath)) {
      SaveCommand("previous").execute()

      fileSystemService.saveProperties(imagePropertiesPath, linkedMapOf(
          "startDate" to imageData.startDate,
          "urlBase" to imageData.urlBase,
          "copyright" to imageData.copyright
      ))

      //  download image
      var imageFile: File? = null
      try {
        val image = imageService.download(imageData.downloadURL)
        val imageWithText = imageService.addText(image!!, imageData.copyright)

        imageFile = File(imagesDir.toFile(), imageData.filename)
        ImageIO.write(imageWithText, "jpg", imageFile)
      } catch (ignored: IOException) {
        System.err.println(imageFile?.absolutePath)
        exitProcess(1)
      }

        opSystemService.setAsWallpaper(imageFile.toPath())
    } else {
        val imageFile = imagesDir.resolve(imageData.filename)
        opSystemService.setAsWallpaper(imageFile)
    }
  }

}
