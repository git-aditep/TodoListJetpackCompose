package com.example.todolistjetpackcompose.ui.screens

import android.annotation.SuppressLint
import android.content.Intent
import android.util.Log
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.Icons.Default
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Color.Companion.Gray
import androidx.compose.ui.graphics.Color.Companion.White
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.todolistjetpackcompose.SettingsActivity
import com.example.todolistjetpackcompose.roomDatabase.Task
import com.example.todolistjetpackcompose.viewModel.TaskViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * ToDoListScreen: หน้าจอหลักจัดการรายการงานทั้งหมด
 * ประกอบด้วยโครงสร้างหลัก (Drawer, Scaffold) และส่วนประกอบย่อยสำหรับการแสดงผล
 */
@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("CheckResult", "ViewModelConstructorInComposable")
@Composable
fun ToDoListScreen(
    viewModel: TaskViewModel,
    onAddTask: () -> Unit,      // Callback เมื่อกดปุ่มเพิ่มงาน
    onTaskClick: (Task) -> Unit, // Callback เมื่อคลิกที่รายการงานเพื่อดูรายละเอียด
    onRefresh: () -> Unit       // Callback เมื่อทำการ Refresh หน้าจอ
) {
    // --- 1. การจัดการสถานะ (State Management) ---
    val tasks by viewModel.tasks.collectAsState()           // รายการงานทั้งหมด
    val completedCount by viewModel.completedCount.collectAsState() // จำนวนงานที่เสร็จแล้ว
    val isDarkMode by viewModel.isDarkMode.collectAsState() // สถานะ Dark Mode
    var selectedFilter by remember { mutableStateOf("All") } // ตัวกรองหมวดหมู่ที่เลือก

    val scope = rememberCoroutineScope()
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val context = LocalContext.current
    val currentDate = remember { SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date()) }

    // --- 2. โครงสร้าง Layout (Layout Structure) ---
    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            // ส่วนของเมนูด้านข้าง (Side Menu)
            ToDoListDrawer(
                selectedFilter = selectedFilter,
                isDarkMode = isDarkMode,
                onFilterSelected = { 
                    selectedFilter = it
                    scope.launch { drawerState.close() }
                },
                onThemeToggle = { viewModel.toggleTheme(it) }
            )
        }
    ) {
        var isRefreshing by remember { mutableStateOf(false) }

        Scaffold(
            topBar = {
                // ส่วนหัวของหน้าจอ (Top Bar)
                ToDoListTopBar(
                    onMenuClick = { scope.launch { drawerState.open() } },
                    onSettingsClick = { context.startActivity(Intent(context, SettingsActivity::class.java)) }
                )
            },
            bottomBar = {
                // ส่วนล่างของหน้าจอ (ปุ่ม Add Task)
                ToDoListBottomBar(onAddTask)
            }
        ) { innerPadding ->
            Column(modifier = Modifier.fillMaxSize().padding(innerPadding).padding(horizontal = 16.dp)) {
                
                // ส่วนเลือกหมวดหมู่และทักทาย
                CategoryFilterSection(
                    taskCount = tasks.size,
                    selectedFilter = selectedFilter,
                    onFilterChange = { selectedFilter = it }
                )

                Spacer(Modifier.height(16.dp))
                
                // กรองรายการงานตามหมวดหมู่ที่เลือก
                val filteredTasks = if (selectedFilter == "All") tasks else tasks.filter { it.category == selectedFilter }

                // ส่วนแสดงรายการงานพร้อมฟีเจอร์ Pull to Refresh
                TaskListSection(
                    tasks = filteredTasks,
                    isRefreshing = isRefreshing,
                    currentDate = currentDate,
                    onRefresh = {
                        scope.launch {
                            isRefreshing = true
                            viewModel.refreshTask()
                            onRefresh()
                            delay(1000)
                            isRefreshing = false
                        }
                    },
                    onTaskClick = onTaskClick,
                    onStatusChange = { id, checked -> viewModel.updateTaskStatus(id, checked) }
                )

                // สรุปจำนวนงานที่ทำเสร็จแล้ว
                CompletedSummarySection(completedCount)
            }
        }
    }
}

// --- 3. ส่วนประกอบย่อยของ UI (Sub-Composables) ---

/**
 * ส่วนของเมนูด้านข้าง (Navigation Drawer)
 */
