package com.example.masterai

class ToolGate {
    private val granted = mutableSetOf<ToolPermission>()

    fun grant(permission: ToolPermission) {
        granted.add(permission)
    }

    fun revoke(permission: ToolPermission) {
        granted.remove(permission)
    }

    fun canUse(permission: ToolPermission): Boolean = permission in granted

    fun require(permission: ToolPermission): Boolean = canUse(permission)
}
