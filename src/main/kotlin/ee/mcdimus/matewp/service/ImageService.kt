package ee.mcdimus.matewp.service

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

}
