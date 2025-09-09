package com.example.data.api.request

import com.google.gson.annotations.SerializedName
import java.io.File

import kotlinx.serialization.Serializable

@Serializable
data class UploadFileRequest(
    @SerializedName("file")
    val file: File
)