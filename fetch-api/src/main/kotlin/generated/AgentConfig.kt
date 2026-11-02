package generated

import ai.koog.agents.core.agent.AIAgent
import ai.koog.agents.core.agent.config.AIAgentConfig
import ai.koog.prompt.dsl.prompt
import ai.koog.prompt.executor.llms.all.simpleOllamaAIExecutor
import ai.koog.prompt.executor.ollama.client.OllamaModels
import ai.koog.prompt.params.LLMParams
import kotlin.String
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Primary

@Configuration
public class AgentConfig(
  @Value("\${ollama.base-url}")
  private val baseUrl: String,
) {
  @Bean
  @Primary
  public fun agent(): AIAgent<String, String> = AIAgent(
      promptExecutor = simpleOllamaAIExecutor(baseUrl),
      agentConfig = AIAgentConfig(
          prompt = prompt("chat", params = LLMParams(temperature = 0.0)) {
              system("Du beantwortest Fragen zu oeffentlichen GitHub-Repositories. Rufe dazu fetch mit method GET und url /repos/<owner>/<repo> auf (die Basis-URL https://api.github.com ist gesetzt). Kuendige nie an, was du tun wirst, sondern rufe die Werkzeuge sofort auf.\nAntworte nur mit Fakten aus der Antwort der API.")
          },
          model = ai.koog.prompt.llm.LLModel(ai.koog.prompt.llm.LLMProvider.Ollama, "arekczarnik/qwen2.5-coder-tools:14b", listOf(ai.koog.prompt.llm.LLMCapability.Temperature, ai.koog.prompt.llm.LLMCapability.Schema.JSON.Standard, ai.koog.prompt.llm.LLMCapability.Tools)),
          maxAgentIterations = 20
      ),
      toolRegistry = createToolRegistry()
  )
}
