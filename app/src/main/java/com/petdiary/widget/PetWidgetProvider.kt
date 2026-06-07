package com.petdiary.widget

import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.widget.RemoteViews
import com.petdiary.MainActivity
import com.petdiary.R
import com.petdiary.data.DataStore

class PetWidgetProvider : AppWidgetProvider() {
    override fun onUpdate(ctx: Context, mgr: AppWidgetManager, ids: IntArray) {
        ids.forEach { updateWidget(ctx, mgr, it) }
    }
    companion object {
        fun updateAll(ctx: Context) {
            val mgr = AppWidgetManager.getInstance(ctx)
            val ids = mgr.getAppWidgetIds(ComponentName(ctx, PetWidgetProvider::class.java))
            if (ids.isNotEmpty()) PetWidgetProvider().onUpdate(ctx, mgr, ids)
        }
    }
}

private fun updateWidget(ctx: Context, mgr: AppWidgetManager, id: Int) {
    val pet = DataStore.get(ctx).getPet()
    val emoji = when (pet.petType) { "dog" -> "🐶"; "bunny" -> "🐰"; else -> "🐱" }
    val face = when { pet.happiness >= 80 -> "😄"; pet.happiness >= 50 -> "🙂"; pet.happiness >= 20 -> "😐"; else -> "😢" }
    val views = RemoteViews(ctx.packageName, R.layout.pet_widget)
    views.setTextViewText(R.id.pet_emoji, emoji)
    views.setTextViewText(R.id.pet_name, pet.name)
    views.setTextViewText(R.id.pet_status, "❤️${pet.hunger}% 😊${pet.happiness}% $face")
    val pi = PendingIntent.getActivity(ctx, 0, Intent(ctx, MainActivity::class.java).apply { flags = Intent.FLAG_ACTIVITY_NEW_TASK },
        PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)
    views.setOnClickPendingIntent(R.id.widget_root, pi)
    mgr.updateAppWidget(id, views)
}
