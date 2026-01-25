package com.flexigrid.compose.config

import androidx.compose.runtime.Immutable
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * Divider configuration for grid lines.
 */
@Immutable
data class DividerConfig(
    val thickness: Dp = 1.dp,
    val showHorizontalDividers: Boolean = true,
    val showVerticalDividers: Boolean = true,
    val showHeaderDivider: Boolean = true
) {
    companion object {
        val Default = DividerConfig()
        val None = DividerConfig(
            showHorizontalDividers = false,
            showVerticalDividers = false,
            showHeaderDivider = false
        )
        val HorizontalOnly = DividerConfig(showVerticalDividers = false)
        val VerticalOnly = DividerConfig(showHorizontalDividers = false)
    }
}

/**
 * Immutable configuration for the FlexiGrid behavior and appearance.
 * 
 * All settings have sensible defaults for typical table use cases.
 * The library inherits theming from the consuming app's Material theme.
 * 
 * @param stickyHeader Whether the header row should stick to the top during vertical scroll
 * @param stickyColumnId ID of the column that should stick to the left/right edges. Replaces stickyFirstColumn.
 * @param stickyFirstColumn Legacy parameter, maps to setting stickyColumnId to the first column.
 * @param horizontalScrollEnabled Enable/disable horizontal scrolling
 * @param verticalScrollEnabled Enable/disable vertical (lazy) scrolling
 * @param prefetchRowCount Number of rows to prefetch beyond visible bounds
 * @param prefetchColumnCount Number of columns to prefetch beyond visible bounds
 * @param enableSortAnimation Whether to animate row reordering on sort
 * @param enableResizeByDrag Whether columns can be resized by dragging dividers
 * @param rowHeight Default height for data rows
 * @param headerHeight Height for the header row
 * @param dividers Divider line configuration
 * @param alternateRowShading Whether to apply alternate row background shading
 * @param clipToBounds Whether to clip content to grid bounds
 */
@Immutable
data class GridConfig(
    val stickyHeader: Boolean = true,
    val stickyColumnId: String? = null,
    val stickyFirstColumn: Boolean = false,
    val horizontalScrollEnabled: Boolean = true,
    val verticalScrollEnabled: Boolean = true,
    val prefetchRowCount: Int = 5,
    val prefetchColumnCount: Int = 2,
    val enableSortAnimation: Boolean = true,
    val enableResizeByDrag: Boolean = false,
    val rowHeight: Dp = 52.dp,
    val headerHeight: Dp = 56.dp,
    val dividers: DividerConfig = DividerConfig.Default,
    val alternateRowShading: Boolean = true,
    val clipToBounds: Boolean = true
) {
    init {
        require(prefetchRowCount >= 0) { "prefetchRowCount must be non-negative" }
        require(prefetchColumnCount >= 0) { "prefetchColumnCount must be non-negative" }
        require(rowHeight > 0.dp) { "rowHeight must be positive" }
        require(headerHeight > 0.dp) { "headerHeight must be positive" }
    }
    
    companion object {
        /**
         * Default configuration with sticky header enabled.
         */
        val Default = GridConfig()
        
        /**
         * Configuration for simple tables without sticky elements.
         */
        val Simple = GridConfig(
            stickyHeader = false,
            stickyFirstColumn = false,
            enableSortAnimation = false
        )
        
        /**
         * High-performance configuration for large datasets.
         */
        val HighPerformance = GridConfig(
            prefetchRowCount = 10,
            prefetchColumnCount = 3,
            enableSortAnimation = false,
            enableResizeByDrag = false
        )
        
        /**
         * Configuration with all sticky features enabled.
         */
        val FullySticky = GridConfig(
            stickyHeader = true,
            stickyFirstColumn = true
        )
    }
    
    /**
     * Builder-style methods for common modifications.
     */
    fun withStickyHeader(enabled: Boolean = true) = copy(stickyHeader = enabled)
    fun withStickyFirstColumn(enabled: Boolean = true) = copy(stickyFirstColumn = enabled)
    fun withPrefetch(rows: Int, columns: Int) = copy(
        prefetchRowCount = rows,
        prefetchColumnCount = columns
    )
    fun withDividers(config: DividerConfig) = copy(dividers = config)
}
