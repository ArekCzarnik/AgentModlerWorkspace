package generated.tools

import ai.koog.agents.core.tools.annotations.LLMDescription
import ai.koog.agents.core.tools.annotations.Tool
import ai.koog.agents.core.tools.reflect.ToolSet
import java.io.File
import java.lang.ProcessBuilder
import kotlin.String

@LLMDescription("Tools for executing shell commands")
public class ShellToolSet(
  private val workingDir: String = "/workspace/sandbox",
) : ToolSet {
  @Tool
  @LLMDescription("Execute a shell command and return its output")
  public fun executeCommand(@LLMDescription("The shell command to execute") command: String): String {
    val process = ProcessBuilder("sh", "-c", command)
        .directory(File(workingDir))
        .redirectErrorStream(true)
        .start()
    val output = process.inputStream.bufferedReader().readText()
    process.waitFor(60, java.util.concurrent.TimeUnit.SECONDS)
    return output.take(100_000)
  }
}
