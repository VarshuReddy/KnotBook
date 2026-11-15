package com.project.knotbook.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.project.knotbook.model.AddTimeRequest
import com.project.knotbook.repository.ProjectRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

@HiltViewModel
class AddTimeViewModel @Inject constructor(
    private val projectRepository: ProjectRepository
) : ViewModel() {

    private val _uiState =
        MutableStateFlow<AddTimeUiState>(AddTimeUiState.Idle)

    val uiState = _uiState.asStateFlow()

    fun addTime(
        projectId: Long,
        hours: Int,
        minutes: Int
    ) {
        if (hours < 0 || minutes !in 0..59) {
            _uiState.value = AddTimeUiState.Error("Enter valid time")
            return
        }

        val totalMinutes = hours * 60L + minutes

        if (totalMinutes <= 0) {
            _uiState.value =
                AddTimeUiState.Error("Please enter some time")
            return
        }

        viewModelScope.launch {
            _uiState.value = AddTimeUiState.Loading

            try {
                projectRepository.addProjectTime(
                    projectId,
                    AddTimeRequest(totalMinutes)
                )

                _uiState.value = AddTimeUiState.Success

            } catch (e: Exception) {
                _uiState.value = AddTimeUiState.Error(
                    e.message ?: "Unable to save crafting time"
                )
            }
        }
    }
}

sealed interface AddTimeUiState {
    data object Idle : AddTimeUiState
    data object Loading : AddTimeUiState
    data object Success : AddTimeUiState
    data class Error(val message: String) : AddTimeUiState
}