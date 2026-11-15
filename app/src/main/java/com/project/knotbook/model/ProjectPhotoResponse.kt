package com.project.knotbook.model

data class ProjectPhotoResponse(
    val id: Long,
    val projectId: Long,
    val imageUrl: String,
    val caption: String?,
    val isCover: Boolean,
    val takenAt: String?,
    val createdAt: String
)