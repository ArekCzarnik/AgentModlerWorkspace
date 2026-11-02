package generated

import ai.koog.agents.core.agent.AIAgent
import ai.koog.agents.core.agent.config.AIAgentConfig
import ai.koog.prompt.dsl.prompt
import ai.koog.prompt.executor.llms.all.simpleOllamaAIExecutor
import ai.koog.prompt.executor.ollama.client.OllamaModels
import ai.koog.prompt.executor.ollama.client.OllamaParams
import kotlin.String

public fun createResearchAgentAgent(): AIAgent<String, String> = AIAgent(
    promptExecutor = simpleOllamaAIExecutor(System.getenv("OLLAMA_BASE_URL") ?: "http://localhost:11434"),
    agentConfig = AIAgentConfig(
        prompt = prompt("chat", params = OllamaParams(temperature = 0.0, think = false)) {
            system("Du liest Jira-Tickets mit getIssue und aenderst nichts in Jira.\nBewerte nur, ob Ziel und Akzeptanzkriterien klar erkennbar sind. Deine Antwort beginnt immer mit genau einer dieser Zeilen:\nREQUIREMENTS_OK\nREQUIREMENTS_MISSING\nBei REQUIREMENTS_OK folgen Ticket-Key, Ziel und alle Akzeptanzkriterien woertlich, inklusive Dateiname, Testdaten und Testbefehlen. Bei REQUIREMENTS_MISSING folgt ein Satz, was fehlt. Kein Markdown-Codeblock.")
        },
        model = OllamaModels.Alibaba.QWEN_3_5_9B,
        maxAgentIterations = 10
    ),
    toolRegistry = createResearchAgentToolRegistry()
)
