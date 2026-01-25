package com.flexigrid.compose.layout

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.painterResource
import com.flexigrid.compose.R
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.flexigrid.compose.config.GridConfig
import com.flexigrid.compose.model.GridColumn
import com.flexigrid.compose.model.SortDirection
import com.flexigrid.compose.model.SortState

/**
 * Renders the header row for the grid.
 * 
 * @param columns List of column definitions
 * @param columnWidths Map of column IDs to calculated widths
 * @param sortState Current sort state
 * @param config Grid configuration
 * @param onSortClick Callback when a sortable column header is clicked
 * @param modifier Modifier for the header row
 */
@Composable
internal fun <T> GridHeader(
    columns: List<GridColumn<T>>,
    columnWidths: Map<String, Dp>,
    sortState: SortState,
    config: GridConfig,
    onSortClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .height(config.headerHeight)
            .background(MaterialTheme.colorScheme.surfaceVariant)
    ) {
        columns.forEach { column ->
            val width = columnWidths[column.id] ?: 100.dp
            
            HeaderCell(
                column = column,
                width = width,
                sortState = sortState,
                onSortClick = { onSortClick(column.id) }
            )
            
            // Vertical divider between header cells
            if (config.dividers.showVerticalDividers) {
                Box(
                    modifier = Modifier
                        .fillMaxHeight()
                        .width(config.dividers.thickness)
                        .background(MaterialTheme.colorScheme.outlineVariant)
                )
            }
        }
    }
}

/**
 * Individual header cell with sort indicator.
 */
@Composable
private fun <T> HeaderCell(
    column: GridColumn<T>,
    width: Dp,
    sortState: SortState,
    onSortClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val isSorted = sortState.columnId == column.id
    val sortDirection = if (isSorted) sortState.direction else SortDirection.NONE
    
    // Use custom header content if provided
    if (column.headerContent != null) {
        Box(modifier = modifier.width(width)) {
            column.headerContent.invoke(sortState)
        }
        return
    }
    
    // Default header rendering
    val clickableModifier = if (column.sortable) {
        modifier
            .clickable(onClick = onSortClick)
            .semantics {
                role = Role.Button
                contentDescription = buildString {
                    append("Sort by ${column.title}")
                    if (isSorted) {
                        append(", currently sorted ${sortDirection.name.lowercase()}")
                    }
                }
            }
    } else {
        modifier
    }
    
    Row(
        modifier = clickableModifier
            .width(width)
            .fillMaxHeight(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = column.title,
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.SemiBold,
            maxLines = 1,
            textAlign = TextAlign.Center,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.weight(1f)
        )
        
        if (column.sortable) {
            SortIndicator(
                direction = sortDirection,
                isActive = isSorted
            )
        }
    }
}

/**
 * Sort direction indicator icon with animation.
 */
@Composable
private fun SortIndicator(
    direction: SortDirection,
    isActive: Boolean,
    modifier: Modifier = Modifier
) {
    val alpha by animateFloatAsState(
        targetValue = if (isActive) 1f else 0.4f,
        label = "sort_indicator_alpha"
    )
    
    val icon = when (direction) {
        SortDirection.ASCENDING -> R.drawable.ic_sort_asc
        SortDirection.DESCENDING -> R.drawable.ic_sort_desc
        SortDirection.NONE -> R.drawable.ic_sort_default
    }
    
    Icon(
        painter = painterResource(id = icon),
        contentDescription = when (direction) {
            SortDirection.ASCENDING -> "Sorted ascending"
            SortDirection.DESCENDING -> "Sorted descending"
            SortDirection.NONE -> "Sortable"
        },
        modifier = modifier
            .size(18.dp)
            .alpha(alpha),
        tint = if (isActive) {
            MaterialTheme.colorScheme.primary
        } else {
            MaterialTheme.colorScheme.onSurfaceVariant
        }
    )
}
