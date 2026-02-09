package com.bitfield.flexigrid

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.bitfield.flexigrid.ui.theme.FlexiGridTheme
import com.flexigrid.compose.config.GridConfig
import com.flexigrid.compose.config.StickyConfig
import com.flexigrid.compose.layout.FlexiGrid
import com.flexigrid.compose.model.ColumnWidth
import com.flexigrid.compose.model.GridColumn
import com.flexigrid.compose.renderers.GridCellRenderers
import com.flexigrid.compose.state.rememberGridState

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            // Theme State
            var isDarkTheme by remember { mutableStateOf(false) }
            
            FlexiGridTheme(darkTheme = isDarkTheme, dynamicColor = false) {
                GridDemoScreen(
                    isDarkTheme = isDarkTheme,
                    onThemeToggle = { isDarkTheme = !isDarkTheme }
                )
            }
        }
    }
}

// Sample data model
data class Employee(
    val id: Int,
    val name: String,
    val email: String,
    val department: String,
    val city: String,
    val salary: Double,
    val status: EmployeeStatus,
    val quantity: Int = 0 
)

enum class EmployeeStatus(val displayName: String, val color: Color) {
    ACTIVE("Active", Color(0xFF4CAF50)),
    ON_LEAVE("On Leave", Color(0xFFFF9800)),
    REMOTE("Remote", Color(0xFF2196F3)),
    INACTIVE("Inactive", Color(0xFF9E9E9E))
}

