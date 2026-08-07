package com.example.util

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

class ReminderReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val reminderType = intent.getStringExtra("REMINDER_TYPE") ?: "WEATHER"
        if (reminderType == "ACTIVITY") {
            NotificationHelper.sendDailyActivityReminder(context)
        } else {
            NotificationHelper.sendWeatherReminder(context)
        }
    }
}
