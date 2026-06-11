package com.ryuzora.dangani.presentation.notification

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.ryuzora.dangani.domain.model.Notification
import com.ryuzora.dangani.presentation.components.NotificationItem
import com.ryuzora.dangani.presentation.components.RoleTabSelector
import com.ryuzora.dangani.ui.theme.BackgroundGray
import com.ryuzora.dangani.ui.theme.DanganiBlue
import com.ryuzora.dangani.ui.theme.NewBadgeGreen
import com.ryuzora.dangani.ui.theme.TextHint
import com.ryuzora.dangani.ui.theme.TextOnPrimary
import com.ryuzora.dangani.ui.theme.TextPrimary
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

@Composable
fun NotificationScreen(
    onNotificationClick: (Notification) -> Unit,
    viewModel: NotificationViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundGray)
    ) {
        // Header
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "Notifikasi",
            style = MaterialTheme.typography.headlineMedium.copy(
                fontWeight = FontWeight.Bold
            ),
            color = TextPrimary,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 12.dp)
        )

        // Role Tab Selector
        RoleTabSelector(
            selectedTab = uiState.selectedTab,
            onTabSelected = viewModel::onTabSelected,
            unreadCounts = listOf(uiState.requesterUnreadCount, uiState.helperUnreadCount)
        )

        // Content
        if (uiState.isLoading) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = DanganiBlue)
            }
        } else {
            val currentNotifications = if (uiState.selectedTab == 0) {
                uiState.requesterNotifications
            } else {
                uiState.helperNotifications
            }
            val currentUnreadCount = if (uiState.selectedTab == 0) {
                uiState.requesterUnreadCount
            } else {
                uiState.helperUnreadCount
            }

            if (currentNotifications.isEmpty()) {
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
                            imageVector = Icons.Outlined.Notifications,
                            contentDescription = null,
                            modifier = Modifier.size(64.dp),
                            tint = TextHint
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "Belum ada notifikasi",
                            style = MaterialTheme.typography.bodyLarge,
                            color = TextHint,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            } else {
                // Group notifications by date
                val groupedNotifications = remember(currentNotifications) {
                    groupNotificationsByDate(currentNotifications)
                }

                LazyColumn(
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxSize()
                ) {
                    groupedNotifications.forEach { (dateLabel, notifications) ->
                        // Section header
                        item(key = "header_$dateLabel") {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.padding(vertical = 8.dp)
                            ) {
                                Text(
                                    text = dateLabel,
                                    style = MaterialTheme.typography.titleSmall.copy(
                                        fontWeight = FontWeight.Bold
                                    ),
                                    color = TextPrimary
                                )

                                // Show NEW badge for "Terbaru" section
                                if (dateLabel == "Terbaru" && currentUnreadCount > 0) {
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = "$currentUnreadCount NEW",
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            fontWeight = FontWeight.Bold
                                        ),
                                        color = TextOnPrimary,
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(50))
                                            .background(NewBadgeGreen)
                                            .padding(horizontal = 8.dp, vertical = 3.dp)
                                    )
                                }
                            }
                        }

                        // Notification items
                        items(
                            items = notifications,
                            key = { it.id }
                        ) { notification ->
                            NotificationItem(
                                notification = notification,
                                onClick = {
                                    viewModel.onNotificationClick(notification)
                                    onNotificationClick(notification)
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}

/**
 * Groups notifications by date label:
 * - "Terbaru" for today
 * - "Kemarin" for yesterday
 * - Actual date (e.g. "5 Juni 2026") for older
 */
private fun groupNotificationsByDate(
    notifications: List<Notification>
): List<Pair<String, List<Notification>>> {
    val today = Calendar.getInstance().apply {
        set(Calendar.HOUR_OF_DAY, 0)
        set(Calendar.MINUTE, 0)
        set(Calendar.SECOND, 0)
        set(Calendar.MILLISECOND, 0)
    }.timeInMillis

    val yesterday = today - 86_400_000L

    val dateFormat = SimpleDateFormat("d MMMM yyyy", Locale("id", "ID"))

    val grouped = notifications.groupBy { notification ->
        when {
            notification.createdAt >= today -> "Terbaru"
            notification.createdAt >= yesterday -> "Kemarin"
            else -> dateFormat.format(notification.createdAt)
        }
    }

    // Ensure "Terbaru" comes first, then "Kemarin", then chronologically descending
    val orderedKeys = mutableListOf<String>()
    if (grouped.containsKey("Terbaru")) orderedKeys.add("Terbaru")
    if (grouped.containsKey("Kemarin")) orderedKeys.add("Kemarin")
    grouped.keys
        .filter { it != "Terbaru" && it != "Kemarin" }
        .sortedByDescending { dateFormat.parse(it)?.time ?: 0L }
        .forEach { orderedKeys.add(it) }

    return orderedKeys.map { key -> key to (grouped[key] ?: emptyList()) }
}
