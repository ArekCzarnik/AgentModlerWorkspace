package generated.a2a

import ai.koog.agents.core.agent.AIAgent
import ai.koog.a2a.exceptions.A2AUnsupportedOperationException
import ai.koog.a2a.model.AgentCapabilities
import ai.koog.a2a.model.AgentCard
import ai.koog.a2a.model.AgentSkill
import ai.koog.a2a.model.Message
import ai.koog.a2a.model.MessageSendParams
import ai.koog.a2a.model.Role
import ai.koog.a2a.model.TextPart
import ai.koog.a2a.model.TransportProtocol
import ai.koog.a2a.server.A2AServer
import ai.koog.a2a.server.agent.AgentExecutor
import ai.koog.a2a.server.session.RequestContext
import ai.koog.a2a.server.session.SessionEventProcessor
import ai.koog.a2a.transport.server.jsonrpc.http.HttpJSONRPCServerTransport
import io.ktor.server.cio.CIO
import java.util.UUID
import kotlinx.coroutines.runBlocking
import org.springframework.beans.factory.DisposableBean
import org.springframework.boot.ApplicationArguments
import org.springframework.boot.ApplicationRunner
import org.springframework.stereotype.Component

class A2AExecutor(private val agent: AIAgent<String, String>) : AgentExecutor {
    override suspend fun execute(context: RequestContext<MessageSendParams>, eventProcessor: SessionEventProcessor) {
        val incoming = context.params.message
        if (context.task != null || !incoming.referenceTaskIds.isNullOrEmpty()) {
            throw A2AUnsupportedOperationException("Tasks are not supported; send a plain message.")
        }
        val text = incoming.parts.filterIsInstance<TextPart>().joinToString("\n") { it.text }
        val answer = agent.run(text)
        eventProcessor.sendMessage(
            Message(
                messageId = UUID.randomUUID().toString(),
                role = Role.Agent,
                parts = listOf(TextPart(answer)),
                contextId = context.contextId
            )
        )
    }
}

@Component
class A2AServerStarter(private val agent: AIAgent<String, String>) : ApplicationRunner, DisposableBean {
    private var transport: HttpJSONRPCServerTransport? = null

    override fun run(args: ApplicationArguments) = runBlocking {
        val card = AgentCard(
            protocolVersion = "0.3.0",
            name = "Capital Expert",
            description = "Answers questions about the capital cities of countries.",
            version = "1.0.0",
            url = "http://capital-expert:9101/capitals",
            preferredTransport = TransportProtocol.JSONRPC,
            capabilities = AgentCapabilities(streaming = false, pushNotifications = false, stateTransitionHistory = false),
            defaultInputModes = listOf("text"),
            defaultOutputModes = listOf("text"),
            skills = listOf(
            AgentSkill(id = "capitals", name = "Capital cities", description = "Names the capital of a country.", tags = listOf("geography"))
            )
        )
        val t = HttpJSONRPCServerTransport(A2AServer(agentExecutor = A2AExecutor(agent), agentCard = card))
        t.start(
            engineFactory = CIO,
            port = 9101,
            path = "/capitals",
            wait = false,
            agentCard = card,
            agentCardPath = "/capitals/agent-card.json"
        )
        transport = t
    }

    override fun destroy() {
        runBlocking { transport?.stop() }
    }
}
