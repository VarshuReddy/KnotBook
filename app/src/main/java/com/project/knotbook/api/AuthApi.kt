package com.project.knotbook.api

import com.project.knotbook.model.AuthResponse
import com.project.knotbook.model.LoginRequest
import com.project.knotbook.model.RegisterRequest
import com.project.knotbook.model.RegisterResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface AuthApi {

    @POST("api/auth/register")
    suspend fun register(
        @Body request: RegisterRequest
    ): Response<RegisterResponse>

    @POST("api/auth/login")
    suspend fun login(
        @Body request: LoginRequest
    ): Response<AuthResponse>
}