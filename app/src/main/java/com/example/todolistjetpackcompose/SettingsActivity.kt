package com.example.todolistjetpackcompose

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.todolistjetpackcompose.ui.screens.SettingsScreen
import com.example.todolistjetpackcompose.ui.theme.TodoListJetpackComposeTheme
import com.example.todolistjetpackcompose.viewModel.TaskViewModel

/**
 * SettingsActivity: จัดการหน้าจอการตั้งค่าของแอปพลิเคชัน
 */
class SettingsActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // --- ส่วนที่ 1: System UI Setup ---
        configureSystemUI()

        setContent {
            // --- ส่วนที่ 2: Root Composable ---
            SettingsRoot()
        }
    }

    /**
     * ตั้งค่า Edge-to-Edge และการจัดการ Status Bar
     */
    private fun configureSystemUI() {
        enableEdgeToEdge()
        val windowInsetsController = WindowCompat.getInsetsController(window, window.decorView)
        windowInsetsController.hide(WindowInsetsCompat.Type.statusBars())
        windowInsetsController.systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
    }

    /**
     * ส่วนจัดการ Theme และการดึงข้อมูลสถานะโหมดมืด
     */
    @Composable
    private fun SettingsRoot() {
        val taskViewModel: TaskViewModel = viewModel()
        val isDarkMode by taskViewModel.isDarkMode.collectAsState()

        TodoListJetpackComposeTheme(darkTheme = isDarkMode) {
            SettingsScaffold()
        }
    }

    /**
     * โครงสร้าง Scaffold สำหรับหน้า Settings
     */
    @Composable
    private fun SettingsScaffold() {
        Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
            // เรียกใช้หน้าจอ SettingsScreen
            SettingsScreen(
                onBack = { handleBackPress() }
            )
            // Log ตรวจสอบระยะห่าง (Optional)
            Log.d("SettingsActivity", "Padding: $innerPadding")
        }
    }

    // --- ส่วนที่ 3: Action Handlers ---

    /**
     * จัดการเมื่อกดปุ่มย้อนกลับ
     */
    private fun handleBackPress() {
        finish()
    }
}
