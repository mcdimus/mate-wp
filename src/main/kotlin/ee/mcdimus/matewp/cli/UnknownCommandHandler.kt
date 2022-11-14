package ee.mcdimus.matewp.cli

class UnknownCommandHandler(
  private val cliCommand: CLICommand
) : CommandHandler {

  override fun handle() {
    println("unknown command: ${cliCommand.id}")
  }

}
