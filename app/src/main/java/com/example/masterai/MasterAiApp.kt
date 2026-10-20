package com.example.masterai

class MemoryRepository(private val dao: AgentMemoryDao) {
    suspend fun save(agentName: String, role: String, content: String) {
        dao.insert(
            AgentMemoryEntity(
                agentName = agentName,
                role = role,
                content = content
            )
        )
    }

    suspend fun getRecent(): List<AgentMemoryEntity> = dao.getRecentMemory()
}
