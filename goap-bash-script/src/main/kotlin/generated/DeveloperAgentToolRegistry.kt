package generated
import generated.tools.*

import ai.koog.agents.core.tools.ToolRegistry

public fun createDeveloperAgentToolRegistry(): ToolRegistry = ToolRegistry {
  tools(ShellToolSet(workingDir = "/workspace/sandbox"))
}
