package com.project.knotbook.repository

import com.project.knotbook.api.ProjectApi
import com.project.knotbook.model.AddTimeRequest
import com.project.knotbook.model.CreateProjectRequest
import com.project.knotbook.model.ProjectDetailResponse
import com.project.knotbook.model.ProjectListResponse
import com.project.knotbook.model.ProjectNoteRequest
import com.project.knotbook.model.ProjectNoteResponse
import com.project.knotbook.model.ProjectPhotoResponse
import jakarta.inject.Inject
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.HttpException

class ProjectRepositoryImpl @Inject constructor(
    private val projectApi: ProjectApi
) : ProjectRepository {

    override suspend fun getProjects(
        page: Int,
        size: Int,
        search: String?,
        status: String?
    ): ProjectListResponse {
        return projectApi.getProjects(page, size, search, status)
    }

    override suspend fun createProject(
        request: CreateProjectRequest
    ): ProjectDetailResponse {
        return projectApi.createProject(request)
    }

    override suspend fun getProject(
        projectId: Long
    ): ProjectDetailResponse {
        return projectApi.getProject(projectId)
    }

    override suspend fun addProjectTime(
        projectId: Long,
        request: AddTimeRequest
    ) {
        projectApi.addProjectTime(projectId, request)
    }

    override suspend fun uploadPhoto(
        projectId: Long,
        file: MultipartBody.Part,
        caption: RequestBody?,
        isCover: RequestBody?,
        takenAt: RequestBody?
    ): ProjectPhotoResponse {
        return projectApi.uploadPhoto(
            projectId,
            file,
            caption,
            isCover,
            takenAt
        )
    }

    override suspend fun getProjectPhotos(
        projectId: Long
    ): List<ProjectPhotoResponse> {
        return projectApi.getProjectPhotos(projectId)
    }

    override suspend fun deletePhoto(
        projectId: Long,
        photoId: Long
    ) {
        projectApi.deletePhoto(projectId, photoId)
    }

    override suspend fun setCoverPhoto(
        projectId: Long,
        photoId: Long
    ) {
        projectApi.setCoverPhoto(projectId, photoId)
    }

    override suspend fun getProjectNotes(
        projectId: Long
    ): ProjectNoteResponse? {
        val response = projectApi.getProjectNotes(projectId)

        return when {
            response.isSuccessful -> response.body()
            response.code() == 404 -> null
            else -> throw HttpException(response)
        }
    }

    override suspend fun saveProjectNotes(
        projectId: Long,
        request: ProjectNoteRequest
    ): ProjectNoteResponse {
        return projectApi.saveProjectNotes(projectId, request)
    }
}