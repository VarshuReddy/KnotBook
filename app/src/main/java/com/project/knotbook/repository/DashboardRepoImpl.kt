package com.project.knotbook.repository

import com.project.knotbook.api.DashboardApi
import com.project.knotbook.model.DashboardResponse
import jakarta.inject.Inject

class DashboardRepositoryImpl @Inject constructor(
    private val dashboardApi: DashboardApi
) : DashboardRepository {

    override suspend fun getDashboard(): DashboardResponse {
        return dashboardApi.getDashboard()
    }
}