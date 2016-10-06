package ee.mcdimus.matewp.command

/**
 * @author Dmitri Maksimov
 */
object CommandFactory {

  fun get(commandId: String, vararg commandArgs: String) = when (commandId) {
    "update" -> UpdateCommand()
    "save" -> SaveCommand(commandArgs.elementAtOrElse(0, { "" }))
    "restore" -> RestoreCommand(commandArgs.elementAtOrElse(0, { "previous" }))
    "list" -> ListCommand()
    else -> UnknownCommand(commandId)
  }

}

