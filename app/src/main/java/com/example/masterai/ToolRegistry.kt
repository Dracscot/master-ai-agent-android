package com.example.masterai

import kotlinx.coroutines.runBlocking

interface AgentTool {
    val name: String
    suspend fun execute(args: Map<String, String>): String
}

class ToolRegistry {
    private val tools = linkedMapOf<String, AgentTool>()

    fun register(tool: AgentTool) {
        tools[tool.name] = tool
    }

    fun execute(name: String, args: Map<String, String>): String {
        val tool = tools[name] ?: return "Tool '$name' not found."
        return runBlocking { tool.execute(args) }
    }

    fun names(): List<String> = tools.keys.toList()
}

class SystemStatusTool : AgentTool {
    override val name = "system_status"
    override suspend fun execute(args: Map<String, String>): String {
        return "Android device is online and ready."
    }
}

class CalculatorTool : AgentTool {
    override val name = "calculator"
    override suspend fun execute(args: Map<String, String>): String {
        val expression = args["expression"] ?: return "Missing expression."
        return try {
            val value = expression.replace(" ", "").toDouble()
            "Result: $value"
        } catch (e: Exception) {
            "Calculation failed: ${e.message}"
        }
    }
}
