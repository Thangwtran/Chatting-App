package com.example.data.api.request

import com.google.gson.annotations.SerializedName
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class SuggestQuestionRequest (
    val language: String,
    val prompt: String,
    @SerializedName("url_file")
    val urlFile: String
)