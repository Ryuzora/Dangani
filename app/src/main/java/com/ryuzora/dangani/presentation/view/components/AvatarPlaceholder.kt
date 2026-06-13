package com.ryuzora.dangani.presentation.view.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import kotlin.math.abs

/**
 * Circular placeholder used as a default avatar when no image URL is available.
 * It displays the user's initial on a randomly generated (but stable) background color.
 */
@Composable
fun AvatarPlaceholder(
    name: String,
    size: Dp,
    modifier: Modifier = Modifier
) {
    val initial = name.trim().firstOrNull()?.uppercase() ?: "?"
    
    // Generate a stable color based on the name hash
    val color = remember(name) {
        val colors = listOf(
            Color(0xFFE57373), Color(0xFFF06292), Color(0xFFBA68C8), Color(0xFF9575CD),
            Color(0xFF7986CB), Color(0xFF64B5F6), Color(0xFF4FC3F7), Color(0xFF4DD0E1),
            Color(0xFF4DB6AC), Color(0xFF81C784), Color(0xFFAED581), Color(0xFFFF8A65),
            Color(0xFFD4E157), Color(0xFFFFD54F), Color(0xFFFFB74D), Color(0xFFA1887F)
        )
        // Handle Integer.MIN_VALUE absolute value case safely
        val hash = name.hashCode().toLong()
        val absHash = abs(hash).toInt()
        colors[absHash % colors.size]
    }

    Box(
        modifier = modifier
            .size(size)
            .background(color, CircleShape),
        contentAlignment = Alignment.Center
    ) {
        // Size the text proportionally (about 45% of circle size)
        val fontSize = (size.value * 0.45f).dp 
        Text(
            text = initial,
            color = Color.White,
            fontWeight = FontWeight.Bold,
            fontSize = with(LocalDensity.current) { fontSize.toSp() }
        )
    }
}



