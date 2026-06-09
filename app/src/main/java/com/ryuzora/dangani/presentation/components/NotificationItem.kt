package com.ryuzora.dangani.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.Refresh
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.ryuzora.dangani.domain.model.Notification
import com.ryuzora.dangani.domain.model.NotificationType
import com.ryuzora.dangani.ui.theme.*

@Composable
fun NotificationItem(
    notification: Notification,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (notification.isRead) CardWhite else DanganiLightBlue.copy(alpha = 0.5f)
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = if (notification.isRead) 0.dp else 1.dp
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Avatar or system icon with unread dot
            Box {
                val isSystemType = notification.type == NotificationType.RATING_RECEIVED ||
                        notification.type == NotificationType.WORK_REVISION

                if (isSystemType) {
                    // System notification icon
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .background(
                                when (notification.type) {
                                    NotificationType.RATING_RECEIVED -> androidx.compose.ui.graphics.Color(0xFFFFF3E0)
                                    NotificationType.WORK_REVISION -> androidx.compose.ui.graphics.Color(0xFFFFEBEE)
                                    else -> DanganiLightBlue
                                },
                                CircleShape
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = when (notification.type) {
                                NotificationType.RATING_RECEIVED -> Icons.Filled.Star
                                NotificationType.WORK_REVISION -> Icons.Outlined.Refresh
                                else -> Icons.Filled.Notifications
                            },
                            contentDescription = null,
                            modifier = Modifier.size(20.dp),
                            tint = when (notification.type) {
                                NotificationType.RATING_RECEIVED -> androidx.compose.ui.graphics.Color(0xFFFFB300)
                                NotificationType.WORK_REVISION -> ErrorRed
                                else -> DanganiBlue
                            }
                        )
                    }
                } else {
                    // User avatar
                    if (notification.senderAvatarUrl.isNotBlank()) {
                        AsyncImage(
                            model = notification.senderAvatarUrl,
                            contentDescription = "Sender Avatar",
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape),
                            contentScale = ContentScale.Crop
                        )
                    } else {
                        AvatarPlaceholder(size = 40.dp)
                    }
                }

                // Red unread dot
                if (!notification.isRead) {
                    Box(
                        modifier = Modifier
                            .size(10.dp)
                            .background(Color.White, CircleShape)
                            .padding(1.dp)
                            .background(UnreadDot, CircleShape)
                            .align(Alignment.TopEnd)
                    )
                }
            }

            // Content
            Column(modifier = Modifier.weight(1f)) {
                // Title
                Text(
                    text = notification.title,
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontWeight = FontWeight.Bold
                    ),
                    color = TextPrimary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(3.dp))

                // Message with [task name] highlighted in blue
                val message = notification.message
                val taskTitle = notification.relatedTaskTitle
                if (taskTitle.isNotBlank() && message.contains(taskTitle)) {
                    val annotatedMessage = buildAnnotatedString {
                        val parts = message.split(taskTitle)
                        parts.forEachIndexed { index, part ->
                            append(part)
                            if (index < parts.size - 1) {
                                withStyle(SpanStyle(
                                    color = DanganiBlue,
                                    fontWeight = FontWeight.SemiBold
                                )) {
                                    append(taskTitle)
                                }
                            }
                        }
                    }
                    Text(
                        text = annotatedMessage,
                        style = MaterialTheme.typography.bodySmall,
                        color = TextSecondary,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                } else {
                    Text(
                        text = message,
                        style = MaterialTheme.typography.bodySmall,
                        color = TextSecondary,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }

            // Timestamp
            Text(
                text = notification.timeAgo,
                style = MaterialTheme.typography.labelSmall,
                color = TextHint
            )
        }
    }
}
