package com.ryuzora.dangani.presentation.view.task

import com.ryuzora.dangani.presentation.viewmodel.task.*

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
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.InsertDriveFile
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.ryuzora.dangani.domain.model.TaskCategory
import com.ryuzora.dangani.domain.model.TaskStatus
import com.ryuzora.dangani.presentation.view.components.ButtonVariant
import com.ryuzora.dangani.presentation.view.components.DanganiButton
import com.ryuzora.dangani.presentation.view.components.DanganiTextField
import com.ryuzora.dangani.presentation.view.components.StatusBadge
import com.ryuzora.dangani.presentation.view.components.TaskProfileCard
import com.ryuzora.dangani.presentation.view.components.TaskPointsSelector
import com.ryuzora.dangani.presentation.view.components.VerifiedBadge
import com.ryuzora.dangani.ui.theme.*
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.Star
import androidx.compose.material3.OutlinedTextField
import androidx.compose.ui.unit.sp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditTaskScreen(
    taskId: String,
    onNavigateBack: () -> Unit,
    onNavigateToSelectHelper: () -> Unit,
    onNavigateToProfile: (String) -> Unit,
    viewModel: EditTaskViewModel = remember { EditTaskViewModel(taskId) }
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current

    LaunchedEffect(uiState.isDeleted, uiState.isSaved) {
        if (uiState.isDeleted || uiState.isSaved) {
            onNavigateBack()
        }
    }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Edit Task",
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
            uiState.task == null -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = uiState.error ?: "Tugas tidak ditemukan",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.error
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

                    // Status badge
                    StatusBadge(status = task.status)

                    Spacer(modifier = Modifier.height(16.dp))

                    // TASK TITLE
                    Text(
                        text = "TASK TITLE",
                        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.SemiBold),
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    DanganiTextField(
                        value = uiState.title,
                        onValueChange = viewModel::onTitleChange,
                        modifier = Modifier.fillMaxWidth(),
                        placeholder = "Masukkan judul tugas",
                        readOnly = !uiState.isEditable,
                        singleLine = true
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // DESCRIPTION
                    Text(
                        text = "DESCRIPTION",
                        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.SemiBold),
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    DanganiTextField(
                        value = uiState.description,
                        onValueChange = viewModel::onDescriptionChange,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(140.dp),
                        placeholder = "Jelaskan detail tugas yang perlu dikerjakan...",
                        readOnly = !uiState.isEditable,
                        singleLine = false,
                        maxLines = 5
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // CATEGORY
                    Text(
                        text = "CATEGORY",
                        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.SemiBold),
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    val selectableCategories = TaskCategory.getSelectableCategories()

                    ExposedDropdownMenuBox(
                        expanded = uiState.isCategoryDropdownExpanded && uiState.isEditable,
                        onExpandedChange = {
                            if (uiState.isEditable) viewModel.onCategoryDropdownToggle()
                        }
                    ) {
                        DanganiTextField(
                            value = uiState.selectedCategory?.displayName ?: "",
                            onValueChange = {},
                            readOnly = true,
                            modifier = Modifier
                                .fillMaxWidth()
                                .menuAnchor(),
                            placeholder = "Pilih kategori",
                            trailingIcon = if (uiState.isEditable) {
                                { ExposedDropdownMenuDefaults.TrailingIcon(expanded = uiState.isCategoryDropdownExpanded) }
                            } else null
                        )

                        ExposedDropdownMenu(
                            expanded = uiState.isCategoryDropdownExpanded && uiState.isEditable,
                            onDismissRequest = { viewModel.onCategoryDropdownDismiss() }
                        ) {
                            selectableCategories.forEach { category ->
                                DropdownMenuItem(
                                    text = {
                                        Text(
                                            text = category.displayName,
                                            style = MaterialTheme.typography.bodyMedium,
                                            color = MaterialTheme.colorScheme.onBackground
                                        )
                                    },
                                    onClick = { viewModel.onCategorySelected(category) }
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // TASK POINTS
                    Text(
                        text = "TASK POINTS",
                        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.SemiBold),
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    TaskPointsSelector(
                        selectedPoints = uiState.selectedPoints,
                        onPointsSelected = viewModel::onPointsSelected,
                        enabled = uiState.isEditable
                    )
                    Spacer(modifier = Modifier.height(16.dp))

                    // Helper section (when helper is assigned)
                    val helper = uiState.helper
                    if (helper != null && task.helperId.isNotBlank()) {
                        HorizontalDivider(color = MaterialTheme.colorScheme.outline, thickness = 1.dp)
                        Spacer(modifier = Modifier.height(16.dp))

                        Text(
                            text = "HELPER",
                            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.SemiBold),
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(10.dp))

                        TaskProfileCard(
                            name = helper.username,
                            avatarUrl = helper.avatarUrl,
                            rating = helper.ratingAverage,
                            statsText = "${helper.tasksCompleted} tugas selesai",
                            contentDescription = "Helper Avatar",
                            isVerified = helper.isVerified,
                            onClick = { onNavigateToProfile(helper.id) }
                        )
                        if (false) {
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { onNavigateToProfile(helper.id) },
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(14.dp)
                            ) {
                                if (helper.avatarUrl.isNotBlank()) {
                                    AsyncImage(
                                        model = helper.avatarUrl,
                                        contentDescription = "Helper Avatar",
                                        modifier = Modifier
                                            .size(48.dp)
                                            .clip(CircleShape),
                                        contentScale = ContentScale.Crop
                                    )
                                } else {
                                    Box(
                                        modifier = Modifier
                                            .size(48.dp)
                                            .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.1f), CircleShape),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            text = helper.username.take(1).uppercase(),
                                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                            color = MaterialTheme.colorScheme.primary
                                        )
                                    }
                                }

                                Column(modifier = Modifier.weight(1f)) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                                    ) {
                                        Text(
                                            text = helper.username,
                                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                            color = MaterialTheme.colorScheme.onBackground,
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis
                                        )
                                        if (helper.isVerified) {
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
                                            text = String.format("%.1f", helper.ratingAverage),
                                            style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.SemiBold),
                                            color = MaterialTheme.colorScheme.onBackground
                                        )
                                        Text("•", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                        Text(
                                            text = "${helper.tasksCompleted} tugas selesai",
                                            style = MaterialTheme.typography.bodySmall,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                }

                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                                    contentDescription = "View Profile",
                                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                    modifier = Modifier.size(24.dp)
                                )
                            }
                        }
                        }
                        Spacer(modifier = Modifier.height(16.dp))
                    }

                    // Proof of Work section (when proof submitted)
                    if (uiState.proofSubmitted) {
                        HorizontalDivider(color = MaterialTheme.colorScheme.outline, thickness = 1.dp)
                        Spacer(modifier = Modifier.height(16.dp))

                        Text(
                            text = "PROOF OF WORK",
                            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.SemiBold),
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(10.dp))

                        val isImage = task.proofOfWorkUrl.lowercase().let { 
                            it.contains(".jpg") || it.contains(".jpeg") || 
                            it.contains(".png") || it.contains(".webp") || it.contains(".gif")
                        }

                        if (isImage) {
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(200.dp)
                                    .clickable {
                                        val intent = android.content.Intent(
                                            android.content.Intent.ACTION_VIEW,
                                            android.net.Uri.parse(task.proofOfWorkUrl)
                                        )
                                        context.startActivity(intent)
                                    },
                                shape = RoundedCornerShape(12.dp),
                                elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
                            ) {
                                coil.compose.AsyncImage(
                                    model = task.proofOfWorkUrl,
                                    contentDescription = "Proof of Work",
                                    modifier = Modifier.fillMaxSize(),
                                    contentScale = ContentScale.Crop
                                )
                            }
                        } else {
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        val intent = android.content.Intent(
                                            android.content.Intent.ACTION_VIEW,
                                            android.net.Uri.parse(task.proofOfWorkUrl)
                                        )
                                        context.startActivity(intent)
                                    },
                                shape = RoundedCornerShape(12.dp),
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f)),
                                elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(16.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Outlined.InsertDriveFile,
                                        contentDescription = null,
                                        modifier = Modifier.size(32.dp),
                                        tint = MaterialTheme.colorScheme.primary
                                    )
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = "File Bukti Pengerjaan",
                                            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Medium),
                                            color = MaterialTheme.colorScheme.primary
                                        )
                                        Text(
                                            text = "Tap untuk membuka file",
                                            style = MaterialTheme.typography.bodySmall,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                    Icon(
                                        imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                                        contentDescription = "Buka",
                                        tint = MaterialTheme.colorScheme.primary,
                                        modifier = Modifier.size(24.dp)
                                    )
                                }
                            }
                        }
                        Spacer(modifier = Modifier.height(16.dp))
                    }

                    // Error message
                    if (uiState.error != null) {
                        Text(
                            text = uiState.error!!,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.error
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                    }

// Dynamic buttons based on task state
                    when (task.status) {
                        TaskStatus.NEED_REVIEW -> {
                            DanganiButton(
                                text = "Accept Work",
                                onClick = { viewModel.acceptWork() },
                                variant = ButtonVariant.PRIMARY,
                                enabled = !uiState.isSaving
                            )

                            Spacer(modifier = Modifier.height(10.dp))

                            DanganiButton(
                                text = "Ask Revision",
                                onClick = { viewModel.requestRevision() },
                                variant = ButtonVariant.DANGER,
                                enabled = !uiState.isSaving
                            )
                        }

                        TaskStatus.ACCEPTED -> {
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(12.dp),
                                colors = CardDefaults.cardColors(
                                    containerColor = Color(0xFFE8F5E9)
                                ),
                                elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
                            ) {
                                Text(
                                    text = "Tugas sudah selesai dan diterima. Tugas ini tidak bisa diubah lagi.",
                                    modifier = Modifier.padding(16.dp),
                                    style = MaterialTheme.typography.bodyMedium.copy(
                                        fontWeight = FontWeight.SemiBold
                                    ),
                                    color = Color(0xFF2E7D32)
                                )
                            }

                            Spacer(modifier = Modifier.height(16.dp))

                            if (uiState.existingReview != null) {
                                val review = uiState.existingReview

                                Card(
                                    modifier = Modifier.fillMaxWidth(),
                                    shape = RoundedCornerShape(12.dp),
                                    colors = CardDefaults.cardColors(
                                        containerColor = Color(0xFFE3F2FD)
                                    ),
                                    elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
                                ) {
                                    Column(
                                        modifier = Modifier.padding(16.dp)
                                    ) {
                                        Text(
                                            text = "Ulasan untuk Helper",
                                            style = MaterialTheme.typography.titleMedium.copy(
                                                fontWeight = FontWeight.Bold
                                            ),
                                            color = Color(0xFF0D47A1)
                                        )

                                        Spacer(modifier = Modifier.height(8.dp))

                                        Text(
                                            text = "Rating: ${review?.rating ?: 0}/5",
                                            style = MaterialTheme.typography.bodyMedium,
                                            color = MaterialTheme.colorScheme.onBackground
                                        )

                                        Spacer(modifier = Modifier.height(6.dp))

                                        Text(
                                            text = review?.comment ?: "",
                                            style = MaterialTheme.typography.bodyMedium,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                }
                            } else {
                                HelperReviewForm(
                                    rating = uiState.reviewRating,
                                    comment = uiState.reviewComment,
                                    isSubmitting = uiState.isReviewSubmitting,
                                    onRatingChange = viewModel::onReviewRatingChange,
                                    onCommentChange = viewModel::onReviewCommentChange,
                                    onSubmit = { viewModel.submitReview() }
                                )
                            }
                        }

                        TaskStatus.UNASSIGNED,
                        TaskStatus.IN_PROGRESS,
                        TaskStatus.REVISION -> {
                            if (uiState.isEditable) {
                                DanganiButton(
                                    text = "Confirm Changes",
                                    onClick = { viewModel.saveTask() },
                                    variant = ButtonVariant.PRIMARY,
                                    enabled = !uiState.isSaving && !uiState.isDeleting
                                )

                                Spacer(modifier = Modifier.height(10.dp))

                                DanganiButton(
                                    text = "Delete Task",
                                    onClick = { viewModel.deleteTask() },
                                    variant = ButtonVariant.DANGER,
                                    enabled = !uiState.isSaving && !uiState.isDeleting
                                )

                                if (task.helperId.isBlank() && task.applicantCount > 0) {
                                    Spacer(modifier = Modifier.height(10.dp))

                                    DanganiButton(
                                        text = "Select Helper (${task.applicantCount})",
                                        onClick = onNavigateToSelectHelper,
                                        variant = ButtonVariant.SECONDARY
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))
                }
            }
        }
    }
}

