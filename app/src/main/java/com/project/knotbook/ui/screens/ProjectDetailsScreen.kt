package com.project.knotbook.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Notes
import androidx.compose.material.icons.filled.PhotoLibrary
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.project.knotbook.model.HookResponse
import com.project.knotbook.model.ProjectDetailResponse
import com.project.knotbook.model.YarnResponse
import com.project.knotbook.ui.components.ProjectCoverImage
import com.project.knotbook.ui.theme.Cocoa
import com.project.knotbook.ui.theme.Cream
import com.project.knotbook.viewmodel.DeleteProjectState
import com.project.knotbook.viewmodel.ProjectDetailsUiState
import com.project.knotbook.viewmodel.ProjectDetailsViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProjectDetailsScreen(
    projectId: Long,
    viewModel: ProjectDetailsViewModel = hiltViewModel(),
    onBackClick: () -> Unit,
    onAddTimeClick: (Long) -> Unit,
    onPhotosClick: (Long) -> Unit,
    onNotesClick: (Long) -> Unit,
    onEditClick: (Long) -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    val deleteState by viewModel.deleteState.collectAsStateWithLifecycle()

    var showDeleteDialog by remember { mutableStateOf(false) }

    val lifecycleOwner = LocalLifecycleOwner.current

    DisposableEffect(lifecycleOwner, projectId) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                viewModel.loadProject(projectId)
            }
        }

        lifecycleOwner.lifecycle.addObserver(observer)

        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

    LaunchedEffect(projectId) {
        viewModel.loadProject(projectId)
    }

    LaunchedEffect(deleteState) {
        when (deleteState) {
            is DeleteProjectState.Success -> {
                showDeleteDialog = false
                onBackClick()
            }

            is DeleteProjectState.Error -> {

            }

            else -> Unit
        }
    }

    Scaffold(
        containerColor = Cream, topBar = {
            TopAppBar(
                title = { Text("Project Details") }, navigationIcon = {
                IconButton(onClick = onBackClick) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back"
                    )
                }
            }, actions = {
                    IconButton(
                        onClick = {
                            showDeleteDialog = true
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = "Delete project",
                            tint = Color(0xFFF45B82)
                        )
                }
            }, colors = TopAppBarDefaults.topAppBarColors(
                containerColor = Cream
            )
            )
        })
    { padding ->

        when (val state = uiState) {
            is ProjectDetailsUiState.Loading -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = Color(0xFFF45B82))
                }
            }

            is ProjectDetailsUiState.Error -> {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding)
                        .padding(24.dp),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(state.message)
                    Spacer(Modifier.height(12.dp))
                    Button(onClick = { viewModel.loadProject(projectId) }) {
                        Text("Try again")
                    }
                }
            }

            is ProjectDetailsUiState.Success -> {
                val project = state.project

                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    item {
                        ProjectHeader(project = project)
                    }

                    item {
                        ProjectInfoCard(project = project)
                    }

                    item {
                        ProjectTimeCard(
                            totalMinutes = project.totalTimeMinutes,
                            onAddTimeClick = { onAddTimeClick(projectId) })
                    }

                    item {
                        ProjectYarnSection(yarns = project.yarns)
                    }

                    item {
                        ProjectHookSection(hooks = project.hooks)
                    }

                    item {
                        ProjectActionCard(
                            title = "Progress Photos",
                            subtitle = "Keep a visual record of your work",
                            icon = Icons.Default.PhotoLibrary,
                            background = Color(0xFFF7DCE8),
                            onClick = { onPhotosClick(projectId) })
                    }

                    item {
                        ProjectActionCard(
                            title = "Project Notes",
                            subtitle = "Patterns, ideas and little reminders",
                            icon = Icons.Default.Notes,
                            background = Color(0xFFE6DDF6),
                            onClick = { onNotesClick(projectId) })
                    }
                }
            }
        }
    }

    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = {
                if (deleteState !is DeleteProjectState.Deleting) {
                    showDeleteDialog = false
                    viewModel.resetDeleteState()
                }
            },
            title = {
                Text("Delete Project?")
            },
            text = {
                Column {
                    Text(
                        "Are you sure you want to delete this project? " +
                                "This project will be removed from your projects list."
                    )

                    if (deleteState is DeleteProjectState.Error) {
                        Spacer(Modifier.height(12.dp))
                        Text(
                            text = (deleteState as DeleteProjectState.Error).message,
                            color = Color.Red
                        )
                    }
                }
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.deleteProject(projectId)
                    },
                    enabled = deleteState !is DeleteProjectState.Deleting
                ) {
                    if (deleteState is DeleteProjectState.Deleting) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(18.dp),
                            strokeWidth = 2.dp
                        )
                    } else {
                        Text(
                            "Delete",
                            color = Color(0xFFF45B82)
                        )
                    }
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        showDeleteDialog = false
                        viewModel.resetDeleteState()
                    },
                    enabled = deleteState !is DeleteProjectState.Deleting
                ) {
                    Text("Cancel")
                }
            }
        )
    }
}

