package com.example.ui.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.data.ChatRepository
import com.example.data.api.request.RagRequest
import com.example.data.api.request.SuggestQuestionRequest
import com.example.data.api.request.UploadFileRequest
import com.example.data.api.response.SuggestionResponse
import com.example.data.model.Message
import com.example.utils.Resource
import com.example.utils.Utils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File

class ChatViewModel(
    private val chatRepository: ChatRepository
) : ViewModel() {
    private val _messages = MutableLiveData<List<Message>>(emptyList())
    val messages: LiveData<List<Message>> = _messages

    private var listMessages = mutableListOf<Message>()

    private var _botResponse = MutableLiveData<Message?>()
    var botResponse: LiveData<Message?> = _botResponse

    private val _fileResponse = MutableLiveData<String>() // file string
    val fileResponse: LiveData<String> = _fileResponse

    private val _suggestedQuestion = MutableLiveData<SuggestionResponse>()
    val suggestedQuestion: LiveData<SuggestionResponse> = _suggestedQuestion

    private val _isError = MutableLiveData<String>()
    val isError: LiveData<String> = _isError

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    init {
        _isLoading.value = false
//        fakeData()
    }

    var token = ""

    init {
        token = Utils.createJwt()
    }

    val header = hashMapOf(
        "Authorization" to "Bearer $token"
    )
    val coroutineDispatcher = Dispatchers.IO

    fun uploadFile(file: File) {
        val requestBody = UploadFileRequest(
            file = file
        )
        _isLoading.postValue(true)

        viewModelScope.launch(coroutineDispatcher) {
            val response = chatRepository.uploadFile(header, requestBody)
            _isLoading.postValue(false)
            when (response) {
                is Resource.Error<*> -> {
                    _isError.postValue(response.message ?: "An unknown error occurred")
                }

                is Resource.Success<*> -> {
                    _fileResponse.postValue(response.data?.url)
                }
            }
        }
    }

    fun getSuggestQuestion(
        language: String,
        prompt: String,
        urlFile: String
    ) {
        val requestBody = SuggestQuestionRequest(
            language = language,
            prompt = prompt,
            urlFile = urlFile
        )
//        _isLoading.postValue(true)
        viewModelScope.launch(coroutineDispatcher) {
            val response = chatRepository.getSuggestQuestion(header, requestBody)
//            _isLoading.postValue(false)
            Log.d("ViewModel", "getSuggestQuestion: ${response.data}")
            when (response) {
                is Resource.Error<*> -> _isError.postValue(
                    response.message ?: "An unknown error occurred"
                )

                is Resource.Success<*> -> _suggestedQuestion.postValue(
                    response.data ?: SuggestionResponse(0, "huhu", "", "")
                )
            }
        }
    }

    fun sendPromptToBot(
        language: String,
        prompt: String,
        urlFile: String
    ) {
        val userMessage = Message(text = prompt, isUser = true)
        listMessages.add(userMessage)
        _messages.postValue(listMessages)
        _isLoading.postValue(true)
        val requestBody = RagRequest(
            language = language,
            chatInput = prompt,
            urlFile = urlFile
        )
        viewModelScope.launch(coroutineDispatcher) {
            val response = chatRepository.getAnswerRag(header, requestBody)
            Log.d("ViewModel", "sendPromptToBot: $response")
            when (response) {
                is Resource.Error<*> -> {
                    _isError.postValue(
                        response.message ?: "An unknown error occurred"
                    )
                    val errorResponse = Message(text = response.message ?: "An unknown error occurred", isUser = false)
                    listMessages.add(errorResponse)
                    _messages.postValue(listMessages)
                    _isLoading.postValue(false)
                }

                is Resource.Success<*> -> {
                    Log.d("ViewModel", "sendPromptToBot: ${response.data}")
                    val responseMsg = response.data?.msg ?: "..."
                    val botResponse = Message(text = responseMsg, isUser = false)
                    _botResponse.postValue(botResponse)
                    listMessages.add(botResponse)
                    _messages.postValue(listMessages)
                    _isLoading.postValue(false)
                }
            }
        }
    }

    fun fakeData() {
        val fakeMessages = chatRepository.fakeDataMessages()
        listMessages.addAll(fakeMessages)
        _messages.value = listMessages
    }

    inner class Factory(
        private val chatRepository: ChatRepository
    ) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return ChatViewModel(chatRepository) as T
        }
    }
}
