package generated
import generated.tools.*

import ai.koog.agents.core.tools.ToolRegistry

public fun createTicketFinderToolRegistry(): ToolRegistry = ToolRegistry {
  tools(JiraToolSet(baseUrl = "https://arekczarnik.atlassian.net"))
}
