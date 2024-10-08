package eu.maksimov.matewp.cli

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.core.context
import com.github.ajalt.clikt.core.obj
import org.kodein.di.DI

class MateWpCommand(di: DI) : CliktCommand() {

  init {
    context {
      this.obj = di
    }
  }

  override fun run() = Unit

}
