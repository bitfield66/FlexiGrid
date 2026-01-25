package com.flexigrid.compose.model

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable

/**
 * Defines a column in the grid with type-safe cell rendering.
 * 
 * @param T The type of data items in the grid
 * @param id Unique identifier for this column (used for sorting, state, and keys)
 * @param title Display title for the column header
 * @param width Width strategy for the column (Fixed, Flexible, or ContentBased)
 * @param sortable Whether this column supports sorting
 * @param sticky Whether this column should stick to the left edge during horizontal scroll
 * @param comparator Custom comparator for sorting. If null and sortable=true, 
 *                   natural ordering will be attempted for Comparable types
 * @param headerContent Optional custom header content. If null, default header with title is used
 * @param cellContent Composable lambda for rendering each cell. Receives the item and row index
 */
@Immutable
data class GridColumn<T>(
    val id: String,
    val title: String,
    val width: ColumnWidth = ColumnWidth.Default,
    val sortable: Boolean = false,
    val sticky: Boolean = false,
    val comparator: Comparator<T>? = null,
    val headerContent: (@Composable (SortState) -> Unit)? = null,
    val cellContent: @Composable (item: T, rowIndex: Int) -> Unit
) {
    /**
     * Creates a copy with a different cell renderer.
     * Useful for creating column variants.
     */
    fun withCellContent(
        content: @Composable (item: T, rowIndex: Int) -> Unit
    ): GridColumn<T> = copy(cellContent = content)
    
    /**
     * Creates a sortable variant of this column.
     */
    fun sortable(comparator: Comparator<T>? = null): GridColumn<T> = 
        copy(sortable = true, comparator = comparator)
    
    /**
     * Creates a sticky variant of this column.
     */
    fun sticky(): GridColumn<T> = copy(sticky = true)
    
    companion object {
        /**
         * Builder function for creating columns with a simpler API.
         */
        inline fun <T> create(
            id: String,
            title: String,
            width: ColumnWidth = ColumnWidth.Default,
            sortable: Boolean = false,
            sticky: Boolean = false,
            comparator: Comparator<T>? = null,
            noinline headerContent: (@Composable (SortState) -> Unit)? = null,
            noinline cellContent: @Composable (item: T, rowIndex: Int) -> Unit
        ): GridColumn<T> = GridColumn(
            id = id,
            title = title,
            width = width,
            sortable = sortable,
            sticky = sticky,
            comparator = comparator,
            headerContent = headerContent,
            cellContent = cellContent
        )
    }
}

/**
 * Type alias for a list of columns with the same data type.
 */
typealias GridColumns<T> = List<GridColumn<T>>
