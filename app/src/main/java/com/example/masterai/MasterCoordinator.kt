package com.example.masterai

class AgentRouter {
    fun route(task: AgentTask): AgentRole {
        val text = task.userRequest.lowercase()

        return when {
            text.contains("research") || text.contains("find") || text.contains("summarize") -> AgentRole.RESEARCHER
            text.contains("code") || text.contains("bug") || text.contains("fix") || text.contains("implement") -> AgentRole.CODER
            text.contains("test") || text.contains("verify") || text.contains("qa") || text.contains("review") -> AgentRole.QA
            text.contains("report") || text.contains("write summary") || text.contains("explain") -> AgentRole.REPORTER
            else -> AgentRole.RESEARCHER
        }
    }
}
