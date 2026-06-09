package com.ryuzora.dangani.presentation.components

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
import com.ryuzora.dangani.ui.theme.TextOnPrimary

@Composable
fun VerifiedBadge(
    modifier: Modifier = Modifier
) {
    Text(
        text = "VERIFIED STUDENT",
        style = MaterialTheme.typography.labelSmall.copy(
            fontWeight = FontWeight.Bold
        ),
        color = com.ryuzora.dangani.ui.theme.VerificationBadgeText,
        modifier = modifier
            .clip(RoundedCornerShape(50))
            .background(com.ryuzora.dangani.ui.theme.VerificationBadgeBg)
            .padding(horizontal = 8.dp, vertical = 4.dp)
    )
}
