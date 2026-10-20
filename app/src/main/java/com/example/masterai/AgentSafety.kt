package com.example.masterai

object AgentSafety {
    private val blockedPatterns = listOf(
        "delete all",
        "wipe device",
        "format phone",
        "disable security",
        "send sms to",
        "call all contacts",
        "steal data",
        "bypass authentication"
    )

    private val allowedTools = setOf(
        "calculator",
        "system_status",
        "lookup_weather",
        "read_calendar",
        "create_agent"
    )

    fun validateUserRequest(text: String): String {
        val lowered = text.lowercase()
        if (blockedPatterns.any { lowered.contains(it) }) {
            return "This request is blocked by safety policy."
        }
        return "ok"
    }

    fun validateToolCall(toolName: String): Boolean = toolName in allowedTools
}
