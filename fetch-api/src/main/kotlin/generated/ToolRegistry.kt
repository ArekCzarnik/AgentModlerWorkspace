package generated
import generated.tools.*

import ai.koog.agents.core.tools.ToolRegistry

public fun createToolRegistry(): ToolRegistry = ToolRegistry {
  tools(FetchToolSet(baseUrl = "https://api.github.com"))
}
