package com.ryuzora.dangani.presentation.task

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
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.ryuzora.dangani.domain.model.TaskCategory
import com.ryuzora.dangani.presentation.components.ButtonVariant
import com.ryuzora.dangani.presentation.components.DanganiButton
import com.ryuzora.dangani.presentation.components.DanganiTextField
import com.ryuzora.dangani.presentation.components.TaskPointsSelector
import com.ryuzora.dangani.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateTaskScreen(
    onNavigateBack: () -> Unit,
    onTaskCreated: () -> Unit,
    viewModel: CreateTaskViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(uiState.isCreated) {
        if (uiState.isCreated) {
            onTaskCreated()
        }
    }

    Scaffold(
        containerColor = BackgroundGray,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Create New Task",
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
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp)
        ) {
            Spacer(modifier = Modifier.height(8.dp))

            // TASK TITLE
            Text(
                text = "TASK TITLE",
                style = MaterialTheme.typography.labelMedium.copy(
                    fontWeight = FontWeight.SemiBold
                ),
                color = TextSecondary
            )
            Spacer(modifier = Modifier.height(8.dp))
            DanganiTextField(
                value = uiState.title,
                onValueChange = viewModel::onTitleChange,
                modifier = Modifier.fillMaxWidth(),
                placeholder = "Masukkan judul tugas",
                singleLine = true,
                isError = uiState.titleError != null,
                errorMessage = uiState.titleError
            )

            Spacer(modifier = Modifier.height(16.dp))

            // DESCRIPTION
            Text(
                text = "DESCRIPTION",
                style = MaterialTheme.typography.labelMedium.copy(
                    fontWeight = FontWeight.SemiBold
                ),
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
                singleLine = false,
                maxLines = 5,
                isError = uiState.descriptionError != null,
                errorMessage = uiState.descriptionError
            )

            Spacer(modifier = Modifier.height(16.dp))

            // CATEGORY
            Text(
                text = "CATEGORY",
                style = MaterialTheme.typography.labelMedium.copy(
                    fontWeight = FontWeight.SemiBold
                ),
                color = TextSecondary
            )
            Spacer(modifier = Modifier.height(8.dp))

            val selectableCategories = TaskCategory.entries.filter { it != TaskCategory.ALL }

            ExposedDropdownMenuBox(
                expanded = uiState.isCategoryDropdownExpanded,
                onExpandedChange = { viewModel.onCategoryDropdownToggle() }
            ) {
                DanganiTextField(
                    value = uiState.selectedCategory?.displayName ?: "",
                    onValueChange = {},
                    readOnly = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .menuAnchor(),
                    placeholder = "Pilih kategori",
                    trailingIcon = {
                        ExposedDropdownMenuDefaults.TrailingIcon(expanded = uiState.isCategoryDropdownExpanded)
                    }
                )

                ExposedDropdownMenu(
                    expanded = uiState.isCategoryDropdownExpanded,
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

            Spacer(modifier = Modifier.height(20.dp))

            // Info card
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        color = DanganiLightBlue,
                        shape = RoundedCornerShape(12.dp)
                    )
                    .padding(14.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalAlignment = Alignment.Top
            ) {
                Icon(
                    imageVector = Icons.Filled.Info,
                    contentDescription = null,
                    modifier = Modifier.size(20.dp),
                    tint = DanganiBlue
                )
                Text(
                    text = "Verified Verification — Pastikan tugas yang dibuat sesuai dengan ketentuan kampus. Tugas akan ditinjau sebelum dipublikasikan.",
                    style = MaterialTheme.typography.bodySmall,
                    color = DanganiBlue
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // TASK POINTS
            Text(
                text = "TASK POINTS",
                style = MaterialTheme.typography.labelMedium.copy(
                    fontWeight = FontWeight.SemiBold
                ),
                color = TextSecondary
            )
            Spacer(modifier = Modifier.height(12.dp))

            TaskPointsSelector(
                selectedPoints = uiState.selectedPoints,
                onPointsSelected = viewModel::onPointsSelected
            )

            // Error message
            if (uiState.error != null) {
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = uiState.error!!,
                    style = MaterialTheme.typography.bodySmall,
                    color = ErrorRed
                )
            }

            Spacer(modifier = Modifier.height(28.dp))

            // Publish button
            DanganiButton(
                text = "Publish Task ▷",
                onClick = { viewModel.publishTask() },
                variant = ButtonVariant.PRIMARY,
                enabled = !uiState.isPublishing
            )

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}
