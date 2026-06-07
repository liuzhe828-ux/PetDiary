package com.petdiary.widget

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.widget.RemoteViews
import com.petdiary.MainActivity
import com.petdiary.R
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

class PetWidgetProvider : AppWidgetProvider() {

    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray
    ) {
        for (appWidgetId in appWidgetIds) {
            updateWidget(context, appWidgetManager, appWidgetId)
        }
    }

    override fun onReceive(context: Context, intent: Intent) {
        super.onReceive(context, intent)
        if (intent.action == AppWidgetManager.ACTION_APPWIDGET_UPDATE) {
            val appWidgetManager = AppWidgetManager.getInstance(context)
            val ids = appWidgetManager.getAppWidgetIds(
                ComponentName(context, PetWidgetProvider::class.java)
            )
            onUpdate(context, appWidgetManager, ids)
        }
    }

    companion object {
        fun updateAllWidgets(context: Context) {
            val appWidgetManager = AppWidgetManager.getInstance(context)
            val ids = appWidgetManager.getAppWidgetIds(
                ComponentName(context, PetWidgetProvider::class.java)
            )
            if (ids.isNotEmpty()) {
                PetWidgetProvider().onUpdate(context, appWidgetManager, ids)
            }
        }
    }
}

private fun updateWidget(
    context: Context,
    appWidgetManager: AppWidgetManager,
    appWidgetId: Int
) {
    val db = com.petdiary.data.AppDatabase.getDatabase(context)
    val petDao = db.petStateDao()

    var petName = "小团子"
    var petType = "cat"
    var hunger = 100
    var happiness = 100

    try {
        val pet = runBlocking(Dispatchers.IO) {
            petDao.getPetStateRaw()
        }
        pet?.let {
            petName = it.name
            petType = it.petType
            hunger = it.hunger
            happiness = it.happiness
        }
    } catch (_: Exception) {}

    val views = RemoteViews(context.packageName, R.layout.pet_widget)

    val petEmoji = when (petType) {
        "dog" -> "🐶"
        "bunny" -> "🐰"
        else -> "🐱"
    }

    val statusEmoji = when {
        happiness >= 80 -> "😄"
        happiness >= 50 -> "🙂"
        happiness >= 20 -> "😐"
        else -> "😢"
    }

    views.setTextViewText(R.id.pet_emoji, petEmoji)
    views.setTextViewText(R.id.pet_name, petName)
    views.setTextViewText(
        R.id.pet_status,
        "❤️$hunger% 😊$happiness% $statusEmoji"
    )

    // 点击打开 App
    val openIntent = Intent(context, MainActivity::class.java).apply {
        flags = Intent.FLAG_ACTIVITY_NEW_TASK
    }
    val openPendingIntent = PendingIntent.getActivity(
        context, 0, openIntent,
        PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
    )
    views.setOnClickPendingIntent(R.id.widget_root, openPendingIntent)

    appWidgetManager.updateAppWidget(appWidgetId, views)
}
