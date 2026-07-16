package com.example.todolistjetpackcompose.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.todolistjetpackcompose.roomDatabase.Task
import com.example.todolistjetpackcompose.viewModel.TaskViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * AddTaskScreen: หน้าจอสำหรับเพิ่มรายการงาน (Task) ใหม่
 * ประกอบด้วยฟอร์มกรอกชื่อ, เลือกหมวดหมู่, วันที่, ระดับความสำคัญ และบันทึกเพิ่มเติม
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddTaskScreen(
    navController: NavController,
    viewModel: TaskViewModel,
    onSave: () -> Unit = {},
    onBack: () -> Unit
) {
    // --- 1. การจัดการสถานะ (State Management) ---
    // ใช้ rememberSaveable เพื่อให้ข้อมูลยังอยู่แม้มีการหมุนหน้าจอ
    var title by rememberSaveable { mutableStateOf("") }
    var category by rememberSaveable { mutableStateOf("") }
    var dueDate by rememberSaveable { mutableStateOf("") }
    var notes by rememberSaveable { mutableStateOf("") }
    var isCompleted by rememberSaveable { mutableStateOf(false) }

    // ตัวเลือกสำหรับระดับความสำคัญ
    val options = listOf("Low", "Medium", "High")
    var priority by rememberSaveable { mutableStateOf(options[0]) }
    var selectedIndex by rememberSaveable { mutableIntStateOf(0) }

    // --- 2. ตัวจัดการการดำเนินการ (Action Handlers) ---
    // ฟังก์ชันสำหรับบันทึกงานใหม่ลงฐานข้อมูล
    val onSaveTask = {
        if (title.isNotBlank()) {
            val newTask = Task(
                id = null, // ID เป็น null เพื่อให้ Room รันตัวเลขให้อัตโนมัติ
                title = title,
                category = category,
                dueDate = dueDate,
                priority = priority,
                notes = notes,
                isCompleted = isCompleted
            )
            viewModel.addTask(newTask)
            onSave()
            navController.popBackStack()
        }
    }

    // --- 3. โครงสร้าง Layout (Layout Structure) ---
    Scaffold(
        topBar = { AddTaskTopBar(onBack, onSaveTask) },
        bottomBar = { AddTaskBottomBar(onSaveTask) }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()), // ทำให้หน้าจอเลื่อนขึ้นลงได้
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // ส่วนกรอกชื่อหัวข้องาน
            TaskTitleInput(title) { title = it }

            // ส่วนเลือกหมวดหมู่
            CategorySelection(category) { category = it }

            // ส่วนเลือกวันที่ครบกำหนด
            DueDatePickerField(dueDate) { dueDate = it }

            // ส่วนเลือกระดับความสำคัญ
            PrioritySelection(options, selectedIndex) { index, label ->
                selectedIndex = index
                priority = label
            }

            // ส่วนกรอกรายละเอียดเพิ่มเติม
            NotesInput(notes) { notes = it }
        }
    }
}

// --- 4. ส่วนประกอบย่อยของ UI (Sub-Composables) ---

/**
 * แถบด้านบนของหน้าจอเพิ่มงาน
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AddTaskTopBar(onBack: () -> Unit, onSave: () -> Unit) {
    CenterAlignedTopAppBar(
        title = { Text("Add New Task", style = MaterialTheme.typography.titleLarge) },
        windowInsets = WindowInsets(0, 0, 0, 0),
        navigationIcon = {
            TextButton(onClick = onBack) { Text("Cancel") }
        },
        actions = {
            TextButton(onClick = onSave) { Text("Save") }
        }
    )
}

/**
 * ปุ่มสร้างงานที่อยู่ด้านล่างสุดของหน้าจอ
 */
@Composable
private fun AddTaskBottomBar(onSave: () -> Unit) {
    Button(
        onClick = onSave,
        modifier = Modifier.fillMaxWidth().padding(16.dp),
        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF7B61FF))
    ) {
        Text("• Create Task")
    }
}

/**
 * ช่องกรอกชื่อรายการงาน
 */
