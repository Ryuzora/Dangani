package com.ryuzora.dangani.presentation.view.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import com.ryuzora.dangani.ui.theme.*

enum class ButtonVariant {
    PRIMARY,
    SECONDARY,
    DANGER,
    CORAL
}

@Composable
fun DanganiButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    variant: ButtonVariant = ButtonVariant.PRIMARY,
    enabled: Boolean = true,
    icon: ImageVector? = null
) {
    val shape = androidx.compose.foundation.shape.CircleShape

    when (variant) {
        ButtonVariant.PRIMARY -> {
            Button(
                onClick = onClick,
                modifier = modifier
                    .fillMaxWidth()
                    .height(50.dp),
                enabled = enabled,
                shape = shape,
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary,
                    disabledContainerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.4f),
                    disabledContentColor = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.6f)
                )
            ) {
                ButtonContent(text = text, icon = icon, iconTint = MaterialTheme.colorScheme.onPrimary)
            }
        }

        ButtonVariant.SECONDARY -> {
            OutlinedButton(
                onClick = onClick,
                modifier = modifier
                    .fillMaxWidth()
                    .height(50.dp),
                enabled = enabled,
                shape = shape,
                border = BorderStroke(
                    1.5.dp,
                    if (enabled) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.primary.copy(alpha = 0.4f)
                ),
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = MaterialTheme.colorScheme.primary,
                    disabledContentColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.4f)
                )
            ) {
                ButtonContent(text = text, icon = icon, iconTint = MaterialTheme.colorScheme.primary)
            }
        }

        ButtonVariant.DANGER -> {
            Button(
                onClick = onClick,
                modifier = modifier
                    .fillMaxWidth()
                    .height(50.dp),
                enabled = enabled,
                shape = shape,
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.errorContainer,
                    contentColor = MaterialTheme.colorScheme.onPrimary,
                    disabledContainerColor = MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.4f),
                    disabledContentColor = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.6f)
                )
            ) {
                ButtonContent(text = text, icon = icon, iconTint = MaterialTheme.colorScheme.onPrimary)
            }
        }

        ButtonVariant.CORAL -> {
            Button(
                onClick = onClick,
                modifier = modifier
                    .fillMaxWidth()
                    .height(50.dp),
                enabled = enabled,
                shape = shape,
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.error,
                    contentColor = MaterialTheme.colorScheme.onPrimary,
                    disabledContainerColor = MaterialTheme.colorScheme.error.copy(alpha = 0.4f),
                    disabledContentColor = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.6f)
                )
            ) {
                ButtonContent(text = text, icon = icon, iconTint = MaterialTheme.colorScheme.onPrimary)
            }
        }
    }
}

@Composable
private fun ButtonContent(
    text: String,
    icon: ImageVector?,
    iconTint: Color
) {
    if (icon != null) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            modifier = Modifier.size(20.dp),
            tint = iconTint
        )
        Spacer(modifier = Modifier.width(8.dp))
    }
    Text(
        text = text,
        style = MaterialTheme.typography.labelLarge
    )
}





