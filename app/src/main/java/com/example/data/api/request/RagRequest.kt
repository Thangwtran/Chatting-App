package com.example.data.api.request

import com.google.gson.annotations.SerializedName
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class RagRequest(
    val language: String,
    @SerializedName("chat_input")
    val chatInput: String,
    @SerializedName("url_file")
    val urlFile: String
)