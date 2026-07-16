package com.example.todolistjetpackcompose.roomDatabase

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Entity สำหรับเก็บข้อมูลงาน (Task) ในฐานข้อมูล Room
 */
@Entity
data class Task(
        /** ID ของงาน (Primary Key) กำหนดให้รันตัวเลขให้อัตโนมัติ */
        @PrimaryKey(autoGenerate = true) val id: Int?,
        
        /** ชื่อของงาน */
        @ColumnInfo(name = "title") val title: String,
        
        /** หมวดหมู่ของงาน */
        @ColumnInfo(name = "category") val category: String,
        
        /** วันที่ครบกำหนด */
        @ColumnInfo(name = "dueDate") val dueDate: String,
        
        /** ระดับความสำคัญ */
        @ColumnInfo(name = "priority") val priority: String,
        
        /** บันทึกเพิ่มเติม */
        @ColumnInfo(name = "notes") val notes: String,
        
        /** สถานะว่าเสร็จสิ้นแล้วหรือไม่ */
        @ColumnInfo(name = "isCompleted") val isCompleted: Boolean?
)