package ee.mcdimus.matewp.command

/**
 * @author Dmitri Maksimov
 */
object CommandFactory {

  fun get(commandId: String, vararg commandArgs: String): Command {
    when (commandId) {
      "update" -> return UpdateCommand()
      "save" -> return SaveCommand(commandArgs.elementAtOrElse(0, { "" }))
      "restore" -> return RestoreCommand(commandArgs.elementAtOrElse(0, { "previous" }))
      "list" -> return ListCommand()
      else -> return UnknownCommand(commandId)
    }
  }

}

