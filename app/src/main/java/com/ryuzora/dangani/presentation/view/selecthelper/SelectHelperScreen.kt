package com.ryuzora.dangani.presentation.view.selecthelper

import com.ryuzora.dangani.presentation.viewmodel.selecthelper.*

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.ryuzora.dangani.presentation.view.components.CategoryChip
import com.ryuzora.dangani.presentation.view.components.HelperCard
import com.ryuzora.dangani.presentation.view.components.TaskPointsBadge
import com.ryuzora.dangani.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SelectHelperScreen(
    taskId: String,
    onNavigateBack: () -> Unit,
    onNavigateToProfile: (String) -> Unit,
    viewModel: SelectHelperViewModel = remember { SelectHelperViewModel(taskId) }
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Select Helper",
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
            uiState.isLoading && uiState.task == null -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
                }
            }
            else -> {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding),
                    contentPadding = PaddingValues(horizontal = 20.dp, vertical = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Task info section
                    uiState.task?.let { task ->
                        item {
                            Column {
                                // Category badge
                                CategoryChip(
                                    text = task.category.displayName,
                                    isSelected = true,
                                    onClick = {}
                                )

                                Spacer(modifier = Modifier.height(8.dp))

                                // Posted time
                                Text(
                                    text = "Posted ${task.timeAgo}",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )

                                Spacer(modifier = Modifier.height(10.dp))

                                // Title
                                Text(
                                    text = task.title,
                                    style = MaterialTheme.typography.headlineSmall.copy(
                                        fontWeight = FontWeight.Bold
                                    ),
                                    color = MaterialTheme.colorScheme.onBackground
                                )

                                Spacer(modifier = Modifier.height(8.dp))

                                // Description
                                Text(
                                    text = task.description,
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )

                                Spacer(modifier = Modifier.height(20.dp))

                                // Large points badge centered
                                Box(
                                    modifier = Modifier.fillMaxWidth(),
                                    contentAlignment = Alignment.Center
                                ) {
                                    TaskPointsBadge(
                                        points = task.taskPoints,
                                        isLarge = true
                                    )
                                }

                                Spacer(modifier = Modifier.height(20.dp))

                                HorizontalDivider(color = MaterialTheme.colorScheme.outline, thickness = 1.dp)

                                Spacer(modifier = Modifier.height(16.dp))

                                // Interested Helpers heading
                                val verifiedCount = uiState.applicants.count { true }
                                Text(
                                    text = "Interested Helpers — ${uiState.applicants.size} verified students",
                                    style = MaterialTheme.typography.titleMedium.copy(
                                        fontWeight = FontWeight.SemiBold
                                    ),
                                    color = MaterialTheme.colorScheme.onBackground
                                )
                            }
                        }
                    }

                    // Helper cards
                    if (uiState.applicants.isEmpty() && !uiState.isLoading) {
                        item {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 40.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "Belum ada yang melamar",
                                    style = MaterialTheme.typography.bodyLarge,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    } else {
                        items(
                            items = uiState.applicants,
                            key = { it.id }
                        ) { application ->
                            HelperCard(
                                application = application,
                                onViewProfile = { onNavigateToProfile(application.helperId) },
                                onAcceptHelper = { viewModel.acceptHelper(application.helperId) },
                                isAccepted = uiState.acceptedHelperId == application.helperId
                            )
                        }
                    }

                    // Error
                    if (uiState.error != null) {
                        item {
                            Text(
                                text = uiState.error!!,
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.error,
                                modifier = Modifier.padding(vertical = 8.dp)
                            )
                        }
                    }

                    // Bottom spacer
                    item {
                        Spacer(modifier = Modifier.height(24.dp))
                    }
                }
            }
        }
    }
}






