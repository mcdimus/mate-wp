package ee.mcdimus.matewp

import io.kotest.core.config.AbstractProjectConfig
import io.kotest.core.spec.IsolationMode

class ProjectConfig: AbstractProjectConfig() {
  override val parallelism = 8
  override val isolationMode = IsolationMode.InstancePerLeaf
}