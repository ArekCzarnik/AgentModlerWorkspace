package generated

import ai.koog.agents.core.agent.AIAgent
import ai.koog.agents.core.agent.config.AIAgentConfig
import ai.koog.prompt.dsl.prompt
import ai.koog.prompt.executor.llms.all.simpleOllamaAIExecutor
import ai.koog.prompt.executor.ollama.client.OllamaModels
import ai.koog.prompt.executor.ollama.client.OllamaParams
import kotlin.String

public fun createTicketRaterAgent(): AIAgent<String, String> = AIAgent(
    promptExecutor = simpleOllamaAIExecutor(System.getenv("OLLAMA_BASE_URL") ?: "http://localhost:11434"),
    agentConfig = AIAgentConfig(
        prompt = prompt("chat", params = OllamaParams(temperature = 0.0, think = false)) {
            system("Du bist ein Jira-Assistent. Die Eingabe ist ein Jira-Ticket als JSON-Objekt mit key, titel und beschreibung. Beginnt eine Nachricht mit \"Bewertung:\", schreibe mit addComment genau einen Kommentar in das genannte Ticket. Der Kommentar beginnt mit \"[KI-Bewertung] Wichtigkeit: <Stufe>\" und enthaelt danach eine Begruendung in zwei bis drei Saetzen auf Basis der Beschreibung. Antworte danach nur mit: OK <KEY>: <Stufe>")
        },
        model = OllamaModels.Alibaba.QWEN_3_5_9B,
        maxAgentIterations = 50
    ),
    strategy = createTicketRaterStrategy(),
    toolRegistry = createTicketRaterToolRegistry()
)
