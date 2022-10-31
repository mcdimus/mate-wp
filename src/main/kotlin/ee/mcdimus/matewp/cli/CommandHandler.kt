package ee.mcdimus.matewp.cli
import org.kodein.di.DI
import org.kodein.di.direct
import org.kodein.di.instance


interface CommandHandler {

  fun handle()

  companion object {
    fun of(cliCommand: CLICommand, di: DI): CommandHandler {
      return when (cliCommand.id.uppercase()) {
        FetchCommandHandler.KEY -> FetchCommandHandler(cliCommand, di.direct.instance())
        UpdateCommandHandler.KEY -> UpdateCommandHandler(
          cliCommand = cliCommand,
          fetchWallpaperMetadata = di.direct.instance(),
          downloadWallpaper = di.direct.instance(),
          installWallpaper = di.direct.instance()
        )
        else -> UnknownCommandHandler(cliCommand)
      }
    }
  }

}

