package com.ryuzora.dangani.presentation.task

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
import com.ryuzora.dangani.presentation.components.ButtonVariant
import com.ryuzora.dangani.presentation.components.CategoryChip
import com.ryuzora.dangani.presentation.components.DanganiButton
import com.ryuzora.dangani.presentation.components.StatusBadge
import com.ryuzora.dangani.presentation.components.TaskProfileCard
import com.ryuzora.dangani.presentation.components.TaskPointsBadge
import com.ryuzora.dangani.presentation.components.VerifiedBadge
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
        containerColor = BackgroundGray,
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
                    containerColor = BackgroundGray,
                    titleContentColor = TextPrimary
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
                    CircularProgressIndicator(color = DanganiBlue)
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
                        color = ErrorRed
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
                        color = TextSecondary
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    // Title
                    Text(
                        text = task.title,
                        style = MaterialTheme.typography.headlineSmall.copy(
                            fontWeight = FontWeight.Bold
                        ),
                        color = TextPrimary
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    // Description
                    Text(
                        text = task.description,
                        style = MaterialTheme.typography.bodyLarge,
                        color = TextSecondary
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
                            color = TextSecondary
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
                        if (false) {
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { onNavigateToProfile(requester.id) },
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(containerColor = CardWhite),
                            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(14.dp)
                            ) {
                                // Avatar
                                if (requester.avatarUrl.isNotBlank()) {
                                    AsyncImage(
                                        model = requester.avatarUrl,
                                        contentDescription = "Requester Avatar",
                                        modifier = Modifier
                                            .size(50.dp)
                                            .clip(CircleShape),
                                        contentScale = ContentScale.Crop
                                    )
                                } else {
                                    Box(
                                        modifier = Modifier
                                            .size(50.dp)
                                            .background(DanganiBlue.copy(alpha = 0.1f), CircleShape),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            text = requester.username.take(1).uppercase(),
                                            style = MaterialTheme.typography.titleLarge.copy(
                                                fontWeight = FontWeight.Bold
                                            ),
                                            color = DanganiBlue
                                        )
                                    }
                                }

                                // Info
                                Column(modifier = Modifier.weight(1f)) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                                    ) {
                                        Text(
                                            text = requester.username,
                                            style = MaterialTheme.typography.titleMedium.copy(
                                                fontWeight = FontWeight.Bold
                                            ),
                                            color = TextPrimary,
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis
                                        )
                                        if (requester.isVerified) {
                                            VerifiedBadge()
                                        }
                                    }
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Filled.Star,
                                            contentDescription = null,
                                            modifier = Modifier.size(14.dp),
                                            tint = androidx.compose.ui.graphics.Color(0xFFFFB300)
                                        )
                                        Text(
                                            text = String.format("%.1f", requester.ratingAverage),
                                            style = MaterialTheme.typography.bodySmall.copy(
                                                fontWeight = FontWeight.SemiBold
                                            ),
                                            color = TextPrimary
                                        )
                                        Text(
                                            text = "•",
                                            style = MaterialTheme.typography.bodySmall,
                                            color = TextSecondary
                                        )
                                        Text(
                                            text = "${requester.tasksUploaded} tugas",
                                            style = MaterialTheme.typography.bodySmall,
                                            color = TextSecondary
                                        )
                                    }
                                }

                                // Arrow
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                                    contentDescription = "View Profile",
                                    tint = TextSecondary,
                                    modifier = Modifier.size(24.dp)
                                )
                            }
                        }
                        }
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
                            color = ErrorRed
                        )
                    }

                    Spacer(modifier = Modifier.height(24.dp))
                }
            }
        }
    }
}
