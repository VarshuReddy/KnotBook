package com.project.knotbook.repository

import com.project.knotbook.model.AddTimeRequest
import com.project.knotbook.model.CreateProjectRequest
import com.project.knotbook.model.ProjectDetailResponse
import com.project.knotbook.model.ProjectListResponse
import com.project.knotbook.model.ProjectNoteRequest
import com.project.knotbook.model.ProjectNoteResponse
import com.project.knotbook.model.ProjectPhotoResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody

interface ProjectRepository {
    suspend fun getProjects(
        page: Int,
        size: Int,
        search: String?,
        status: String?
    ): ProjectListResponse

    suspend fun createProject(
        request: CreateProjectRequest
    ): ProjectDetailResponse

    suspend fun getProject(projectId: Long): ProjectDetailResponse

    suspend fun addProjectTime(
        projectId: Long,
        request: AddTimeRequest
    )

    suspend fun uploadPhoto(
        projectId: Long,
        file: MultipartBody.Part,
        caption: RequestBody? = null,
        isCover: RequestBody? = null,
        takenAt: RequestBody? = null
    ): ProjectPhotoResponse

    suspend fun getProjectPhotos(
        projectId: Long
    ): List<ProjectPhotoResponse>

    suspend fun deletePhoto(
        projectId: Long,
        photoId: Long
    )

    suspend fun setCoverPhoto(
        projectId: Long,
        photoId: Long
    )

    suspend fun getProjectNotes(projectId: Long): ProjectNoteResponse?

    suspend fun saveProjectNotes(
        projectId: Long,
        request: ProjectNoteRequest
    ): ProjectNoteResponse
}