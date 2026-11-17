package com.project.knotbook.model

data class ProjectNoteRequest(
    val content: String
)

data class ProjectNoteResponse(
    val id: Long,
    val projectId: Long,
    val content: String,
    val createdAt: String,
    val updatedAt: String
)