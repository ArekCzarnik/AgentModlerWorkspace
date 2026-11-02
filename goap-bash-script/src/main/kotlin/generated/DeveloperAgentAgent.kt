package generated

import ai.koog.agents.core.agent.AIAgent
import ai.koog.agents.core.agent.config.AIAgentConfig
import ai.koog.prompt.dsl.prompt
import ai.koog.prompt.executor.llms.all.simpleOllamaAIExecutor
import ai.koog.prompt.executor.ollama.client.OllamaModels
import ai.koog.prompt.executor.ollama.client.OllamaParams
import kotlin.String

public fun createDeveloperAgentAgent(): AIAgent<String, String> = AIAgent(
    promptExecutor = simpleOllamaAIExecutor(System.getenv("OLLAMA_BASE_URL") ?: "http://localhost:11434"),
    agentConfig = AIAgentConfig(
        prompt = prompt("chat", params = OllamaParams(temperature = 0.0, think = false)) {
            system("Du bist Bash-Entwickler. Du arbeitest ausschliesslich in /workspace/sandbox; dein einziges Werkzeug ist executeCommand, die Shell laeuft bereits dort, nutze relative Pfade.\nKuendige nie an, was du tun wirst, sondern rufe die Werkzeuge sofort auf. Wiederhole keinen Befehl unveraendert.\nHalte dich genau an die Schritte und das Antwortformat der Aufgabe.")
        },
        model = ai.koog.prompt.llm.LLModel(ai.koog.prompt.llm.LLMProvider.Ollama, "arekczarnik/qwen2.5-coder-tools:14b", listOf(ai.koog.prompt.llm.LLMCapability.Temperature, ai.koog.prompt.llm.LLMCapability.Schema.JSON.Standard, ai.koog.prompt.llm.LLMCapability.Tools)),
        maxAgentIterations = 30
    ),
    toolRegistry = createDeveloperAgentToolRegistry()
)