@Composable
private fun HelperReviewForm(
    rating: Int,
    comment: String,
    isSubmitting: Boolean,
    onRatingChange: (Int) -> Unit,
    onCommentChange: (String) -> Unit,
    onSubmit: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "Beri Ulasan untuk Helper",
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold
                ),
                color = MaterialTheme.colorScheme.onBackground
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Rating",
                style = MaterialTheme.typography.bodySmall.copy(
                    fontWeight = FontWeight.SemiBold
                ),
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                for (star in 1..5) {
                    Text(
                        text = if (star <= rating) "★" else "☆",
                        fontSize = 34.sp,
                        color = Color(0xFFFFC107),
                        modifier = Modifier.clickable(enabled = !isSubmitting) {
                            onRatingChange(star)
                        }
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            OutlinedTextField(
                value = comment,
                onValueChange = onCommentChange,
                modifier = Modifier.fillMaxWidth(),
                enabled = !isSubmitting,
                minLines = 3,
                label = {
                    Text("Komentar")
                },
                placeholder = {
                    Text("Tulis ulasan untuk helper...")
                }
            )

            Spacer(modifier = Modifier.height(14.dp))

            DanganiButton(
                text = if (isSubmitting) "Mengirim..." else "Kirim Ulasan",
                onClick = onSubmit,
                variant = ButtonVariant.PRIMARY,
                enabled = !isSubmitting && comment.isNotBlank()
            )
        }
    }
}






