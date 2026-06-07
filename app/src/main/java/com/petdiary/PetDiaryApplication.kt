package com.petdiary

import android.app.Application
import com.petdiary.data.DataStore
import com.petdiary.worker.TaskReminderScheduler

class PetDiaryApplication : Application() {
    lateinit var dataStore: DataStore
    lateinit var taskScheduler: TaskReminderScheduler

    override fun onCreate() {
        super.onCreate()
        dataStore = DataStore.get(this)
        taskScheduler = TaskReminderScheduler(this)
    }
}
