package com.example.todolistjetpackcompose.dataModel

/**
 * Data class สำหรับเก็บข้อมูลงาน (Task) ทั่วไปที่ไม่ได้เชื่อมต่อกับ Room (Data Model)
 */
data class Task(
        val id: Int,
        val title: String,
        val category: String,
        val dueDate: String,
        val priority: String,
        val notes: String,
        val isCompleted: Boolean
)