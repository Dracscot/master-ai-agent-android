package com.example.masterai

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.chatDataStore: DataStore<Preferences> by preferencesDataStore(name = "chat_history")

class ConversationStore(private val context: Context) {
    private val gson = Gson()
    private val key = stringPreferencesKey("chat_history_json")

    val historyFlow: Flow<List<ChatMessage>> = context.chatDataStore.data.map { prefs ->
        val json = prefs[key] ?: "[]"
        if (json.isBlank()) return@map emptyList()
        val type = object : TypeToken<List<ChatMessage>>() {}.type
        gson.fromJson(json, type) ?: emptyList()
    }

    suspend fun saveHistory(messages: List<ChatMessage>) {
        context.chatDataStore.edit { prefs ->
            prefs[key] = gson.toJson(messages)
        }
    }
}
