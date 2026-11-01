package generated

import ai.koog.agents.core.agent.AIAgent
import ai.koog.agents.core.agent.config.AIAgentConfig
import ai.koog.prompt.dsl.prompt
import ai.koog.prompt.executor.llms.all.simpleOllamaAIExecutor
import ai.koog.prompt.executor.ollama.client.OllamaModels
import ai.koog.prompt.executor.ollama.client.OllamaParams
import kotlin.String

public fun createReporterAgent(): AIAgent<String, String> = AIAgent(
    promptExecutor = simpleOllamaAIExecutor(System.getenv("OLLAMA_BASE_URL") ?: "http://localhost:11434"),
    agentConfig = AIAgentConfig(
        prompt = prompt("chat", params = OllamaParams(temperature = 0.0, think = false)) {
            system("Du bekommst ein JSON-Array mit einem Eintrag je Ticket: {input, decisions, result oder error}. Schreibe einen kurzen deutschen Bericht: pro Ticket der Key, die Wichtigkeitsstufe (lies sie aus result im Format \"OK <KEY>: <Stufe>\") und, falls vorhanden, der Fehler.")
        },
        model = OllamaModels.Alibaba.QWEN_3_5_9B,
        maxAgentIterations = 50
    )
)
