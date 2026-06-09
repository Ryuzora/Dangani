package com.ryuzora.dangani.presentation.components

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
                    containerColor = DanganiBlue,
                    contentColor = TextOnPrimary,
                    disabledContainerColor = DanganiBlue.copy(alpha = 0.4f),
                    disabledContentColor = TextOnPrimary.copy(alpha = 0.6f)
                )
            ) {
                ButtonContent(text = text, icon = icon, iconTint = TextOnPrimary)
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
                    if (enabled) DanganiBlue else DanganiBlue.copy(alpha = 0.4f)
                ),
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = DanganiBlue,
                    disabledContentColor = DanganiBlue.copy(alpha = 0.4f)
                )
            ) {
                ButtonContent(text = text, icon = icon, iconTint = DanganiBlue)
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
                    containerColor = DeleteButtonRed,
                    contentColor = TextOnPrimary,
                    disabledContainerColor = DeleteButtonRed.copy(alpha = 0.4f),
                    disabledContentColor = TextOnPrimary.copy(alpha = 0.6f)
                )
            ) {
                ButtonContent(text = text, icon = icon, iconTint = TextOnPrimary)
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
                    containerColor = ErrorRed,
                    contentColor = TextOnPrimary,
                    disabledContainerColor = ErrorRed.copy(alpha = 0.4f),
                    disabledContentColor = TextOnPrimary.copy(alpha = 0.6f)
                )
            ) {
                ButtonContent(text = text, icon = icon, iconTint = TextOnPrimary)
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
