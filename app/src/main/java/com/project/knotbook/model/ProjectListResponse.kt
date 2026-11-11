package com.project.knotbook.model



data class ProjectListResponse(
    val content: List<ProjectListItemResponse>,
    val page: Int,
    val size: Int,
    val totalElements: Long,
    val totalPages: Int
)
