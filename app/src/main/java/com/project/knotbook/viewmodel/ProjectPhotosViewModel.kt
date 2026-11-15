package com.project.knotbook.viewmodel

import android.content.Context
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.project.knotbook.model.ProjectPhotoResponse
import com.project.knotbook.repository.ProjectRepository
import com.project.knotbook.utils.createPhotoPart
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import jakarta.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody

@HiltViewModel
class ProjectPhotosViewModel @Inject constructor(
    private val projectRepository: ProjectRepository,
    @ApplicationContext private val context: Context
) : ViewModel() {

    private val _uiState =
        MutableStateFlow<PhotosUiState>(PhotosUiState.Loading)

    val uiState = _uiState.asStateFlow()

    fun loadPhotos(projectId: Long) {
        viewModelScope.launch {
            _uiState.value = PhotosUiState.Loading

            try {
                val photos = projectRepository.getProjectPhotos(projectId)
                _uiState.value = PhotosUiState.Success(photos)
            } catch (e: Exception) {
                _uiState.value = PhotosUiState.Error(
                    e.message ?: "Unable to load photos"
                )
            }
        }
    }

    fun uploadPhoto(
        projectId: Long,
        uri: Uri,
        caption: String?
    ) {
        viewModelScope.launch {
            _uiState.value = PhotosUiState.Uploading

            try {
                val filePart = createPhotoPart(context, uri)

                val captionPart = caption
                    ?.takeIf { it.isNotBlank() }
                    ?.toRequestBody("text/plain".toMediaTypeOrNull())

                projectRepository.uploadPhoto(
                    projectId = projectId,
                    file = filePart,
                    caption = captionPart
                )

                loadPhotos(projectId)

            } catch (e: Exception) {
                _uiState.value = PhotosUiState.Error(
                    e.message ?: "Unable to upload photo"
                )
            }
        }
    }

    fun deletePhoto(projectId: Long, photoId: Long) {
        viewModelScope.launch {
            try {
                projectRepository.deletePhoto(projectId, photoId)
                loadPhotos(projectId)
            } catch (e: Exception) {
                _uiState.value = PhotosUiState.Error(
                    e.message ?: "Unable to delete photo"
                )
            }
        }
    }

    fun setCoverPhoto(projectId: Long, photoId: Long) {
        viewModelScope.launch {
            try {
                projectRepository.setCoverPhoto(projectId, photoId)
                loadPhotos(projectId)
            } catch (e: Exception) {
                _uiState.value = PhotosUiState.Error(
                    e.message ?: "Unable to update cover photo"
                )
            }
        }
    }
}

sealed interface PhotosUiState {
    data object Loading : PhotosUiState
    data object Uploading : PhotosUiState

    data class Success(
        val photos: List<ProjectPhotoResponse>
    ) : PhotosUiState

    data class Error(
        val message: String
    ) : PhotosUiState
}