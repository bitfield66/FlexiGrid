package com.flexigrid.compose.model

import androidx.compose.runtime.Immutable

/**
 * Sort direction for grid columns.
 */
enum class SortDirection {
    ASCENDING,
    DESCENDING,
    NONE;
    
    /**
     * Toggle to next sort direction: NONE -> ASCENDING -> DESCENDING -> NONE
     */
    fun toggle(): SortDirection = when (this) {
        NONE -> ASCENDING
        ASCENDING -> DESCENDING
        DESCENDING -> NONE
    }
    
    /**
     * Toggle between ASCENDING and DESCENDING only.
     */
    fun toggleBinary(): SortDirection = when (this) {
        ASCENDING -> DESCENDING
        else -> ASCENDING
    }
}

/**
 * Immutable state representing the current sort configuration.
 * 
 * @param columnId The ID of the column being sorted, or null if no sort active
 * @param direction The current sort direction
 */
@Immutable
data class SortState(
    val columnId: String? = null,
    val direction: SortDirection = SortDirection.NONE
) {
    /**
     * Whether sorting is currently active.
     */
    val isActive: Boolean
        get() = columnId != null && direction != SortDirection.NONE
    
    /**
     * Creates a new SortState with the given column sorted.
     * If the same column is already sorted, toggles the direction.
     */
    fun sortBy(newColumnId: String): SortState {
        return if (columnId == newColumnId) {
            val newDirection = direction.toggle()
            if (newDirection == SortDirection.NONE) {
                SortState()
            } else {
                copy(direction = newDirection)
            }
        } else {
            SortState(columnId = newColumnId, direction = SortDirection.ASCENDING)
        }
    }
    
    companion object {
        val Unsorted = SortState()
    }
}
