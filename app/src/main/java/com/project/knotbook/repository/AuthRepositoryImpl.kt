package com.project.knotbook.repository

import com.project.knotbook.api.AuthApi
import com.project.knotbook.model.AuthResponse
import com.project.knotbook.model.LoginRequest
import com.project.knotbook.model.RegisterRequest
import com.project.knotbook.model.RegisterResponse
import com.project.knotbook.utils.TokenDataStore
import com.project.knotbook.ui.state.UiState
import jakarta.inject.Inject

class AuthRepositoryImpl @Inject constructor(
    private val authApi: AuthApi,
    private val tokenDataStore: TokenDataStore
) : AuthRepository{
    override suspend fun register(request: RegisterRequest): UiState<RegisterResponse> {
        return try{
            val response = authApi.register(request)
            if(response.isSuccessful){
                response.body()?.let {
                    UiState.Success(it)
                } ?: UiState.Error("Empty response from server")
            }else{
                UiState.Error(
                    when (response.code()) {
                        409 -> "An account with this email already exists."
                        400 -> "Please check the details you entered."
                        else -> "Registration failed. Please try again."
                    }
                )
            }
        }catch (e: Exception)
        {
            UiState.Error(
                e.localizedMessage ?: "Unable to connect to server."
            )
        }
    }

    override suspend fun login(request: LoginRequest): UiState<AuthResponse> {
        return try {
            val response = authApi.login(request)

            if (response.isSuccessful) {
                val authResponse = response.body()
                    ?: return UiState.Error("Empty response from server")

                // Save tokens only after a successful login response.
                tokenDataStore.saveTokens(
                    accessToken = authResponse.accessToken,
                    refreshToken = authResponse.refreshToken
                )

                UiState.Success(authResponse)
            } else {
                UiState.Error(
                    when (response.code()) {
                        401 -> "Invalid email or password."
                        400 -> "Please check the details you entered."
                        else -> "Login failed. Please try again."
                    }
                )
            }
        } catch (exception: Exception) {
            UiState.Error(
                exception.localizedMessage ?: "Unable to connect to server."
            )
        }
    }

}