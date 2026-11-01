package generated

import ai.koog.agents.core.agent.entity.AIAgentGraphStrategy
import ai.koog.agents.core.dsl.builder.node
import ai.koog.agents.core.dsl.builder.strategy
import ai.koog.agents.core.dsl.builder.subgraph
import ai.koog.agents.core.dsl.extension.nodeExecuteTools
import ai.koog.agents.core.dsl.extension.nodeLLMRequest
import ai.koog.agents.core.dsl.extension.nodeLLMSendToolResults
import ai.koog.agents.core.dsl.extension.onTextMessage
import ai.koog.agents.core.dsl.extension.onToolCalls
import kotlin.String

public fun createTicketRaterStrategy(): AIAgentGraphStrategy<String, String> = strategy<String, String>("TicketRater") {
  val Wie_wichtig_ by node<String, Decision>("Wie_wichtig_") {
    input -> Decision(input, SystemOneDecisions.score(input, "Wie wichtig ist dieses Jira-Ticket? Beruecksichtige Kundenauswirkung, Anzahl betroffener Nutzer, Sicherheits- und Datenrisiken sowie Dringlichkeit laut Beschreibung.", listOf("Niedrig", "Mittel", "Hoch")))
  }
  val commentLow by node<String, String> {
    input -> "Bewertung: Niedrig\nSchreibe jetzt den [KI-Bewertung]-Kommentar mit Wichtigkeit Niedrig in dieses Ticket:\n\n" + input
  }
  val commentMedium by node<String, String> {
    input -> "Bewertung: Mittel\nSchreibe jetzt den [KI-Bewertung]-Kommentar mit Wichtigkeit Mittel in dieses Ticket:\n\n" + input
  }
  val commentHigh by node<String, String> {
    input -> "Bewertung: Hoch\nSchreibe jetzt den [KI-Bewertung]-Kommentar mit Wichtigkeit Hoch in dieses Ticket:\n\n" + input
  }
  val writeComment by subgraph<String, String>(
      name = "writeComment",
      tools = createTicketRaterToolRegistry().tools.filter { it.name in setOf("addComment") }
  ) {
    val commentLlm by nodeLLMRequest()
    val execJiraComment by nodeExecuteTools()
    val sendJiraCommentResult by nodeLLMSendToolResults()

    edge(nodeStart forwardTo commentLlm onCondition { true })
    edge(commentLlm forwardTo execJiraComment onToolCalls { true })
    edge(commentLlm forwardTo nodeFinish onTextMessage { true })
    edge(execJiraComment forwardTo sendJiraCommentResult onCondition { true })
    edge(sendJiraCommentResult forwardTo execJiraComment onToolCalls { true })
    edge(sendJiraCommentResult forwardTo nodeFinish onTextMessage { true })
  }

  edge(nodeStart forwardTo Wie_wichtig_ onCondition { true })
  edge(Wie_wichtig_ forwardTo commentLow onCondition { it.answer in setOf("Niedrig") } transformed { it.input })
  edge(Wie_wichtig_ forwardTo commentMedium onCondition { it.answer in setOf("Mittel") } transformed { it.input })
  edge(Wie_wichtig_ forwardTo commentHigh onCondition { it.answer in setOf("Hoch") } transformed { it.input })
  edge(commentLow forwardTo writeComment onCondition { true })
  edge(commentMedium forwardTo writeComment onCondition { true })
  edge(commentHigh forwardTo writeComment onCondition { true })
  edge(writeComment forwardTo nodeFinish onCondition { true })
}
