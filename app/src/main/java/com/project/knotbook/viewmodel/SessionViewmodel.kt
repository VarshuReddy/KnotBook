package com.project.knotbook.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.project.knotbook.repository.AuthRepository
import com.project.knotbook.ui.navigation.Screen
import com.project.knotbook.utils.TokenDataStore
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

@HiltViewModel
class SessionViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val tokenDataStore: TokenDataStore
) : ViewModel() {

    private val _startDestination =
        MutableStateFlow<String?>(null)

    val startDestination = _startDestination.asStateFlow()

    init {
        checkSession()
    }

    private fun checkSession() {
        viewModelScope.launch {
            val refreshToken = tokenDataStore.refreshToken.first()

            if (refreshToken.isNullOrBlank()) {
                tokenDataStore.clearTokens()
                _startDestination.value = Screen.Login.route
                return@launch
            }
            val isSessionValid = authRepository.refreshSession()

            _startDestination.value =
                if (isSessionValid) {
                    Screen.Home.route
                } else {
                    Screen.Login.route
                }

        }
    }
}