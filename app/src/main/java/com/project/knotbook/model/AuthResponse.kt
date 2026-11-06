package com.project.knotbook.model

data class AuthResponse(
    val userId: Long,
    val name: String,
    val email: String,
    val accessToken: String,
    val refreshToken: String,
    val tokenType: String,
    val expiresIn: Long
)
data class RegisterResponse(
    val userId: Long,
    val name: String,
    val email: String,
    val createdAt: String,
    val message: String
)