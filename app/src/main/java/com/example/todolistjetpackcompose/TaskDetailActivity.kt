package com.example.todolistjetpackcompose

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.todolistjetpackcompose.ui.screens.TaskDetailScreen
import com.example.todolistjetpackcompose.viewModel.TaskViewModel
import com.example.todolistjetpackcompose.ui.theme.TodoListJetpackComposeTheme

/**
 * TaskDetailActivity: จัดการการแสดงผลและโต้ตอบกับรายละเอียดของ Task
 */
class TaskDetailActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // --- ส่วนที่ 1: System UI Setup ---
        configureSystemUI()

        // รับค่า ID จาก Intent
        val taskId = intent.getIntExtra("TASK_ID", -1)

        setContent {
            // --- ส่วนที่ 2: Root Composable ---
            TaskDetailRoot(taskId)
        }
    }

    /**
     * ตั้งค่า Edge-to-Edge และการซ่อน Status Bar
     */
    private fun configureSystemUI() {
        enableEdgeToEdge()
        val windowInsetsController = WindowCompat.getInsetsController(window, window.decorView)
        windowInsetsController.hide(WindowInsetsCompat.Type.statusBars())
        windowInsetsController.systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
    }

    /**
     * ส่วนจัดการ Theme และ ViewModel
     */
    @Composable
    private fun TaskDetailRoot(taskId: Int) {
        val taskViewModel: TaskViewModel = viewModel()
        val isDarkMode by taskViewModel.isDarkMode.collectAsState()

        TodoListJetpackComposeTheme(darkTheme = isDarkMode) {
            TaskDetailScaffold(taskId, taskViewModel)
        }
    }

    /**
     * โครงสร้าง Scaffold และการเรียกใช้ Screen
     */
    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    private fun TaskDetailScaffold(taskId: Int, viewModel: TaskViewModel) {
        // โหลดข้อมูล Task เมื่อเริ่มต้น
        LaunchedEffect(taskId) {
            if (taskId != -1) {
                viewModel.selectTaskById(taskId)
            }
        }

        Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
            Box(modifier = Modifier.padding(innerPadding)) {
                TaskDetailScreen(
                    onMarkComplete = { handleMarkComplete(taskId, viewModel) },
                    onDelete = { handleDelete(taskId, viewModel) },
                    onBack = { handleBackPress() },
                    viewModel = viewModel,
                )
            }
        }
    }

    // --- ส่วนที่ 3: Action Handlers (Logic & Navigation) ---

    private fun handleMarkComplete(taskId: Int, viewModel: TaskViewModel) {
        viewModel.markComplete(taskId)
        Log.d("TaskDetail", "Task $taskId marked as complete")
        finish()
    }

    private fun handleDelete(taskId: Int, viewModel: TaskViewModel) {
        viewModel.deleteTask(taskId)
        Log.d("TaskDetail", "Task $taskId deleted")
        finish()
    }

    private fun handleBackPress() {
        finish()
    }
}
