package com.project.knotbook.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.project.knotbook.model.CreateProjectRequest
import com.project.knotbook.repository.ProjectRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed interface AddProjectUiState {
    data object Idle : AddProjectUiState
    data object Loading : AddProjectUiState
    data class Success(val projectId: Long) : AddProjectUiState
    data class Error(val message: String) : AddProjectUiState
}

@HiltViewModel
class AddProjectViewModel @Inject constructor(
    private val repository: ProjectRepository
) : ViewModel() {

    private val _uiState =
        MutableStateFlow<AddProjectUiState>(AddProjectUiState.Idle)

    val uiState = _uiState.asStateFlow()

    fun createProject(request: CreateProjectRequest) {
        viewModelScope.launch {
            _uiState.value = AddProjectUiState.Loading

            try {
                val response = repository.createProject(request)
                _uiState.value = AddProjectUiState.Success(response.id)
            } catch (e: Exception) {
                _uiState.value = AddProjectUiState.Error(
                    e.message ?: "Unable to create project"
                )
            }
        }
    }

    fun resetState() {
        _uiState.value = AddProjectUiState.Idle
    }
}