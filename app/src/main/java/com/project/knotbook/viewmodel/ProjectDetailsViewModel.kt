package com.project.knotbook.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.project.knotbook.model.ProjectDetailResponse
import com.project.knotbook.repository.ProjectRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch


@HiltViewModel
class ProjectDetailsViewModel @Inject constructor(
    private val projectRepository: ProjectRepository
) : ViewModel() {

    private val _uiState =
        MutableStateFlow<ProjectDetailsUiState>(ProjectDetailsUiState.Loading)

    val uiState: StateFlow<ProjectDetailsUiState> = _uiState.asStateFlow()

    private val _deleteState =
        MutableStateFlow<DeleteProjectState>(DeleteProjectState.Idle)

    val deleteState: StateFlow<DeleteProjectState> = _deleteState.asStateFlow()

    fun loadProject(projectId: Long) {
        viewModelScope.launch {
            _uiState.value = ProjectDetailsUiState.Loading

            try {
                val project = projectRepository.getProject(projectId)
                _uiState.value = ProjectDetailsUiState.Success(project)
            } catch (e: Exception) {
                _uiState.value = ProjectDetailsUiState.Error(
                    e.message ?: "Unable to load project"
                )
            }
        }
    }

    fun deleteProject(projectId: Long) {
        viewModelScope.launch {
            _deleteState.value = DeleteProjectState.Deleting

            projectRepository.deleteProject(projectId)
                .onSuccess {
                    _deleteState.value = DeleteProjectState.Success
                }
                .onFailure { exception ->
                    _deleteState.value = DeleteProjectState.Error(
                        exception.message ?: "Unable to delete project"
                    )
                }
        }
    }

    fun resetDeleteState() {
        _deleteState.value = DeleteProjectState.Idle
    }
}

sealed interface DeleteProjectState {
    data object Idle : DeleteProjectState
    data object Deleting : DeleteProjectState
    data object Success : DeleteProjectState
    data class Error(val message: String) : DeleteProjectState
}

sealed interface ProjectDetailsUiState {
    data object Loading : ProjectDetailsUiState

    data class Success(
        val project: ProjectDetailResponse
    ) : ProjectDetailsUiState

    data class Error(
        val message: String
    ) : ProjectDetailsUiState
}