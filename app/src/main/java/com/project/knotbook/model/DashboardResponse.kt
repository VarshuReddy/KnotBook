package com.project.knotbook.model

data class DashboardResponse (
    val totalProjects: Long,
    val inProgressProjects: Long,
    val completedProjects: Long,
    val totalTimeMinutes: Long,
    val recentProjects: List<ProjectListItemResponse>
)