package generated
import generated.tools.*

import ai.koog.agents.core.tools.ToolRegistry

public fun createResearchAgentToolRegistry(): ToolRegistry = ToolRegistry {
  tools(JiraToolSet(baseUrl = "https://arekczarnik.atlassian.net"))
}
