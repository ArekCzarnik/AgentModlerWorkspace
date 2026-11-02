package generated.tools

import ai.koog.a2a.client.A2AClient
import ai.koog.a2a.client.UrlAgentCardResolver
import ai.koog.a2a.model.Message
import ai.koog.a2a.model.MessageSendParams
import ai.koog.a2a.model.Role
import ai.koog.a2a.model.Task
import ai.koog.a2a.model.TextPart
import ai.koog.a2a.transport.Request
import ai.koog.a2a.transport.client.jsonrpc.http.HttpJSONRPCClientTransport
import ai.koog.agents.core.tools.annotations.LLMDescription
import ai.koog.agents.core.tools.annotations.Tool
import ai.koog.agents.core.tools.reflect.ToolSet
import java.util.UUID
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withTimeoutOrNull

@LLMDescription("Answers questions about the capital cities of countries.")
class CapitalExpertA2AToolSet : ToolSet {

    // Env override lets the same export point at another host (e.g. a sibling harness in Kubernetes)
    private val agentUrl = System.getenv("A2A_CAPITALEXPERT_URL")?.takeIf { it.isNotBlank() } ?: "http://capital-expert:9101/capitals"
    private val agentUri = java.net.URI(agentUrl.trim())
    private val path = agentUri.path.trimEnd('/')
    private val origin = "${agentUri.scheme}://${agentUri.authority}"
    private val fullUrl = origin + path

    private val client = A2AClient(
        transport = HttpJSONRPCClientTransport(url = fullUrl),
        agentCardResolver = UrlAgentCardResolver(baseUrl = origin, path = if (path.isEmpty()) "/.well-known/agent-card.json" else "$path/agent-card.json")
    )
    private val mutex = Mutex()
    private var connected = false

    private suspend fun connectedClient(): A2AClient = mutex.withLock {
        if (!connected) {
            client.connect()
            connected = true
        }
        client
    }

    @Tool
    @LLMDescription("Send a message to the remote agent CapitalExpert and return its answer. Answers questions about the capital cities of countries.")
    suspend fun askCapitalExpert(@LLMDescription("The message to send") message: String): String = withTimeoutOrNull(30000) {
        val request = Message(
            messageId = UUID.randomUUID().toString(),
            role = Role.User,
            parts = listOf(TextPart(message))
        )
        when (val answer = connectedClient().sendMessage(Request(MessageSendParams(message = request))).data) {
            is Message -> answer.parts.filterIsInstance<TextPart>().joinToString("\n") { it.text }
            is Task -> answer.status.message?.parts?.filterIsInstance<TextPart>()?.joinToString("\n") { it.text }
                ?: answer.status.state.toString()
        }
    } ?: "The A2A agent CapitalExpert did not answer within 30000 ms"
}
