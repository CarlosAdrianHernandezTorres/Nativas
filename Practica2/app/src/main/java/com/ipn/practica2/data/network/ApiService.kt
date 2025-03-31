package com.ipn.practica2.data.network

import com.ipn.practica2.data.model.RegisterRequest
import com.ipn.practica2.data.model.User
import retrofit2.http.*
import retrofit2.Response


interface ApiService {

    @POST("register")
    suspend fun registerUser(@Body user: RegisterRequest): Response<User>

    @POST("login")
    suspend fun loginUser(@Body user: User): Response<User>

    @GET("users")
    suspend fun getAllUsers(): Response<List<User>>

    @GET("users/{id}")
    suspend fun getUserById(@Path("id") id: String): Response<User>

    @PUT("users/{id}")
    suspend fun updateUser(@Path("id") id: String, @Body user: User): Response<User>

    @DELETE("users/{id}")
    suspend fun deleteUser(@Path("id") id: String): Response<Unit>
}