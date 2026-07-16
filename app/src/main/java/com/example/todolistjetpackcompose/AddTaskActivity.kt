package com.example.todolistjetpackcompose

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.rememberNavController
import com.example.todolistjetpackcompose.ui.screens.AddTaskScreen
import com.example.todolistjetpackcompose.viewModel.TaskViewModel
import com.example.todolistjetpackcompose.ui.theme.TodoListJetpackComposeTheme

/**
 * AddTaskActivity: จัดการการเพิ่มข้อมูล Task ใหม่
 */
class AddTaskActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // --- ส่วนที่ 1: System UI Setup ---
        configureSystemUI()

        setContent {
            // --- ส่วนที่ 2: Root Composable ---
            AddTaskRoot()
        }
    }

    /**
     * ตั้งค่า Edge-to-Edge และซ่อน Status Bar
     */
    private fun configureSystemUI() {
        enableEdgeToEdge()
        val windowInsetsController = WindowCompat.getInsetsController(window, window.decorView)
        windowInsetsController.hide(WindowInsetsCompat.Type.statusBars())
        windowInsetsController.systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
    }

    /**
     * คอมโพสซเบิลหลักที่จัดการ Theme และเรียกใช้ Layout
     */
    @Composable
    private fun AddTaskRoot() {
        val viewModel: TaskViewModel = viewModel()
        val isDarkMode by viewModel.isDarkMode.collectAsState()

        TodoListJetpackComposeTheme(darkTheme = isDarkMode) {
            AddTaskScaffold(viewModel)
        }
    }

    /**
     * โครงสร้างหน้าจอ Scaffold
     */
    @Composable
    private fun AddTaskScaffold(viewModel: TaskViewModel) {
        Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
            Box(modifier = Modifier.padding(innerPadding)) {
                AddTaskScreen(
                    navController = rememberNavController(),
                    viewModel = viewModel,
                    onSave = { handleSaveSuccess() },
                    onBack = { handleBackPress() }
                )
            }
        }
    }

    // --- ส่วนที่ 3: Action Handlers (Logic & Navigation) ---

    /**
     * จัดการเมื่อบันทึกข้อมูลสำเร็จ
     */
    private fun handleSaveSuccess() {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()
        Log.d("AddTaskActivity", "Task saved successfully")
    }

    /**
     * จัดการการกดปุ่มย้อนกลับ
     */
    private fun handleBackPress() {
        finish()
    }
}
