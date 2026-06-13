package com.ryuzora.dangani.presentation.view.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@Composable
fun VerifiedBadge(
    modifier: Modifier = Modifier
) {
    Text(
        text = "VERIFIED STUDENT",
        style = MaterialTheme.typography.labelSmall.copy(
            fontWeight = FontWeight.Bold
        ),
        color = MaterialTheme.colorScheme.onTertiary,
        modifier = modifier
            .clip(RoundedCornerShape(50))
            .background(MaterialTheme.colorScheme.tertiary)
            .padding(horizontal = 8.dp, vertical = 4.dp)
    )
}