@Composable
private fun ProjectHeader(project: ProjectDetailResponse) {
    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(180.dp)
                .clip(RoundedCornerShape(24.dp))
                .background(Color(0xFFF7DCE8)),
            contentAlignment = Alignment.Center
        ) {
            ProjectCoverImage(
                imageUrl = project.photos.find { it-> it.isCover }?.imageUrl,
                modifier = Modifier.fillMaxWidth(),
                height = 145.dp
            )
        }

        Text(
            text = project.title,
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            color = Cocoa
        )

        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            ProjectStatusChip(project.status)

            project.category?.takeIf { it.isNotBlank() }?.let {
                Text(
                    text = it,
                    color = Color(0xFF887982),
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
    }
}

@Composable
private fun ProjectStatusChip(status: String) {
    val background = when (status.uppercase()) {
        "COMPLETED" -> Color(0xFFD9EAD9)
        "IN_PROGRESS" -> Color(0xFFF9E9B9)
        "ON_HOLD" -> Color(0xFFE6DDF6)
        else -> Color(0xFFF7DCE8)
    }

    val label = status.lowercase()
        .replace("_", " ")
        .replaceFirstChar { it.uppercase() }

    Surface(
        color = background,
        shape = RoundedCornerShape(50)
    ) {
        Text(
            text = label,
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
            color = Cocoa,
            style = MaterialTheme.typography.labelMedium
        )
    }
}

@Composable
private fun ProjectInfoCard(project: ProjectDetailResponse) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(
            modifier = Modifier.padding(18.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                "About this project",
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp,
                color = Cocoa
            )

            project.description?.takeIf { it.isNotBlank() }?.let {
                Text(it, color = Color(0xFF887982))
            }

            HorizontalDivider(color = Color(0xFFF0E8E8))

            DetailRow("Started", project.startDate ?: "Not set")
            DetailRow("Target / End date", project.endDate ?: "Not set")
        }
    }
}

@Composable
private fun ProjectTimeCard(
    totalMinutes: Long,
    onAddTimeClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFFF9E9B9)
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(18.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(
                    "Crafting time",
                    color = Cocoa,
                    fontWeight = FontWeight.SemiBold
                )

                Text(
                    formatCraftingTime(totalMinutes),
                    color = Cocoa,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Button(
                onClick = onAddTimeClick,
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFFF45B82)
                )
            ) {
                Icon(Icons.Default.Add, contentDescription = null)
                Spacer(Modifier.width(4.dp))
                Text("Add time")
            }
        }
    }
}

@Composable
private fun ProjectYarnSection(yarns: List<YarnResponse>) {
    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        Text(
            "Yarns",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            color = Cocoa
        )

        if (yarns.isEmpty()) {
            Text("No yarn details added.", color = Color(0xFF887982))
        } else {
            yarns.forEach { yarn ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = Color.White
                    )
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(42.dp)
                                .clip(CircleShape)
                                .background(Color(0xFFF7DCE8)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("🧶", fontSize = 22.sp)
                        }

                        Spacer(Modifier.width(12.dp))

                        Column {
                            Text(
                                yarn.color,
                                fontWeight = FontWeight.SemiBold,
                                color = Cocoa
                            )
                            Text(
                                "${yarn.weight} • ${yarn.type}",
                                color = Color(0xFF887982),
                                style = MaterialTheme.typography.bodySmall
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ProjectHookSection(hooks: List<HookResponse>) {
    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        Text(
            "Hooks",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            color = Cocoa
        )

        if (hooks.isEmpty()) {
            Text("No hooks added.", color = Color(0xFF887982))
        } else {
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.horizontalScroll(rememberScrollState())
            ) {
                hooks.forEach { hook ->
                    Surface(
                        color = Color(0xFFD9EAD9),
                        shape = RoundedCornerShape(14.dp)
                    ) {
                        Text(
                            "${hook.hookSizeMm} mm",
                            modifier = Modifier.padding(
                                horizontal = 16.dp,
                                vertical = 12.dp
                            ),
                            color = Cocoa,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun ProjectActionCard(
    title: String,
    subtitle: String,
    icon: ImageVector,
    background: Color,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = background)
    ) {
        Row(
            modifier = Modifier.padding(18.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                modifier = Modifier.size(32.dp),
                tint = Cocoa
            )

            Spacer(Modifier.width(14.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    title,
                    fontWeight = FontWeight.Bold,
                    color = Cocoa
                )
                Text(
                    subtitle,
                    color = Color(0xFF665961),
                    style = MaterialTheme.typography.bodySmall
                )
            }

            Icon(
                Icons.AutoMirrored.Filled.ArrowForward,
                contentDescription = null,
                tint = Cocoa
            )
        }
    }
}

@Composable
private fun DetailRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(label, color = Color(0xFF887982))
        Text(
            value,
            color = Cocoa,
            fontWeight = FontWeight.Medium
        )
    }
}

private fun formatCraftingTime(totalMinutes: Long): String {
    val hours = totalMinutes / 60
    val minutes = totalMinutes % 60

    return when {
        hours > 0 && minutes > 0 -> "${hours}h ${minutes}m"
        hours > 0 -> "${hours}h"
        else -> "${minutes}m"
    }
}