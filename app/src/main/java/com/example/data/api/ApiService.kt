package com.example.data.api

import com.example.data.api.request.RagRequest
import com.example.data.api.request.SuggestQuestionRequest
import com.example.data.api.request.UploadFileRequest
import com.example.data.api.response.RagResponse
import com.example.data.api.response.SuggestionResponse
import com.example.data.api.response.UploadFileResponse
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.HeaderMap
import retrofit2.http.POST

interface ApiService {
    @POST("pdf-upload-file")
    suspend fun upLoadFile(
        @HeaderMap header: HashMap<String, String>,
        @Body body: UploadFileRequest
    ): UploadFileResponse

    @POST("pdf-get-question") // https://n8n.viettelmedia.vn/webhook/pdf-get-question
    suspend fun getSuggestQuestion(
        @HeaderMap header: HashMap<String, String>,
        @Body body: SuggestQuestionRequest
    ):SuggestionResponse

    @POST("pdf-rag")
    suspend fun getAnswerRag(
        @HeaderMap header: HashMap<String, String>,
        @Body body: RagRequest
    ):RagResponse
}
