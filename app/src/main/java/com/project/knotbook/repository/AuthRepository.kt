package com.project.knotbook.repository

import com.project.knotbook.model.AuthResponse
import com.project.knotbook.model.LoginRequest
import com.project.knotbook.model.RegisterRequest
import com.project.knotbook.model.RegisterResponse
import com.project.knotbook.ui.state.UiState

interface AuthRepository {
    suspend fun register(
        request: RegisterRequest
    ): UiState<RegisterResponse>

    suspend fun login(
        request: LoginRequest
    ): UiState<AuthResponse>
}