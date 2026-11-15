package com.project.knotbook.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.project.knotbook.ui.theme.Cocoa
import com.project.knotbook.ui.theme.Cream
import com.project.knotbook.ui.theme.MutedText
import com.project.knotbook.viewmodel.AddTimeUiState
import com.project.knotbook.viewmodel.AddTimeViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddTimeScreen(
    projectId: Long,
    viewModel: AddTimeViewModel = hiltViewModel(),
    onBackClick: () -> Unit,
    onTimeSaved: () -> Unit
) {
    var hours by remember { mutableStateOf("") }
    var minutes by remember { mutableStateOf("") }

    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(uiState) {
        if (uiState is AddTimeUiState.Success) {
            onTimeSaved()
        }
    }

    Scaffold(
        containerColor = Cream,
        topBar = {
            TopAppBar(
                title = { Text("Add Crafting Time") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Cream
                )
            )
        }
    ) { padding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {

            Text("🧶", fontSize = 64.sp)

            Text(
                "Every stitch counts!",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = Cocoa
            )

            Text(
                "How much time did you spend on this project?",
                color = MutedText
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                OutlinedTextField(
                    value = hours,
                    onValueChange = {
                        if (it.all(Char::isDigit) && it.length <= 4) {
                            hours = it
                        }
                    },
                    modifier = Modifier.weight(1f),
                    label = { Text("Hours") },
                    placeholder = { Text("0") },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Number
                    )
                )

                OutlinedTextField(
                    value = minutes,
                    onValueChange = {
                        if (it.all(Char::isDigit) && it.length <= 2) {
                            minutes = it
                        }
                    },
                    modifier = Modifier.weight(1f),
                    label = { Text("Minutes") },
                    placeholder = { Text("0") },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Number
                    )
                )
            }

            if (uiState is AddTimeUiState.Error) {
                Text(
                    (uiState as AddTimeUiState.Error).message,
                    color = MaterialTheme.colorScheme.error
                )
            }

            Button(
                onClick = {
                    viewModel.addTime(
                        projectId = projectId,
                        hours = hours.toIntOrNull() ?: 0,
                        minutes = minutes.toIntOrNull() ?: 0
                    )
                },
                enabled = uiState !is AddTimeUiState.Loading,
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFFF45B82)
                )
            ) {
                if (uiState is AddTimeUiState.Loading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(20.dp),
                        color = Color.White
                    )
                } else {
                    Text("Save Time")
                }
            }
        }
    }
}