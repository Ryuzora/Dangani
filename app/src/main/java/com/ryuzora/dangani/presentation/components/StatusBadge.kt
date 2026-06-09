package com.ryuzora.dangani.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.ryuzora.dangani.domain.model.TaskStatus
import com.ryuzora.dangani.ui.theme.*

@Composable
fun StatusBadge(
    status: TaskStatus,
    modifier: Modifier = Modifier
) {
    val (backgroundColor, textColor, dotColor) = when (status) {
        TaskStatus.UNASSIGNED -> Triple(com.ryuzora.dangani.ui.theme.SurfaceLight, TextPrimary, com.ryuzora.dangani.ui.theme.TextSecondary)
        TaskStatus.IN_PROGRESS -> Triple(DanganiBlue, TextOnPrimary, Color.White)
        TaskStatus.NEED_REVIEW -> Triple(Color(0xFFFFB300), TextOnPrimary, Color.White)
        TaskStatus.REVISION -> Triple(Color(0xFFFFB300), TextOnPrimary, Color.White)
        TaskStatus.ACCEPTED -> Triple(Color(0xFF4CAF50), TextOnPrimary, Color.White)
    }

    Row(
        modifier = modifier
            .clip(RoundedCornerShape(50))
            .background(backgroundColor)
            .padding(horizontal = 10.dp, vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        Box(
            modifier = Modifier
                .size(6.dp)
                .background(dotColor, CircleShape)
        )
        Text(
            text = status.displayName,
            style = MaterialTheme.typography.labelSmall,
            color = textColor
        )
    }
}
