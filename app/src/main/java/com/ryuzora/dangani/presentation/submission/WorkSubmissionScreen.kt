package com.ryuzora.dangani.presentation.submission

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.outlined.Link
import androidx.compose.material.icons.outlined.OpenInNew
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.ryuzora.dangani.presentation.components.ButtonVariant
import com.ryuzora.dangani.presentation.components.CategoryChip
import com.ryuzora.dangani.presentation.components.DanganiButton
import com.ryuzora.dangani.presentation.components.StatusBadge
import com.ryuzora.dangani.presentation.components.TaskPointsBadge
import com.ryuzora.dangani.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WorkSubmissionScreen(
    taskId: String,
    onNavigateBack: () -> Unit,
    viewModel: WorkSubmissionViewModel = remember { WorkSubmissionViewModel(taskId) }
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current

    LaunchedEffect(uiState.isSubmitted) {
        if (uiState.isSubmitted) {
            onNavigateBack()
        }
    }

    Scaffold(
        containerColor = BackgroundGray,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Work Submission",
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
            uiState.task == null -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Tugas tidak ditemukan",
                        style = MaterialTheme.typography.bodyLarge,
                        color = ErrorRed
                    )
                }
            }
            else -> {
                val task = uiState.task!!

                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                        .verticalScroll(rememberScrollState())
                        .padding(horizontal = 20.dp)
                ) {
                    Spacer(modifier = Modifier.height(8.dp))

                    // Task title + status badge
                    Text(
                        text = task.title,
                        style = MaterialTheme.typography.headlineSmall.copy(
                            fontWeight = FontWeight.Bold
                        ),
                        color = TextPrimary
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    StatusBadge(status = task.status)

                    Spacer(modifier = Modifier.height(20.dp))

                    // Task info card
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = CardWhite),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp)
                        ) {
                            // Category
                            CategoryChip(
                                text = task.category.displayName,
                                isSelected = true,
                                onClick = {}
                            )

                            Spacer(modifier = Modifier.height(12.dp))

                            // Description
                            Text(
                                text = task.description,
                                style = MaterialTheme.typography.bodyMedium,
                                color = TextSecondary
                            )

                            Spacer(modifier = Modifier.height(14.dp))

                            // Points
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                TaskPointsBadge(points = task.taskPoints)
                                Text(
                                    text = "Task Points",
                                    style = MaterialTheme.typography.bodySmall.copy(
                                        fontWeight = FontWeight.Medium
                                    ),
                                    color = TextSecondary
                                )
                            }

                            Spacer(modifier = Modifier.height(12.dp))

                            // Assigned by
                            Text(
                                text = "Assigned by ${task.requesterName}",
                                style = MaterialTheme.typography.bodySmall,
                                color = TextHint
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    // Google Drive link section
                    Text(
                        text = "LINK BUKTI PENGERJAAN",
                        style = MaterialTheme.typography.labelMedium.copy(
                            fontWeight = FontWeight.SemiBold
                        ),
                        color = TextSecondary
                    )
                    Spacer(modifier = Modifier.height(10.dp))

                    // Drive link input field
                    OutlinedTextField(
                        value = uiState.driveLink,
                        onValueChange = viewModel::onDriveLinkChanged,
                        modifier = Modifier.fillMaxWidth(),
                        placeholder = {
                            Text(
                                text = "Paste link Google Drive di sini...",
                                style = MaterialTheme.typography.bodyMedium,
                                color = TextHint
                            )
                        },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Outlined.Link,
                                contentDescription = null,
                                tint = DanganiBlue
                            )
                        },
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = DanganiBlue,
                            unfocusedBorderColor = DividerColor,
                            focusedContainerColor = CardWhite,
                            unfocusedContainerColor = CardWhite,
                            cursorColor = DanganiBlue
                        ),
                        singleLine = true
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    // Open Google Drive button
                    TextButton(
                        onClick = {
                            val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://drive.google.com"))
                            context.startActivity(intent)
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.OpenInNew,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp),
                            tint = DanganiBlue
                        )
                        Spacer(modifier = Modifier.size(6.dp))
                        Text(
                            text = "Buka Google Drive",
                            style = MaterialTheme.typography.bodySmall.copy(
                                fontWeight = FontWeight.Medium
                            ),
                            color = DanganiBlue
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Info notice
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(
                                color = DanganiLightBlue.copy(alpha = 0.3f),
                                shape = RoundedCornerShape(12.dp)
                            )
                            .padding(14.dp),
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        verticalAlignment = Alignment.Top
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Link,
                            contentDescription = null,
                            modifier = Modifier.size(20.dp),
                            tint = DanganiBlue
                        )
                        Text(
                            text = "Upload file hasil kerja ke Google Drive, lalu paste link share-nya di sini. Pastikan akses link diatur ke \"Anyone with the link\".",
                            style = MaterialTheme.typography.bodySmall,
                            color = TextPrimary
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Warning notice
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(
                                color = androidx.compose.ui.graphics.Color(0xFFFFEBEE),
                                shape = RoundedCornerShape(12.dp)
                            )
                            .padding(14.dp),
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        verticalAlignment = Alignment.Top
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Warning,
                            contentDescription = null,
                            modifier = Modifier.size(20.dp),
                            tint = ErrorRed
                        )
                        Text(
                            text = "Pastikan pekerjaan sudah selesai dan sesuai dengan deskripsi tugas sebelum mengirim. Revisi mungkin diminta oleh requester.",
                            style = MaterialTheme.typography.bodySmall,
                            color = TextPrimary
                        )
                    }

                    // Error message
                    if (uiState.error != null) {
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = uiState.error!!,
                            style = MaterialTheme.typography.bodySmall,
                            color = ErrorRed
                        )
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    // Mark as Completed button
                    DanganiButton(
                        text = if (uiState.isUploading) "Mengirim..." else "Mark as Completed",
                        onClick = { viewModel.submitWork() },
                        variant = ButtonVariant.CORAL,
                        enabled = !uiState.isUploading && uiState.driveLink.isNotBlank()
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    // Cancel Task button
                    DanganiButton(
                        text = "Cancel Task",
                        onClick = { viewModel.cancelTask() },
                        variant = ButtonVariant.DANGER,
                        enabled = !uiState.isUploading
                    )

                    Spacer(modifier = Modifier.height(24.dp))
                }
            }
        }
    }
}
