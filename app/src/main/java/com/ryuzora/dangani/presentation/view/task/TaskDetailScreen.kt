package com.ryuzora.dangani.presentation.view.task

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.ryuzora.dangani.presentation.view.components.ButtonVariant
import com.ryuzora.dangani.presentation.view.components.CategoryChip
import com.ryuzora.dangani.presentation.view.components.DanganiButton
import com.ryuzora.dangani.presentation.view.components.StatusBadge
import com.ryuzora.dangani.presentation.view.components.TaskProfileCard
import com.ryuzora.dangani.presentation.view.components.TaskPointsBadge
import com.ryuzora.dangani.presentation.view.components.VerifiedBadge
import com.ryuzora.dangani.presentation.viewmodel.task.TaskDetailViewModel
import com.ryuzora.dangani.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TaskDetailScreen(
    taskId: String,
    onNavigateBack: () -> Unit,
    onNavigateToProfile: (String) -> Unit,
    onNavigateToEditTask: (String) -> Unit,
    onNavigateToWorkSubmission: (String) -> Unit,
    viewModel: TaskDetailViewModel = remember { TaskDetailViewModel(taskId) }
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Task Detail",
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Bold
                        )
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background,
                    titleContentColor = MaterialTheme.colorScheme.onBackground
                )
            )
        }
    ) { innerPadding ->
        when {
            uiState.isLoading -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
                }
            }
            uiState.error != null && uiState.task == null -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = uiState.error ?: "Terjadi kesalahan",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.error
                    )
                }
            }
            uiState.task != null -> {
                val task = uiState.task!!
                val requester = uiState.requester

                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                        .verticalScroll(rememberScrollState())
                        .padding(horizontal = 20.dp)
                ) {
                    Spacer(modifier = Modifier.height(8.dp))

                    // Category + Status row
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        CategoryChip(
                            text = task.category.displayName,
                            isSelected = true,
                            onClick = {}
                        )
                        StatusBadge(status = task.status)
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Posted time ago
                    Text(
                        text = "Posted ${task.timeAgo}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    // Title
                    Text(
                        text = task.title,
                        style = MaterialTheme.typography.headlineSmall.copy(
                            fontWeight = FontWeight.Bold
                        ),
                        color = MaterialTheme.colorScheme.onBackground
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    // Description
                    Text(
                        text = task.description,
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Spacer(modifier = Modifier.height(28.dp))

                    // Large Task Points Badge (centered)
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        TaskPointsBadge(
                            points = task.taskPoints,
                            isLarge = true
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "TASK POINTS",
                            style = MaterialTheme.typography.labelMedium.copy(
                                fontWeight = FontWeight.SemiBold
                            ),
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    Spacer(modifier = Modifier.height(28.dp))

                    // Requester profile card
                    if (requester != null) {
                        TaskProfileCard(
                            name = requester.username,
                            avatarUrl = requester.avatarUrl,
                            rating = requester.ratingAverage,
                            statsText = "${requester.tasksUploaded} tugas",
                            contentDescription = "Requester Avatar",
                            isVerified = requester.isVerified,
                            onClick = { onNavigateToProfile(requester.id) }
                        )
                    }

                    Spacer(modifier = Modifier.height(28.dp))

                    // Action button
                    if (uiState.isCurrentUserRequester) {
                        DanganiButton(
                            text = "Edit Task",
                            onClick = { onNavigateToEditTask(taskId) },
                            variant = ButtonVariant.PRIMARY,
                            icon = Icons.Filled.Edit
                        )
                    } else if (uiState.isCurrentUserHelper && task.status == com.ryuzora.dangani.domain.model.TaskStatus.IN_PROGRESS) {
                        // If current user is the assigned helper and task is in progress, show submit work
                        DanganiButton(
                            text = "Submit Work",
                            onClick = { onNavigateToWorkSubmission(taskId) },
                            variant = ButtonVariant.PRIMARY,
                            icon = Icons.Filled.Check
                        )
                    } else {
                        DanganiButton(
                            text = if (uiState.hasApplied) "Already Applied" else "✓ Apply To This Task",
                            onClick = { viewModel.applyToTask() },
                            variant = ButtonVariant.PRIMARY,
                            enabled = !uiState.hasApplied
                        )
                    }
                    
                    if (uiState.error != null) {
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = uiState.error!!,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.error
                        )
                    }

                    Spacer(modifier = Modifier.height(24.dp))
                }
            }
        }
    }
}
