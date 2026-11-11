package com.project.knotbook.api

import com.project.knotbook.model.DashboardResponse
import retrofit2.http.GET

interface DashboardApi {
    
    @GET("api/dashboard")
    suspend fun getDashboard(): DashboardResponse
}