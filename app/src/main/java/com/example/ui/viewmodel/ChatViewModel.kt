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
    private val _messages = MutableLiveData<List<Message>>()
    val messages: LiveData<List<Message>> = _messages

    private val _fileResponse = MutableLiveData<String>() // file string
    val fileResponse: LiveData<String> = _fileResponse

    private val _suggestedQuestion = MutableLiveData<SuggestionResponse>()
    val suggestedQuestion: LiveData<SuggestionResponse> = _suggestedQuestion

    private val _answerRag = MutableLiveData<String>()
    val answerRag: LiveData<String> = _answerRag

    private val _isError = MutableLiveData<String>()
    val isError: LiveData<String> = _isError

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    var token = ""
    init {
        token = Utils.createJwt()
    }

    val header = hashMapOf(
        "Authorization" to "Bearer $token"
    )
    val coroutineDispatcher = Dispatchers.IO

    fun sendMessage(message: String) {
        val newMessage = Message(text = message, isUser = true)
        _messages.value = _messages.value?.plus(newMessage)
    }

    fun uploadFile(file: File) {
        val requestBody = UploadFileRequest(
            file = file
        )
//        _isLoading.value = true
        viewModelScope.launch(coroutineDispatcher) {
            val response = chatRepository.uploadFile(header, requestBody)
//            _isLoading.value = false
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
//        _isLoading.value = true
        viewModelScope.launch(coroutineDispatcher) {
            val response = chatRepository.getSuggestQuestion(header, requestBody)
//            _isLoading.value = false
            Log.d("ViewModel", "getSuggestQuestion: ${response.data}")
            when (response) {
                is Resource.Error<*> -> _isError.postValue(response.message ?: "An unknown error occurred")
                is Resource.Success<*> -> _suggestedQuestion.postValue(response.data ?: SuggestionResponse(0, "huhu", "", ""))
            }
        }
    }

    fun chatWithBot(
        language: String,
        prompt: String,
        urlFile: String
    ) {
        val requestBody = RagRequest(
            language = language,
            chatInput = prompt,
            urlFile = urlFile
        )
//        _isLoading.value = true
        viewModelScope.launch(coroutineDispatcher) {
            val response = chatRepository.getAnswerRag(header, requestBody)
//            _isLoading.value = false
            when (response) {
                is Resource.Error<*> -> _isError.postValue( response.message ?: "An unknown error occurred")
                is Resource.Success<*> -> _answerRag.postValue(response.data?.msg ?: "")
            }
        }
    }

    fun fakeData() {
        _messages.value = chatRepository.fakeDataMessages()
    }

    fun addMessage(message: Message) {
        _messages.value = _messages.value?.plus(message)
    }
    inner class Factory(
        private val chatRepository: ChatRepository
    ) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return ChatViewModel(chatRepository) as T
        }
    }
}
