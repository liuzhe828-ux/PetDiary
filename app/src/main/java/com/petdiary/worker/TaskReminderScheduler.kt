package com.petdiary.worker

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import androidx.work.*
import com.petdiary.MainActivity
import com.petdiary.data.TaskData
import java.util.concurrent.TimeUnit

class TaskReminderScheduler(private val context: Context) {
    fun scheduleReminder(task: TaskData) {
        val time = task.reminderTime ?: return
        val delay = time - System.currentTimeMillis()
        if (delay <= 0) return

        val data = Data.Builder().putString("title", "📋 ${task.title}").putLong("id", task.id).build()
        val work = OneTimeWorkRequestBuilder<TaskReminderWorker>().setInitialDelay(delay, TimeUnit.MILLISECONDS).setInputData(data).addTag("remind_${task.id}").build()
        WorkManager.getInstance(context).enqueueUniqueWork("remind_${task.id}", ExistingWorkPolicy.REPLACE, work)
    }

    fun cancelReminder(task: TaskData) {
        WorkManager.getInstance(context).cancelUniqueWork("remind_${task.id}")
    }
}

class TaskReminderWorker(ctx: Context, params: WorkerParameters) : Worker(ctx, params) {
    override fun doWork(): Result {
        val title = inputData.getString("title") ?: "你有待办事项"
        val id = inputData.getLong("id", 0)
        showNotification(title, id)
        return Result.success()
    }

    private fun showNotification(title: String, id: Long) {
        val ch = NotificationChannel("tasks", "任务提醒", NotificationManager.IMPORTANCE_HIGH).apply { description = "待办提醒" }
        (applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager).createNotificationChannel(ch)

        val intent = Intent(applicationContext, MainActivity::class.java).apply { flags = Intent.FLAG_ACTIVITY_NEW_TASK }
        val pi = PendingIntent.getActivity(applicationContext, id.toInt(), intent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)
        val notif = NotificationCompat.Builder(applicationContext, "tasks").setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentTitle("🐾 宠物提醒你").setContentText(title).setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(pi).setAutoCancel(true).build()

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU || ContextCompat.checkSelfPermission(applicationContext, Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED) {
            NotificationManagerCompat.from(applicationContext).notify(id.toInt(), notif)
        }
    }
}