enum class WidthStrategy { FIXED, CONTENT }
enum class StickyMode { NONE, LEFT, MIDDLE, RIGHT }

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GridDemoScreen(
    isDarkTheme: Boolean,
    onThemeToggle: () -> Unit
) {
    val gridState = rememberGridState()
    
    // UI State for filters
    var widthStrategy by remember { mutableStateOf(WidthStrategy.FIXED) }
    var stickyMode by remember { mutableStateOf(StickyMode.MIDDLE) }
    
    // Generate sample data (Stateful list for editing)
    val employees = remember {
        mutableStateListOf<Employee>().apply {
            addAll(generateSampleEmployees(100))
        }
    }
    
    // Define columns based on selected width strategy
    val columns = remember(widthStrategy) {
        val cols = mutableListOf<GridColumn<Employee>>()
        
        fun getWidth(fixed: Dp, min: Dp = 40.dp): ColumnWidth = when (widthStrategy) {
            WidthStrategy.FIXED -> ColumnWidth.Fixed(fixed)
            WidthStrategy.CONTENT -> ColumnWidth.ContentBased(minWidth = min)
        }
        
        // 1. ID Column (Left candidate)
        cols.add(
            GridColumn(
                id = "id", // "id"
                title = "ID",
                width = getWidth(60.dp),
                sortable = true,
                comparator = compareBy { it.id }
            ) { employee, _ ->
                GridCellRenderers.NumericCell(value = employee.id.toString())
            }
        )
        
        // 2-6. Filler columns
        for (i in 2..6) {
            cols.add(
                GridColumn(
                    id = "col_$i",
                    title = "Col $i",
                    width = getWidth(100.dp)
                ) { employee, _ ->
                     GridCellRenderers.TextCell(text = "Data $i-${employee.id}")
                }
            )
        }
        
        // 7. Middle Candidate (Sticky Middle)
        cols.add(
            GridColumn(
                id = "sticky_middle",
                title = "Middle Sticky",
                width = getWidth(120.dp),
                sortable = true,
                comparator = compareBy { it.email } 
            ) { employee, _ ->
                GridCellRenderers.StatusBadgeCell(
                    text = "Middle",
                    backgroundColor = MaterialTheme.colorScheme.tertiary
                )
            }
        )
        
        // 8-19. More columns
        for (i in 8..19) {
           cols.add(
                GridColumn(
                    id = "col_$i",
                    title = "Col $i",
                    width = getWidth(200.dp)
                ) { employee, _ ->
                     GridCellRenderers.TextCell(text = "Data Width $i-${employee.id}")
                }
            )
        }
        
        // 20. Right Candidate
        cols.add(
            GridColumn(
                id = "right_col",
                title = "RIGHT (20)",
                width = getWidth(110.dp),
                sortable = true
            ) { _, _ ->
                GridCellRenderers.StatusBadgeCell(
                    text = "Right",
                    backgroundColor = MaterialTheme.colorScheme.secondary
                )
            }
        )
        
        cols
    }
    
    // Determine sticky ID based on mode
    val stickyId = when (stickyMode) {
        StickyMode.NONE -> null
        StickyMode.LEFT -> "id"
        StickyMode.MIDDLE -> "sticky_middle"
        StickyMode.RIGHT -> "right_col"
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("FlexiGrid", style = MaterialTheme.typography.titleLarge) },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                    titleContentColor = MaterialTheme.colorScheme.onSurface,
                    scrolledContainerColor = MaterialTheme.colorScheme.surfaceContainer
                ),
                actions = {
                    IconButton(onClick = onThemeToggle) {
                        Icon(
                            imageVector = if (isDarkTheme) Icons.Default.LightMode else Icons.Default.DarkMode,
                            contentDescription = "Toggle Theme"
                        )
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            // Controls Area
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Text(
                    text = "Configuration", 
                    style = MaterialTheme.typography.labelLarge, 
                    color = MaterialTheme.colorScheme.primary
                )
                
                Row(
                    modifier = Modifier.padding(top = 8.dp).horizontalScroll(rememberScrollState())
                ) {
                    Text(
                        "Width:", 
                        style = MaterialTheme.typography.bodyMedium, 
                        modifier = Modifier.padding(end = 8.dp).align(androidx.compose.ui.Alignment.CenterVertically)
                    )
                    WidthStrategy.entries.forEach { strategy ->
                        FilterChip(
                            selected = widthStrategy == strategy,
                            onClick = { widthStrategy = strategy },
                            label = { Text(strategy.name) },
                            modifier = Modifier.padding(end = 8.dp)
                        )
                    }
                }
                
                Row(
                    modifier = Modifier.padding(top = 4.dp).horizontalScroll(rememberScrollState())
                ) {
                    Text(
                        "Sticky:", 
                        style = MaterialTheme.typography.bodyMedium, 
                        modifier = Modifier.padding(end = 8.dp).align(androidx.compose.ui.Alignment.CenterVertically)
                    )
                    StickyMode.entries.forEach { mode ->
                        FilterChip(
                            selected = stickyMode == mode,
                            onClick = { stickyMode = mode },
                            label = { Text(mode.name) },
                            modifier = Modifier.padding(end = 8.dp)
                        )
                    }
                }
            }

            HorizontalDivider()
            
            Text(
                text = "${employees.size} Items • ${columns.size} Columns",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)
            )
            
            FlexiGrid(
                columns = columns,
                items = employees,
                config = GridConfig(
                    sticky = StickyConfig(stickyHeader = true, stickyColumnId = stickyId),
                    enableSortAnimation = true
                ),
                state = gridState,
                modifier = Modifier.fillMaxSize()
            )
        }
    }
}
private fun generateSampleEmployees(count: Int): List<Employee> {
    val firstNames = listOf("Alice", "Bob", "Charlie", "Diana", "Edward", "Fiona", "George", "Hannah", "Ivan", "Julia")
    val lastNames = listOf("Smith", "Johnson", "Williams", "Brown", "Jones", "Garcia", "Miller", "Davis", "Martinez", "Anderson")
    val departments = listOf("Engineering", "Marketing", "Sales", "HR", "Finance", "Design", "Product", "Support")
    val cities = listOf("New York", "San Francisco", "Chicago", "Seattle", "Austin", "Boston", "Denver", "Miami")
    val statuses = EmployeeStatus.entries
    
    return (1..count).map { id ->
        val firstName = firstNames.random()
        val lastName = lastNames.random()
        Employee(
            id = id,
            name = "$firstName $lastName",
            email = "${firstName.lowercase()}.${lastName.lowercase()}@company.com",
            department = departments.random(),
            city = cities.random(),
            salary = (50000..150000).random().toDouble(),
            status = statuses.random(),
            quantity = (0..100).random()
        )
    }
}

@Preview(showBackground = true)
@Composable
fun FlexiGridDemoPreview() {
    FlexiGridTheme {
        GridDemoScreen(isDarkTheme = false, onThemeToggle = {})
    }
}