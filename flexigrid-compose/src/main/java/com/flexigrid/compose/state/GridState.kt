package com.flexigrid.compose.state

import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.listSaver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateMap
import com.flexigrid.compose.model.SortDirection
import com.flexigrid.compose.model.SortState

/**
 * State holder for FlexiGrid that manages scroll positions, sort state,
 * and internal measurement cache.
 * 
 * Use [rememberGridState] to create and remember an instance.
 */
@Stable
class GridState(
    initialSortState: SortState = SortState(),
    initialHorizontalScroll: Int = 0,
    initialFirstVisibleItemIndex: Int = 0,
    initialFirstVisibleItemOffset: Int = 0
) {
    /**
     * Current sort state (column ID and direction).
     */
    var sortState: SortState by mutableStateOf(initialSortState)
        internal set
    
    /**
     * Horizontal scroll state for the main content area (legacy, kept for compatibility).
     */
    val horizontalScrollState: ScrollState = ScrollState(initialHorizontalScroll)
    
    /**
     * Vertical lazy list state for virtualized row rendering.
     */
    val verticalLazyListState: LazyListState = LazyListState(
        firstVisibleItemIndex = initialFirstVisibleItemIndex,
        firstVisibleItemScrollOffset = initialFirstVisibleItemOffset
    )
    

    
    /**
     * Secondary vertical lazy list state for content columns (when sticky column exists).
     * Synchronized with verticalLazyListState.
     */
    val contentVerticalLazyListState: LazyListState = LazyListState(
        firstVisibleItemIndex = initialFirstVisibleItemIndex,
        firstVisibleItemScrollOffset = initialFirstVisibleItemOffset
    )
    
    /**
     * Internal cache for measured column widths (in pixels).
     * Key: column ID, Value: measured width in pixels
     */
    internal val columnWidthCache: SnapshotStateMap<String, Int> = mutableStateMapOf()
    
    /**
     * Total calculated width of all columns (cached for performance).
     */
    internal var totalContentWidth: Int by mutableIntStateOf(0)
    
    /**
     * First visible column index for virtualization.
     */
    internal var firstVisibleColumnIndex: Int by mutableIntStateOf(0)
    
    /**
     * Last visible column index for virtualization.
     */
    internal var lastVisibleColumnIndex: Int by mutableIntStateOf(Int.MAX_VALUE)
    
    /**
     * Updates the sort state for the given column.
     * Toggles direction if same column, resets to ascending if different.
     */
    fun updateSort(columnId: String) {
        sortState = sortState.sortBy(columnId)
    }
    
    /**
     * Clears the current sort.
     */
    fun clearSort() {
        sortState = SortState.Unsorted
    }
    
    /**
     * Gets the cached width for a column, or null if not measured.
     */
    fun getColumnWidth(columnId: String): Int? = columnWidthCache[columnId]
    
    /**
     * Updates the cached width for a column.
     */
    internal fun updateColumnWidth(columnId: String, width: Int) {
        val existingWidth = columnWidthCache[columnId]
        if (existingWidth == null || width > existingWidth) {
            columnWidthCache[columnId] = width
        }
    }
    
    /**
     * Clears the measurement cache, forcing re-measurement.
     */
    fun clearMeasurementCache() {
        columnWidthCache.clear()
        totalContentWidth = 0
    }
    
    /**
     * Scrolls to a specific row index.
     */
    suspend fun scrollToRow(index: Int) {
        verticalLazyListState.scrollToItem(index)
        contentVerticalLazyListState.scrollToItem(index)
    }
    
    /**
     * Animates scroll to a specific row index.
     */
    suspend fun animateScrollToRow(index: Int) {
        verticalLazyListState.animateScrollToItem(index)
        contentVerticalLazyListState.animateScrollToItem(index)
    }
    
    companion object {
        /**
         * Saver for preserving GridState across configuration changes.
         */
        val Saver: Saver<GridState, *> = listSaver(
            save = { state ->
                listOf(
                    state.sortState.columnId,
                    state.sortState.direction.ordinal,
                    state.horizontalScrollState.value,
                    state.verticalLazyListState.firstVisibleItemIndex,
                    state.verticalLazyListState.firstVisibleItemScrollOffset
                )
            },
            restore = { saved ->
                GridState(
                    initialSortState = SortState(
                        columnId = saved[0] as? String,
                        direction = SortDirection.entries[saved[1] as Int]
                    ),
                    initialHorizontalScroll = saved[2] as Int,
                    initialFirstVisibleItemIndex = saved[3] as Int,
                    initialFirstVisibleItemOffset = saved[4] as Int
                )
            }
        )
    }
}

/**
 * Creates and remembers a [GridState] instance that survives configuration changes.
 * 
 * @param initialSortState Initial sort state
 * @param key Optional key for state restoration
 */
@Composable
fun rememberGridState(
    initialSortState: SortState = SortState(),
    key: String? = null
): GridState {
    return rememberSaveable(key, saver = GridState.Saver) {
        GridState(initialSortState = initialSortState)
    }
}
