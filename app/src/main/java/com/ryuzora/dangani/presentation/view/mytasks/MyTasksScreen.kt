package com.ryuzora.dangani.presentation.view.mytasks

import com.ryuzora.dangani.presentation.viewmodel.mytasks.*

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Assignment
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.ryuzora.dangani.presentation.view.components.MyTaskCard
import com.ryuzora.dangani.presentation.view.components.RoleTabSelector

@Composable
fun MyTasksScreen(
    onTaskClick: (String) -> Unit,
    onHelperTaskClick: (String) -> Unit,
    viewModel: MyTasksViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // Header
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "Tugas Saya",
            style = MaterialTheme.typography.headlineMedium.copy(
                fontWeight = FontWeight.Bold
            ),
            color = MaterialTheme.colorScheme.onBackground,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 12.dp)
        )

        // Role Tab Selector
        RoleTabSelector(
            selectedTab = uiState.selectedTab,
            onTabSelected = viewModel::onTabSelected
        )

        // Content
        if (uiState.isLoading) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
            }
        } else {
            val currentTasks = if (uiState.selectedTab == 0) {
                uiState.requesterTasks
            } else {
                uiState.helperTasks
            }
            val isRequesterView = uiState.selectedTab == 0

            if (currentTasks.isEmpty()) {
                // Empty state
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Assignment,
                            contentDescription = null,
                            modifier = Modifier.size(64.dp),
                            tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = if (isRequesterView) {
                                "Belum ada tugas yang dibuat"
                            } else {
                                "Belum ada tugas yang dikerjakan"
                            },
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f),
                            textAlign = TextAlign.Center
                        )
                    }
                }
            } else {
                LazyColumn(
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(
                        items = currentTasks,
                        key = { it.id }
                    ) { task ->
                        MyTaskCard(
                            task = task,
                            isRequesterView = isRequesterView,
                            onClick = {
                                if (isRequesterView) {
                                    onTaskClick(task.id)
                                } else {
                                    onHelperTaskClick(task.id)
                                }
                            }
                        )
                    }
                }
            }
        }
    }
}







