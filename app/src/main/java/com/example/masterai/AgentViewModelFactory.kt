package com.example.masterai

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.crashlytics.ktx.crashlytics
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

private const val TAG = "MasterAiAgent"

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
    private val appContext: Context
) : ViewModel() {
    private val _uiState = MutableStateFlow(AgentUiState())
    val uiState: StateFlow<AgentUiState> = _uiState.asStateFlow()

    private val router = AgentRouter()
    private val coordinator = MasterCoordinator(router)
    private val toolGate = ToolGate()
    private val agentManager = AgentManager()
    private val db = AppDatabase.getDatabase(appContext)
    private val memoryRepository = MemoryRepository(db.agentMemoryDao())
    private val proxyClient = ProxyAiClient("http://10.0.2.2:3000")
    private val analytics: FirebaseAnalytics = Firebase.analytics

    init {
        toolGate.grant(ToolPermission.SYSTEM_STATUS)
        toolGate.grant(ToolPermission.CREATE_AGENT)
        toolGate.grant(ToolPermission.INTERNET)
    }

    fun createAgent(name: String, role: String, instructions: String) {
        val hasPermission = toolGate.require(ToolPermission.CREATE_AGENT)
        if (!hasPermission) {
            _uiState.update {
                it.copy(
                    messages = it.messages + ChatMessage("assistant", "Permission denied: create agent requires explicit consent.")
                )
            }
            return
        }

        val created = agentManager.createAgent(name, role, instructions)
        _uiState.update { it.copy(activeAgents = agentManager.listAgents()) }
        persistMemory(created.name, created.role, "Agent created: ${created.name}")
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

        val task = coordinator.handle(text)
        val assignedRole = task.role

        val newMessages = uiState.value.messages + ChatMessage("user", text)
        _uiState.update {
            it.copy(
                messages = newMessages,
                isBusy = true
            )
        }

        analytics.logEvent("agent_task_routed", android.os.Bundle().apply {
            putString("role", assignedRole.name)
        })

        persistMemory("master", assignedRole.name, text)

        viewModelScope.launch {
            val promptMessages = listOf(
                ProxyAiClient.MessageDto(
                    role = "system",
                    content = "You are the Master AI Agent routing to ${assignedRole.name}. Keep output concise, safe, and useful."
                ),
                ProxyAiClient.MessageDto(
                    role = "user",
                    content = text
                )
            )

            val reply = try {
                proxyClient.sendPrompt(promptMessages)
            } catch (e: Exception) {
                Firebase.crashlytics.recordException(e)
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

            persistMemory("master", assignedRole.name, reply)
        }
    }

    private fun persistMemory(agentName: String, role: String, content: String) {
        viewModelScope.launch {
            memoryRepository.save(agentName, role, content)
        }
    }
}
