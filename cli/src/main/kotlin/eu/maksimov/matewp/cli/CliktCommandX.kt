package eu.maksimov.matewp.cli

import com.github.ajalt.clikt.core.CliktCommand
import org.kodein.di.DI
import org.kodein.di.direct
import org.kodein.di.instance

inline fun <reified T> CliktCommand.di(): Lazy<T> =
  lazy { currentContext.findObject<DI>()?.direct?.instance() ?: error("DI is not available") }
