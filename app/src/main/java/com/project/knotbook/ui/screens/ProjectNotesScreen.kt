package com.project.knotbook.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.project.knotbook.ui.theme.Cocoa
import com.project.knotbook.ui.theme.Cream
import com.project.knotbook.ui.theme.HotPink
import com.project.knotbook.viewmodel.NotesUiState
import com.project.knotbook.viewmodel.ProjectNotesViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProjectNotesScreen(
    projectId: Long,
    viewModel: ProjectNotesViewModel = hiltViewModel(),
    onBackClick: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    var content by rememberSaveable { mutableStateOf("") }
    var hasLoaded by remember { mutableStateOf(false) }

    LaunchedEffect(projectId) {
        viewModel.loadNotes(projectId)
    }

    LaunchedEffect(uiState) {
        if (uiState is NotesUiState.Success) {
            content = (uiState as NotesUiState.Success).note?.content.orEmpty()
            hasLoaded = true
        }
    }

    Scaffold(
        containerColor = Cream,
        topBar = {
            TopAppBar(
                title = { Text("Project Notes") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                },
                actions = {
                    TextButton(
                        onClick = {
                            viewModel.saveNotes(projectId, content)
                        },
                        enabled = hasLoaded &&
                                uiState !is NotesUiState.Saving
                    ) {
                        Text("Save")
                    }
                }
            )
        }
    ) { padding ->

        when (uiState) {
            NotesUiState.Loading -> {
                Box(
                    Modifier.fillMaxSize().padding(padding),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }

            else -> {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding)
                        .padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Text(
                        "A little space for your ideas ✨",
                        style = MaterialTheme.typography.titleMedium,
                        color = Cocoa
                    )

                    OutlinedTextField(
                        value = content,
                        onValueChange = { content = it },
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f),
                        placeholder = {
                            Text(
                                "Write down your pattern, yarn changes, " +
                                        "measurements, ideas or anything you want to remember..."
                            )
                        },
                        shape = RoundedCornerShape(18.dp)
                    )

                    if (uiState is NotesUiState.Saving) {
                        LinearProgressIndicator(
                            modifier = Modifier.fillMaxWidth()
                        )
                    }

                    if (uiState is NotesUiState.Error) {
                        Text(
                            (uiState as NotesUiState.Error).message,
                            color = MaterialTheme.colorScheme.error
                        )
                    }

                    Button(
                        onClick = {
                            viewModel.saveNotes(projectId, content)
                        },
                        enabled = hasLoaded &&
                                uiState !is NotesUiState.Saving,
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = HotPink
                        )
                    ) {
                        Text("Save Notes")
                    }

                }
            }
        }
    }
}