package com.project.knotbook.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Color.Companion.White
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.project.knotbook.model.DashboardResponse
import com.project.knotbook.ui.theme.Blush
import com.project.knotbook.ui.theme.ButterYellow
import com.project.knotbook.ui.theme.Cocoa
import com.project.knotbook.ui.theme.Cream
import com.project.knotbook.ui.theme.HotPink
import com.project.knotbook.ui.theme.Lavender
import com.project.knotbook.ui.theme.Mint
import com.project.knotbook.ui.theme.MutedText
import com.project.knotbook.ui.theme.Tangerine
import com.project.knotbook.viewmodel.DashBoardViewModel
import com.project.knotbook.viewmodel.DashboardUiState

@Composable
fun HomeScreen(
    viewModel: DashBoardViewModel = hiltViewModel(),
    onProjectsClick: () -> Unit,
    onAddProjectClick: () -> Unit,
    onProjectClick: (Long) -> Unit
) {
   val uiState by viewModel.uiState.collectAsStateWithLifecycle()


    Scaffold(
        containerColor = Cream,
        bottomBar = {
            NavigationBar( containerColor = White,
                tonalElevation = 4.dp) {
                NavigationBarItem(
                    selected = true,
                    onClick = {},
                    icon = { Text("⌂") },
                    label = { Text("Home") }
                )

                NavigationBarItem(
                    selected = false,
                    onClick = onProjectsClick,
                    icon = { Text("▦") },
                    label = { Text("Projects") }
                )
            }
        }
    ){padding->
        when (val state = uiState){
            is DashboardUiState.Loading -> {
                Box(
                    modifier = Modifier.fillMaxSize().padding(padding),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }
            is DashboardUiState.Error -> {
                Box(
                    modifier = Modifier.fillMaxSize().padding(padding),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(state.message)
                        Spacer(modifier = Modifier.height(12.dp))
                        Button(onClick = viewModel::loadDashboard) {
                            Text("Try again")
                        }
                    }
                }
            }

            is DashboardUiState.Success -> {
                DashboardContent(data = state.data,
                    modifier = Modifier.padding(padding),
                    onAddProjectClick = onAddProjectClick,
                    onProjectClick = onProjectClick,
                    onProjectsClick = onProjectsClick)
            }
        }
    }

}

@Composable
fun DashboardContent(data: DashboardResponse, modifier: Modifier = Modifier,
                     onAddProjectClick: () -> Unit,
                     onProjectClick: (Long) -> Unit,
                     onProjectsClick: () -> Unit){

    val activeProject = data.recentProjects.firstOrNull {
        it.status == "IN_PROGRESS"
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(Cream),
        contentPadding = PaddingValues(
            start = 20.dp,
            end = 20.dp,
            top = 22.dp,
            bottom = 28.dp
        ),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        item {
            WelcomeHeader()
        }

        item {
            if (activeProject != null) {
                FeaturedProjectCard(
                    title = activeProject.title,
                    onClick = { onProjectClick(activeProject.id) }
                )
            } else {
                NewProjectCard(onClick = onAddProjectClick)
            }
        }

        item {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                SectionTitle("Your crochet journey ✨")

                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    StatCard(
                        "Projects",
                        data.totalProjects.toString(),
                        Blush,
                        Modifier.weight(1f)
                    )
                    StatCard(
                        "In progress",
                        data.inProgressProjects.toString(),
                        ButterYellow,
                        Modifier.weight(1f)
                    )
                    StatCard(
                        "Completed",
                        data.completedProjects.toString(),
                        Mint,
                        Modifier.weight(1f)
                    )
                }
            }
        }

        item {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    SectionTitle("Recently updated")
                    Text(
                        "See all →",
                        color = HotPink,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier.clickable(onClick = onProjectsClick)
                    )
                }

                if (data.recentProjects.isEmpty()) {
                    Text(
                        "Your first project is waiting to happen!",
                        color = MutedText
                    )
                } else {
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(data.recentProjects) { project ->
                            RecentProjectCard(
                                title = project.title,
                                status = project.status,
                                minutes = project.totalTimeMinutes,
                                onClick = { onProjectClick(project.id) }
                            )
                        }
                    }
                }
            }
        }

        item {
            CraftingTimeCard(data.totalTimeMinutes)
        }
    }
}

@Composable
private fun WelcomeHeader() {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(5.dp)) {
            Text(
                "YOUR LITTLE CREATIVE CORNER",
                color = MutedText,
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.Bold
            )
            Text(
                "Hello, maker! ",
                color = Cocoa,
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold
            )
            Text(
                "Ready to make something lovely?",
                color = MutedText,
                style = MaterialTheme.typography.bodyMedium
            )
        }

    }
}

@Composable
private fun FeaturedProjectCard(
    title: String,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = HotPink)
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Text(
                "YOUR NEXT LITTLE ADVENTURE",
                color = White.copy(alpha = 0.85f),
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.Bold
            )

            Text(
                "Pick up where you left off",
                color = White,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )

            Text(title, color = White)

            Text("Continue crafting  →", color = White)
        }
    }
}

@Composable
private fun NewProjectCard(onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(containerColor = Tangerine)
    ) {
        Row(
            modifier = Modifier.padding(20.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(5.dp)
            ) {
                Text(
                    "Have a new idea?",
                    color = White,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    "Start a new crochet project",
                    color = White
                )
                Text("+ Add project", color = White)
            }
        }
    }
}

@Composable
private fun StatCard(
    label: String,
    value: String,
    color: Color,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = color)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 18.dp, horizontal = 8.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(5.dp)
        ) {
            Text(
                value,
                color = Cocoa,
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold
            )
            Text(
                label,
                color = Cocoa,
                style = MaterialTheme.typography.labelSmall
            )
        }
    }
}

@Composable
private fun RecentProjectCard(
    title: String,
    status: String,
    minutes: Long,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .width(230.dp)
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(containerColor = Lavender)
    ) {
        Column(
            modifier = Modifier.padding(18.dp),
            verticalArrangement = Arrangement.spacedBy(9.dp)
        ) {
            Text("🧶", style = MaterialTheme.typography.headlineMedium)

            Text(
                title,
                color = Cocoa,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )

            Text(status.replace("_", " "), color = Cocoa)

            Text(
                "$minutes minutes crafted",
                color = MutedText,
                style = MaterialTheme.typography.bodySmall
            )
        }
    }
}

@Composable
private fun CraftingTimeCard(totalMinutes: Long) {
    val hours = totalMinutes / 60
    val minutes = totalMinutes % 60

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(containerColor = Mint)
    ) {
        Row(
            modifier = Modifier.padding(20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    "Time spent creating",
                    color = Cocoa,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    "${hours}h ${minutes}m of creativity",
                    color = Cocoa
                )
            }
            Text("⏰", style = MaterialTheme.typography.headlineLarge)
        }
    }
}

@Composable
private fun SectionTitle(title: String) {
    Text(
        title,
        color = Cocoa,
        style = MaterialTheme.typography.titleLarge,
        fontWeight = FontWeight.Bold
    )
}