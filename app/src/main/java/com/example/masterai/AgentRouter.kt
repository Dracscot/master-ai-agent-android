package com.example.masterai

import java.util.UUID

data class AgentTask(
    val id: String = UUID.randomUUID().toString(),
    val userRequest: String,
    val role: AgentRole,
    val context: Map<String, String> = emptyMap()
)
