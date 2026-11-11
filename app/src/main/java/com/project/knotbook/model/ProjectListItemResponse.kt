package com.project.knotbook.model

data class ProjectListItemResponse(
    val id: Long,
    val title: String,
    val category: String?,
    val status: String,
    val coverImageUrl: String?,
    val startDate: String?,
    val totalTimeMinutes: Long
)