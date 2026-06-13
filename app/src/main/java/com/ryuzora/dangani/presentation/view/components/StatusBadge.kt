package com.ryuzora.dangani.presentation.view.components

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
import androidx.compose.ui.unit.sp
import com.ryuzora.dangani.domain.model.TaskStatus
import com.ryuzora.dangani.ui.theme.*

@Composable
fun StatusBadge(
    status: TaskStatus,
    modifier: Modifier = Modifier
) {
    val (backgroundColor, textColor) = when (status) {
        TaskStatus.UNASSIGNED -> Pair(Color(0xFF9AA0A6), Color.White)
        TaskStatus.IN_PROGRESS -> Pair(Color(0xFFAECBFA), Color(0xFF174EA6))
        TaskStatus.NEED_REVIEW -> Pair(Color(0xFFFAD2CF), Color(0xFFB31412))
        TaskStatus.REVISION -> Pair(Color(0xFFFAD2CF), Color(0xFFB31412))
        TaskStatus.ACCEPTED -> Pair(Color(0xFFCEEAD6), Color(0xFF0D652D))
    }

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(50))
            .background(backgroundColor)
            .padding(horizontal = 12.dp, vertical = 4.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = status.displayName.uppercase(),
            style = MaterialTheme.typography.labelSmall.copy(
                fontWeight = androidx.compose.ui.text.font.FontWeight.Bold,
                fontSize = 10.sp
            ),
            color = textColor
        )
    }
}






