package com.project.knotbook.repository

import com.project.knotbook.api.ProjectApi
import com.project.knotbook.model.CreateProjectRequest
import com.project.knotbook.model.ProjectDetailResponse
import com.project.knotbook.model.ProjectListResponse
import jakarta.inject.Inject

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
}