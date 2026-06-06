package com.example.maia.network

import com.example.maia.model.notification.NotificationItem
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Path

interface NotificationApi {
    @GET("gateway/notifications/notifications")
    suspend fun getMyNotifications(): List<NotificationItem>

    @GET("gateway/notifications/notifications/all")
    suspend fun getAllNotifications(): List<NotificationItem>

    @PATCH("gateway/notifications/notifications/{id}/read")
    suspend fun markAsRead(@Path("id") id: Int)

    @POST("gateway/notifications/notifications/send")
    suspend fun sendNotification(@Body request: Map<String, String>)
}
