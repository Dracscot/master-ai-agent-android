package com.example.masterai

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface AgentMemoryDao {
    @Insert
    suspend fun insert(memory: AgentMemoryEntity)

    @Query("SELECT * FROM agent_memory ORDER BY timestamp DESC LIMIT 100")
    suspend fun getRecentMemory(): List<AgentMemoryEntity>
}
