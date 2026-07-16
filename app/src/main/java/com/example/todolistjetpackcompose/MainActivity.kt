package com.example.todolistjetpackcompose

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import com.example.todolistjetpackcompose.ui.theme.TodoListJetpackComposeTheme
import com.example.todolistjetpackcompose.ui.screens.ToDoListScreen
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.todolistjetpackcompose.viewModel.TaskViewModel
import androidx.compose.runtime.getValue

/**
 * MainActivity: จุดเริ่มต้นหลักของแอปพลิเคชัน
 */
class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // ส่วนที่ 1: การตั้งค่าระบบ UI (System UI Configuration)
        setupSystemUI()

        // ส่วนที่ 2: การแสดงผลเนื้อหา (Compose Content)
        setContent {
            TodoAppMain()
        }
    }

    /**
     * ฟังก์ชันสำหรับตั้งค่า Edge-to-Edge และการซ่อน Status Bar
     */
    private fun setupSystemUI() {
        enableEdgeToEdge()
        val windowInsetsController = WindowCompat.getInsetsController(window, window.decorView)
        windowInsetsController.hide(WindowInsetsCompat.Type.statusBars())
        windowInsetsController.systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
    }

    /**
     * Composable หลักที่จัดการ Theme และ ViewModel
     */
    @Composable
    private fun TodoAppMain() {
        val taskViewModel: TaskViewModel = viewModel()
        val isDarkMode by taskViewModel.isDarkMode.collectAsState()

        TodoListJetpackComposeTheme(darkTheme = isDarkMode) {
            TodoAppContent(taskViewModel)
        }
    }

    /**
     * Composable สำหรับโครงสร้าง Scaffold และการจัดการ Navigation
     */
    @Composable
    private fun TodoAppContent(viewModel: TaskViewModel) {
        Scaffold(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.surface)
                .padding(10.dp)
        ) { innerPadding ->
            Box(modifier = Modifier.padding(innerPadding)) {
                ToDoListScreen(
                    viewModel = viewModel,
                    onAddTask = { navigateToAddTask() },
                    onTaskClick = { task -> navigateToTaskDetail(task.id, task.title) },
                    onRefresh = { refreshActivity() }
                )
            }
            Log.d("MainActivity", "Padding: $innerPadding")
        }
    }

    // --- ส่วนของฟังก์ชันการนำทาง (Navigation Functions) ---

    private fun navigateToAddTask() {
        val intent = Intent(this, AddTaskActivity::class.java)
        startActivity(intent)
    }

    private fun navigateToTaskDetail(taskId: Int?, taskTitle: String) {
        val intent = Intent(this, TaskDetailActivity::class.java).apply {
            putExtra("TASK_ID", taskId)
            putExtra("TASK_TILE", taskTitle)
        }
        startActivity(intent)
        Log.d("Navigation", "Navigating to detail of: $taskTitle")
    }

    private fun refreshActivity() {
        finish()
        startActivity(Intent(this, MainActivity::class.java))
    }
}
