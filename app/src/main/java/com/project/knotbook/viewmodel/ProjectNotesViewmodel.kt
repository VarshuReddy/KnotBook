package com.project.knotbook.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.project.knotbook.model.ProjectNoteRequest
import com.project.knotbook.model.ProjectNoteResponse
import com.project.knotbook.repository.ProjectRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

@HiltViewModel
class ProjectNotesViewModel @Inject constructor(
    private val repository: ProjectRepository
) : ViewModel() {

    private val _uiState =
        MutableStateFlow<NotesUiState>(NotesUiState.Loading)

    val uiState = _uiState.asStateFlow()

    fun loadNotes(projectId: Long) {
        viewModelScope.launch {
            _uiState.value = NotesUiState.Loading

            try {
                val note = repository.getProjectNotes(projectId)
                _uiState.value = NotesUiState.Success(note)
            } catch (e: Exception) {
                _uiState.value = NotesUiState.Error(
                    e.message ?: "Unable to load notes"
                )
            }
        }
    }

    fun saveNotes(projectId: Long, content: String) {
        viewModelScope.launch {
            _uiState.value = NotesUiState.Saving

            try {
                val note = repository.saveProjectNotes(
                    projectId,
                    ProjectNoteRequest(content)
                )

                _uiState.value = NotesUiState.Success(note)
            } catch (e: Exception) {
                _uiState.value = NotesUiState.Error(
                    e.message ?: "Unable to save notes"
                )
            }
        }
    }
}

sealed interface NotesUiState {
    data object Loading : NotesUiState
    data object Saving : NotesUiState
    data class Success(val note: ProjectNoteResponse?) : NotesUiState
    data class Error(val message: String) : NotesUiState
}