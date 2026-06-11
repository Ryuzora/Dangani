package com.ryuzora.dangani.presentation.profile

import android.content.Intent
import android.net.Uri
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
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
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.ryuzora.dangani.presentation.components.AcademicStandingCard
import com.ryuzora.dangani.presentation.components.AvatarPlaceholder
import com.ryuzora.dangani.presentation.components.ProfileStatsCard
import com.ryuzora.dangani.presentation.components.ReviewCard
import com.ryuzora.dangani.presentation.components.VerifiedBadge
import com.ryuzora.dangani.R
import com.ryuzora.dangani.ui.theme.BackgroundGray
import com.ryuzora.dangani.ui.theme.DanganiBlue
import com.ryuzora.dangani.ui.theme.DividerColor
import com.ryuzora.dangani.ui.theme.TextHint
import com.ryuzora.dangani.ui.theme.TextPrimary
import com.ryuzora.dangani.ui.theme.TextSecondary

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OtherProfileScreen(
    userId: String,
    onNavigateBack: () -> Unit,
    viewModel: ProfileViewModel = remember {
        ProfileViewModel(targetUserId = userId)
    }
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Profil",
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Bold
                        ),
                        color = TextPrimary
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Kembali",
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
                // Avatar section (no camera overlay)
                item {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp)
                    ) {
                        Spacer(modifier = Modifier.height(8.dp))

                        // Avatar with blue ring (no camera icon)
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

                        Spacer(modifier = Modifier.height(12.dp))

                        // Verified badge
                        if (user?.isVerified == true) {
                            VerifiedBadge()
                            Spacer(modifier = Modifier.height(8.dp))
                        }

                        // Username (no edit icon)
                        Text(
                            text = user?.username ?: "User",
                            style = MaterialTheme.typography.headlineSmall.copy(
                                fontWeight = FontWeight.Bold
                            ),
                            color = TextPrimary
                        )

                        Spacer(modifier = Modifier.height(4.dp))

                        // Email
                        Text(
                            text = user?.email ?: "",
                            style = MaterialTheme.typography.bodyMedium,
                            color = TextSecondary
                        )

                        Spacer(modifier = Modifier.height(16.dp))

// Social links
                        val hasInstagram = user?.instagram?.isNotBlank() == true

                        Row(
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            WhatsAppIconButton(
                                whatsappNumber = user?.whatsapp,
                                onClick = { url ->
                                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                                    context.startActivity(intent)
                                }
                            )

                            if (hasInstagram) {
                                Spacer(modifier = Modifier.width(16.dp))

                                SocialLinkButton(
                                    label = "Instagram",
                                    color = DanganiBlue,
                                    onClick = {
                                        val igHandle = user?.instagram?.removePrefix("@") ?: ""
                                        val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://instagram.com/$igHandle"))
                                        context.startActivity(intent)
                                    }
                                )
                            }
                        }

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

                // Bottom spacing
                item {
                    Spacer(modifier = Modifier.height(32.dp))
                }
            }
        }
    }
}

@Composable
private fun SocialLinkButton(
    label: String,
    color: androidx.compose.ui.graphics.Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .border(1.dp, color, CircleShape)
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelMedium.copy(
                fontWeight = FontWeight.Medium
            ),
            color = color
        )
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
