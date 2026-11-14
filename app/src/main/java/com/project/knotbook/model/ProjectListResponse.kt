package com.project.knotbook.model



data class ProjectListResponse(
    val content: List<ProjectListItemResponse>,
    val page: Int,
    val size: Int,
    val totalElements: Long,
    val totalPages: Int
)

data class ProjectDetailResponse(
    val id: Long,
    val title: String,
    val description: String?,
    val category: String?,
    val status: String,
    val startDate: String?,
    val endDate: String?,
    val totalTimeMinutes: Long,
    val yarns: List<YarnResponse>,
    val hooks: List<HookResponse>,
    val createdAt: String,
    val updatedAt: String
)

data class YarnResponse(
    val id: Long,
    val color: String,
    val weight: String,
    val type: String
)

data class HookResponse(
    val id: Long,
    val hookSizeMm: Double
)