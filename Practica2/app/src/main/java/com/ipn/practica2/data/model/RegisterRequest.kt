package com.ipn.practica2.data.model

data class RegisterRequest(
    val name: String,
    val email: String,
    val password: String,
    val role: String = "user"
)