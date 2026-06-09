package com.ryuzora.dangani.presentation.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.StarBorder
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.ryuzora.dangani.domain.model.Review
import com.ryuzora.dangani.ui.theme.*

@Composable
fun ReviewCard(
    review: Review,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Reviewer info
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier.weight(1f)
            ) {
                if (review.reviewerAvatarUrl.isNotBlank()) {
                    AsyncImage(
                        model = review.reviewerAvatarUrl,
                        contentDescription = "Reviewer Avatar",
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    AvatarPlaceholder(name = review.reviewerName, size = 36.dp)
                }

                Column {
                    Text(
                        text = review.reviewerName,
                        style = MaterialTheme.typography.bodyMedium.copy(
                            fontWeight = FontWeight.SemiBold
                        ),
                        color = TextPrimary,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text = review.timeAgo,
                        style = MaterialTheme.typography.labelSmall,
                        color = TextHint
                    )
                }
            }

            // Star rating
            Row(horizontalArrangement = Arrangement.spacedBy(2.dp)) {
                repeat(5) { index ->
                    Icon(
                        imageVector = if (index < review.rating) Icons.Filled.Star else Icons.Outlined.StarBorder,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp),
                        tint = if (index < review.rating) androidx.compose.ui.graphics.Color(0xFFFFB300) else TextHint
                    )
                }
            }
        }

        // Comment
        if (review.comment.isNotBlank()) {
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = review.comment,
                style = MaterialTheme.typography.bodyMedium,
                color = TextPrimary
            )
        }
    }
}
