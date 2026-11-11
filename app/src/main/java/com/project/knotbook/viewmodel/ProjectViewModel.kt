package com.project.knotbook.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.project.knotbook.model.ProjectListItemResponse
import com.project.knotbook.repository.ProjectRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlin.time.Duration.Companion.milliseconds

sealed interface ProjectsUiState {
    data object Loading : ProjectsUiState

    data class Success(
        val projects: List<ProjectListItemResponse>
    ) : ProjectsUiState

    data class Error(val message: String) : ProjectsUiState
}

@HiltViewModel
class ProjectViewModel @Inject constructor(
    private val repository: ProjectRepository
) : ViewModel() {private val _uiState =
    MutableStateFlow<ProjectsUiState>(ProjectsUiState.Loading)
    val uiState = _uiState.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery = _searchQuery.asStateFlow()

    private val _selectedStatus = MutableStateFlow<String?>(null)
    val selectedStatus = _selectedStatus.asStateFlow()

    private var searchJob: Job? = null

    init {
        loadProjects()
    }

    fun onSearchChange(query: String) {
        _searchQuery.value = query
        searchJob?.cancel()
        searchJob = viewModelScope.launch {
            delay(350.milliseconds)
            loadProjects()
        }
    }

    fun onStatusChange(status: String?) {
        _selectedStatus.value = status
        loadProjects()
    }

    fun loadProjects() {
        viewModelScope.launch {
            _uiState.value = ProjectsUiState.Loading

            try {
                val response = repository.getProjects(
                    page = 0,
                    size = 50,
                    search = _searchQuery.value.trim().takeIf { it.isNotEmpty() },
                    status = _selectedStatus.value
                )

                _uiState.value = ProjectsUiState.Success(response.content)

            } catch (e: Exception) {
                _uiState.value = ProjectsUiState.Error(
                    e.message ?: "Unable to load projects"
                )
            }
        }
    }
}