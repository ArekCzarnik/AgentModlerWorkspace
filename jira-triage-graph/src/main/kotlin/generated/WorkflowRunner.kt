package generated

import kotlin.String
import kotlin.collections.MutableMap
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.addJsonObject
import kotlinx.serialization.json.buildJsonArray
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.put
import kotlinx.serialization.json.putJsonObject

public class WorkflowRunner {
  public suspend fun run(input: String): String = stepAgent_finder(input)

  private suspend fun stepAgent_finder(input: String): String {
    val out = createTicketFinderAgent().run(input)
    return stepAgent_reporter(collect(parseItems(out).map { runItem(it) { trace -> itemAgent_rater(it, trace) } }))
  }

  private suspend fun itemAgent_rater(input: String, trace: MutableMap<String, String>): String {
    val out = createTicketRaterAgent().run(input)
    return out
  }

  private suspend fun stepAgent_reporter(input: String): String {
    val out = createReporterAgent().run(input)
    return out
  }
}

private class ItemResult(val input: String, val decisions: Map<String, String>, val result: String?, val error: String?)

private suspend fun runItem(input: String, body: suspend (MutableMap<String, String>) -> String): ItemResult {
    val trace = linkedMapOf<String, String>()
    return try {
        ItemResult(input, trace, body(trace), null)
    } catch (e: kotlinx.coroutines.CancellationException) {
        throw e
    } catch (e: Exception) {
        ItemResult(input, trace, null, e.message ?: e::class.simpleName ?: "error")
    }
}

private fun parseItems(output: String): List<String> {
    val start = output.indexOf('['); val end = output.lastIndexOf(']')
    // JSON errors and non-array elements are IllegalArgumentExceptions too; report them all the same way.
    val items = try {
        require(start >= 0 && end > start)
        Json.parseToJsonElement(output.substring(start, end + 1)).jsonArray
    } catch (e: IllegalArgumentException) {
        throw IllegalArgumentException("Expected a JSON array, got: ${output.take(200)}", e)
    }
    return items.map {
        if (it is JsonPrimitive && it.isString) it.content else it.toString()
    }
}

private fun collect(results: List<ItemResult>): String = buildJsonArray {
    results.forEach { r ->
        addJsonObject {
            put("input", r.input)
            putJsonObject("decisions") { r.decisions.forEach { (k, v) -> put(k, v) } }
            if (r.error == null) put("result", r.result) else put("error", r.error)
        }
    }
}.toString()