@Composable
private fun ToDoListDrawer(
    selectedFilter: String,
    isDarkMode: Boolean,
    onFilterSelected: (String) -> Unit,
    onThemeToggle: (Boolean) -> Unit
) {
    ModalDrawerSheet(
        drawerContainerColor = MaterialTheme.colorScheme.surface,
        drawerShape = RoundedCornerShape(topEnd = 24.dp, bottomEnd = 24.dp),
        modifier = Modifier.width(300.dp)
    ) {
        // ส่วนหัวของ Drawer แสดงสัญลักษณ์แอป
        Column(
            modifier = Modifier.fillMaxWidth().background(Color(0xFF7B61FF)).padding(vertical = 40.dp, horizontal = 24.dp)
        ) {
            Icon(Default.CheckCircle, contentDescription = null, tint = White, modifier = Modifier.size(48.dp))
            Spacer(modifier = Modifier.height(12.dp))
            Text("ToDoList", style = MaterialTheme.typography.headlineSmall, color = White, fontWeight = FontWeight.Bold)
            Text("Organize your life", style = MaterialTheme.typography.bodyMedium, color = White.copy(alpha = 0.8f))
        }

        Spacer(modifier = Modifier.height(12.dp))
        
        // เมนู "All Tasks"
        NavigationDrawerItem(
            label = { Text("All Tasks", style = MaterialTheme.typography.bodyMedium) },
            selected = selectedFilter == "All",
            onClick = { onFilterSelected("All") },
            icon = { Icon(Icons.AutoMirrored.Filled.List, contentDescription = null) },
            colors = NavigationDrawerItemDefaults.colors(
                selectedContainerColor = Color(0xFF7B61FF).copy(alpha = 0.2f),
                selectedIconColor = Color(0xFF7B61FF),
                selectedTextColor = Color(0xFF7B61FF)
            ),
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp)
        )

        // ส่วนการตั้งค่า Theme (Light/Dark Mode)
        Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)) {
            Text("Theme", style = MaterialTheme.typography.labelMedium, color = Gray, modifier = Modifier.padding(start = 8.dp, bottom = 8.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                ThemeButton("Light", !isDarkMode) { onThemeToggle(false) }
                ThemeButton("Dark", isDarkMode) { onThemeToggle(true) }
            }
        }
    }
}

/**
 * ปุ่มสำหรับเลือก Theme ในเมนู Drawer
 */
@Composable
private fun RowScope.ThemeButton(label: String, isSelected: Boolean, onClick: () -> Unit) {
    Button(
        onClick = onClick,
        modifier = Modifier.weight(1f).height(40.dp),
        shape = RoundedCornerShape(12.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = if (isSelected) Color(0xFF7B61FF) else MaterialTheme.colorScheme.surfaceVariant,
            contentColor = if (isSelected) White else MaterialTheme.colorScheme.onSurfaceVariant
        )
    ) {
        Text(label, fontSize = 12.sp)
    }
}

/**
 * แถบด้านบนของหน้าจอหลัก
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ToDoListTopBar(onMenuClick: () -> Unit, onSettingsClick: () -> Unit) {
    CenterAlignedTopAppBar(
        title = { Text("ToDoList", style = MaterialTheme.typography.titleLarge) },
        windowInsets = WindowInsets(0, 0, 0, 0),
        navigationIcon = {
            IconButton(onClick = onMenuClick) { Icon(Default.Menu, contentDescription = "Menu") }
        },
        actions = {
            IconButton(onClick = onSettingsClick) { Icon(Default.Settings, contentDescription = "Setting") }
        }
    )
}

/**
 * ปุ่มด้านล่างสำหรับเพิ่มงานใหม่
 */
@Composable
private fun ToDoListBottomBar(onAddTask: () -> Unit) {
    Button(
        onClick = onAddTask,
        modifier = Modifier.fillMaxWidth().padding(16.dp),
        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF7B61FF))
    ) {
        Text("+ Add Task")
    }
}

