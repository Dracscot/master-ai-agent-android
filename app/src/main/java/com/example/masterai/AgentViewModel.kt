package com.example.masterai

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class ChatMessage(
    val role: String,
    val text: String
)

data class AgentUiState(
    val messages: List<ChatMessage> = emptyList(),
    val isBusy: Boolean = false,
    val activeAgents: List<AgentSpec> = emptyList()
)

class AgentViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(AgentUiState())
    val uiState: StateFlow<AgentUiState> = _uiState.asStateFlow()

    private val toolRegistry = ToolRegistry().apply {
        register(SystemStatusTool())
        register(CalculatorTool())
    }

    private val agentManager = AgentManager()

    // In a production app, keep the API key off-device or behind a secure backend proxy.
    private val llm = OpenAICompatibleClient(
        baseUrl = "https://api.openai.com/v1",
        apiKey = "PUT_YOUR_OPENAI_API_KEY_HERE",
        model = "gpt-4o-mini"
    )

    private fun handleCommand(userText: String) {
        when {
            userText.contains("create a researcher agent", true) -> {
                createAgent(
                    name = "Researcher",
                    role = "Research and summarize",
                    instructions = "Research the topic, verify essential facts, and summarize clearly."
                )
            }
            userText.contains("create a coding agent", true) -> {
                createAgent(
                    name = "Coder",
                    role = "Write and fix code",
                    instructions = "Write correct, efficient code for the task and explain key decisions."
                )
            }
            userText.contains("create a qa agent", true) -> {
                createAgent(
                    name = "QA",
                    role = "Quality and validation",
                    instructions = "Review the output for bugs, edge cases, and reliability."
                )
            }
        }
    }

    fun createAgent(name: String, role: String, instructions: String) {
        val created = agentManager.createAgent(name, role, instructions)
        _uiState.update {
            it.copy(activeAgents = agentManager.listAgents())
        }
        sendMessage("Agent '${created.name}' created successfully.")
    }

    fun sendMessage(text: String) {
        if (text.isBlank()) return

        handleCommand(text)

        viewModelScope.launch {
            _uiState.update {
                it.copy(
                    messages = it.messages + ChatMessage("user", text),
                    isBusy = true
                )
            }

            val prompt = """
                You are the master AI agent.
                Available tools: ${toolRegistry.names().joinToString()}
                Active agents: ${agentManager.listAgents().joinToString { it.name }}

                User request:
                $text
            """.trimIndent()

            val reply = llm.complete(prompt)

            _uiState.update {
                it.copy(
                    messages = it.messages + ChatMessage("assistant", reply),
                    isBusy = false,
                    activeAgents = agentManager.listAgents()
                )
            }
        }
    }
}
