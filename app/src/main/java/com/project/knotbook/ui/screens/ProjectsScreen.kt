package com.project.knotbook.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
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
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color.Companion.White
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.project.knotbook.model.ProjectListItemResponse
import com.project.knotbook.ui.theme.Blush
import com.project.knotbook.ui.theme.ButterYellow
import com.project.knotbook.ui.theme.Cocoa
import com.project.knotbook.ui.theme.Cream
import com.project.knotbook.ui.theme.HotPink
import com.project.knotbook.ui.theme.Lavender
import com.project.knotbook.ui.theme.Mint
import com.project.knotbook.ui.theme.MutedText
import com.project.knotbook.viewmodel.ProjectViewModel
import com.project.knotbook.viewmodel.ProjectsUiState

private val projectStatuses = listOf(
    "All" to null,
    "Planning" to "PLANNING",
    "In progress" to "IN_PROGRESS",
    "Completed" to "COMPLETED",
    "On hold" to "ON_HOLD"
)

@Composable
fun ProjectsScreen(
    viewModel: ProjectViewModel = hiltViewModel(),
    onHomeClick: () -> Unit,
    onAddProjectClick: () -> Unit,
    onProjectClick: (Long) -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val searchQuery by viewModel.searchQuery.collectAsStateWithLifecycle()
    val selectedStatus by viewModel.selectedStatus.collectAsStateWithLifecycle()

    val focusManager = LocalFocusManager.current
    val keyboardController = LocalSoftwareKeyboardController.current

    Scaffold(
        containerColor = Cream,
        bottomBar = {
            NavigationBar(containerColor = White) {
                NavigationBarItem(
                    selected = false,
                    onClick = onHomeClick,
                    icon = { Text("⌂") },
                    label = { Text("Home") }
                )

                NavigationBarItem(
                    selected = true,
                    onClick = {},
                    icon = { Text("▦") },
                    label = { Text("Projects") }
                )
            }
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = onAddProjectClick,
                containerColor = HotPink,
                contentColor = White
            ) {
                Text("+", style = MaterialTheme.typography.headlineMedium)
            }
        }
    ) { padding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(Cream)
                .padding(horizontal = 20.dp)
                .pointerInput(Unit) {
                    detectTapGestures {
                        focusManager.clearFocus()
                        keyboardController?.hide()
                    }
                }
        ) {
            Spacer(Modifier.height(20.dp))

            Text(
                "My Projects 🧶",
                style = MaterialTheme.typography.headlineMedium,
                color = Cocoa,
                fontWeight = FontWeight.Bold
            )

            Spacer(Modifier.height(4.dp))

            Text(
                "All your little creations in one place",
                color = MutedText
            )

            Spacer(Modifier.height(20.dp))

            OutlinedTextField(
                value = searchQuery,
                onValueChange = viewModel::onSearchChange,
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text("Search projects...") },
                leadingIcon = { Text("⌕", color = MutedText) },
                singleLine = true,
                shape = RoundedCornerShape(16.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = HotPink,
                    unfocusedBorderColor = Lavender,
                    focusedContainerColor = White,
                    unfocusedContainerColor = White
                ),
                keyboardActions = KeyboardActions(
                    onDone = {
                        focusManager.clearFocus()
                        keyboardController?.hide()
                    }
                )
            )

            Spacer(Modifier.height(16.dp))

            Text(
                "Filter by status",
                color = Cocoa,
                fontWeight = FontWeight.SemiBold
            )

            Spacer(Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                projectStatuses.take(3).forEach { (label, status) ->
                    StatusChip(
                        label = label,
                        selected = selectedStatus == status,
                        onClick = { viewModel.onStatusChange(status) }
                    )
                }
            }

            Spacer(Modifier.height(8.dp))

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                projectStatuses.drop(3).forEach { (label, status) ->
                    StatusChip(
                        label = label,
                        selected = selectedStatus == status,
                        onClick = { viewModel.onStatusChange(status) }
                    )
                }
            }

            Spacer(Modifier.height(20.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    "Your creations",
                    style = MaterialTheme.typography.titleLarge,
                    color = Cocoa,
                    fontWeight = FontWeight.Bold
                )

                Text(
                    "Newest first ↓",
                    color = MutedText,
                    style = MaterialTheme.typography.bodySmall
                )
            }

            Spacer(Modifier.height(12.dp))

            when (val state = uiState) {
                is ProjectsUiState.Loading -> {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(color = HotPink)
                    }
                }

                is ProjectsUiState.Error -> {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(state.message, color = Cocoa)
                        TextButton(onClick = viewModel::loadProjects) {
                            Text("Try again", color = HotPink)
                        }
                    }
                }

                is ProjectsUiState.Success -> {
                    if (state.projects.isEmpty()) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .weight(1f),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text("🧶", style = MaterialTheme.typography.displayMedium)
                                Text(
                                    "No projects found",
                                    color = Cocoa,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    "Try another search or start a new project.",
                                    color = MutedText
                                )
                            }
                        }
                    } else {
                        LazyVerticalGrid(
                            columns = GridCells.Fixed(2),
                            modifier = Modifier.weight(1f),
                            contentPadding = PaddingValues(bottom = 20.dp),
                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            items(state.projects, key = { it.id }) { project ->
                                ProjectCard(
                                    project = project,
                                    onClick = { onProjectClick(project.id) }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun StatusChip(
    label: String,
    selected: Boolean,
    onClick: () -> Unit
) {
    FilterChip(
        selected = selected,
        onClick = onClick,
        label = { Text(label) },
        colors = FilterChipDefaults.filterChipColors(
            selectedContainerColor = HotPink,
            selectedLabelColor = White,
            containerColor = White,
            labelColor = Cocoa
        ),
        border = FilterChipDefaults.filterChipBorder(
            enabled = true,
            selected = selected,
            borderColor = Lavender,
            selectedBorderColor = HotPink
        )
    )
}

@Composable
private fun ProjectCard(
    project: ProjectListItemResponse,
    onClick: () -> Unit
) {
    val cardColor = when (project.status) {
        "IN_PROGRESS" -> Mint
        "COMPLETED" -> ButterYellow
        "ON_HOLD" -> Lavender
        else -> Blush
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = cardColor)
    ) {
        Column {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(115.dp)
                    .background(cardColor),
                contentAlignment = Alignment.Center
            ) {
                Text("🧶", style = MaterialTheme.typography.displayMedium)
            }

            Column(
                modifier = Modifier.padding(12.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Text(
                    project.title,
                    color = Cocoa,
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.titleSmall,
                    maxLines = 2
                )

                Text(
                    project.status.replace("_", " "),
                    color = Cocoa,
                    style = MaterialTheme.typography.labelSmall
                )

                Text(
                    "${project.totalTimeMinutes} min crafted",
                    color = MutedText,
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }
    }
}