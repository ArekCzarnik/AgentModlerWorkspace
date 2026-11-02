package generated.tools

import ai.koog.agents.core.tools.annotations.LLMDescription
import ai.koog.agents.core.tools.annotations.Tool
import ai.koog.agents.core.tools.reflect.ToolSet
import java.net.URI
import java.net.URLEncoder
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse
import java.util.Base64
import kotlin.Int
import kotlin.String
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.addJsonObject
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import kotlinx.serialization.json.put
import kotlinx.serialization.json.putJsonArray
import kotlinx.serialization.json.putJsonObject

@LLMDescription("Tools for searching, reading, creating and updating Jira issues")
public class JiraToolSet(
  baseUrl: String = "https://arekczarnik.atlassian.net",
) : ToolSet {
  private val baseUrl: String = URI.create(baseUrl).let { it.scheme + "://" + it.authority }

  private val client: HttpClient = HttpClient.newHttpClient()

  private val auth: String =
      "Basic " + Base64.getEncoder().encodeToString((env("ATLASSIAN_EMAIL") + ":" + env("ATLASSIAN_API_TOKEN")).toByteArray())

  private fun env(name: String): String = System.getenv(name)?.takeIf { it.isNotBlank() } ?: error(name + " is not set")

  private fun call(
    method: String,
    path: String,
    body: JsonObject? = null,
  ): HttpResponse<String> {
    val request = HttpRequest.newBuilder(URI.create(baseUrl + path))
        .header("Authorization", auth)
        .header("Accept", "application/json")
    if (body != null) request.header("Content-Type", "application/json")
    request.method(method, if (body != null) HttpRequest.BodyPublishers.ofString(body.toString()) else HttpRequest.BodyPublishers.noBody())
    return client.send(request.build(), HttpResponse.BodyHandlers.ofString())
  }

  private fun text(response: HttpResponse<String>): String = "HTTP " + response.statusCode() + "\n" + response.body()

  private fun enc(`value`: String): String = URLEncoder.encode(value, Charsets.UTF_8)

  private fun adf(text: String): JsonObject = buildJsonObject {
      put("type", "doc")
      put("version", 1)
      putJsonArray("content") {
          for (line in text.lines()) {
              addJsonObject {
                  put("type", "paragraph")
                  if (line.isNotBlank()) putJsonArray("content") {
                      addJsonObject { put("type", "text"); put("text", line) }
                  }
              }
          }
      }
  }

  @Tool
  @LLMDescription("Search Jira issues with JQL. Returns key, summary, status and assignee per issue.")
  public fun searchIssues(@LLMDescription("JQL query, e.g. project = ABC AND status = 'In Progress'") jql: String, @LLMDescription("Maximum number of issues (1-50)") maxResults: Int): String = text(call("GET", "/rest/api/3/search/jql?jql=" + enc(jql) + "&maxResults=" + maxResults.coerceIn(1, 50) + "&fields=summary,status,assignee"))

  @Tool
  @LLMDescription("Get a Jira issue with all its fields")
  public fun getIssue(@LLMDescription("Issue key, e.g. ABC-123") issueKey: String): String {
    if (!Regex("[A-Za-z][A-Za-z0-9_]*-\\d+|\\d+").matches(issueKey)) return "Invalid issueKey: " + issueKey
    return text(call("GET", "/rest/api/3/issue/" + issueKey))
  }

  @Tool
  @LLMDescription("Create a Jira issue")
  public fun createIssue(
    @LLMDescription("Project key, e.g. ABC") projectKey: String,
    @LLMDescription("Issue type name, e.g. Task, Bug, Story") issueType: String,
    @LLMDescription("One-line summary") summary: String,
    @LLMDescription("Plain text; line breaks start new paragraphs") description: String,
  ): String {
    val body = buildJsonObject {
        putJsonObject("fields") {
            putJsonObject("project") { put("key", projectKey) }
            putJsonObject("issuetype") { put("name", issueType) }
            put("summary", summary)
            put("description", adf(description))
        }
    }
    return text(call("POST", "/rest/api/3/issue", body))
  }

  @Tool
  @LLMDescription("Add a comment to a Jira issue")
  public fun addComment(@LLMDescription("Issue key, e.g. ABC-123") issueKey: String, @LLMDescription("Plain text; line breaks start new paragraphs") comment: String): String {
    if (!Regex("[A-Za-z][A-Za-z0-9_]*-\\d+|\\d+").matches(issueKey)) return "Invalid issueKey: " + issueKey
    return text(call("POST", "/rest/api/3/issue/" + issueKey + "/comment", buildJsonObject { put("body", adf(comment)) }))
  }

  @Tool
  @LLMDescription("Move a Jira issue to another status")
  public fun transitionIssue(@LLMDescription("Issue key, e.g. ABC-123") issueKey: String, @LLMDescription("Target status name, e.g. Done") statusName: String): String {
    if (!Regex("[A-Za-z][A-Za-z0-9_]*-\\d+|\\d+").matches(issueKey)) return "Invalid issueKey: " + issueKey
    val path = "/rest/api/3/issue/" + issueKey + "/transitions"
    val response = call("GET", path)
    if (response.statusCode() !in 200..299) return text(response)
    val transitions = Json.parseToJsonElement(response.body()).jsonObject["transitions"]?.jsonArray.orEmpty().map { it.jsonObject }
    val match = transitions.firstOrNull { t ->
        t["name"]?.jsonPrimitive?.content.equals(statusName, ignoreCase = true) ||
            t["to"]?.jsonObject?.get("name")?.jsonPrimitive?.content.equals(statusName, ignoreCase = true)
    } ?: return "No transition to '" + statusName + "'. Available: " +
        transitions.joinToString { it["name"]?.jsonPrimitive?.content.orEmpty() }
    val id = match["id"]!!.jsonPrimitive.content
    return text(call("POST", path, buildJsonObject { putJsonObject("transition") { put("id", id) } }))
  }
}
