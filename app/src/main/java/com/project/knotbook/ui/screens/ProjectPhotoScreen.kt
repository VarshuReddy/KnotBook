package com.project.knotbook.ui.screens

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AddPhotoAlternate
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.project.knotbook.model.ProjectPhotoResponse
import com.project.knotbook.ui.theme.Cream
import com.project.knotbook.viewmodel.PhotosUiState
import com.project.knotbook.viewmodel.ProjectPhotosViewModel


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProjectPhotosScreen(
    projectId: Long,
    viewModel: ProjectPhotosViewModel = hiltViewModel(),
    onBackClick: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    var selectedUri by remember { mutableStateOf<Uri?>(null) }
    var caption by remember { mutableStateOf("") }

    val picker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        selectedUri = uri
    }

    LaunchedEffect(projectId) {
        viewModel.loadPhotos(projectId)
    }

    Scaffold(
        containerColor = Cream,
        topBar = {
            TopAppBar(
                title = { Text("Project Photos") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    picker.launch(
                        PickVisualMediaRequest(
                            ActivityResultContracts.PickVisualMedia.ImageOnly
                        )
                    )
                },
                containerColor = Color(0xFFF45B82)
            ) {
                Icon(
                    Icons.Default.AddPhotoAlternate,
                    contentDescription = "Add photo",
                    tint = Color.White
                )
            }
        }
    ) { padding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
        )
        {

            Text(
                "Capture every little milestone 🧶",
                style = MaterialTheme.typography.titleMedium,
                color = Color(0xFF493B42)
            )

            Spacer(Modifier.height(16.dp))

            if (selectedUri != null) {
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = Color.White
                    )
                ) {
                    Column(Modifier.padding(12.dp)) {
                        AsyncImage(
                            model = selectedUri,
                            contentDescription = "Selected photo",
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(180.dp)
                                .clip(RoundedCornerShape(16.dp)),
                            contentScale = ContentScale.Crop
                        )

                        Spacer(Modifier.height(8.dp))

                        OutlinedTextField(
                            value = caption,
                            onValueChange = { caption = it },
                            label = { Text("Add a caption") },
                            modifier = Modifier.fillMaxWidth()
                        )

                        Spacer(Modifier.height(8.dp))

                        Button(
                            onClick = {
                                selectedUri?.let {
                                    viewModel.uploadPhoto(
                                        projectId,
                                        it,
                                        caption
                                    )
                                }
                                selectedUri = null
                                caption = ""
                            },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("Upload photo")
                        }
                    }
                }

                Spacer(Modifier.height(16.dp))
            }

            when (val state = uiState) {

                PhotosUiState.Loading -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                }

                PhotosUiState.Uploading -> {
                    CircularProgressIndicator()
                    Text("Uploading your photo...")
                }

                is PhotosUiState.Error -> {
                    Text(
                        state.message,
                        color = MaterialTheme.colorScheme.error
                    )
                }

                is PhotosUiState.Success -> {

                    if (state.photos.isEmpty()) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(24.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                "No photos yet. Add your first project photo!",
                                color = Color(0xFF887982)
                            )
                        }
                    } else {
                        LazyVerticalGrid(
                            columns = GridCells.Fixed(2),
                            modifier = Modifier.fillMaxSize(),
                            contentPadding = PaddingValues(bottom = 80.dp),
                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            items(
                                items = state.photos,
                                key = { it.id }
                            ) { photo ->

                                Card(
                                    shape = RoundedCornerShape(16.dp),
                                    colors = CardDefaults.cardColors(
                                        containerColor = Color.White
                                    )
                                ) {
                                    Column {
                                        AsyncImage(
                                            model = photo.imageUrl,
                                            contentDescription = photo.caption,
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .height(150.dp),
                                            contentScale = ContentScale.Crop
                                        )

                                        Column(
                                            modifier = Modifier.padding(10.dp)
                                        ) {
                                            if (photo.isCover) {
                                                Text(
                                                    "★ Cover photo",
                                                    color = Color(0xFFF45B82),
                                                    fontWeight = FontWeight.Bold
                                                )
                                            }

                                            Text(
                                                photo.caption ?: "Project photo",
                                                color = Color(0xFF493B42),
                                                maxLines = 2
                                            )

                                            Row(
                                                horizontalArrangement =
                                                    Arrangement.SpaceBetween,
                                                modifier = Modifier.fillMaxWidth()
                                            ) {
                                                if (!photo.isCover) {
                                                    TextButton(
                                                        onClick = {
                                                            viewModel.setCoverPhoto(
                                                                projectId,
                                                                photo.id
                                                            )
                                                        }
                                                    ) {
                                                        Text("Set cover")
                                                    }
                                                }

                                                IconButton(
                                                    onClick = {
                                                        viewModel.deletePhoto(
                                                            projectId,
                                                            photo.id
                                                        )
                                                    }
                                                ) {
                                                    Icon(
                                                        Icons.Default.DeleteOutline,
                                                        contentDescription = "Delete photo",
                                                        tint = Color(0xFFF45B82)
                                                    )
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
