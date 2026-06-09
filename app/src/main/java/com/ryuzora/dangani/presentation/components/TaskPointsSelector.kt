package com.ryuzora.dangani.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.ryuzora.dangani.domain.model.TaskPoints
import com.ryuzora.dangani.ui.theme.*

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun TaskPointsSelector(
    selectedPoints: Int,
    onPointsSelected: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    FlowRow(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp),
        maxItemsInEachRow = 3
    ) {
        TaskPoints.entries.forEach { tp ->
            val isSelected = tp.value == selectedPoints
            Column(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(24.dp))
                    .background(
                        if (isSelected) TaskPointsGreen
                        else com.ryuzora.dangani.ui.theme.SurfaceLight
                    )
                    .clickable { onPointsSelected(tp.value) }
                    .padding(vertical = 18.dp, horizontal = 8.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = tp.value.toString(),
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.Bold
                    ),
                    color = if (isSelected) TextOnPrimary else TextPrimary
                )
                Text(
                    text = tp.estimatedCost,
                    style = MaterialTheme.typography.labelSmall,
                    color = if (isSelected) TextOnPrimary.copy(alpha = 0.85f)
                    else TextSecondary,
                    maxLines = 1
                )
            }
        }
    }
}
