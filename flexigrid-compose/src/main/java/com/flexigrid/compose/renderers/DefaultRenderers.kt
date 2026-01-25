package com.flexigrid.compose.renderers

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage

/**
 * Built-in cell renderers for common use cases.
 * These can be used directly or as reference for custom implementations.
 */
object GridCellRenderers {

    /**
     * Simple single-line text cell.
     */
    @Composable
    fun TextCell(
        modifier: Modifier = Modifier,
        text: String,
        textAlign: TextAlign = TextAlign.Start,
        maxLines: Int = 1,
        fontWeight: FontWeight? = null
    ) {
        Box(
            modifier = modifier
                .fillMaxSize()
                .padding(horizontal = 12.dp, vertical = 8.dp),
            contentAlignment = when (textAlign) {
                TextAlign.Center -> Alignment.Center
                TextAlign.End -> Alignment.CenterEnd
                else -> Alignment.CenterStart
            }
        ) {
            Text(
                text = text,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = fontWeight,
                maxLines = maxLines,
                overflow = TextOverflow.Ellipsis,
                textAlign = textAlign
            )
        }
    }

    /**
     * Two-line text cell with primary and secondary text.
     */
    @Composable
    fun TwoLineCell(
        modifier: Modifier = Modifier,
        primaryText: String,
        secondaryText: String,
    ) {
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(horizontal = 12.dp, vertical = 6.dp),
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = primaryText,
                style = MaterialTheme.typography.bodyMedium,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                text = secondaryText,
                style = MaterialTheme.typography.bodySmall,
                color = LocalContentColor.current.copy(alpha = 0.6f),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }

    /**
     * Image cell using Coil for async loading.
     */
    @Composable
    fun ImageCell(
        modifier: Modifier = Modifier,
        imageUrl: String?,
        contentDescription: String? = null,
        imageSize: Dp = 36.dp,
        cornerRadius: Dp = 4.dp
    ) {
        Box(
            modifier = modifier
                .fillMaxSize()
                .padding(8.dp),
            contentAlignment = Alignment.Center
        ) {
            AsyncImage(
                model = imageUrl,
                contentDescription = contentDescription,
                modifier = Modifier
                    .size(imageSize)
                    .clip(RoundedCornerShape(cornerRadius)),
                contentScale = ContentScale.Crop
            )
        }
    }

    /**
     * Icon with text cell.
     */
    @Composable
    fun IconTextCell(
        modifier: Modifier = Modifier,
        icon: ImageVector,
        text: String,
        iconTint: Color = LocalContentColor.current
    ) {
        Row(
            modifier = modifier
                .fillMaxSize()
                .padding(horizontal = 12.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                modifier = Modifier.size(20.dp),
                tint = iconTint
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = text,
                style = MaterialTheme.typography.bodyMedium,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }

    /**
     * Button cell for actions.
     */
    @Composable
    fun ButtonCell(
        modifier: Modifier = Modifier,
        text: String,
        onClick: () -> Unit,
        enabled: Boolean = true
    ) {
        Box(
            modifier = modifier
                .fillMaxSize()
                .padding(8.dp),
            contentAlignment = Alignment.Center
        ) {
            Button(
                onClick = onClick,
                enabled = enabled,
                contentPadding = ButtonDefaults.TextButtonContentPadding
            ) {
                Text(text = text, style = MaterialTheme.typography.labelMedium)
            }
        }
    }

    /**
     * Status badge cell with colored background.
     */
    @Composable
    fun StatusBadgeCell(
        modifier: Modifier = Modifier,
        text: String,
        backgroundColor: Color,
        textColor: Color = Color.White,
    ) {
        Box(
            modifier = modifier
                .fillMaxSize()
                .padding(8.dp),
            contentAlignment = Alignment.Center
        ) {
            Box(
                modifier = Modifier
                    .background(
                        color = backgroundColor,
                        shape = RoundedCornerShape(12.dp)
                    )
                    .padding(horizontal = 12.dp, vertical = 4.dp)
            ) {
                Text(
                    text = text,
                    style = MaterialTheme.typography.labelSmall,
                    color = textColor,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }

    /**
     * Numeric cell with right alignment (typical for numbers/currency).
     */
    @Composable
    fun NumericCell(
        value: String,
        modifier: Modifier = Modifier,
        fontWeight: FontWeight? = null
    ) {
        TextCell(
            text = value,
            modifier = modifier,
            textAlign = TextAlign.End,
            fontWeight = fontWeight
        )
    }

    /**
     * Clickable text cell.
     */
    @Composable
    fun ClickableTextCell(
        text: String,
        onClick: () -> Unit,
        modifier: Modifier = Modifier,
        enabled: Boolean = true
    ) {
        Box(
            modifier = modifier
                .fillMaxSize()
                .clickable(enabled = enabled, onClick = onClick)
                .padding(horizontal = 12.dp, vertical = 8.dp),
            contentAlignment = Alignment.CenterStart
        ) {
            Text(
                text = text,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.primary,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }

    /**
     * Editable text field cell.
     */
    @Composable
    fun TextFieldCell(
        value: String,
        onValueChange: (String) -> Unit,
        modifier: Modifier = Modifier,
        placeholder: String? = null,
        keyboardOptions: KeyboardOptions = KeyboardOptions.Default
    ) {
        Box(
            modifier = modifier
                .fillMaxSize()
                .padding(4.dp),
            contentAlignment = Alignment.Center
        ) {

            OutlinedTextField(
                value = value,
                onValueChange =onValueChange,
                placeholder = if(placeholder!=null) { { Text(text = placeholder) } } else { null },
                keyboardOptions = keyboardOptions
            )

        }
    }
}
