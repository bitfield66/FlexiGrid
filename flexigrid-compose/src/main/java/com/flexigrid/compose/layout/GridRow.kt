package com.flexigrid.compose.layout

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.key
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.flexigrid.compose.config.GridConfig
import com.flexigrid.compose.model.GridColumn

/**
 * Renders a single data row in the grid.
 * 
 * @param item The data item for this row
 * @param rowIndex Index of this row in the data list
 * @param columns List of column definitions
 * @param columnWidths Map of column IDs to calculated widths
 * @param config Grid configuration
 * @param isAlternate Whether this is an alternate row (for striping)
 * @param modifier Modifier for the row
 */
@Composable
internal fun <T> GridRow(
    item: T,
    rowIndex: Int,
    columns: List<GridColumn<T>>,
    columnWidths: Map<String, Dp>,
    config: GridConfig,
    isAlternate: Boolean,
    modifier: Modifier = Modifier
) {
    val rowBackground = if (config.alternateRowShading && isAlternate) {
        MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
    } else {
        MaterialTheme.colorScheme.surface
    }
    
    Row(
        modifier = modifier
            .height(config.rowHeight)
            .background(rowBackground)
    ) {
        columns.forEach { column ->
            val width = columnWidths[column.id] ?: 100.dp
            
            // Use key for stable recomposition
            key(column.id) {
                GridCell(
                    column = column,
                    item = item,
                    rowIndex = rowIndex,
                    width = width
                )
            }
            
            // Vertical divider between cells
            if (config.dividers.showVerticalDividers) {
                Box(
                    modifier = Modifier
                        .fillMaxHeight()
                        .width(config.dividers.thickness)
                        .background(MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
                )
            }
        }
    }
    
    // Horizontal divider below row
    if (config.dividers.showHorizontalDividers) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(config.dividers.thickness)
                .background(MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f))
        )
    }
}

/**
 * Individual cell renderer that delegates to the column's cellContent.
 */
@Composable
private fun <T> GridCell(
    column: GridColumn<T>,
    item: T,
    rowIndex: Int,
    width: Dp,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier.width(width)
    ) {
        column.cellContent(item, rowIndex)
    }
}
