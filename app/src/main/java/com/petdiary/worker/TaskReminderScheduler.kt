package com.petdiary.worker

import android.content.Context
import androidx.work.Data
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.petdiary.data.AppDatabase
import com.petdiary.data.entity.TaskItem
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit

class TaskReminderScheduler(private val context: Context) {

    fun scheduleReminder(task: TaskItem) {
        val reminderTime = task.reminderTime ?: return
        val delay = reminderTime - System.currentTimeMillis()
        if (delay <= 0) return

        val data = Data.Builder()
            .putString("task_title", "📋 ${task.title}")
            .putLong("task_id", task.id)
            .build()

        val workRequest = OneTimeWorkRequestBuilder<TaskReminderWorker>()
            .setInitialDelay(delay, TimeUnit.MILLISECONDS)
            .setInputData(data)
            .addTag("task_reminder_${task.id}")
            .build()

        WorkManager.getInstance(context).enqueueUniqueWork(
            "task_reminder_${task.id}",
            ExistingWorkPolicy.REPLACE,
            workRequest
        )
    }

    fun cancelReminder(task: TaskItem) {
        WorkManager.getInstance(context).cancelUniqueWork("task_reminder_${task.id}")
    }

    fun rescheduleAllReminders() {
        CoroutineScope(Dispatchers.IO).launch {
            val db = AppDatabase.getDatabase(context)
            val tasks = db.taskDao().getTasksWithReminders()
            for (task in tasks) {
                scheduleReminder(task)
            }
        }
    }
}
