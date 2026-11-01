package generated

import kotlinx.coroutines.runBlocking
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController

@RestController
class ChatController {
    private val runner = WorkflowRunner()

    @PostMapping("/api/chat")
    fun chat(@RequestBody request: ChatRequest): ChatResponse {
        val result = runBlocking { runner.run(request.message) }
        return ChatResponse(response = result)
    }
}

data class ChatRequest(val message: String)

data class ChatResponse(val response: String)
