package com.example.masterai

class MasterCoordinator(
    private val router: AgentRouter
) {
    fun handle(request: String): AgentTask {
        val task = AgentTask(userRequest = request, role = AgentRole.MASTER)
        return task.copy(role = router.route(task))
    }
}
