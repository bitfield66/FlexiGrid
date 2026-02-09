package com.flexigrid.compose.model

import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.vector.ImageVector

/**
 * Defines the background content for a row.
 */
sealed interface RowBackground {
    data class Color(val color: androidx.compose.ui.graphics.Color) : RowBackground
    data class ImageUrl(val url: String) : RowBackground
    data class DrawableRes(val id: Int) : RowBackground
    data class Vector(val imageVector: ImageVector) : RowBackground
}

/**
 * Configuration for individual row styling.
 * 
 * @param background The background content of the row (color, image, etc.)
 * @param shape The shape of the row. If null, uses GridConfig.rowShape.
 */
data class RowStyle(
    val background: RowBackground? = null,
    val shape: Shape? = null
)
