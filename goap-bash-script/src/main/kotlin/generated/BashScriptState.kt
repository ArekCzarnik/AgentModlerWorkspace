package generated

import ai.koog.agents.planner.goap.GoapAgentState
import kotlin.Boolean
import kotlin.Int
import kotlin.String

public data class BashScriptState(
  public val request: String,
  public val requirementsKnown: Boolean = false,
  public val requirements: String = "",
  public val ticketTried: Boolean = false,
  public val scriptWritten: Boolean = false,
  public val writeAttempts: Int = 0,
  public val devReport: String = "",
  public val reviewDone: Boolean = false,
  public val reviewPassed: Boolean = false,
  public val reviewAttempts: Int = 0,
  public val reviewFindings: String = "",
  public val reportSaved: Boolean = false,
  public val saveAttempts: Int = 0,
  public val report: String = "",
) : GoapAgentState<String, String>() {
  override val agentInput: String = request

  override fun provideOutput(): String = report
}
