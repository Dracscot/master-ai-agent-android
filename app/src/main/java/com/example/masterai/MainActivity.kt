package com.example.masterai

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class AgentViewModelFactory(private val appContext: Context) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AgentViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return AgentViewModel(appContext) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
