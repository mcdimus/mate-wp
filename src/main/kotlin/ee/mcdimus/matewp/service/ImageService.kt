package ee.mcdimus.matewp.service

import java.awt.Color
import java.awt.Font
import java.awt.image.BufferedImage
import java.net.URL
import javax.imageio.ImageIO

/**
 * @author Dmitri Maksimov
 */
class ImageService {

  fun download(url: String) = this.download(URL(url))

  fun download(url: URL): BufferedImage? {
    println("Downloading image: $url")

    return try {
      val contentLength = getImageSize(url)
      println("\t [-] size: $contentLength bytes")

      ImageIO.read(url)
    } catch(e: Exception) {
      System.err.println("\t [-] download failed: ${e.message}")
      null
    }
  }

  private fun getImageSize(url: URL) = url.openConnection().contentLengthLong

  fun addText(image: BufferedImage, text: String): BufferedImage {
    val shapeX = 10
    val shapeY = 40

    val imageGraphics = image.graphics

    val mainTokens = text.substringBeforeLast('(').split(',').map(String::trim)
//    val copyrightToken = text.substringAfterLast('(').trimEnd { it == ')' }
//    val textTokens = mainTokens + copyrightToken
    val textTokens = mainTokens

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

    return image
  }

}
