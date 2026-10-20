package com.example.masterai

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.Headers
import retrofit2.http.POST
import java.util.concurrent.TimeUnit

interface LLMProvider {
    suspend fun complete(prompt: String): String
}

class OpenAICompatibleClient(
    private val baseUrl: String,
    private val apiKey: String,
    private val model: String = "gpt-4o-mini"
) : LLMProvider {

    private val service: OpenAiApi by lazy {
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
            .create(OpenAiApi::class.java)
    }

    override suspend fun complete(prompt: String): String {
        val request = ChatCompletionRequest(
            model = model,
            messages = listOf(
                ChatMessage("system", "You are a helpful Master AI Agent for Android, concise and safe."),
                ChatMessage("user", prompt)
            ),
            temperature = 0.3
        )

        val response = service.chatCompletion(
            request = request,
            authToken = "Bearer $apiKey"
        )

        return response.choices.firstOrNull()?.message?.content
            ?: "No response generated."
    }

    interface OpenAiApi {
        @POST("chat/completions")
        @Headers("Content-Type: application/json")
        suspend fun chatCompletion(
            @Body request: ChatCompletionRequest,
            @Header("Authorization") authToken: String
        ): ChatCompletionResponse
    }

    data class ChatCompletionRequest(
        val model: String,
        val messages: List<ChatMessage>,
        val temperature: Double
    )

    data class ChatMessage(
        val role: String,
        val content: String
    )

    data class ChatCompletionResponse(
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
