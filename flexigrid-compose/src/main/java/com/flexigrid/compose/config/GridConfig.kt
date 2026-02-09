package com.flexigrid.compose.config

import androidx.compose.runtime.Immutable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.flexigrid.compose.model.RowBackground

/**
 * Divider configuration for grid lines.
 */
@Immutable
data class DividerConfig(
    val thickness: Dp = 1.dp,
    val showHorizontalDividers: Boolean = true,
    val showVerticalDividers: Boolean = true,
    val showHeaderVerticalDivider: Boolean = true,
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
 * Scrolling behavior configuration.
 */
@Immutable
data class ScrollConfig(
    val horizontalScrollEnabled: Boolean = true,
    val verticalScrollEnabled: Boolean = true
) {
    companion object {
        val Default = ScrollConfig()
        val HorizontalOnly = ScrollConfig(verticalScrollEnabled = false)
        val VerticalOnly = ScrollConfig(horizontalScrollEnabled = false)
        val Disabled = ScrollConfig(
            horizontalScrollEnabled = false,
            verticalScrollEnabled = false
        )
    }
}

/**
 * Sticky element behavior configuration.
 */
@Immutable
data class StickyConfig(
    val stickyHeader: Boolean = true,
    val stickyColumnId: String? = null
) {
    companion object {
        val Default = StickyConfig()
        val None = StickyConfig(stickyHeader = false, stickyColumnId = null)
    }
}

/**
 * Size and spacing configuration.
 */
@Immutable
data class SizingConfig(
    val rowHeight: Dp = 52.dp,
    val headerHeight: Dp = 56.dp,
    val rowSpacing: Dp = 0.dp
) {
    init {
        require(rowHeight > 0.dp) { "rowHeight must be positive" }
        require(headerHeight > 0.dp) { "headerHeight must be positive" }
        require(rowSpacing >= 0.dp) { "rowSpacing must be non-negative" }
    }

    companion object {
        val Default = SizingConfig()
        val Compact = SizingConfig(rowHeight = 40.dp, headerHeight = 44.dp)
        val Comfortable = SizingConfig(rowHeight = 64.dp, headerHeight = 68.dp)
    }
}

/**
 * Row styling defaults configuration.
 */
@Immutable
data class HeaderStyleConfig(
    val backgroundColor: Color? = Color.White,
) {
    companion object {
        val Default = HeaderStyleConfig()
    }
}

/**
 * Row styling defaults configuration.
 */
@Immutable
data class RowStyleConfig(
    val rowShape: Shape = RectangleShape,
    val defaultRowBackground: RowBackground? = RowBackground.Color(Color.White),
) {
    companion object {
        val Default = RowStyleConfig()
    }
}

@Immutable
data class DividerStyleConfig(
    val horizontalDividerColor: Color = Color.LightGray,
    val verticalDividerColor: Color = Color.LightGray
) {
    companion object {
        val Default = DividerStyleConfig()
    }
}

/**
 * Immutable configuration for the FlexiGrid behavior and appearance.
 *
 * All settings have sensible defaults for typical table use cases.
 * The library inherits theming from the consuming app's Material theme.
 *
 * @param scroll Scrolling behavior configuration
 * @param sticky Sticky element behavior configuration
 * @param sizing Size and spacing configuration
 * @param headerStyle Header styling defaults configuration
 * @param rowStyle Row styling defaults configuration
 * @param dividers Divider line configuration
 * @param enableSortAnimation Whether to animate row reordering on sort
 * @param enableResizeByDrag Whether columns can be resized by dragging dividers
 * @param clipToBounds Whether to clip content to grid bounds
 */
@Immutable
data class GridConfig(
    val scroll: ScrollConfig = ScrollConfig.Default,
    val sticky: StickyConfig = StickyConfig.Default,
    val sizing: SizingConfig = SizingConfig.Default,
    val dividers: DividerConfig = DividerConfig.Default,
    val headerStyle: HeaderStyleConfig = HeaderStyleConfig.Default,
    val rowStyle: RowStyleConfig = RowStyleConfig.Default,
    val dividerStyle: DividerStyleConfig = DividerStyleConfig.Default,
    val enableSortAnimation: Boolean = true,
    val enableResizeByDrag: Boolean = false,
    val clipToBounds: Boolean = true
) {
    companion object {
        /**
         * Default configuration with sticky header enabled.
         */
        val Default = GridConfig()

        /**
         * Configuration for simple tables without sticky elements.
         */
        val Simple = GridConfig(
            sticky = StickyConfig.None,
            enableSortAnimation = false
        )

        /**
         * Compact configuration with smaller row heights.
         */
        val Compact = GridConfig(
            sizing = SizingConfig.Compact
        )

        /**
         * Comfortable configuration with larger row heights.
         */
        val Comfortable = GridConfig(
            sizing = SizingConfig.Comfortable
        )
    }
}
