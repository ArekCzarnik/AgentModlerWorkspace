package generated
import generated.tools.*

import ai.koog.agents.core.tools.ToolRegistry

public fun createToolRegistry(): ToolRegistry = ToolRegistry {
  tools(CapitalExpertA2AToolSet())
}
