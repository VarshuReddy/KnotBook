package com.project.knotbook.api

import com.project.knotbook.model.AddTimeRequest
import com.project.knotbook.model.CreateProjectRequest
import com.project.knotbook.model.ProjectDetailResponse
import com.project.knotbook.model.ProjectListResponse
import com.project.knotbook.model.ProjectNoteRequest
import com.project.knotbook.model.ProjectNoteResponse
import com.project.knotbook.model.ProjectPhotoResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Part
import retrofit2.http.Path
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

    @GET("api/projects/{projectId}")
    suspend fun getProject(@Path("projectId") id:Long): ProjectDetailResponse

    @POST("api/projects/{projectId}/time")
    suspend fun addProjectTime(
        @Path("projectId") projectId: Long,
        @Body request: AddTimeRequest
    )

    @Multipart
    @POST("api/projects/{projectId}/photos")
    suspend fun uploadPhoto(
        @Path("projectId") projectId: Long,
        @Part file: MultipartBody.Part,
        @Part("caption") caption: RequestBody? = null,
        @Part("isCover") isCover: RequestBody? = null,
        @Part("takenAt") takenAt: RequestBody? = null
    ): ProjectPhotoResponse

    @GET("api/projects/{projectId}/photos")
    suspend fun getProjectPhotos(
        @Path("projectId") projectId: Long
    ): List<ProjectPhotoResponse>


    @DELETE("api/projects/{projectId}/photos/{photoId}")
    suspend fun deletePhoto(
        @Path("projectId") projectId: Long,
        @Path("photoId") photoId: Long
    )


    @PATCH("api/projects/{projectId}/photos/{photoId}/cover")
    suspend fun setCoverPhoto(
        @Path("projectId") projectId: Long,
        @Path("photoId") photoId: Long
    )

    @GET("api/projects/{projectId}/notes")
    suspend fun getProjectNotes(
        @Path("projectId") projectId: Long
    ): Response<ProjectNoteResponse>


    @PUT("api/projects/{projectId}/notes")
    suspend fun saveProjectNotes(
        @Path("projectId") projectId: Long,
        @Body request: ProjectNoteRequest
    ): ProjectNoteResponse



}