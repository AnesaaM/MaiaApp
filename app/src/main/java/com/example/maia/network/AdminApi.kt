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
    @GET("api/users")
    suspend fun getAllUsers(): List<User>

    @GET("api/users/customers")
    suspend fun getCustomers(): List<User>

    @GET("api/users/roles")
    suspend fun getRoles(): List<Role>

    @POST("api/users/staff")
    suspend fun createStaff(@Body request: CreateStaffRequest)

    @PUT("api/users/{id}")
    suspend fun updateUser(@Path("id") id: Int, @Body request: UpdateUserRequest)

    @DELETE("api/users/{id}")
    suspend fun deleteUser(@Path("id") id: Int)

    @PUT("api/users/role")
    suspend fun changeRole(@Body request: ChangeRoleRequest)

    @PUT("api/users/{id}/status")
    suspend fun setUserStatus(@Path("id") id: Int, @Body isActive: Boolean)
}
