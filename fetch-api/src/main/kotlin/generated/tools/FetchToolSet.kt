package generated.tools

import ai.koog.agents.core.tools.annotations.LLMDescription
import ai.koog.agents.core.tools.annotations.Tool
import ai.koog.agents.core.tools.reflect.ToolSet
import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse
import kotlin.String

@LLMDescription("Tools for making HTTP requests")
public class FetchToolSet(
  private val baseUrl: String = "https://api.github.com",
) : ToolSet {
  private val client: HttpClient = HttpClient.newHttpClient()

  @Tool
  @LLMDescription("Make an HTTP request and return the response body")
  public fun fetch(@LLMDescription("The URL to fetch") url: String, @LLMDescription("HTTP method (GET, POST, PUT, DELETE)") method: String): String {
    val fullUrl = if (baseUrl.isEmpty() || url.startsWith("http://") || url.startsWith("https://")) url else baseUrl + url
    val request = HttpRequest.newBuilder()
            .uri(URI.create(fullUrl))
            .method(method.uppercase(), HttpRequest.BodyPublishers.noBody())
            .build()
    val response = client.send(request, HttpResponse.BodyHandlers.ofString())
    return "HTTP " + response.statusCode().toString() + "\n" + response.body()
  }
}
