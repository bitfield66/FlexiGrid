package com.flexigrid.compose.model

import androidx.compose.runtime.Immutable
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * Defines the width strategy for a grid column.
 * 
 * Use [Fixed] for explicit widths, [Flexible] for weight-based distribution,
 * or [ContentBased] for auto-sizing with optional constraints.
 */
@Immutable
sealed class ColumnWidth {
    /**
     * Fixed width column with explicit Dp value.
     */
    @Immutable
    data class Fixed(val width: Dp) : ColumnWidth()
    
    /**
     * Content-based width that measures the widest cell content.
     * @param minWidth Minimum width constraint (default 10.dp)
     * @param maxWidth Maximum width constraint (default 300.dp)
     * @param padding Additional padding to add to measured width
     */
    @Immutable
    data class ContentBased(
        val minWidth: Dp = 10.dp,
        val maxWidth: Dp = 300.dp,
        val padding: Dp = 0.dp
    ) : ColumnWidth()
    
    companion object {
        /**
         * Wrap content with sensible defaults.
         */
        val Default: ColumnWidth = ContentBased()
    }
}
