package generated
import generated.tools.*

import ai.koog.agents.core.tools.ToolRegistry
import ai.koog.agents.ext.tool.`file`.EditFileTool
import ai.koog.agents.ext.tool.`file`.ListDirectoryTool
import ai.koog.agents.ext.tool.`file`.ReadFileTool
import ai.koog.agents.ext.tool.`file`.WriteFileTool
import ai.koog.agents.ext.tool.search.RegexSearchTool
import ai.koog.rag.base.files.JVMFileSystemProvider

public fun createReviewAgentToolRegistry(): ToolRegistry = ToolRegistry {
  tools(ShellToolSet(workingDir = "/workspace/sandbox"))
  val fileSystem = JVMFileSystemProvider.ReadWrite
  tool(ReadFileTool(fileSystem))
  tool(ListDirectoryTool(fileSystem))
  tool(RegexSearchTool(fileSystem))
  tool(WriteFileTool(fileSystem))
  tool(EditFileTool(fileSystem))
}
