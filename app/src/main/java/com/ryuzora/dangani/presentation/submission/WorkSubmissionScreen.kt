package com.ryuzora.dangani.presentation.submission

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.outlined.CloudUpload
import androidx.compose.material.icons.outlined.InsertDriveFile
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import com.ryuzora.dangani.domain.model.TaskStatus
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

    // File picker launcher
    val filePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            val fileName = it.lastPathSegment ?: "file"
            viewModel.onFileSelected(it.toString(), fileName)
        }
    }

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
                val isLocked = task.status == TaskStatus.ACCEPTED ||
                        task.status == TaskStatus.NEED_REVIEW

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
                        shape = RoundedCornerShape(14.dp),
                        colors = CardDefaults.cardColors(containerColor = CardWhite),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                CategoryChip(
                                    text = task.category.displayName,
                                    isSelected = true,
                                    onClick = {}
                                )

                                TaskPointsBadge(points = task.taskPoints)
                            }

                            Spacer(modifier = Modifier.height(14.dp))

                            Text(
                                text = task.description,
                                style = MaterialTheme.typography.bodyMedium,
                                color = TextSecondary
                            )

                            if (task.requesterName.isNotBlank()) {
                                Spacer(modifier = Modifier.height(14.dp))

                                Text(
                                    text = "Assigned by ${task.requesterName}",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = TextHint
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    // Upload section
                    Text(
                        text = "BUKTI PENGERJAAN",
                        style = MaterialTheme.typography.labelMedium.copy(
                            fontWeight = FontWeight.SemiBold
                        ),
                        color = TextSecondary
                    )
                    Spacer(modifier = Modifier.height(10.dp))

                    // File upload area
                    val hasExistingProof = task.proofOfWorkUrl.isNotBlank()
                    val showExisting = uiState.selectedFileName == null && hasExistingProof
                    val isImage = if (showExisting) {
                        task.proofOfWorkUrl.lowercase().let { 
                            it.contains(".jpg") || it.contains(".jpeg") || 
                            it.contains(".png") || it.contains(".webp") || it.contains(".gif")
                        }
                    } else false

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(140.dp)
                            .border(
                                width = 2.dp,
                                color = if (uiState.selectedFileName != null || hasExistingProof) DanganiBlue else DividerColor,
                                shape = RoundedCornerShape(12.dp)
                            )
                            .background(CardWhite, RoundedCornerShape(12.dp))
                            .clip(RoundedCornerShape(12.dp))
                            .clickable(enabled = !isLocked) {
                                filePickerLauncher.launch("*/*")
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        if (showExisting) {
                            if (isImage) {
                                coil.compose.AsyncImage(
                                    model = task.proofOfWorkUrl,
                                    contentDescription = "Uploaded Proof",
                                    modifier = Modifier.fillMaxSize(),
                                    contentScale = androidx.compose.ui.layout.ContentScale.Crop
                                )
                                // Overlay
                                Box(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .background(androidx.compose.ui.graphics.Color.Black.copy(alpha = 0.4f)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                        Icon(Icons.Outlined.CloudUpload, contentDescription = null, tint = androidx.compose.ui.graphics.Color.White)
                                        Text(
                                            text = when (task.status) {
                                                TaskStatus.ACCEPTED -> "Work accepted"
                                                TaskStatus.NEED_REVIEW -> "Waiting for requester"
                                                else -> "Tap to change file"
                                            },
                                            style = MaterialTheme.typography.bodySmall,
                                            color = androidx.compose.ui.graphics.Color.White
                                        )
                                    }
                                }
                            } else {
                                Column(
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    verticalArrangement = Arrangement.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Outlined.InsertDriveFile,
                                        contentDescription = null,
                                        modifier = Modifier.size(36.dp),
                                        tint = DanganiBlue
                                    )
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Text(
                                        text = "File Uploaded",
                                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Medium),
                                        color = TextPrimary
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = when (task.status) {
                                            TaskStatus.ACCEPTED -> "Work accepted"
                                            TaskStatus.NEED_REVIEW -> "Waiting for requester"
                                            else -> "Tap to change file"
                                        },
                                        style = MaterialTheme.typography.bodySmall,
                                        color = TextHint
                                    )
                                }
                            }
                        } else if (uiState.selectedFileName != null) {
                            // Show selected file
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Outlined.InsertDriveFile,
                                    contentDescription = null,
                                    modifier = Modifier.size(36.dp),
                                    tint = DanganiBlue
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = uiState.selectedFileName!!,
                                    style = MaterialTheme.typography.bodyMedium.copy(
                                        fontWeight = FontWeight.Medium
                                    ),
                                    color = TextPrimary,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "Tap untuk mengganti file",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = TextHint
                                )
                            }
                        } else {
                            // Show upload prompt
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Outlined.CloudUpload,
                                    contentDescription = null,
                                    modifier = Modifier.size(40.dp),
                                    tint = DanganiBlue
                                )
                                Spacer(modifier = Modifier.height(10.dp))
                                Text(
                                    text = "Tap untuk memilih file",
                                    style = MaterialTheme.typography.bodyMedium.copy(
                                        fontWeight = FontWeight.Medium
                                    ),
                                    color = DanganiBlue
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "Semua tipe file didukung",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = TextHint
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Warning notice
                    if (task.status != TaskStatus.ACCEPTED && task.status != TaskStatus.NEED_REVIEW) {
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

                    when (task.status) {
                        TaskStatus.ACCEPTED -> {
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(12.dp),
                                colors = CardDefaults.cardColors(
                                    containerColor = androidx.compose.ui.graphics.Color(0xFFE8F5E9)
                                ),
                                elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
                            ) {
                                Text(
                                    text = "Pekerjaan sudah diterima oleh requester. Task ini sudah selesai dan tidak bisa diubah lagi.",
                                    modifier = Modifier.padding(16.dp),
                                    style = MaterialTheme.typography.bodyMedium.copy(
                                        fontWeight = FontWeight.SemiBold
                                    ),
                                    color = androidx.compose.ui.graphics.Color(0xFF2E7D32)
                                )
                            }

                            Spacer(modifier = Modifier.height(16.dp))

                            if (uiState.review != null) {
                                val review = uiState.review

                                Card(
                                    modifier = Modifier.fillMaxWidth(),
                                    shape = RoundedCornerShape(12.dp),
                                    colors = CardDefaults.cardColors(
                                        containerColor = androidx.compose.ui.graphics.Color(0xFFE3F2FD)
                                    ),
                                    elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
                                ) {
                                    Column(
                                        modifier = Modifier.padding(16.dp)
                                    ) {
                                        Text(
                                            text = "Ulasan dari Requester",
                                            style = MaterialTheme.typography.titleMedium.copy(
                                                fontWeight = FontWeight.Bold
                                            ),
                                            color = androidx.compose.ui.graphics.Color(0xFF0D47A1)
                                        )

                                        Spacer(modifier = Modifier.height(8.dp))

                                        Text(
                                            text = "Rating: ${"★".repeat(review?.rating ?: 0)} ${review?.rating ?: 0}/5",
                                            style = MaterialTheme.typography.bodyMedium.copy(
                                                fontWeight = FontWeight.SemiBold
                                            ),
                                            color = TextPrimary
                                        )

                                        Spacer(modifier = Modifier.height(6.dp))

                                        Text(
                                            text = review?.comment ?: "",
                                            style = MaterialTheme.typography.bodyMedium,
                                            color = TextSecondary
                                        )
                                    }
                                }
                            } else {
                                Card(
                                    modifier = Modifier.fillMaxWidth(),
                                    shape = RoundedCornerShape(12.dp),
                                    colors = CardDefaults.cardColors(
                                        containerColor = androidx.compose.ui.graphics.Color(0xFFFFF3E0)
                                    ),
                                    elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
                                ) {
                                    Text(
                                        text = "Belum ada ulasan dari requester.",
                                        modifier = Modifier.padding(16.dp),
                                        style = MaterialTheme.typography.bodyMedium.copy(
                                            fontWeight = FontWeight.SemiBold
                                        ),
                                        color = androidx.compose.ui.graphics.Color(0xFFE65100)
                                    )
                                }
                            }
                        }

                        TaskStatus.NEED_REVIEW -> {
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(12.dp),
                                colors = CardDefaults.cardColors(
                                    containerColor = androidx.compose.ui.graphics.Color(0xFFFFF3E0)
                                ),
                                elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
                            ) {
                                Text(
                                    text = "Bukti pekerjaan sudah dikirim. Menunggu requester menerima pekerjaan atau meminta revisi.",
                                    modifier = Modifier.padding(16.dp),
                                    style = MaterialTheme.typography.bodyMedium.copy(
                                        fontWeight = FontWeight.SemiBold
                                    ),
                                    color = androidx.compose.ui.graphics.Color(0xFFE65100)
                                )
                            }
                        }

                        else -> {
                            DanganiButton(
                                text = if (uiState.isUploading) "Mengunggah..." else "Mark as Completed",
                                onClick = { viewModel.submitWork() },
                                variant = ButtonVariant.CORAL,
                                enabled = !uiState.isUploading && uiState.selectedFileUri != null
                            )

                            Spacer(modifier = Modifier.height(10.dp))

                            DanganiButton(
                                text = "Cancel Task",
                                onClick = { viewModel.cancelTask() },
                                variant = ButtonVariant.DANGER,
                                enabled = !uiState.isUploading
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))
                }
            }
        }
    }
}
