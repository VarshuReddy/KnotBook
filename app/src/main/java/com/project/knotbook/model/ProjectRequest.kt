package com.project.knotbook.model

data class YarnRequest(
    val color: String,
    val weight: String,
    val type: String
)

data class HookRequest(
    val hookSizeMm: Double
)

data class CreateProjectRequest(
    val title: String,
    val description: String?,
    val category: String?,
    val status: String,
    val startDate: String?,
    val endDate: String?,
    val yarns: List<YarnRequest>,
    val hooks: List<HookRequest>
)