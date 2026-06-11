package com.ryuzora.dangani.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ryuzora.dangani.ui.theme.TaskPointsGreen
import com.ryuzora.dangani.ui.theme.TextOnPrimary

@Composable
fun TaskPointsBadge(
    points: Int,
    isLarge: Boolean = false,
    modifier: Modifier = Modifier
) {
    if (isLarge) {
        Column(
            modifier = modifier
                .size(112.dp)
                .clip(RoundedCornerShape(28.dp))
                .background(TaskPointsGreen)
                .padding(horizontal = 14.dp, vertical = 18.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = points.toString(),
                style = MaterialTheme.typography.displaySmall.copy(
                    fontWeight = FontWeight.Bold,
                    fontSize = 44.sp
                ),
                color = TextOnPrimary
            )
            Text(
                text = "TASK POINTS",
                style = MaterialTheme.typography.labelSmall.copy(
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 2.sp
                ),
                color = TextOnPrimary
            )
        }
    } else {
        Column(
            modifier = modifier
                .clip(RoundedCornerShape(50))
                .background(TaskPointsGreen)
                .padding(horizontal = 12.dp, vertical = 6.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "$points TASK POINTS",
                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                color = TextOnPrimary
            )
        }
    }
}
