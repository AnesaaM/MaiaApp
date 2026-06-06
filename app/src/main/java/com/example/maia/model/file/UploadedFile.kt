package com.example.maia.model.file

data class UploadedFile(
    val id: Int,
    val fileName: String,
    val url: String,
    val contentType: String = "",
    val size: Long = 0
)
