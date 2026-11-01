package generated

import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse
import java.time.Duration
import kotlin.Double
import kotlin.Pair
import kotlin.String
import kotlin.collections.List
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonNull
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.add
import kotlinx.serialization.json.buildJsonArray
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.double
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import kotlinx.serialization.json.put
import kotlinx.serialization.json.putJsonObject

public data class Decision(
  public val input: String,
  public val answer: String,
)

/**
 * System One (RKEV) decision client. Env: RKEV_BASE_URL (required), RKEV_API_KEY (optional).
 */
public object SystemOneDecisions {
  private val client: HttpClient =
      HttpClient.newBuilder().connectTimeout(Duration.ofSeconds(10)).build()

  private val baseUrl: String by
      lazy { (System.getenv("RKEV_BASE_URL") ?: error("RKEV_BASE_URL is not set")).trimEnd('/') }

  public fun choice(
    state: String,
    question: String,
    options: List<Pair<String, String?>>,
    model: String? = null,
  ): String {
    val criteria = buildJsonObject {
        options.forEach { (name, description) -> put(name, description?.let { JsonPrimitive(it) } ?: JsonNull) }
    }
    return ask(state, question, "choice", criteria, model)["choice"]!!.jsonPrimitive.content
  }

  public fun noul(
    state: String,
    question: String,
    threshold: Double,
    model: String? = null,
  ): String {
    val probability = ask(state, question, "noul", null, model)["noul"]!!.jsonPrimitive.double
    return if (probability >= threshold) "yes" else "no"
  }

  public fun score(
    state: String,
    question: String,
    levels: List<String>,
    model: String? = null,
  ): String {
    val criteria = buildJsonArray { levels.forEach { add(it) } }
    val value = ask(state, question, "score", criteria, model)["score"]!!.jsonPrimitive.double
    return levels[Math.round(value).toInt().coerceIn(0, levels.lastIndex)]
  }

  private fun ask(
    state: String,
    question: String,
    type: String,
    criteria: JsonElement?,
    model: String?,
  ): JsonObject {
    val payload = buildJsonObject {
        put("state", state)
        if (model != null) put("model", model)
        putJsonObject("questions") {
            putJsonObject("decision") {
                put("type", type)
                put("instructions", question)
                if (criteria != null) put("criteria", criteria)
            }
        }
    }
    val request = HttpRequest.newBuilder(URI.create(baseUrl + "/v1/systemone"))
        .header("Content-Type", "application/json")
        .timeout(Duration.ofSeconds(60))
        .POST(HttpRequest.BodyPublishers.ofString(payload.toString()))
    val apiKey = System.getenv("RKEV_API_KEY")
    if (apiKey != null) request.header("Authorization", "Bearer " + apiKey)
    val response = client.send(request.build(), HttpResponse.BodyHandlers.ofString())
    if (response.statusCode() != 200) error("System One returned HTTP " + response.statusCode() + ": " + response.body())
    return Json.parseToJsonElement(response.body()).jsonObject["answers"]!!.jsonObject["decision"]!!.jsonObject
  }
}
