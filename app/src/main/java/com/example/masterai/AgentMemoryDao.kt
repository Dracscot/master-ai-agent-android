package com.example.masterai

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "agent_memory")
data class AgentMemoryEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val agentName: String,
    val role: String,
    val content: String,
    val timestamp: Long = System.currentTimeMillis()
)
