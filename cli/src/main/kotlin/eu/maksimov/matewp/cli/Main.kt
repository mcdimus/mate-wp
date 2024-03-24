package eu.maksimov.matewp.cli

import com.github.ajalt.clikt.core.CliktError
import com.github.ajalt.clikt.core.subcommands
import eu.maksimov.matewp.core.usecase.DownloadWallpaper
import eu.maksimov.matewp.core.usecase.FetchWallpaperMetadata
import eu.maksimov.matewp.core.usecase.InstallWallpaper
import io.github.resilience4j.core.IntervalFunction
import io.github.resilience4j.retry.RetryConfig
import io.github.resilience4j.retry.RetryRegistry
import org.kodein.di.DI
import org.kodein.di.bindSingleton
import org.kodein.di.instance
import org.slf4j.LoggerFactory
import java.io.IOException
import java.net.http.HttpClient
import kotlin.system.measureTimeMillis


private val LOG = LoggerFactory.getLogger("cli-main")

fun main(args: Array<String>) {
  val elapsedMillis = measureTimeMillis {
    LOG.info("CLI execution start. Arguments: {}.", args)
    val di = DI {
      bindSingleton { HttpClient.newHttpClient() }
      bindSingleton { FetchWallpaperMetadata(httpClient = instance(), retryRegistry = instance()) }
      bindSingleton { DownloadWallpaper() }
      bindSingleton { InstallWallpaper() }
      bindSingleton { retryRegistry() }
    }

    val command = MateWpCommand(di).subcommands(UpdateCommand())
    try {
      command.parse(args)
    } catch (e: CliktError) {
      command.echoFormattedHelp(e)
    }
  }
  LOG.info("CLI execution end. Time elapsed: {} ms.", elapsedMillis)
}

private fun retryRegistry(): RetryRegistry {
  @Suppress("MagicNumber")
  val config = RetryConfig.custom<Any>()
    .maxAttempts(3)
    .retryExceptions(IOException::class.java)
    .intervalFunction(IntervalFunction.ofExponentialBackoff())
    .build()

  return RetryRegistry.of(config)
}
//
//private fun printUsage() {
//  println("Usage:")
//  println("\t[x] 'mate-wp update': will download and set as wallpaper the current photo of the day.")
//  println("\t[x] 'mate-wp save <name>': will save the current wallpaper with <name> so that it will be possible to restore it.")
//  println("\t[x] 'mate-wp restore <name>': will set as wallpaper the photo which was saved with <name>.")
//  println("\t[x] 'mate-wp list': will list all saved wallpapers.")
//}
