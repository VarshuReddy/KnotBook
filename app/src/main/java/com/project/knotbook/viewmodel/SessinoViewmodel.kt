package com.project.knotbook.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.project.knotbook.utils.TokenDataStore
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

@HiltViewModel
class SessionViewModel @Inject constructor(
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
            val accessToken = tokenDataStore.accessToken.first()
            val refreshToken = tokenDataStore.refreshToken.first()

            _startDestination.value =
                if (!accessToken.isNullOrBlank() &&
                    !refreshToken.isNullOrBlank()
                ) {
                    "home"
                } else {
                    "login"
                }
        }
    }
}