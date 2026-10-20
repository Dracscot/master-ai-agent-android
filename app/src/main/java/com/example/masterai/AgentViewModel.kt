package com.example.masterai

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
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

class AgentViewModel(
    private val appContext: android.content.Context
) : ViewModel() {
    private val _uiState = MutableStateFlow(AgentUiState())
    val uiState: StateFlow<AgentUiState> = _uiState.asStateFlow()

    private val agentManager = AgentManager()
    private val tokenStore = SecureTokenStore(appContext)
    private val conversationStore = ConversationStore(appContext)
    private val proxyClient = ProxyAiClient("http://10.0.2.2:3000")
    private val gson = Gson()

    init {
        loadHistory()
    }

    private fun loadHistory() {
        viewModelScope.launch {
            conversationStore.historyFlow.collect { savedMessages ->
                if (savedMessages.isNotEmpty()) {
                    _uiState.update { it.copy(messages = savedMessages) }
                }
            }
        }
    }

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
                    instructions = "Review outputs for correctness, edge cases, and reliability."
                )
            }
        }
    }

    fun createAgent(name: String, role: String, instructions: String) {
        val created = agentManager.createAgent(name, role, instructions)
        _uiState.update { it.copy(activeAgents = agentManager.listAgents()) }
        sendMessage("Agent '${created.name}' created successfully.")
    }

    fun sendMessage(text: String) {
        if (text.isBlank()) return

        val safetyCheck = AgentSafety.validateUserRequest(text)
        if (safetyCheck != "ok") {
            _uiState.update {
                it.copy(
                    messages = it.messages + ChatMessage("assistant", safetyCheck),
                    isBusy = false
                )
            }
            return
        }

        handleCommand(text)

        val newMessages = uiState.value.messages + ChatMessage("user", text)
        _uiState.update {
            it.copy(
                messages = newMessages,
                isBusy = true
            )
        }

        viewModelScope.launch {
            val promptMessages = listOf(
                ProxyAiClient.MessageDto(
                    role = "system",
                    content = "You are the Master AI Agent. Keep responses concise, helpful, and safe."
                ),
                ProxyAiClient.MessageDto(
                    role = "user",
                    content = "Active agents: ${agentManager.listAgents().joinToString { it.name }}. User request: $text"
                )
            )

            val reply = try {
                val response = proxyClient.sendPrompt(promptMessages)
                response
            } catch (e: Exception) {
                "Secure proxy request failed: ${e.message}"
            }

            val finalMessages = uiState.value.messages + ChatMessage("assistant", reply)
            _uiState.update {
                it.copy(
                    messages = finalMessages,
                    isBusy = false,
                    activeAgents = agentManager.listAgents()
                )
            }

            conversationStore.saveHistory(finalMessages)

            val token = tokenStore.getToken()
            if (token != null) {
                // token is available for future secure requests if needed
            }
        }
    }
}
