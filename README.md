# FlexGrid

A high-performance, configurable grid library for Jetpack Compose.

[![API](https://img.shields.io/badge/API-24%2B-brightgreen.svg?style=flat)](https://android-arsenal.com/api?level=24)
[![License](https://img.shields.io/badge/License-Apache%202.0-blue.svg)](LICENSE)

## Features

- 🚀 **Lazy virtualized rendering** for large datasets
- 📌 **Sticky headers and columns** 
- 🔄 **Column sorting** with custom comparators
- 📐 **Flexible column widths** (fixed, weighted, content-based)
- 🎨 **Built-in cell renderers** (text, image, badge, button, etc.)
- 🧩 **Slot-based API** for custom cells
- ♿ **Accessibility support**
- 🌙 **Material 3 theming** (inherits from consumer app)

## Installation

```kotlin
// settings.gradle.kts
include(":flexigrid-compose")

// app/build.gradle.kts
dependencies {
    implementation(project(":flexigrid-compose"))
}
```

## Quick Start

```kotlin
data class Person(val id: Int, val name: String, val age: Int)

val columns = listOf(
    GridColumn<Person>(
        id = "name",
        title = "Name",
        width = ColumnWidth.Fixed(150.dp),
        sortable = true,
        comparator = compareBy { it.name }
    ) { person, _ ->
        GridCellRenderers.TextCell(text = person.name)
    },
    GridColumn<Person>(
        id = "age",
        title = "Age",
        width = ColumnWidth.Fixed(80.dp),
        sortable = true,
        comparator = compareBy { it.age }
    ) { person, _ ->
        GridCellRenderers.NumericCell(value = person.age.toString())
    }
)

FlexiGrid(
    columns = columns,
    items = listOf(Person(1, "Alice", 30), Person(2, "Bob", 25)),
    config = GridConfig.Default,
    key = { it.id }
)
```

## Configuration

```kotlin
GridConfig(
    stickyHeader = true,           // Header sticks to top
    stickyFirstColumn = true,      // First column sticks to left
    alternateRowShading = true,    // Zebra striping
    enableSortAnimation = true,    // Animate sort indicator
    rowHeight = 52.dp,
    headerHeight = 56.dp,
    prefetchRowCount = 5,          // Prefetch for smooth scroll
    dividers = DividerConfig.Default
)
```

## Column Width Options

```kotlin
ColumnWidth.Fixed(100.dp)              // Explicit width
ColumnWidth.Flexible(weight = 1f)      // Fills space by weight
ColumnWidth.ContentBased(              // Measures content
    minWidth = 40.dp,
    maxWidth = 300.dp
)
```

## Built-in Renderers

```kotlin
GridCellRenderers.TextCell(text = "Hello")
GridCellRenderers.TwoLineCell(primary = "Title", secondary = "Subtitle")
GridCellRenderers.ImageCell(imageUrl = "https://...")
GridCellRenderers.IconTextCell(icon = Icons.Default.Star, text = "Favorite")
GridCellRenderers.ButtonCell(text = "Action") { /* onClick */ }
GridCellRenderers.StatusBadgeCell(text = "Active", backgroundColor = Color.Green)
GridCellRenderers.NumericCell(value = "$1,234")
```

## Custom Cells

```kotlin
GridColumn<MyData>(id = "custom", title = "Custom") { item, rowIndex ->
    Row(verticalAlignment = Alignment.CenterVertically) {
        Image(painter = painterResource(R.drawable.icon), "")
        Column {
            Text(item.title, style = MaterialTheme.typography.bodyMedium)
            Text(item.subtitle, style = MaterialTheme.typography.bodySmall)
        }
    }
}
```

## State Management

```kotlin
val gridState = rememberGridState()

// Programmatic scroll
LaunchedEffect(Unit) {
    gridState.animateScrollToRow(50)
}

// React to sort changes
FlexiGrid(
    ...,
    state = gridState,
    onSortChanged = { sortState ->
        Log.d("Grid", "Sorted by ${sortState.columnId} ${sortState.direction}")
    }
)
```

## Performance Tips

1. **Use stable keys**: `key = { it.id }`
2. **Prefer Fixed widths** for known column sizes
3. **Increase prefetch** for large datasets: `prefetchRowCount = 10`
4. **Avoid heavy cell content** - offload to background if needed

## License

```
Copyright 2026 bitfiled66

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
```
