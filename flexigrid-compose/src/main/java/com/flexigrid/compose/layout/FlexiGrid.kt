package com.flexigrid.compose.layout

import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.SubcomposeLayout
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import com.flexigrid.compose.config.GridConfig
import com.flexigrid.compose.model.ColumnWidth
import com.flexigrid.compose.model.GridColumn
import com.flexigrid.compose.model.RowStyle
import com.flexigrid.compose.model.SortDirection
import com.flexigrid.compose.model.SortState
import com.flexigrid.compose.state.GridState
import com.flexigrid.compose.state.rememberGridState

/**
 * A high-performance, configurable grid component for Jetpack Compose.
 * 
 * Features:
 * - Two-axis scrolling with synchronized sticky header
 * - Column sorting with customizable comparators
 * - Lazy virtualized row rendering
 * - Flexible column width strategies (fixed, flexible, content-based)
 * - Sticky first column support
 * - Alternate row shading
 * - Customizable dividers
 * 
 * @param T The type of data items displayed in the grid
 * @param columns List of column definitions
 * @param items List of data items to display
 * @param modifier Modifier for the grid container
 * @param config Grid configuration (sticky behavior, dividers, etc.)
 * @param state Grid state for scroll position and sort state
 * @param key Optional key provider for stable item identity
 * @param onSortChanged Callback when sort state changes
 */
@Composable
fun <T> FlexiGrid(
    columns: List<GridColumn<T>>,
    items: List<T>,
    modifier: Modifier = Modifier,
    config: GridConfig = GridConfig.Default,
    state: GridState = rememberGridState(),
    key: ((T) -> Any)? = null,
    onSortChanged: ((SortState) -> Unit)? = null,
    rowStyle: ((T, Int) -> RowStyle)? = null
) {

    // Sort items based on current sort state
    val sortedItems by remember(items, columns) {
        derivedStateOf {
            val currentSortState = state.sortState
            if (!currentSortState.isActive) {
                items
            } else {
                val column = columns.find { it.id == currentSortState.columnId }
                val comparator = column?.comparator
                if (comparator != null) {
                    when (currentSortState.direction) {
                        SortDirection.ASCENDING -> items.sortedWith(comparator)
                        SortDirection.DESCENDING -> items.sortedWith(comparator.reversed())
                        SortDirection.NONE -> items
                    }
                } else {
                    items
                }
            }
        }
    }
    

    // Handle sort changes
    val handleSortClick: (String) -> Unit = remember(onSortChanged) {
        { columnId ->
            state.updateSort(columnId)
            onSortChanged?.invoke(state.sortState)
        }
    }
    
    // Determine sticky column ID
    val stickyColumnId = remember(config, columns) {
        config.sticky.stickyColumnId
    }

    val containerModifier = if (config.clipToBounds) {
        modifier.clipToBounds()
    } else {
        modifier
    }

    SubcomposeLayout(modifier = containerModifier) { constraints ->
        val measuredWidths = mutableMapOf<String, Dp>()
        
        // 1. Measure ContentBased columns
        val sampleSize = 100
        
        columns.forEach { column ->
            val widthPx = when (val strategy = column.width) {
                is ColumnWidth.Fixed -> strategy.width.roundToPx()
                
                is ColumnWidth.ContentBased -> {
                    val paddingPx = strategy.padding.roundToPx()
                    val minPx = strategy.minWidth.roundToPx()
                    val maxPx = strategy.maxWidth.roundToPx()
                    
                    var maxContentWidth = 0
                    
                    // Measure Header
                    val headerPlaceables = subcompose("${column.id}_header") {
                        Box { 
                            if (column.headerContent != null) {
                                column.headerContent.invoke(state.sortState)
                            } else {
                                Text(text = column.title, maxLines = 1)
                            }
                        }
                    }
                    headerPlaceables.forEach {
                        var w = it.measure(Constraints()).width
                        // If using default header, account for HeaderCell padding and sort icon
                        if (column.headerContent == null) {
//                            w += headerPaddingPx // Use configured padding
                            if (column.sortable) {
                                w += 18.dp.roundToPx() // Sort icon size
                            }
                        }
                        maxContentWidth = maxOf(maxContentWidth, w)
                    }
                    
                    // Measure Sample Items
                    val itemPlaceables = subcompose("${column.id}_items") {
                        val itemsToMeasure = items.take(sampleSize)
                        itemsToMeasure.forEachIndexed { index, item ->
                            Box { column.cellContent(item, index) }
                        }
                    }
                    itemPlaceables.forEach {
                        maxContentWidth = maxOf(maxContentWidth, it.measure(Constraints()).width)
                    }
                    
                    (maxContentWidth + paddingPx * 2).coerceIn(minPx, maxPx)
                }
            }
            measuredWidths[column.id] = widthPx.toDp()
        }
        
        // Calculate offsets for sticky column logic
        // We need to know the X position of the sticky column in the layout
        var stickyColX = 0f
        var stickyColWidth = 0f
        var currentX = 0f
        
        columns.forEach { column ->
            val w = measuredWidths[column.id]?.toPx() ?: 0f
            if (column.id == stickyColumnId) {
                stickyColX = currentX
                stickyColWidth = w
            }
            currentX += w
        }
        val totalGridWidth = currentX
        val viewportWidth = constraints.maxWidth.toFloat()
        
        // Calculate target scroll to center the sticky column if it exists
        // We trigger this only if we haven't scrolled yet (value == 0) and we have a sticky column
        val initialScrollTarget = if (stickyColumnId != null) {
             (stickyColX - (viewportWidth - stickyColWidth) / 2f).toInt()
                .coerceIn(0, (totalGridWidth - viewportWidth).toInt().coerceAtLeast(0))
        } else 0

        // 2. Compose Main Content
        val contentPlaceables = subcompose("content") {
            // Auto-scroll effect
            LaunchedEffect(stickyColumnId, initialScrollTarget) {
                if (state.horizontalScrollState.value == 0 && initialScrollTarget > 0) {
                    state.horizontalScrollState.scrollTo(initialScrollTarget)
                }
            }
            
            Column(
                modifier = Modifier
                    .fillMaxSize()
            ) {
                // Determine horizontal scroll state
                val scrollState = state.horizontalScrollState
                
                // Header row
                if (config.sticky.stickyHeader) {
                    GridHeaderRow(
                        columns = columns,
                        columnWidths = measuredWidths,
                        sortState = state.sortState,
                        config = config,
                        onSortClick = handleSortClick,
                        scrollState = scrollState,
                        stickyColumnId = stickyColumnId,
                        stickyColX = stickyColX,
                        stickyColWidth = stickyColWidth,
                        viewportWidth = viewportWidth
                    )
                    
                    if (config.dividers.showHeaderDivider) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(config.dividers.thickness)
                                .background(config.dividerStyle.horizontalDividerColor)
                        )
                    }
                }
                
                // Data rows - Single LazyColumn
                LazyColumn(
                    state = state.verticalLazyListState,
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = if (config.sizing.rowSpacing > 0.dp) Arrangement.spacedBy(config.sizing.rowSpacing) else Arrangement.Top
                ) {
                    itemsIndexed(
                        items = sortedItems
                    ) { index, item ->
                        val itemRowStyle = rowStyle?.invoke(item, index)
                        UnifiedGridRow(
                            item = item,
                            rowIndex = index,
                            columns = columns,
                            columnWidths = measuredWidths,
                            config = config,
                            scrollState = scrollState,
                            stickyColumnId = stickyColumnId,
                            stickyColX = stickyColX,
                            stickyColWidth = stickyColWidth,
                            viewportWidth = viewportWidth,
                            rowStyle = itemRowStyle
                        )
                    }
                }
            }
        }.map { it.measure(constraints) }
        
        layout(constraints.maxWidth, constraints.maxHeight) {
            contentPlaceables.forEach { it.place(0, 0) }
        }
    }
}

