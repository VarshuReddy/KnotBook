package com.project.knotbook.api

import com.project.knotbook.model.RefreshAuthResponse
import com.project.knotbook.model.RefreshTokenRequest
import retrofit2.http.Body
import retrofit2.http.POST

interface RefreshAuthApi {

    @POST("api/auth/refresh")
    suspend fun refreshToken(
        @Body request: RefreshTokenRequest
    ): RefreshAuthResponse
}