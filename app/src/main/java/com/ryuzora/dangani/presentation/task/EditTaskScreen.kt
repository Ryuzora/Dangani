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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.ryuzora.dangani.domain.model.TaskCategory
import com.ryuzora.dangani.presentation.components.ButtonVariant
import com.ryuzora.dangani.presentation.components.DanganiButton
import com.ryuzora.dangani.presentation.components.DanganiTextField
import com.ryuzora.dangani.presentation.components.StatusBadge
import com.ryuzora.dangani.presentation.components.TaskPointsSelector
import com.ryuzora.dangani.presentation.components.VerifiedBadge
import com.ryuzora.dangani.ui.theme.*

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
        containerColor = BackgroundGray,
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
                        text = uiState.error ?: "Tugas tidak ditemukan",
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

                    // Status badge
                    StatusBadge(status = task.status)

                    Spacer(modifier = Modifier.height(16.dp))

                    // TASK TITLE
                    Text(
                        text = "TASK TITLE",
                        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.SemiBold),
                        color = TextSecondary
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
                        color = TextSecondary
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
                        color = TextSecondary
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
                                            color = TextPrimary
                                        )
                                    },
                                    onClick = { viewModel.onCategorySelected(category) }
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // TASK POINTS
                    if (uiState.isEditable) {
                        Text(
                            text = "TASK POINTS",
                            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.SemiBold),
                            color = TextSecondary
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        TaskPointsSelector(
                            selectedPoints = uiState.selectedPoints,
                            onPointsSelected = viewModel::onPointsSelected
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                    }

                    // Helper section (when helper is assigned)
                    val helper = uiState.helper
                    if (helper != null && task.helperId.isNotBlank()) {
                        HorizontalDivider(color = DividerColor, thickness = 1.dp)
                        Spacer(modifier = Modifier.height(16.dp))

                        Text(
                            text = "HELPER",
                            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.SemiBold),
                            color = TextSecondary
                        )
                        Spacer(modifier = Modifier.height(10.dp))

                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { onNavigateToProfile(helper.id) },
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
                                            .background(DanganiBlue.copy(alpha = 0.1f), CircleShape),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            text = helper.username.take(1).uppercase(),
                                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                            color = DanganiBlue
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
                                            color = TextPrimary,
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
                                            color = TextPrimary
                                        )
                                        Text("•", style = MaterialTheme.typography.bodySmall, color = TextSecondary)
                                        Text(
                                            text = "${helper.tasksCompleted} tugas selesai",
                                            style = MaterialTheme.typography.bodySmall,
                                            color = TextSecondary
                                        )
                                    }
                                }

                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                                    contentDescription = "View Profile",
                                    tint = TextSecondary,
                                    modifier = Modifier.size(24.dp)
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(16.dp))
                    }

                    // Proof of Work section (when proof submitted)
                    if (uiState.proofSubmitted) {
                        HorizontalDivider(color = DividerColor, thickness = 1.dp)
                        Spacer(modifier = Modifier.height(16.dp))

                        Text(
                            text = "PROOF OF WORK",
                            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.SemiBold),
                            color = TextSecondary
                        )
                        Spacer(modifier = Modifier.height(10.dp))

                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(containerColor = DanganiLightBlue.copy(alpha = 0.3f)),
                            elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp)
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Outlined.InsertDriveFile,
                                        contentDescription = null,
                                        modifier = Modifier.size(28.dp),
                                        tint = DanganiBlue
                                    )
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = "Bukti pengerjaan dikirim",
                                            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Medium),
                                            color = DanganiBlue
                                        )
                                        Text(
                                            text = task.updatedTimeAgo,
                                            style = MaterialTheme.typography.bodySmall,
                                            color = TextSecondary
                                        )
                                    }
                                }

                                // Show the Google Drive link
                                if (task.proofOfWorkUrl.isNotBlank()) {
                                    Spacer(modifier = Modifier.height(10.dp))
                                    Text(
                                        text = task.proofOfWorkUrl,
                                        style = MaterialTheme.typography.bodySmall,
                                        color = DanganiBlue,
                                        maxLines = 2,
                                        overflow = TextOverflow.Ellipsis,
                                        modifier = Modifier.clickable {
                                            val intent = android.content.Intent(
                                                android.content.Intent.ACTION_VIEW,
                                                android.net.Uri.parse(task.proofOfWorkUrl)
                                            )
                                            context.startActivity(intent)
                                        }
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
                            color = ErrorRed
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                    }

                    // Dynamic buttons based on task state
                    if (uiState.proofSubmitted) {
                        // Proof submitted → Ask Revision + Accept buttons
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
                    } else {
                        // Editable states → Delete + Confirm buttons
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

                        // Select Helper button if task is UNASSIGNED and has applicants
                        if (task.helperId.isBlank() && task.applicantCount > 0) {
                            Spacer(modifier = Modifier.height(10.dp))
                            DanganiButton(
                                text = "Select Helper (${task.applicantCount})",
                                onClick = onNavigateToSelectHelper,
                                variant = ButtonVariant.SECONDARY
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))
                }
            }
        }
    }
}