/**
 * ส่วนแสดงการทักทายและปุ่มกรองหมวดหมู่ (Categories)
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CategoryFilterSection(taskCount: Int, selectedFilter: String, onFilterChange: (String) -> Unit) {
    val categories = listOf("All", "Work", "Personal", "Shopping")
    Column {
        Text("Hello", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
        Text("You have $taskCount tasks today", color = Gray)
        Spacer(Modifier.height(16.dp))
        Text("Categories", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
        Row(horizontalArrangement = Arrangement.spacedBy(2.dp), modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)) {
            categories.forEach { category ->
                FilterChip(
                    selected = (selectedFilter == category),
                    onClick = { onFilterChange(category) },
                    label = { Text(text = category) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = getCategoryColor(category),
                        selectedLabelColor = White
                    ),
                    border = null
                )
            }
        }
    }
}

/**
 * ส่วนแสดงรายการงานทั้งหมด (Task List)
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ColumnScope.TaskListSection(
    tasks: List<Task>,
    isRefreshing: Boolean,
    currentDate: String,
    onRefresh: () -> Unit,
    onTaskClick: (Task) -> Unit,
    onStatusChange: (Int?, Boolean) -> Unit
) {
    Text("Today's Tasks", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
    PullToRefreshBox(
        isRefreshing = isRefreshing,
        onRefresh = onRefresh,
        modifier = Modifier.weight(1f).fillMaxWidth().padding(vertical = 4.dp)
    ) {
        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            color = MaterialTheme.colorScheme.surface,
            shadowElevation = 4.dp,
            border = BorderStroke(0.5.dp, Gray.copy(alpha = 0.2f))
        ) {
            LazyColumn(modifier = Modifier.fillMaxSize()) {
                items(tasks) { task ->
                    // รายการงานแต่ละชิ้น
                    TaskListItem(task, currentDate, onTaskClick, onStatusChange)
                    HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp), thickness = 0.5.dp, color = Gray.copy(alpha = 0.2f))
                }
            }
        }
    }
}

/**
 * รูปแบบการแสดงผลของแต่ละรายการงาน (Task Item Row)
 */
@Composable
private fun TaskListItem(
    task: Task,
    currentDate: String,
    onTaskClick: (Task) -> Unit,
    onStatusChange: (Int?, Boolean) -> Unit
) {
    //var itemChecked by remember(task.id) { mutableStateOf(task.isCompleted ?: false) }
    Card(
        modifier = Modifier.fillMaxWidth().clickable { onTaskClick(task) },
        shape = RectangleShape,
        colors = CardDefaults.cardColors(containerColor = Color.Transparent)
    ) {
        Row(modifier = Modifier.padding(2.dp), verticalAlignment = Alignment.CenterVertically) {
            // ช่องสำหรับติ๊กถูก (Checkbox)
            Checkbox(
                checked = task.isCompleted ?: false,
                onCheckedChange = { isChecked ->
                    //itemChecked = isChecked
                    onStatusChange(task.id, isChecked)
                }
            )
            // ชื่อของงาน
            Text(task.title,
                modifier = Modifier.padding(start = 8.dp),
                style = MaterialTheme.typography.bodyMedium)
            
            // หากงานครบกำหนดวันนี้ ให้แสดงสัญลักษณ์และข้อความ Due Today
            if (task.dueDate == currentDate) {
                Spacer(Modifier.weight(1f))
                CategoryIcon(task.category)
                Text("Due Today", modifier = Modifier.padding(start = 4.dp), color = Gray, fontSize = 12.sp)
            }
        }
    }
}

/**
 * ไอคอนแสดงตามหมวดหมู่ของงาน
 */
@Composable
private fun CategoryIcon(category: String) {
    val icon = when (category) {
        "Work" -> Default.Build
        "Personal" -> Default.Person
        "Shopping" -> Default.ShoppingCart
        else -> Icons.AutoMirrored.Default.List
    }
    Icon(imageVector = icon, contentDescription = null, tint = Gray, modifier = Modifier.size(20.dp))
}

/**
 * ส่วนสรุปจำนวนงานที่ทำเสร็จแล้วที่ด้านล่างของรายการ
 */
@Composable
private fun CompletedSummarySection(count: Int) {
    Button(
        onClick = { },
        modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
        enabled = false,
        colors = ButtonDefaults.buttonColors(
            disabledContainerColor = Gray.copy(alpha = 0.1f),
            disabledContentColor = Gray
        )
    ) {
        Text("Completed ($count)")
    }
}

/**
 * ฟังก์ชันช่วยกำหนดสีตามหมวดหมู่
 */
private fun getCategoryColor(category: String): Color {
    return when (category) {
        "Work" -> Color(0xFF757575)
        "Personal" -> Color(0xFFFF5252)
        "Shopping" -> Color(0xFFFFA500)
        else -> Color(0xFF2196F3)
    }
}
