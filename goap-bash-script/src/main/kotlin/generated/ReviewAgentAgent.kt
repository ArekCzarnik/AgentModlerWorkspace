package generated

import ai.koog.agents.core.agent.AIAgent
import ai.koog.agents.core.agent.config.AIAgentConfig
import ai.koog.prompt.dsl.prompt
import ai.koog.prompt.executor.llms.all.simpleOllamaAIExecutor
import ai.koog.prompt.executor.ollama.client.OllamaModels
import ai.koog.prompt.executor.ollama.client.OllamaParams
import kotlin.String

public fun createReviewAgentAgent(): AIAgent<String, String> = AIAgent(
    promptExecutor = simpleOllamaAIExecutor(System.getenv("OLLAMA_BASE_URL") ?: "http://localhost:11434"),
    agentConfig = AIAgentConfig(
        prompt = prompt("chat", params = OllamaParams(temperature = 0.0, think = false)) {
            system("Du bist ein strenger Tester. Du arbeitest ausschliesslich in /workspace/sandbox. Die Shell (executeCommand) laeuft bereits dort, nutze relative Pfade. Dateien schreibst du mit __write_file__ und absolutem Pfad unter /workspace/sandbox/.\nKuendige nie an, was du tun wirst, sondern rufe die Werkzeuge sofort auf. Wiederhole keinen Befehl unveraendert.\nBeim Review aenderst du das Skript nicht, fuehrst die Tests selbst aus und beginnst deine Antwort mit genau einer Zeile APPROVED oder REJECTED. Verwende das Wort APPROVED niemals in einer Ablehnung.\nBeim Speichern rufst du zwingend __write_file__ auf. Erst danach antwortest du nur mit SAVED <Pfad>. Ohne Werkzeugaufruf ist nichts gespeichert, dann darfst du niemals SAVED antworten.")
        },
        model = ai.koog.prompt.llm.LLModel(ai.koog.prompt.llm.LLMProvider.Ollama, "arekczarnik/qwen2.5-coder-tools:14b", listOf(ai.koog.prompt.llm.LLMCapability.Temperature, ai.koog.prompt.llm.LLMCapability.Schema.JSON.Standard, ai.koog.prompt.llm.LLMCapability.Tools)),
        maxAgentIterations = 30
    ),
    toolRegistry = createReviewAgentToolRegistry()
)
