package com.project.knotbook.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color.Companion.White
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.project.knotbook.model.CreateProjectRequest
import com.project.knotbook.model.HookRequest
import com.project.knotbook.model.YarnRequest
import com.project.knotbook.ui.theme.Cocoa
import com.project.knotbook.ui.theme.Cream
import com.project.knotbook.ui.theme.HotPink
import com.project.knotbook.ui.theme.Lavender
import com.project.knotbook.viewmodel.AddProjectUiState
import com.project.knotbook.viewmodel.AddProjectViewModel

private data class YarnForm(
    val color: String = "",
    val weight: String = "",
    val type: String = ""
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddProjectScreen(
    viewModel: AddProjectViewModel = hiltViewModel(),
    onBackClick: () -> Unit,
    onProjectCreated: (Long) -> Unit
) {
    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var category by remember { mutableStateOf("") }
    var status by remember { mutableStateOf("PLANNING") }
    var startDate by remember { mutableStateOf("") }
    var endDate by remember { mutableStateOf("") }

    var yarns by remember { mutableStateOf(listOf(YarnForm())) }
    var hooks by remember { mutableStateOf(listOf("")) }

    var expanded by remember { mutableStateOf(false) }
    var validationError by remember { mutableStateOf<String?>(null) }

    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(uiState) {
        if (uiState is AddProjectUiState.Success) {
            onProjectCreated((uiState as AddProjectUiState.Success).projectId)
            viewModel.resetState()
        }
    }

    Scaffold(
        containerColor = Cream,
        topBar = {
            TopAppBar(
                title = {
                    Text("New Project 🧶", color = Cocoa)
                },
                navigationIcon = {
                    TextButton(onClick = onBackClick) {
                        Text("Cancel", color = HotPink)
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
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(18.dp)
        ) {

            // Project information
            FormSection("Project information")

            OutlinedTextField(
                value = title,
                onValueChange = {
                    title = it
                    validationError = null
                },
                modifier = Modifier.fillMaxWidth(),
                label = { Text("Project name *") },
                placeholder = { Text("e.g. Granny Square Cardigan") },
                singleLine = true
            )

            OutlinedTextField(
                value = description,
                onValueChange = { description = it },
                modifier = Modifier.fillMaxWidth(),
                label = { Text("Description") },
                minLines = 3
            )

            OutlinedTextField(
                value = category,
                onValueChange = { category = it },
                modifier = Modifier.fillMaxWidth(),
                label = { Text("Category") },
                placeholder = { Text("e.g. Clothing, Accessories") },
                singleLine = true
            )

            // Status
            FormSection("Project status")

            ExposedDropdownMenuBox(
                expanded = expanded,
                onExpandedChange = { expanded = !expanded }
            ) {
                OutlinedTextField(
                    value = status.replace("_", " "),
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Status") },
                    trailingIcon = {
                        ExposedDropdownMenuDefaults.TrailingIcon(expanded)
                    },
                    modifier = Modifier
                        .menuAnchor()
                        .fillMaxWidth()
                )

                ExposedDropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false }
                ) {
                    listOf(
                        "PLANNING",
                        "IN_PROGRESS",
                        "COMPLETED",
                        "ON_HOLD"
                    ).forEach { option ->
                        DropdownMenuItem(
                            text = { Text(option.replace("_", " ")) },
                            onClick = {
                                status = option
                                expanded = false
                            }
                        )
                    }
                }
            }

            // Dates
            FormSection("Project dates")

            OutlinedTextField(
                value = startDate,
                onValueChange = { startDate = it },
                modifier = Modifier.fillMaxWidth(),
                label = { Text("Start date") },
                placeholder = { Text("YYYY-MM-DD") },
                singleLine = true
            )

            OutlinedTextField(
                value = endDate,
                onValueChange = { endDate = it },
                modifier = Modifier.fillMaxWidth(),
                label = { Text("End date") },
                placeholder = { Text("YYYY-MM-DD") },
                singleLine = true
            )

            // Yarns
            FormSection("Yarn details 🧶")

            yarns.forEachIndexed { index, yarn ->
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = Lavender
                    ),
                    shape = RoundedCornerShape(18.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(14.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Yarn ${index + 1}", color = Cocoa)

                            if (yarns.size > 1) {
                                TextButton(
                                    onClick = {
                                        yarns = yarns.toMutableList().also {
                                            it.removeAt(index)
                                        }
                                    }
                                ) {
                                    Text("Remove", color = HotPink)
                                }
                            }
                        }

                        OutlinedTextField(
                            value = yarn.color,
                            onValueChange = { value ->
                                yarns = yarns.toMutableList().also {
                                    it[index] = yarn.copy(color = value)
                                }
                            },
                            modifier = Modifier.fillMaxWidth(),
                            label = { Text("Color") },
                            singleLine = true
                        )

                        OutlinedTextField(
                            value = yarn.weight,
                            onValueChange = { value ->
                                yarns = yarns.toMutableList().also {
                                    it[index] = yarn.copy(weight = value)
                                }
                            },
                            modifier = Modifier.fillMaxWidth(),
                            label = { Text("Weight") },
                            placeholder = { Text("e.g. DK, Worsted") },
                            singleLine = true
                        )

                        OutlinedTextField(
                            value = yarn.type,
                            onValueChange = { value ->
                                yarns = yarns.toMutableList().also {
                                    it[index] = yarn.copy(type = value)
                                }
                            },
                            modifier = Modifier.fillMaxWidth(),
                            label = { Text("Yarn type") },
                            placeholder = { Text("e.g. Cotton, Acrylic") },
                            singleLine = true
                        )
                    }
                }
            }

            TextButton(
                onClick = { yarns = yarns + YarnForm() }
            ) {
                Text("+ Add another yarn", color = HotPink)
            }

            // Hooks
            FormSection("Crochet hooks")

            hooks.forEachIndexed { index, hook ->
                Row(
                    verticalAlignment = androidx.compose.ui.Alignment.CenterVertically
                ) {
                    OutlinedTextField(
                        value = hook,
                        onValueChange = { value ->
                            hooks = hooks.toMutableList().also {
                                it[index] = value
                            }
                        },
                        modifier = Modifier.weight(1f),
                        label = { Text("Hook size (mm)") },
                        placeholder = { Text("e.g. 4.5") },
                        singleLine = true
                    )

                    if (hooks.size > 1) {
                        TextButton(
                            onClick = {
                                hooks = hooks.toMutableList().also {
                                    it.removeAt(index)
                                }
                            }
                        ) {
                            Text("Remove", color = HotPink)
                        }
                    }
                }
            }

            TextButton(
                onClick = { hooks = hooks + "" }
            ) {
                Text("+ Add another hook", color = HotPink)
            }

            validationError?.let {
                Text(it, color = MaterialTheme.colorScheme.error)
            }

            if (uiState is AddProjectUiState.Error) {
                Text(
                    (uiState as AddProjectUiState.Error).message,
                    color = MaterialTheme.colorScheme.error
                )
            }

            Button(
                onClick = {
                    val parsedYarns = yarns.map {
                        YarnRequest(
                            color = it.color.trim(),
                            weight = it.weight.trim(),
                            type = it.type.trim()
                        )
                    }

                    val parsedHooks = hooks.mapNotNull {
                        it.toDoubleOrNull()?.let { size ->
                            HookRequest(size)
                        }
                    }

                    val invalidDate =
                        (startDate.isNotBlank() && runCatching {
                            java.time.LocalDate.parse(startDate)
                        }.isFailure) ||
                                (endDate.isNotBlank() && runCatching {
                                    java.time.LocalDate.parse(endDate)
                                }.isFailure)

                    if (title.isBlank()) {
                        validationError = "Please enter a project name"
                    } else if (invalidDate) {
                        validationError = "Dates must use YYYY-MM-DD"
                    } else if (
                        startDate.isNotBlank() &&
                        endDate.isNotBlank() &&
                        endDate < startDate
                    ) {
                        validationError = "End date cannot be before start date"
                    } else if (yarns.any {
                            it.color.isBlank() ||
                                    it.weight.isBlank() ||
                                    it.type.isBlank()
                        }) {
                        validationError = "Complete or remove each yarn entry"
                    } else if (hooks.any {
                            it.isNotBlank() &&
                                    it.toDoubleOrNull() == null
                        }) {
                        validationError = "Enter valid hook sizes"
                    } else if (hooks.any {
                            it.isNotBlank() &&
                                    (it.toDoubleOrNull() ?: 0.0) <= 0
                        }) {
                        validationError = "Hook sizes must be positive"
                    } else {
                        validationError = null

                        viewModel.createProject(
                            CreateProjectRequest(
                                title = title.trim(),
                                description = description.trim()
                                    .takeIf { it.isNotEmpty() },
                                category = category.trim()
                                    .takeIf { it.isNotEmpty() },
                                status = status,
                                startDate = startDate.trim()
                                    .takeIf { it.isNotEmpty() },
                                endDate = endDate.trim()
                                    .takeIf { it.isNotEmpty() },
                                yarns = parsedYarns,
                                hooks = parsedHooks
                            )
                        )
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(54.dp),
                enabled = uiState !is AddProjectUiState.Loading,
                colors = ButtonDefaults.buttonColors(
                    containerColor = HotPink
                ),
                shape = RoundedCornerShape(16.dp)
            ) {
                if (uiState is AddProjectUiState.Loading) {
                    CircularProgressIndicator(
                        color = White,
                        modifier = Modifier.size(22.dp)
                    )
                } else {
                    Text("Create project 🧶")
                }
            }

            Spacer(Modifier.height(20.dp))
        }
    }
}

@Composable
private fun FormSection(title: String) {
    Text(
        title,
        style = MaterialTheme.typography.titleLarge,
        color = Cocoa
    )
}