package ee.mcdimus.matewp.command

class UnknownCommand(val unknownCommandId: String) : Command {

  override fun execute() {
    System.err.println("Unknown command: $unknownCommandId")
  }

}
