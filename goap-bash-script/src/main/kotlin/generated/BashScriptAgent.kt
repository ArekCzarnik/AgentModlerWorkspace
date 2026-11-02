package generated

import ai.koog.agents.core.agent.AIAgent
import ai.koog.agents.core.agent.config.AIAgentConfig
import ai.koog.prompt.dsl.prompt
import ai.koog.prompt.executor.llms.all.simpleOllamaAIExecutor
import ai.koog.prompt.executor.ollama.client.OllamaModels
import ai.koog.prompt.executor.ollama.client.OllamaParams
import kotlin.String
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
public class BashScriptAgentConfig {
  @Bean
  public fun plannerAgent(@Value("\${ollama.base-url}") baseUrl: String): AIAgent<String, String> = AIAgent(
      promptExecutor = simpleOllamaAIExecutor(baseUrl),
      agentConfig = AIAgentConfig(
          prompt = prompt("planner", params = OllamaParams(temperature = 0.0, think = false)) {
              system("Plant per GOAP die Schritte vom Jira-Ticket bis zum getesteten, geprueften Bash-Skript samt Bericht. Voraussetzung: /workspace/sandbox muss vor dem Lauf existieren (mkdir -p /workspace/sandbox), das Schreib-Tool legt keine Ordner an. Developer- und ReviewAgent brauchen: ollama pull arekczarnik/qwen2.5-coder-tools:14b (qwen2.5-coder:14b mit einer Vorlage, die seine ```json-Werkzeugaufrufe fuer Ollama lesbar macht)")
          },
          model = OllamaModels.Alibaba.QWEN_3_5_9B,
          maxAgentIterations = 30
      ),
      strategy = createBashScriptPlanner()
  )
}