@Composable
private fun <T> GridHeaderRow(
    columns: List<GridColumn<T>>,
    columnWidths: Map<String, Dp>,
    sortState: SortState,
    config: GridConfig,
    onSortClick: (String) -> Unit,
    scrollState: ScrollState,
    stickyColumnId: String?,
    stickyColX: Float,
    stickyColWidth: Float,
    viewportWidth: Float
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(config.sizing.headerHeight)
            .horizontalScroll(scrollState)
    ) {
        Row {
            columns.forEach { column ->
                val width = columnWidths[column.id] ?: 100.dp
                val isSticky = column.id == stickyColumnId
                
                Box(
                    modifier = Modifier
                        .width(width)
                        .then(
                            if (isSticky) {
                                Modifier
                                    .zIndex(1f)
                                    .graphicsLayer {
                                        val scrollX = scrollState.value.toFloat()
                                        val currentVisualX = stickyColX - scrollX
                                        val targetVisualX = currentVisualX.coerceIn(0f, viewportWidth - stickyColWidth)
                                        translationX = targetVisualX + scrollX - stickyColX
                                        
                                        // Add shadow only when stuck (approximation: when translation is required)
                                        // But constant shadow is fine for sticky column concept
                                    }
                                    .shadow(elevation = 4.dp) // Add shadow for "placed on top" feel
                            } else Modifier
                        )
                ) {
                    if (isSticky && config.headerStyle.backgroundColor != null) {
                        // Background required for sticky header to cover scrolled content
                        // Ensure it's opaque
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(config.headerStyle.backgroundColor )
                        )
                    }

                    GridHeader(
                        columns = listOf(column),
                        columnWidths = columnWidths,
                        sortState = sortState,
                        config = config.copy(dividers = config.dividers.copy(showVerticalDividers = false)), // Handle dividers locally
                        onSortClick = onSortClick,
                        modifier = Modifier.width(width)
                    )
                    
                    // Add divider manually
                    if (config.dividers.showHeaderVerticalDivider) {
                        Box(
                            modifier = Modifier
                                .align(Alignment.CenterEnd)
                                .fillMaxHeight()
                                .width(config.dividers.thickness)
                                .background(config.dividerStyle.verticalDividerColor)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun <T> UnifiedGridRow(
    item: T,
    rowIndex: Int,
    columns: List<GridColumn<T>>,
    columnWidths: Map<String, Dp>,
    config: GridConfig,
    scrollState: ScrollState,
    stickyColumnId: String?,
    stickyColX: Float,
    stickyColWidth: Float,
    viewportWidth: Float,
    rowStyle: RowStyle? = null
) {
    var rowModifier = Modifier
        .fillMaxWidth()
        .height(config.sizing.rowHeight)

    // Apply row shape
    val shape = rowStyle?.shape ?: config.rowStyle.rowShape
    if (shape != androidx.compose.ui.graphics.RectangleShape) {
        rowModifier = rowModifier.clip(shape)
    }

    // Apply row background
    val background = rowStyle?.background ?: config.rowStyle.defaultRowBackground
    if (background is com.flexigrid.compose.model.RowBackground.Color) {
        rowModifier = rowModifier.background(background.color)
    }
    
    rowModifier = rowModifier.horizontalScroll(scrollState)

    Box(
        modifier = rowModifier
    ) {
        // Image/drawable backgrounds rendered as content layer
        when (background) {
            is com.flexigrid.compose.model.RowBackground.ImageUrl -> {
                 coil.compose.AsyncImage(
                    model = background.url,
                    contentDescription = null,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = androidx.compose.ui.layout.ContentScale.Crop
                )
            }
            is com.flexigrid.compose.model.RowBackground.DrawableRes -> {
                 coil.compose.AsyncImage(
                    model = background.id,
                    contentDescription = null,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = androidx.compose.ui.layout.ContentScale.Crop
                 )
            }
            is com.flexigrid.compose.model.RowBackground.Vector -> {
                androidx.compose.material3.Icon(
                    imageVector = background.imageVector,
                    contentDescription = null,
                    modifier = Modifier.fillMaxSize(),
                    tint = androidx.compose.ui.graphics.Color.Unspecified
                )
            }
            else -> {}
        }

        Row {
            columns.forEach { column ->
                val width = columnWidths[column.id] ?: 100.dp
                val isSticky = column.id == stickyColumnId
                
                Box(
                    modifier = Modifier
                        .width(width)
                        .then(
                            if (isSticky) {
                                Modifier
                                    .zIndex(1f)
                                    .graphicsLayer {
                                        val scrollX = scrollState.value.toFloat()
                                        val currentVisualX = stickyColX - scrollX
                                        val targetVisualX = currentVisualX.coerceIn(0f, viewportWidth - stickyColWidth)
                                        translationX = targetVisualX + scrollX - stickyColX
                                    }
                                    .shadow(elevation = 2.dp) // Subtle shadow for data rows
                            } else Modifier
                        )
                ) {
                    // Sticky background to cover content underneath
                     if (isSticky) {
                        // Calculate opaque background color for sticky cells to prevent bleed-through
                        val stickyBackground = when (background) {
                             is com.flexigrid.compose.model.RowBackground.Color -> background.color
                             else -> MaterialTheme.colorScheme.surface // Fallback for image rows...
                        }

                         Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(stickyBackground)
                        )
                    }
                    
                    GridRow(
                        item = item,
                        rowIndex = rowIndex,
                        columns = listOf(column),
                        columnWidths = columnWidths,
                        config = config.copy(
                            dividers = config.dividers.copy(
                                showVerticalDividers = false, 
                                showHorizontalDividers = false // Handled by container
                            )
                        ),
                        modifier = Modifier.width(width)
                    )
                    
                     if (config.dividers.showVerticalDividers) {
                        Box(
                            modifier = Modifier
                                .align(Alignment.CenterEnd)
                                .fillMaxHeight()
                                .width(config.dividers.thickness)
                                .background(config.dividerStyle.verticalDividerColor)
                        )
                    }
                }
            }
        }
    }
    
    // Bottom divider
    if (config.dividers.showHorizontalDividers) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(config.dividers.thickness)
                .background(config.dividerStyle.horizontalDividerColor)
        )
    }
}

