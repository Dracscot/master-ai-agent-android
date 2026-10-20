package com.example.masterai

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.POST
import java.util.concurrent.TimeUnit

class ProxyAiClient(baseUrl: String) {
    private val service: ProxyApi by lazy {
        val logging = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BASIC
        }

        val client = OkHttpClient.Builder()
            .addInterceptor(logging)
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(60, TimeUnit.SECONDS)
            .build()

        Retrofit.Builder()
            .baseUrl(baseUrl.trimEnd('/') + "/")
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ProxyApi::class.java)
    }

    suspend fun sendPrompt(messages: List<MessageDto>): String {
        val request = ChatRequest(messages = messages, temperature = 0.3, max_tokens = 400)
        val response = service.sendChat(request)
        return response.choices.firstOrNull()?.message?.content
            ?: "No response generated."
    }

    interface ProxyApi {
        @POST("api/chat")
        suspend fun sendChat(@Body request: ChatRequest): ChatResponse
    }

    data class ChatRequest(
        val messages: List<MessageDto>,
        val temperature: Double,
        val max_tokens: Int
    )

    data class MessageDto(
        val role: String,
        val content: String
    )

    data class ChatResponse(
        val choices: List<Choice>
    ) {
        data class Choice(
            val message: Message
        )

        data class Message(
            val content: String
        )
    }
}
