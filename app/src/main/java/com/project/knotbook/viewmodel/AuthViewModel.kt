package com.project.knotbook.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.project.knotbook.model.LoginRequest
import com.project.knotbook.model.RegisterRequest
import com.project.knotbook.repository.AuthRepository
import com.project.knotbook.ui.state.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val authRepository: AuthRepository) : ViewModel(){

    private val _loginState =
        MutableStateFlow<UiState<Unit>>(UiState.Idle)
    val loginState: StateFlow<UiState<Unit>> =
        _loginState.asStateFlow()

    private val _registerState =
        MutableStateFlow<UiState<Unit>>(UiState.Idle)
    val registerState: StateFlow<UiState<Unit>> =
        _registerState.asStateFlow()

    fun login(email: String, password: String) {
        viewModelScope.launch {
            _loginState.value = UiState.Loading

            when (
                val result = authRepository.login(
                    LoginRequest(email.trim(), password)
                )
            ) {
                is UiState.Success -> {
                    _loginState.value = UiState.Success(Unit)
                }

                is UiState.Error -> {
                    _loginState.value = result
                }

                else -> Unit
            }
        }
    }

    fun register(name: String, email: String, password: String) {
        viewModelScope.launch {
            _registerState.value = UiState.Loading

            when (
                val result = authRepository.register(
                    RegisterRequest(
                        name.trim(),
                        email.trim(),
                        password
                    )
                )
            ) {
                is UiState.Success -> {
                    _registerState.value = UiState.Success(Unit)
                }

                is UiState.Error -> {
                    _registerState.value = result
                }

                else -> Unit
            }
        }
    }

    fun resetLoginState() {
        _loginState.value = UiState.Idle
    }

    fun resetRegisterState() {
        _registerState.value = UiState.Idle
    }

}