@Composable
private fun TaskTitleInput(title: String, onValueChange: (String) -> Unit) {
    Column {
        Text("Task Title", style = MaterialTheme.typography.labelLarge)
        TextField(
            value = title,
            onValueChange = onValueChange,
            placeholder = { Text("Enter task name", style = MaterialTheme.typography.bodyMedium) },
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp)
                .padding(horizontal = 4.dp)
                .shadow(elevation = 6.dp, shape = RoundedCornerShape(12.dp), clip = false)
                .background(MaterialTheme.colorScheme.surface, shape = RoundedCornerShape(12.dp))
                .border(width = 1.dp, color = MaterialTheme.colorScheme.outlineVariant, shape = RoundedCornerShape(12.dp)),
            colors = TextFieldDefaults.colors(
                focusedContainerColor = Color.Transparent,
                unfocusedContainerColor = Color.Transparent,
                focusedTextColor = MaterialTheme.colorScheme.onSurface,
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent,
            ),
            textStyle = MaterialTheme.typography.bodyMedium,
            shape = RoundedCornerShape(12.dp),
            singleLine = true
        )
    }
}

/**
 * เมนูแบบ Dropdown สำหรับเลือกหมวดหมู่ของงาน
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CategorySelection(selectedCategory: String, onCategoryChange: (String) -> Unit) {
    var expanded by remember { mutableStateOf(false) }
    val categories = listOf("Work", "Personal", "Shopping")

    Column {
        Text("Category", style = MaterialTheme.typography.labelLarge)
        ExposedDropdownMenuBox(
            expanded = expanded,
            onExpandedChange = { expanded = !expanded },
            modifier = Modifier.fillMaxWidth()
        ) {
            TextField(
                value = selectedCategory,
                onValueChange = {},
                readOnly = true,
                placeholder = { Text("Select Category", style = MaterialTheme.typography.bodyMedium) },
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                modifier = Modifier
                    .menuAnchor(type = MenuAnchorType.PrimaryNotEditable, enabled = true)
                    .height(50.dp)
                    .fillMaxWidth()
                    .padding(horizontal = 4.dp)
                    .shadow(elevation = 6.dp, shape = RoundedCornerShape(12.dp))
                    .background(MaterialTheme.colorScheme.surface, shape = RoundedCornerShape(12.dp))
                    .border(width = 1.dp, color = Color.LightGray.copy(alpha = 0.5f), shape = RoundedCornerShape(12.dp)),
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color.Transparent,
                    unfocusedContainerColor = Color.Transparent,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                    disabledIndicatorColor = Color.Transparent
                ),
                textStyle = MaterialTheme.typography.bodyMedium,
                shape = RoundedCornerShape(12.dp)
            )
            ExposedDropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false },
                modifier = Modifier.background(MaterialTheme.colorScheme.surfaceVariant),
            ) {
                categories.forEach { option ->
                    DropdownMenuItem(
                        text = { Text(option, style = MaterialTheme.typography.bodyMedium) },
                        onClick = {
                            onCategoryChange(option)
                            expanded = false
                        }
                    )
                }
            }
        }
    }
}

/**
 * ช่องแสดงวันที่ พร้อมระบบเลือกวันที่ (DatePicker)
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DueDatePickerField(dueDate: String, onDateChange: (String) -> Unit) {
    val datePickerState = rememberDatePickerState()
    var showDatePicker by remember { mutableStateOf(false) }
    val dateFormatter = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
    val interactionSource = remember { MutableInteractionSource() }

    Column {
        Text("Due date", style = MaterialTheme.typography.labelLarge)
        if (showDatePicker) {
            DatePickerDialog(
                onDismissRequest = { showDatePicker = false },
                confirmButton = {
                    TextButton(onClick = {
                        datePickerState.selectedDateMillis?.let { millis ->
                            onDateChange(dateFormatter.format(Date(millis)))
                        }
                        showDatePicker = false
                    }) { Text("OK") }
                },
                dismissButton = {
                    TextButton(onClick = { showDatePicker = false }) { Text("Cancel") }
                }
            ) {
                DatePicker(state = datePickerState)
            }
        }

        TextField(
            value = dueDate,
            onValueChange = { },
            placeholder = { Text("Select Date", style = MaterialTheme.typography.bodyMedium) },
            readOnly = true,
            interactionSource = interactionSource.also { source ->
                LaunchedEffect(source) {
                    source.interactions.collect {
                        if (it is androidx.compose.foundation.interaction.PressInteraction.Release) {
                            showDatePicker = true
                        }
                    }
                }
            },
            trailingIcon = {
                IconButton(onClick = { showDatePicker = true }) {
                    Icon(Icons.Default.DateRange, contentDescription = "Select Date")
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp)
                .padding(horizontal = 4.dp)
                .shadow(elevation = 6.dp, shape = RoundedCornerShape(12.dp))
                .background(MaterialTheme.colorScheme.surface, shape = RoundedCornerShape(12.dp))
                .border(width = 1.dp, color = Color.LightGray.copy(alpha = 0.5f), shape = RoundedCornerShape(12.dp)),
            colors = TextFieldDefaults.colors(
                focusedContainerColor = Color.Transparent,
                unfocusedContainerColor = Color.Transparent,
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent,
                disabledIndicatorColor = Color.Transparent,
                disabledContainerColor = Color.Transparent,
                disabledTextColor = Color.Black
            ),
            textStyle = MaterialTheme.typography.bodyMedium,
            shape = RoundedCornerShape(12.dp)
        )
    }
}

/**
 * แถบเลือกลำดับความสำคัญ (Low/Medium/High) แบบ Segmented Button
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun PrioritySelection(
    options: List<String>,
    selectedIndex: Int,
    onPriorityChange: (Int, String) -> Unit
) {
    Column {
        Text("Priority", style = MaterialTheme.typography.labelLarge)
        SingleChoiceSegmentedButtonRow(modifier = Modifier.fillMaxWidth().height(32.dp)) {
            options.forEachIndexed { index, label ->
                val priorityColor = when (index) {
                    0 -> Color(0xFFFFD54F) // Low - สีเหลือง
                    1 -> Color(0xFFE1BBC7) // Medium - สีชมพู
                    2 -> Color(0xFFEE1717) // High - สีแดง
                    else -> Color.Gray
                }
                SegmentedButton(
                    shape = SegmentedButtonDefaults.itemShape(index = index, count = options.size),
                    onClick = { onPriorityChange(index, label) },
                    selected = index == selectedIndex,
                    label = {
                        Text(text = label, style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Medium), maxLines = 1)
                    },
                    colors = SegmentedButtonDefaults.colors(
                        activeContainerColor = Color.White,
                        activeContentColor = Color.Black,
                        activeBorderColor = Color.Transparent,
                        inactiveContainerColor = priorityColor,
                        inactiveContentColor = Color.Black,
                        inactiveBorderColor = Color.Transparent
                    )
                )
            }
        }
    }
}

/**
 * ช่องกรอกรายละเอียดหรือบันทึกเพิ่มเติม
 */
@Composable
private fun NotesInput(notes: String, onValueChange: (String) -> Unit) {
    Column {
        Text("Notes", style = MaterialTheme.typography.labelLarge)
        TextField(
            value = notes,
            onValueChange = onValueChange,
            placeholder = { Text("Add extra details", style = MaterialTheme.typography.bodyMedium) },
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 10.dp)
                .height(140.dp)
                .shadow(elevation = 6.dp, shape = RoundedCornerShape(12.dp), clip = false)
                .background(MaterialTheme.colorScheme.surface, shape = RoundedCornerShape(12.dp))
                .border(width = 1.dp, color = Color.LightGray.copy(alpha = 0.5f), shape = RoundedCornerShape(12.dp)),
            colors = TextFieldDefaults.colors(
                focusedContainerColor = Color.Transparent,
                focusedIndicatorColor = Color.Transparent,
                unfocusedContainerColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent,
                disabledIndicatorColor = Color.Transparent
            ),
            textStyle = MaterialTheme.typography.bodyMedium,
            shape = RoundedCornerShape(12.dp),
            singleLine = false
        )
    }
}
