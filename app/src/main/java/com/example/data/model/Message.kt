package com.example.data.model

data class Message(
    val id: Long = System.currentTimeMillis(),
    var text: String,
    val isUser: Boolean,
    val timestamp: Long = System.currentTimeMillis(),
)