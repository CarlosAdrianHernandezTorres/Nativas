package com.ipn.practica2.data.model

data class User(
    val id: String? = null,
    val name: String,
    val email: String,
    val password: String,
    val role: String = "user", // "admin" o "user"
    val profileImageUrl: String? = null,
    val token: String? = null
)