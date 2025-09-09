package com.example.data

import com.example.data.api.ApiService
import com.example.data.api.request.RagRequest
import com.example.data.api.request.SuggestQuestionRequest
import com.example.data.api.request.UploadFileRequest
import com.example.data.api.response.RagResponse
import com.example.data.api.response.SuggestionResponse
import com.example.data.api.response.UploadFileResponse
import com.example.data.model.Message
import com.example.utils.Resource

class ChatRepository(
    private val chatApi: ApiService
) {
    fun fakeDataMessages(): List<Message> {
        return listOf(
            Message(
                text = "Hello! How can I help you today?",
                isUser = false
            ),
            Message(
                text = "Hi, can you tell me about your features?",
                isUser = true
            ),
            Message(
                text = "Sure! I can answer questions, generate ideas, and help you with code.",
                isUser = false
            ),
            Message(
                text = "Can you also chat like a human?",
                isUser = true
            ),
            Message(
                text = "Yes 😊 I can hold conversations naturally.",
                isUser = false
            ),
            Message(
                text = "Great! Can you give me a joke?",
                isUser = true
            ),
            Message(
                text = "Why don’t programmers like nature? It has too many bugs 🐞",
                isUser = false
            ),
            Message(
                text = "😂 That’s funny.",
                isUser = true
            ),
            Message(
                text = "Do you know today’s weather?",
                isUser = true
            ),
            Message(
                text = "I can check it for you if connected to an API ☁️",
                isUser = false
            ),
            Message(
                text = "Typing...",
                isUser = false,
                isTyping = true
            )
        )
    }

    suspend fun uploadFile(
        headers: HashMap<String, String>,
        body: UploadFileRequest
    ): Resource<UploadFileResponse> {
        val response = try {
            chatApi.upLoadFile(headers, body)
        } catch (e: Exception) {
            return Resource.Error(message = e.message ?: "An unknown error occurred")
        }
        return Resource.Success(response)
    }

    suspend fun getSuggestQuestion(
        headers: HashMap<String, String>,
        body: SuggestQuestionRequest
    ): Resource<SuggestionResponse> {
        val response = try {
            chatApi.getSuggestQuestion(headers, body)
        } catch (e: Exception) {
            return Resource.Error(message = e.message ?: "An unknown error occurred")
        }
        return Resource.Success(response)
    }

    suspend fun getAnswerRag(
        headers: HashMap<String, String>,
        body: RagRequest
    ): Resource<RagResponse> {
        val response = try {
            chatApi.getAnswerRag(headers, body)
        } catch (e: Exception) {
            return Resource.Error(message = e.message ?: "An unknown error occurred")
        }
        return Resource.Success(response)
    }

}

