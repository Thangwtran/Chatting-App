package com.example.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.data.ChatRepository
import com.example.data.model.Message

class ChatViewModel(
    private val chatRepository: ChatRepository
) : ViewModel() {
    private val _messages = MutableLiveData<List<Message>>()
    val messages: LiveData<List<Message>> = _messages

    fun sendMessage(message: String) {
        val newMessage = Message(text = message, isUser = true)
        _messages.value = _messages.value?.plus(newMessage)
    }

    fun fakeData(){
        _messages.value = chatRepository.fakeDataMessages()
    }

    inner class Factory(
        private val chatRepository: ChatRepository
    ): ViewModelProvider.Factory{
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return ChatViewModel(chatRepository) as T
        }
    }
}
