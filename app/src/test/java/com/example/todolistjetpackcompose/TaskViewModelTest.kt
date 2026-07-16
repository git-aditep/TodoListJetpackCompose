import android.app.Application
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import app.cash.turbine.test
import com.example.todolistjetpackcompose.MainDispatcherRule
import com.example.todolistjetpackcompose.roomDatabase.AppDatabase
import com.example.todolistjetpackcompose.roomDatabase.Task
import com.example.todolistjetpackcompose.viewModel.TaskViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@OptIn(ExperimentalCoroutinesApi::class)
@RunWith(RobolectricTestRunner::class)
@Config(manifest = Config.NONE) // ป้องกัน Error No such manifest
class TaskViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private lateinit var viewModel: TaskViewModel
    private lateinit var db: AppDatabase
    private lateinit var app: Application

    @Before
    fun setup() {
        app = ApplicationProvider.getApplicationContext()

        // ✅ สร้าง Database จำลองใน RAM (ข้อมูลจะหายไปเมื่อจบเทส)
        db = Room.inMemoryDatabaseBuilder(app, AppDatabase::class.java)
            .allowMainThreadQueries()
            .build()

        viewModel = TaskViewModel(app)
    }

    @After
    fun tearDown() {
        db.close()
    }

    @Test
    fun `addTask should update tasks list locally`() = runTest {
        // ✅ ประกาศตัวแปร newTask ก่อนนำไปใช้
        val newTask = Task(
            id = 1,
            title = "Test Task",
            category = "Test Category",
            dueDate = "17/6/2569",
            priority = "Low",
            notes = "Test Notes",
            isCompleted = false
        )

        viewModel.tasks.test {
            val initialItems = awaitItem()
            assertEquals(0, initialItems.size)

            viewModel.addTask(newTask)

            val updatedItems = awaitItem()
            assertEquals(1, updatedItems.size)
            assertEquals("Test Task", updatedItems[0].title)

            cancelAndIgnoreRemainingEvents()
        }
    }
}