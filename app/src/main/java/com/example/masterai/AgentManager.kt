package com.example.masterai

data class AgentSpec(
    val id: String,
    val name: String,
    val role: String,
    val instructions: String,
    val model: String,
    val tools: List<String>,
    val active: Boolean = true
)

class AgentManager {
    private val agents = linkedMapOf<String, AgentSpec>()

    fun createAgent(
        name: String,
        role: String,
        instructions: String,
        model: String = "gpt-4o-mini",
        tools: List<String> = listOf("calculator", "system_status")
    ): AgentSpec {
        val id = "agent_${System.currentTimeMillis()}"
        val spec = AgentSpec(
            id = id,
            name = name,
            role = role,
            instructions = instructions,
            model = model,
            tools = tools,
            active = true
        )
        agents[id] = spec
        return spec
    }

    fun listAgents(): List<AgentSpec> = agents.values.toList()

    fun getAgent(id: String): AgentSpec? = agents[id]

    fun runAgent(agentId: String, task: String): String {
        val agent = agents[agentId] ?: return "Agent not found."
        return """
            Agent: ${agent.name}
            Role: ${agent.role}
            Task: $task
            Instructions: ${agent.instructions}
            Tools: ${agent.tools.joinToString()}
        """.trimIndent()
    }
}
