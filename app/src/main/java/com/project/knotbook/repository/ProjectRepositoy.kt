package com.project.knotbook.repository

import com.project.knotbook.model.CreateProjectRequest
import com.project.knotbook.model.ProjectListResponse

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
}