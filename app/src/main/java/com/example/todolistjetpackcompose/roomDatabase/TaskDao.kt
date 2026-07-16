package com.example.todolistjetpackcompose.roomDatabase

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import io.reactivex.rxjava3.core.Flowable
import kotlinx.coroutines.flow.Flow

/**
 * Data Access Object (DAO) สำหรับเข้าถึงข้อมูล Task ในฐานข้อมูล
 */
@Dao
interface TaskDao {
    /** เพิ่มงานใหม่ลงในฐานข้อมูล */
    @Insert
    fun insertTask(task: Task)

    /** ดึงรายการงานทั้งหมด (คืนค่าเป็น RxJava Flowable) */
    @Query("SELECT * FROM task")
    fun selectAllTasks(): Flowable<List<Task>>

    /** นับจำนวนงานที่ทำเสร็จแล้ว (คืนค่าเป็น RxJava Flowable) */
    @Query("SELECT COUNT(*) FROM task WHERE isCompleted = 1")
    fun selectCompletedCount(): Flowable<Int>

    /** นับจำนวนงานที่ทำเสร็จแล้ว (คืนค่าเป็น Kotlin Flow) */
    @Query("SELECT COUNT(*) FROM task WHERE isCompleted = 1")
    fun getCompletedCountFlow(): Flow<Int>

    /** ดึงข้อมูลงานตาม ID */
    @Query("SELECT * FROM task WHERE id = :taskId")
    fun selectTaskById(taskId : Int) : Flowable<Task>

    /** อัปเดตสถานะการเสร็จสิ้นของงาน */
    @Query("UPDATE task SET isCompleted = :isChecked WHERE id = :id")
    fun updateIsCompleted(id: Int?, isChecked: Boolean) : Int

    /** ลบงานตาม ID */
    @Query("DELETE FROM task WHERE id = :id")
    fun deleteTask(id: Int?) : Int

}