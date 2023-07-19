package ee.mcdimus.matewp

import ee.mcdimus.matewp.cli.CLICommand
import ee.mcdimus.matewp.cli.CommandHandler
import ee.mcdimus.matewp.usecase.DownloadWallpaper
import ee.mcdimus.matewp.usecase.FetchWallpaperMetadata
import ee.mcdimus.matewp.usecase.InstallWallpaper
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
    if (args.isEmpty()) {
      printUsage()
    } else {
      val di = DI {
        bindSingleton { HttpClient.newHttpClient() }
        bindSingleton { FetchWallpaperMetadata(httpClient = instance(), retryRegistry = instance()) }
        bindSingleton { DownloadWallpaper() }
        bindSingleton { InstallWallpaper() }
        bindSingleton {
          @Suppress("MagicNumber")
          val config = RetryConfig.custom<Any>()
            .maxAttempts(3)
            .retryExceptions(IOException::class.java)
            .intervalFunction(IntervalFunction.ofExponentialBackoff())
            .build()

          RetryRegistry.of(config)
        }
      }

      val cliCommand = CLICommand(id = args[0], args = args.drop(1))
      CommandHandler.of(cliCommand, di).handle()
    }
  }
  LOG.info("CLI execution end. Time elapsed: {} ms.", elapsedMillis)
}

private fun printUsage() {
  println("Usage:")
  println("\t[x] 'mate-wp update': will download and set as wallpaper the current photo of the day.")
  println("\t[x] 'mate-wp save <name>': will save the current wallpaper with <name> so that it will be possible to restore it.")
  println("\t[x] 'mate-wp restore <name>': will set as wallpaper the photo which was saved with <name>.")
  println("\t[x] 'mate-wp list': will list all saved wallpapers.")
}
