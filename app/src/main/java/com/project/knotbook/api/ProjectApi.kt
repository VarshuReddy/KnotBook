package com.project.knotbook.api

import com.project.knotbook.model.CreateProjectRequest
import com.project.knotbook.model.ProjectListResponse
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface ProjectApi {

    @GET("api/projects")
    suspend fun getProjects(
        @Query("page") page: Int = 0,
        @Query("size") size: Int = 10,
        @Query("search") search: String? = null,
        @Query("status") status: String? = null
    ): ProjectListResponse

    @POST("api/projects")
    suspend fun createProject(
        @Body request: CreateProjectRequest
    ): ProjectDetailResponse
}