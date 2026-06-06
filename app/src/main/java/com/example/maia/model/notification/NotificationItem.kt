package com.example.maia.model.notification

data class NotificationItem(
    val id: Int,
    val title: String,
    val message: String,
    val isRead: Boolean = false,
    val createdAt: String = ""
)
