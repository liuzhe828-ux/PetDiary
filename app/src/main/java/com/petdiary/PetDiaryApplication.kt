package com.petdiary

import android.app.Application
import androidx.work.Configuration
import androidx.work.WorkManager
import com.petdiary.data.AppDatabase
import com.petdiary.data.repository.DiaryRepository
import com.petdiary.data.repository.PetRepository
import com.petdiary.data.repository.TaskRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class PetDiaryApplication : Application() {

    val database by lazy { AppDatabase.getDatabase(this) }
    val petRepository by lazy { PetRepository(database.petStateDao()) }
    val diaryRepository by lazy { DiaryRepository(database.diaryDao()) }
    val taskRepository by lazy { TaskRepository(database.taskDao()) }

    override fun onCreate() {
        super.onCreate()

        // 初始化 WorkManager
        WorkManager.initialize(
            this,
            Configuration.Builder()
                .setMinimumLoggingLevel(android.util.Log.INFO)
                .build()
        )

        // 初始化宠物状态
        CoroutineScope(Dispatchers.IO).launch {
            val existing = database.petStateDao().getPetState()
            // 由于是 Flow，需要在别处收集，这里用简单方法
        }
    }
}
