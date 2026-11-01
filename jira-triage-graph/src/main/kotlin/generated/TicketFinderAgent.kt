package generated

import ai.koog.agents.core.agent.AIAgent
import ai.koog.agents.core.agent.config.AIAgentConfig
import ai.koog.prompt.dsl.prompt
import ai.koog.prompt.executor.llms.all.simpleOllamaAIExecutor
import ai.koog.prompt.executor.ollama.client.OllamaModels
import ai.koog.prompt.executor.ollama.client.OllamaParams
import kotlin.String

public fun createTicketFinderAgent(): AIAgent<String, String> = AIAgent(
    promptExecutor = simpleOllamaAIExecutor(System.getenv("OLLAMA_BASE_URL") ?: "http://localhost:11434"),
    agentConfig = AIAgentConfig(
        prompt = prompt("chat", params = OllamaParams(temperature = 0.0, think = false)) {
            system("Du bist ein Jira-Assistent. Die Nachricht nennt ein Projekt. Suche mit searchIssues alle offenen Tickets dieses Projekts (JQL: project = PROJEKTKEY AND statusCategory != Done ORDER BY created ASC) und lies jedes Ticket mit getIssue.\nDeine abschliessende Antwort ist ein JSON-Array mit einem Objekt pro Ticket, genau in dieser Form:\n[{\"key\": \"KAN-1\", \"titel\": \"...\", \"beschreibung\": \"...\"}, {\"key\": \"KAN-2\", \"titel\": \"...\", \"beschreibung\": \"...\"}]\nEckige Klammern aussen, kein Markdown-Codeblock. Fehlt eine Beschreibung, schreibe \"\".\n\nUse your tools as needed first. When you have finished all tool calls, give your final answer as a JSON array only: each element is processed separately in the next step. Write nothing before or after the array in that final answer.")
        },
        model = OllamaModels.Alibaba.QWEN_3_5_9B,
        maxAgentIterations = 50
    ),
    toolRegistry = createTicketFinderToolRegistry()
)
