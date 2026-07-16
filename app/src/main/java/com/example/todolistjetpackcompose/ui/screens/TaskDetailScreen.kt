package com.example.todolistjetpackcompose.ui.screens

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.todolistjetpackcompose.roomDatabase.Task
import com.example.todolistjetpackcompose.viewModel.TaskViewModel

/**
 * TaskDetailScreen: หน้าจอสำหรับแสดงรายละเอียดเชิงลึกของแต่ละงาน
 * ประกอบด้วยข้อมูลชื่อหมวดหมู่ วันที่ครบกำหนด ระดับความสำคัญ และบันทึกเพิ่มเติม
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TaskDetailScreen(
    onMarkComplete: (Task) -> Unit, // Callback เมื่อกดปุ่มทำเสร็จแล้ว
    onDelete: (Task) -> Unit,       // Callback เมื่อกดปุ่มลบงาน
    onBack: () -> Unit,             // Callback เมื่อกดปุ่มย้อนกลับ
    viewModel: TaskViewModel        // ViewModel สำหรับดึงข้อมูล
) {
    // ติดตามข้อมูลรายละเอียดงานจาก ViewModel
    val taskDetailsFromViewModel = viewModel.taskDetails.collectAsState()
    val task = taskDetailsFromViewModel.value

    // หากข้อมูลงานยังเป็น null (ยังโหลดไม่เสร็จ) ให้แสดงหน้า Loading
    if (task == null) {
        LoadingSection()
    } else {
        Scaffold(
            containerColor = MaterialTheme.colorScheme.background,
            topBar = { TaskDetailTopBar(onBack) },
            bottomBar = { TaskDetailBottomBar(task, onMarkComplete, onDelete) }
        ) { innerPadding ->
            // แสดงเนื้อหาหลักของงาน
            TaskDetailContent(innerPadding, task)
        }
    }
}

// --- Sub-Composables (UI Components) ---

/**
 * ส่วนแสดงสถานะการโหลดข้อมูล
 */
@Composable
private fun LoadingSection() {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
    }
}

/**
 * แถบเครื่องมือด้านบน พร้อมชื่อหน้าและปุ่มย้อนกลับ
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TaskDetailTopBar(onBack: () -> Unit) {
    CenterAlignedTopAppBar(
        title = { Text("Task Details", style = MaterialTheme.typography.titleLarge) },
        windowInsets = WindowInsets(0, 0, 0, 0),
        navigationIcon = {
            IconButton(onClick = onBack) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
            }
        },
    )
}

/**
 * แถบปุ่มกดด้านล่าง (Action Buttons) สำหรับจัดการงาน
 */
@Composable
private fun TaskDetailBottomBar(
    task: Task,
    onMarkComplete: (Task) -> Unit,
    onDelete: (Task) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        // ปุ่ม "Mark as Complete"
        Button(
            contentPadding = PaddingValues(0.dp),
            onClick = { onMarkComplete(task) },
            modifier = Modifier.weight(1f),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary
            )
        ) {
            Text("Mark as Complete", maxLines = 1)
        }
        // ปุ่ม "Delete Task"
        Button(
            onClick = { onDelete(task) },
            modifier = Modifier.weight(1f),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.error,
                contentColor = MaterialTheme.colorScheme.onError
            )
        ) {
            Text("Delete Task", maxLines = 1)
        }
    }
}

/**
 * ส่วนรวบรวมเนื้อหารายละเอียดทั้งหมดของงาน
 */
@Composable
private fun TaskDetailContent(innerPadding: PaddingValues, task: Task) {
    Column(
        modifier = Modifier
            .padding(innerPadding)
            .padding(16.dp)
            .fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // ชื่อหัวข้องาน
        Text(
            text = task.title,
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground
        )

        // หมวดหมู่ของงาน
        CategoryChip(task.category)

        // วันที่ครบกำหนด
        InfoRow(label = "Due Date: ", value = task.dueDate)

        // ระดับความสำคัญ
        PrioritySection(task.priority)

        // บันทึกเพิ่มเติม
        NotesSection(task.notes)
    }
}

/**
 * ส่วนแสดงหมวดหมู่ในรูปแบบ Chip
 */
@Composable
private fun CategoryChip(category: String) {
    // กำหนดสีตามธีมระบบ (Light/Dark)
    val categoryColor = if (isSystemInDarkTheme()) Color(0xFF90CAF9) else Color(0xFF1976D2)
    AssistChip(
        onClick = {},
        label = { Text(text = category, fontWeight = FontWeight.SemiBold) },
        colors = AssistChipDefaults.assistChipColors(
            labelColor = categoryColor,
            containerColor = categoryColor.copy(alpha = 0.15f)
        ),
        border = AssistChipDefaults.assistChipBorder(
            enabled = true,
            borderColor = categoryColor.copy(alpha = 0.4f),
            borderWidth = 1.dp
        )
    )
}

/**
 * ฟังก์ชันช่วยแสดงข้อมูลแบบ Label และ Value ในบรรทัดเดียวกัน
 */
@Composable
private fun InfoRow(label: String, value: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Text(text = label, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
        Text(text = value, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}

/**
 * ส่วนแสดงระดับความสำคัญ พร้อมกำหนดสีตามระดับ (High/Medium/Low)
 */
@Composable
private fun PrioritySection(priority: String) {
    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(text = "Priority : ", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)

        val isDark = isSystemInDarkTheme()
        // กำหนดสีของระดับความสำคัญ
        val priorityColor = when (priority.uppercase()) {
            "HIGH" -> if (isDark) Color(0xFFFF8A80) else Color.Red
            "MEDIUM" -> if (isDark) Color(0xFFFFB74D) else Color(0xFFF57C00)
            "LOW" -> if (isDark) Color(0xFF81C784) else Color(0xFF388E3C)
            else -> MaterialTheme.colorScheme.outline
        }

        AssistChip(
            onClick = {},
            label = { Text(priority, fontWeight = FontWeight.Bold) },
            colors = AssistChipDefaults.assistChipColors(
                labelColor = priorityColor,
                containerColor = priorityColor.copy(alpha = 0.15f)
            ),
            border = AssistChipDefaults.assistChipBorder(enabled = true, borderColor = priorityColor.copy(alpha = 0.4f))
        )
    }
}

/**
 * ส่วนแสดงบันทึกเพิ่มเติมของงาน
 */
@Composable
private fun NotesSection(notes: String) {
    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        Text(text = "Notes", style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.onSurface)
        Text(text = " • $notes", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}
