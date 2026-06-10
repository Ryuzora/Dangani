package com.ryuzora.dangani.presentation.profile

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
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.Assignment
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.Upload
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.ryuzora.dangani.presentation.components.AcademicStandingCard
import com.ryuzora.dangani.presentation.components.AvatarPlaceholder
import com.ryuzora.dangani.presentation.components.ButtonVariant
import com.ryuzora.dangani.presentation.components.DanganiButton
import com.ryuzora.dangani.presentation.components.ProfileStatsCard
import com.ryuzora.dangani.presentation.components.ReviewCard
import com.ryuzora.dangani.presentation.components.VerifiedBadge
import com.ryuzora.dangani.ui.theme.BackgroundGray
import com.ryuzora.dangani.ui.theme.CardWhite
import com.ryuzora.dangani.ui.theme.DanganiBlue
import com.ryuzora.dangani.ui.theme.DividerColor
import com.ryuzora.dangani.ui.theme.ErrorRed
import com.ryuzora.dangani.ui.theme.TextHint
import com.ryuzora.dangani.ui.theme.TextOnPrimary
import com.ryuzora.dangani.ui.theme.TextPrimary
import com.ryuzora.dangani.ui.theme.TextSecondary

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    onNavigateToLogin: () -> Unit,
    viewModel: ProfileViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    // Photo picker launcher
    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let { viewModel.uploadPhoto(it.toString()) }
    }



    // Navigate to login on logout
    LaunchedEffect(uiState.isLoggedOut) {
        if (uiState.isLoggedOut) {
            onNavigateToLogin()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { },
                actions = {
                    IconButton(onClick = { /* TODO: Navigate to settings */ }) {
                        Icon(
                            imageVector = Icons.Filled.Settings,
                            contentDescription = "Pengaturan",
                            tint = TextPrimary
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = BackgroundGray
                )
            )
        },
        containerColor = BackgroundGray
    ) { paddingValues ->
        if (uiState.isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = DanganiBlue)
            }
        } else {
            val user = uiState.user

            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Avatar section
                item {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp)
                    ) {
                        // Avatar with blue ring + camera overlay
                        Box(contentAlignment = Alignment.BottomEnd) {
                            Box(
                                modifier = Modifier
                                    .size(108.dp)
                                    .border(3.dp, DanganiBlue, CircleShape)
                                    .padding(4.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                if (user?.avatarUrl?.isNotBlank() == true) {
                                    AsyncImage(
                                        model = user.avatarUrl,
                                        contentDescription = "Profile Photo",
                                        modifier = Modifier
                                            .size(100.dp)
                                            .clip(CircleShape),
                                        contentScale = ContentScale.Crop
                                    )
                                } else {
                                    AvatarPlaceholder(name = user?.username ?: "?", size = 100.dp)
                                }
                            }

                            // Camera icon overlay
                            Box(
                                modifier = Modifier
                                    .size(32.dp)
                                    .offset(x = (-2).dp, y = (-2).dp)
                                    .background(DanganiBlue, CircleShape)
                                    .clickable { photoPickerLauncher.launch("image/*") },
                                contentAlignment = Alignment.Center
                            ) {
                                if (uiState.isUploadingPhoto) {
                                    CircularProgressIndicator(
                                        modifier = Modifier.size(16.dp),
                                        color = TextOnPrimary,
                                        strokeWidth = 2.dp
                                    )
                                } else {
                                    Icon(
                                        imageVector = Icons.Filled.CameraAlt,
                                        contentDescription = "Ubah foto",
                                        modifier = Modifier.size(16.dp),
                                        tint = TextOnPrimary
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        // Verified badge
                        if (user?.isVerified == true) {
                            VerifiedBadge()
                            Spacer(modifier = Modifier.height(8.dp))
                        }

                        // Username with edit icon
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Text(
                                text = user?.username ?: "User",
                                style = MaterialTheme.typography.headlineSmall.copy(
                                    fontWeight = FontWeight.Bold
                                ),
                                color = TextPrimary
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Icon(
                                imageVector = Icons.Filled.Edit,
                                contentDescription = "Edit username",
                                modifier = Modifier
                                    .size(18.dp)
                                    .clickable { /* TODO: Edit username */ },
                                tint = TextHint
                            )
                        }

                        Spacer(modifier = Modifier.height(4.dp))

                        // Email
                        Text(
                            text = user?.email ?: "",
                            style = MaterialTheme.typography.bodyMedium,
                            color = TextSecondary
                        )

                        Spacer(modifier = Modifier.height(20.dp))

                        // Academic Standing Card
                        AcademicStandingCard(
                            totalPoints = user?.totalPoints ?: 0
                        )

                        Spacer(modifier = Modifier.height(24.dp))
                    }
                }

                // Helper Stats section
                item {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp)
                    ) {
                        Text(
                            text = "Statistik Helper",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold
                            ),
                            color = TextPrimary
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            ProfileStatsCard(
                                icon = Icons.Outlined.CheckCircle,
                                value = "${user?.tasksCompleted ?: 0}",
                                label = "Tugas Selesai",
                                modifier = Modifier.weight(1f)
                            )
                            ProfileStatsCard(
                                icon = Icons.Filled.Star,
                                value = String.format("%.1f", user?.ratingAverage ?: 0.0),
                                label = "Rating Rata-rata",
                                modifier = Modifier.weight(1f)
                            )
                        }

                        Spacer(modifier = Modifier.height(24.dp))
                    }
                }

                // Requester Stats section
                item {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp)
                    ) {
                        Text(
                            text = "Statistik Requester",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold
                            ),
                            color = TextPrimary
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            ProfileStatsCard(
                                icon = Icons.Outlined.Upload,
                                value = "${user?.tasksUploaded ?: 0}",
                                label = "Tugas Diunggah",
                                modifier = Modifier.weight(1f)
                            )
                            ProfileStatsCard(
                                icon = Icons.Outlined.Assignment,
                                value = String.format("%.1f", user?.averageTaskPoints ?: 0.0),
                                label = "Points Rata-rata",
                                modifier = Modifier.weight(1f)
                            )
                        }

                        Spacer(modifier = Modifier.height(24.dp))
                    }
                }

                // Reviews section
                item {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp)
                    ) {
                        Text(
                            text = "Ulasan",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold
                            ),
                            color = TextPrimary
                        )

                        if (uiState.reviews.isEmpty()) {
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(
                                text = "Belum ada ulasan",
                                style = MaterialTheme.typography.bodyMedium,
                                color = TextHint,
                                textAlign = TextAlign.Center,
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                    }
                }

                // Review items
                items(
                    items = uiState.reviews,
                    key = { it.id }
                ) { review ->
                    Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                        ReviewCard(review = review)
                        HorizontalDivider(color = DividerColor)
                    }
                }

                // Action buttons
                item {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp)
                            .padding(top = 24.dp, bottom = 32.dp)
                    ) {
                        // Edit Profile button
                        DanganiButton(
                            text = "Edit Profil",
                            onClick = { /* TODO: Navigate to edit profile */ },
                            variant = ButtonVariant.PRIMARY
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        // Logout button
                        TextButton(
                            onClick = viewModel::logout,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = "Keluar",
                                style = MaterialTheme.typography.labelLarge,
                                color = ErrorRed
                            )
                        }

                        // Error message
                        if (uiState.error != null) {
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = uiState.error!!,
                                style = MaterialTheme.typography.bodySmall,
                                color = ErrorRed,
                                textAlign = TextAlign.Center,
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                    }
                }
            }
        }
    }
}
