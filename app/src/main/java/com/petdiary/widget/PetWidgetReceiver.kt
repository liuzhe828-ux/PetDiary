package com.petdiary.widget

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.petdiary.PetDiaryApplication
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class PetWidgetReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val app = context.applicationContext as PetDiaryApplication
        val petRepo = app.petRepository

        when (intent.action) {
            "com.petdiary.action.FEED_PET" -> {
                // 喂食操作从 App 内触发
            }
            "com.petdiary.action.PLAY_PET" -> {
                // 玩耍操作从 App 内触发
            }
            "com.petdiary.action.OPEN_APP" -> {
                val launchIntent = context.packageManager
                    .getLaunchIntentForPackage(context.packageName)
                if (launchIntent != null) {
                    launchIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                    context.startActivity(launchIntent)
                }
            }
        }
        // 更新 Widget
        PetWidgetProvider.updateAllWidgets(context)
    }
}
