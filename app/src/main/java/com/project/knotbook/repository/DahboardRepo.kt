package com.project.knotbook.repository

import com.project.knotbook.model.DashboardResponse

interface DashboardRepository {
    suspend fun getDashboard(): DashboardResponse
}