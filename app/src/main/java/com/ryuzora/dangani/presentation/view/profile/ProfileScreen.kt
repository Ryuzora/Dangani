package com.ryuzora.dangani.presentation.view.profile

import com.ryuzora.dangani.presentation.viewmodel.profile.*

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.SupportAgent
import androidx.compose.material3.Switch
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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
import com.ryuzora.dangani.presentation.view.components.AcademicStandingCard
import com.ryuzora.dangani.presentation.view.components.AvatarPlaceholder
import com.ryuzora.dangani.presentation.view.components.ButtonVariant
import com.ryuzora.dangani.presentation.view.components.DanganiButton
import com.ryuzora.dangani.presentation.view.components.DanganiTextField
import com.ryuzora.dangani.presentation.view.components.ProfileStatsCard
import com.ryuzora.dangani.presentation.view.components.ReviewCard
import com.ryuzora.dangani.presentation.view.components.VerifiedBadge
import android.content.Intent
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import com.ryuzora.dangani.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    viewModel: ProfileViewModel = viewModel(),
    onNavigateToLogin: () -> Unit,
    onNavigateToCustomerService: () -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current
    var showAllReviews by remember { mutableStateOf(false) }

    var isEditing by remember { mutableStateOf(false) }
    var editUsername by remember(uiState.user) { mutableStateOf(uiState.user?.username ?: "") }
    var editWhatsapp by remember(uiState.user) { mutableStateOf(uiState.user?.whatsapp ?: "") }
    var editInstagram by remember(uiState.user) { mutableStateOf(uiState.user?.instagram ?: "") }

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
        containerColor = MaterialTheme.colorScheme.background
    ) { paddingValues ->
        if (uiState.isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
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
                        // Top actions
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 8.dp, bottom = 16.dp),
                            horizontalArrangement = Arrangement.End
                        ) {
                            IconButton(onClick = onNavigateToCustomerService) {
                                Icon(
                                    imageVector = Icons.Filled.SupportAgent,
                                    contentDescription = "Customer Service",
                                    tint = MaterialTheme.colorScheme.onBackground
                                )
                            }
                        }

                        val isVerifiedBadge = user?.let { it.tasksCompleted > 15 && it.ratingAverage >= 4.5 } ?: false

                        // Avatar with blue ring and verified badge overlay
                        Box(contentAlignment = Alignment.BottomCenter) {
                            Box(
                                modifier = Modifier
                                    .size(108.dp)
                                    .border(3.dp, MaterialTheme.colorScheme.primary, CircleShape)
                                    .padding(4.dp)
                                    .clip(CircleShape)
                                    .clickable { photoPickerLauncher.launch("image/*") },
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

                                if (uiState.isUploadingPhoto) {
                                    CircularProgressIndicator(
                                        modifier = Modifier.size(24.dp),
                                        color = MaterialTheme.colorScheme.primary,
                                        strokeWidth = 3.dp
                                    )
                                }
                            }

                            // Verified badge overlay at bottom center
                            if (isVerifiedBadge) {
                                VerifiedBadge(
                                    modifier = Modifier.offset(y = 10.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(20.dp))

                        // Username with edit icon
                        if (isEditing) {
                            Text("Username", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            Spacer(modifier = Modifier.height(4.dp))
                            DanganiTextField(
                                value = editUsername,
                                onValueChange = { editUsername = it },
                                placeholder = "Username",
                                modifier = Modifier.fillMaxWidth()
                            )
                        } else {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.Center
                            ) {
                                Text(
                                    text = user?.username ?: "User",
                                    style = MaterialTheme.typography.headlineSmall.copy(
                                        fontWeight = FontWeight.Bold
                                    ),
                                    color = MaterialTheme.colorScheme.onBackground
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Icon(
                                    imageVector = Icons.Filled.Edit,
                                    contentDescription = "Edit username",
                                    modifier = Modifier
                                        .size(18.dp)
                                        .clickable { isEditing = true },
                                    tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(4.dp))

                        // Email
                        Text(
                            text = user?.email ?: "",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        if (isEditing) {
                            Text("Nomor WhatsApp", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            Spacer(modifier = Modifier.height(4.dp))
                            DanganiTextField(
                                value = editWhatsapp,
                                onValueChange = { editWhatsapp = it },
                                placeholder = "+628123...",
                                modifier = Modifier.fillMaxWidth()
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                            Text("Username Instagram", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            Spacer(modifier = Modifier.height(4.dp))
                            DanganiTextField(
                                value = editInstagram,
                                onValueChange = { editInstagram = it },
                                placeholder = "@username",
                                modifier = Modifier.fillMaxWidth()
                            )
                        } else {
                            Row(
                                horizontalArrangement = Arrangement.Center,
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                if (!user?.whatsapp.isNullOrBlank()) {
                                    WhatsAppIconButton(
                                        whatsappNumber = user?.whatsapp,
                                        onClick = { url ->
                                            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                                            context.startActivity(intent)
                                        }
                                    )
                                }
                                if (!user?.whatsapp.isNullOrBlank() && !user?.instagram.isNullOrBlank()) {
                                    Spacer(modifier = Modifier.width(16.dp))
                                }
                                if (!user?.instagram.isNullOrBlank()) {
                                    InstagramIconButton(
                                        instagramUsername = user?.instagram,
                                        onClick = { url ->
                                            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                                            context.startActivity(intent)
                                        }
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(20.dp))

                        // Academic Standing Card
                        AcademicStandingCard(
                            totalPoints = user?.totalPoints ?: 0
                        )

                        Spacer(modifier = Modifier.height(24.dp))

                        // Theme Settings
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Mode Gelap",
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                color = MaterialTheme.colorScheme.onBackground
                            )
                            val context = androidx.compose.ui.platform.LocalContext.current
                            val themeManager = remember { com.ryuzora.dangani.ui.theme.ThemeManager.getInstance(context) }
                            val isDarkMode by themeManager.isDarkMode.collectAsStateWithLifecycle()
                            
                            Switch(
                                checked = isDarkMode,
                                onCheckedChange = { themeManager.toggleTheme() }
                            )
                        }

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
                            color = MaterialTheme.colorScheme.onBackground
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
                            color = MaterialTheme.colorScheme.onBackground
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

                // Action buttons will be moved down

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
                            color = MaterialTheme.colorScheme.onBackground
                        )

                        if (uiState.reviews.isEmpty()) {
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(
                                text = "Belum ada ulasan",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f),
                                textAlign = TextAlign.Center,
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                    }
                }

                // Review items
                items(
                    items = if (showAllReviews) uiState.reviews else uiState.reviews.take(3),
                    key = { it.id }
                ) { review ->
                    Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                        ReviewCard(review = review)
                        HorizontalDivider(color = MaterialTheme.colorScheme.outline)
                    }
                }

                if (uiState.reviews.size > 3) {
                    item {
                        TextButton(
                            onClick = { showAllReviews = !showAllReviews },
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp)
                        ) {
                            Text(
                                text = if (showAllReviews) {
                                    "Tampilkan lebih sedikit"
                                } else {
                                    "Lihat semua ulasan (${uiState.reviews.size})"
                                },
                                style = MaterialTheme.typography.labelLarge,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                }

                // Action buttons moved here
                item {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp)
                            .padding(vertical = 24.dp)
                    ) {
                        if (isEditing) {
                            DanganiButton(
                                text = "Simpan Profil",
                                onClick = { 
                                    viewModel.updateProfile(editUsername, editWhatsapp, editInstagram)
                                    isEditing = false
                                },
                                variant = ButtonVariant.PRIMARY
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                            DanganiButton(
                                text = "Batal",
                                onClick = { 
                                    editWhatsapp = user?.whatsapp ?: ""
                                    editInstagram = user?.instagram ?: ""
                                    isEditing = false 
                                },
                                variant = ButtonVariant.SECONDARY
                            )
                        } else {
                            androidx.compose.material3.Button(
                                onClick = { isEditing = true },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(48.dp),
                                shape = androidx.compose.foundation.shape.RoundedCornerShape(8.dp),
                                colors = androidx.compose.material3.ButtonDefaults.buttonColors(
                                    containerColor = androidx.compose.ui.graphics.Color(0xFFDEE8FA)
                                )
                            ) {
                                Text(
                                    text = "Edit Profile",
                                    style = MaterialTheme.typography.labelLarge.copy(
                                        fontWeight = FontWeight.Bold
                                    ),
                                    color = androidx.compose.ui.graphics.Color(0xFF0F47A1)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        TextButton(
                            onClick = viewModel::logout,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = "Logout",
                                style = MaterialTheme.typography.labelLarge.copy(
                                    fontWeight = FontWeight.Bold
                                ),
                                color = androidx.compose.ui.graphics.Color(0xFFD32F2F)
                            )
                        }

                        if (uiState.error != null) {
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = uiState.error!!,
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.error,
                                textAlign = TextAlign.Center,
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                    }
                }

                item {
                    Spacer(modifier = Modifier.height(32.dp))
                }
            }
        }
    }
}

@Composable
private fun WhatsAppIconButton(
    whatsappNumber: String?,
    onClick: (String) -> Unit
) {
    val cleanedNumber = whatsappNumber
        ?.replace("+", "")
        ?.replace(" ", "")
        ?.replace("-", "")
        .orEmpty()

    val whatsappUrl = if (cleanedNumber.isNotBlank()) {
        "https://wa.me/$cleanedNumber"
    } else {
        "https://wa.me/"
    }

    Box(
        modifier = Modifier
            .size(44.dp)
            .border(
                width = 1.dp,
                color = androidx.compose.ui.graphics.Color(0xFF25D366),
                shape = CircleShape
            )
            .clickable { onClick(whatsappUrl) },
        contentAlignment = Alignment.Center
    ) {
        Icon(
            painter = painterResource(id = R.drawable.ic_whatsapp),
            contentDescription = "WhatsApp",
            modifier = Modifier.size(24.dp),
            tint = androidx.compose.ui.graphics.Color.Unspecified
        )
    }
}

@Composable
fun InstagramIconButton(
    instagramUsername: String?,
    onClick: (String) -> Unit
) {
    val cleanedUsername = instagramUsername?.replace("@", "") ?: ""

    val instagramUrl = if (cleanedUsername.isNotBlank()) {
        "https://instagram.com/$cleanedUsername"
    } else {
        "https://instagram.com/"
    }

    Box(
        modifier = Modifier
            .size(44.dp)
            .border(
                width = 1.dp,
                color = androidx.compose.ui.graphics.Color(0xFFE1306C),
                shape = CircleShape
            )
            .clickable { onClick(instagramUrl) },
        contentAlignment = Alignment.Center
    ) {
        Icon(
            painter = painterResource(id = R.drawable.ic_instagram),
            contentDescription = "Instagram",
            modifier = Modifier.size(24.dp),
            tint = androidx.compose.ui.graphics.Color.Unspecified
        )
    }
}







