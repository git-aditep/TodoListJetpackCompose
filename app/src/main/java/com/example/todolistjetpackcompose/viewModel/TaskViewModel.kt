package com.example.todolistjetpackcompose.viewModel

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context.MODE_PRIVATE
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.todolistjetpackcompose.roomDatabase.AppDatabase
import com.example.todolistjetpackcompose.roomDatabase.Task
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import androidx.core.content.edit
import kotlinx.coroutines.reactive.asFlow
import kotlinx.coroutines.withContext

@SuppressLint("CheckResult")
class TaskViewModel(application: Application) : AndroidViewModel(application) {

    private val db = AppDatabase.getDatabase(application)

    private var _tasks = MutableStateFlow<List<Task>>(emptyList())
    val tasks: StateFlow<List<Task>> = _tasks.asStateFlow()

    // จัดการการเก็บค่าการตั้งค่า (SharedPreferences)
    private val prefs = application.getSharedPreferences("settings_prefs", MODE_PRIVATE)

    // สถานะ Dark Mode
    private val _isDarkMode = MutableStateFlow(prefs.getBoolean("dark_mode", false))
    val isDarkMode: StateFlow<Boolean> = _isDarkMode

    // ข้อมูลรายละเอียดของงานแต่ละงาน
    private val _taskDetails = MutableStateFlow<Task?>(null)
    val taskDetails: StateFlow<Task?> = _taskDetails.asStateFlow()

    // สถานะการลบงาน (เก็บจำนวนแถวที่ได้รับผลกระทบ)
    private val _deleteTask = MutableStateFlow(0)
    val deleteTask: MutableStateFlow<Int> = _deleteTask


    init {
        // ดึงข้อมูลงานทั้งหมดเมื่อเริ่มต้น ViewModel
        viewModelScope.launch {
            try {
                db.taskDao().selectAllTasks()
                    .asFlow()
                    .collect { result ->
                        _tasks.value = result
                    }
            } catch (e: Exception) {
                Log.e("TAG", e.message.toString())
            }
        }
    }

    // นับจำนวนงานที่ทำเสร็จแล้ว (Flow)
    val completedCount: StateFlow<Int> = db.taskDao().getCompletedCountFlow()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = 0
        )

    /**
     * เพิ่มงานใหม่ลงในฐานข้อมูล
     */
    fun addTask(task: Task) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                db.taskDao().insertTask(task)
                withContext(Dispatchers.Main) {
                    _tasks.value = _tasks.value + task
                }
            } catch (e : Exception){
                Log.e("TAG", e.message.toString())
            }
        }
    }

    /**
     * อัปเดตสถานะการเสร็จสิ้นของงาน
     */
    fun updateTaskStatus(taskId: Int?, isChecked: Boolean) {
            viewModelScope.launch(Dispatchers.IO) {
            try {
                db.taskDao().updateIsCompleted(taskId, isChecked)
            }catch (e : Exception){
                Log.e("TAG", e.message.toString())
            }
        }
    }

    /**
     * ทำเครื่องหมายว่างานเสร็จสิ้นแล้ว
     */
    fun markComplete(taskId: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val rowsAffected = db.taskDao().updateIsCompleted(taskId, true)
                if (rowsAffected > 0) {
                    selectTaskById(taskId)
                }
                Log.e("TAG", "Update Success")
            } catch (e: Exception) {
                Log.e("TAG", "Error: ${e.message}")
            }
        }
    }

    /**
     * ลบงานออกจากฐานข้อมูล
     */
    fun deleteTask(taskId: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val result = db.taskDao().deleteTask(taskId)
                withContext(Dispatchers.Main) {
                    deleteTask.value = result
                }
                Log.e("TAG", "Delete Task Success.")
            } catch (e: Exception) {
                Log.e("TAG", "Error delete task: ${e.message}")
            }
        }
    }

    /**
     * สลับโหมดกลางคืน (Dark Mode) และบันทึกลง SharedPrefs
     */
    fun toggleTheme(dark: Boolean) {
        _isDarkMode.value = dark
        prefs.edit { putBoolean("dark_mode", dark) }
    }

    /**
     * ดึงข้อมูลงานตาม ID
     */
    suspend fun selectTaskById(taskId: Int) {
        try {
            db.taskDao().selectTaskById(taskId)
                .asFlow()
                .collect { result ->
                    _taskDetails.value = result
                }
        }catch (e : Exception){
            Log.e("TAG", e.message.toString())
        }
    }

    /**
     * รีเฟรชรายการงานทั้งหมด
     */
    fun refreshTask(){
        viewModelScope.launch {
            try {
                db.taskDao().selectAllTasks()
                    .asFlow()
                    .collect { result ->
                        _tasks.value = result
                    }
            } catch (e : Exception){
                Log.e("TAG", e.message.toString())
            }
        }
    }
}