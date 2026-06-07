package com.example.maia.network

import com.example.maia.model.admin.ChangeRoleRequest
import com.example.maia.model.admin.CreateStaffRequest
import com.example.maia.model.admin.Role
import com.example.maia.model.admin.UpdateUserRequest
import com.example.maia.model.admin.User
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface AdminApi {
    @GET("gateway/auth/users")
    suspend fun getAllUsers(): List<User>

    @GET("gateway/auth/users/customers")
    suspend fun getCustomers(): List<User>

    @GET("gateway/auth/users/roles")
    suspend fun getRoles(): List<Role>

    @POST("gateway/auth/users/staff")
    suspend fun createStaff(@Body request: CreateStaffRequest)

    @PUT("gateway/auth/users/{id}")
    suspend fun updateUser(@Path("id") id: Int, @Body request: UpdateUserRequest)

    @DELETE("gateway/auth/users/{id}")
    suspend fun deleteUser(@Path("id") id: Int)

    @PUT("gateway/auth/users/role")
    suspend fun changeRole(@Body request: ChangeRoleRequest)

    @PUT("gateway/auth/users/{id}/status")
    suspend fun setUserStatus(@Path("id") id: Int, @Body isActive: Boolean)
}
