package generated

import ai.koog.agents.core.agent.AIAgent
import ai.koog.prompt.executor.llms.all.simpleOllamaAIExecutor
import ai.koog.prompt.executor.ollama.client.OllamaModels
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
      systemPrompt = "You are a geography expert. Answer questions about capital cities briefly and precisely.",
      llmModel = ai.koog.prompt.llm.LLModel(ai.koog.prompt.llm.LLMProvider.Ollama, "arekczarnik/qwen2.5-coder-tools:14b", listOf(ai.koog.prompt.llm.LLMCapability.Temperature, ai.koog.prompt.llm.LLMCapability.Schema.JSON.Standard, ai.koog.prompt.llm.LLMCapability.Tools)),
      temperature = 0.0
  )
}
