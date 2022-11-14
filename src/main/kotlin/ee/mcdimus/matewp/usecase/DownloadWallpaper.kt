package ee.mcdimus.matewp.usecase

import ee.mcdimus.matewp.model.WallpaperMetadata
import ee.mcdimus.matewp.usecase.DownloadWallpaper.DownloadWallpaperCommand
import ee.mcdimus.matewp.usecase.DownloadWallpaper.DownloadWallpaperResult
import org.slf4j.LoggerFactory
import java.awt.Color
import java.awt.Font
import java.awt.image.BufferedImage
import java.io.IOException
import java.net.URL
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import java.util.Properties
import javax.imageio.ImageIO

class DownloadWallpaper : UseCase<DownloadWallpaperCommand, DownloadWallpaperResult> {

  companion object {
    @JvmStatic
    private val LOG = LoggerFactory.getLogger(this::class.java.enclosingClass)

    private const val USER_HOME = "user.home"
  }

  private val homeDirectory by lazy {
    val homeDirectoryPath = System.getProperty(USER_HOME)
      ?: throw IllegalStateException("system property $USER_HOME is not defined")

    LOG.debug("resolved home directory: {}", homeDirectoryPath)

    Paths.get(homeDirectoryPath)
  }

  private val picturesDirectory by lazy {
    val picturesDir = homeDirectory.resolve("Pictures/mate-wp")

    LOG.debug("resolved pictures directory: {}", picturesDir)

    if (Files.exists(picturesDir)) picturesDir else Files.createDirectories(picturesDir)
  }

  private val metadataDirectory by lazy {
    val metadataDir = picturesDirectory.resolve("configs")

    LOG.debug("resolved metadata directory: {}", metadataDir)

    if (Files.exists(metadataDir)) metadataDir else Files.createDirectories(metadataDir)
  }

  override fun execute(command: DownloadWallpaperCommand): DownloadWallpaperResult {
    LOG.info("downloading image: {}", command.wallpaperMetadata.url)
    return try {
      val url = URL(command.wallpaperMetadata.url)
      val contentLength = getImageSize(url)
      LOG.info("image size: $contentLength bytes")

      val bufferedImage = ImageIO.read(url).addText(command.wallpaperMetadata.title, command.wallpaperMetadata.copyright)
      val wallpaperPath = picturesDirectory.resolve(command.wallpaperMetadata.startDate.toString() + ".jpg")
      val metadataPath = metadataDirectory.resolve(command.wallpaperMetadata.startDate.toString() + ".properties")

      saveProperties(
        metadataPath, linkedMapOf(
          "startDate" to command.wallpaperMetadata.startDate.toString(),
          "urlBase" to command.wallpaperMetadata.url,
          "copyright" to command.wallpaperMetadata.copyright
        )
      )

      val isWritten = ImageIO.write(bufferedImage, "jpg", wallpaperPath.toFile())
      if (isWritten) {
        DownloadWallpaperResult.Success(wallpaperPath = wallpaperPath, metadataPath = metadataPath).also { LOG.info(it.toString()) }
      } else {
        DownloadWallpaperResult.Failure.also { LOG.info(it.toString()) }
      }
    } catch (e: IOException) {
      LOG.error("download failed: {}", e.message, e)
      DownloadWallpaperResult.Failure
    }
  }

  private fun getImageSize(url: URL) = url.openConnection().contentLengthLong

  @Suppress("MagicNumber")
  private fun BufferedImage.addText(text: String, copyright: String): BufferedImage {
    val shapeX = 10
    val shapeY = 40

    val imageGraphics = graphics

    val mainTokens = mutableListOf(text)
    val copyrightToken = copyright.substringBeforeLast('(').split(',').map(String::trim)
    val textTokens = mainTokens + copyrightToken

    val font = Font(Font.SANS_SERIF, Font.BOLD, 24)
    val fontMetrics = imageGraphics.getFontMetrics(font)
    val lineHeight = fontMetrics.height

    val longestTextToken = textTokens.maxByOrNull { it.length }!!
    val shapeWidth = fontMetrics.getStringBounds(longestTextToken, imageGraphics).width + (2 * lineHeight)
    val shapeHeight = (textTokens.size + 2) * lineHeight
    imageGraphics.color = Color(255, 255, 255, 128)

    imageGraphics.fillRoundRect(shapeX, shapeY, shapeWidth.toInt(), shapeHeight, 15, 15)

    imageGraphics.font = font
    imageGraphics.color = Color(0, 0, 0, 128)

    val startX = shapeX + lineHeight
    var startY = shapeY + (fontMetrics.ascent) + lineHeight
    textTokens.forEach {
      imageGraphics.drawString(it, startX, startY)
      startY += lineHeight
    }

    return this
  }

  private fun saveProperties(propertiesPath: Path, propertyMap: Map<String, String>): Path {
    val properties = Properties()
    for ((key, value) in propertyMap) {
      properties.setProperty(key, value)
    }

    Files.newOutputStream(propertiesPath).use {
      properties.store(it, null)
    }

    return propertiesPath
  }

  data class DownloadWallpaperCommand(val wallpaperMetadata: WallpaperMetadata)

  sealed class DownloadWallpaperResult {
    data class Success(val wallpaperPath: Path, val metadataPath: Path) : DownloadWallpaperResult()
    data object Failure : DownloadWallpaperResult()
  }

}