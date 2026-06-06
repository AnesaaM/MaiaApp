package com.example.maia.network

import com.example.maia.model.file.UploadedFile
import okhttp3.MultipartBody
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Path

interface FileUploadApi {
    @GET("gateway/files/files")
    suspend fun getFiles(): List<UploadedFile>

    @Multipart
    @POST("gateway/files/files/upload")
    suspend fun uploadFile(@Part file: MultipartBody.Part): UploadedFile

    @DELETE("gateway/files/files/{id}")
    suspend fun deleteFile(@Path("id") id: Int)
}
