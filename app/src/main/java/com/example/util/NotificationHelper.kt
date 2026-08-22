package com.example.util

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.example.MainActivity
import com.example.R
import com.example.domain.models.AppLanguage

object NotificationHelper {
    const val CHANNEL_ID = "farm_reminders_channel"
    const val CHANNEL_NAME = "Farm Reminders"
    const val WEATHER_NOTIFICATION_ID = 1001
    const val ACTIVITY_NOTIFICATION_ID = 1002

    private fun getCurrentLanguage(context: Context): AppLanguage {
        val prefs = context.getSharedPreferences("farm_app_prefs", Context.MODE_PRIVATE)
        val langStr = prefs.getString("selected_lang", AppLanguage.ENGLISH.name) ?: AppLanguage.ENGLISH.name
        return try {
            AppLanguage.valueOf(langStr)
        } catch (e: Exception) {
            AppLanguage.ENGLISH
        }
    }

    fun createNotificationChannel(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(
                CHANNEL_ID,
                CHANNEL_NAME,
                importance
            ).apply {
                description = "Reminders for farm weather forecasts and daily farm activity logs"
            }
            val notificationManager: NotificationManager =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    fun sendWeatherReminder(context: Context) {
        createNotificationChannel(context)

        val currentLang = getCurrentLanguage(context)

        val title = when (currentLang) {
            AppLanguage.ENGLISH -> "🌾 Farm Weather Forecast Reminder"
            AppLanguage.TAGALOG -> "🌾 Paalala sa Taya ng Panahon sa Bukid"
            AppLanguage.TAGLISH -> "🌾 Farm Weather Forecast Reminder"
            AppLanguage.ILOCANO -> "🌾 Pakdaar ti Panawen ti Talon"
            AppLanguage.CEBUANO -> "🌾 Pahinumdom sa Panahon sa Yuta"
        }

        val text = when (currentLang) {
            AppLanguage.ENGLISH -> "Check today's local farm weather forecast to plan your irrigation and field work!"
            AppLanguage.TAGALOG -> "Suriin ang taya ng panahon sa bukid ngayon para sa pag-aabono at pagpapatubig!"
            AppLanguage.TAGLISH -> "Check ang weather forecast sa farm mo today para sa irrigation at pag-aabono!"
            AppLanguage.ILOCANO -> "Kitaem ti taya ti panawen iti talonmo tapno maiplano ti panag-ipaitaba ken panagpattog!"
            AppLanguage.CEBUANO -> "Susiha ang taya sa panahon sa imong yuta aron ma-plano ang pag-abuno ug patubig!"
        }

        val bigText = when (currentLang) {
            AppLanguage.ENGLISH -> "Check today's local farm weather forecast! Sunny skies or rain expected? Plan your spraying, irrigation, and field activities efficiently."
            AppLanguage.TAGALOG -> "Suriin ang taya ng panahon sa iyong bukid ngayon! Ulan man o sikat ng araw, i-plano nang maayos ang pag-aabono, pagpapatubig, at mga gawain sa bukid."
            AppLanguage.TAGLISH -> "Check ang weather forecast ngayon! Plan your spraying, irrigation, and fertilizer application efficiently to save money and increase crop yield."
            AppLanguage.ILOCANO -> "Kitaem ti taya ti panawen iti talonmo ita nga aldaw! Planoem a naimbag ti panag-ipaitaba ken dadduma pay nga obra iti talon."
            AppLanguage.CEBUANO -> "Susiha ang taya sa panahon sa imong yuta karong adlawa! I-plano og maayo ang pag-abuno, pagpatubig, ug mga buluhaton sa yuta."
        }

        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pendingIntent = PendingIntent.getActivity(
            context,
            0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val builder = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentTitle(title)
            .setContentText(text)
            .setStyle(NotificationCompat.BigTextStyle().bigText(bigText))
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)

        try {
            with(NotificationManagerCompat.from(context)) {
                notify(WEATHER_NOTIFICATION_ID, builder.build())
            }
        } catch (e: SecurityException) {
            e.printStackTrace()
        }
    }

    fun sendWeatherWarningNotification(context: Context, title: String, message: String) {
        createNotificationChannel(context)

        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pendingIntent = PendingIntent.getActivity(
            context,
            2,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val builder = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentTitle(title)
            .setContentText(message)
            .setStyle(NotificationCompat.BigTextStyle().bigText(message))
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)

        try {
            with(NotificationManagerCompat.from(context)) {
                notify(WEATHER_NOTIFICATION_ID + 100, builder.build())
            }
        } catch (e: SecurityException) {
            e.printStackTrace()
        }
    }

    fun sendWeatherWarningNotification(
        context: Context,
        advisory: com.example.domain.models.FertilizerAdvisory,
        language: AppLanguage? = null
    ) {
        val currentLang = language ?: getCurrentLanguage(context)
        val notifTitle = advisory.title
        val notifBody = "${advisory.summary}\n• ${advisory.ureaAdvice}\n• ${advisory.bestApplicationWindow}"
        sendWeatherWarningNotification(context, notifTitle, notifBody)
    }

    fun sendDailyActivityReminder(context: Context) {
        createNotificationChannel(context)

        val currentLang = getCurrentLanguage(context)

        val title = when (currentLang) {
            AppLanguage.ENGLISH -> "📝 Daily Farm Activity Reminder"
            AppLanguage.TAGALOG -> "📝 Paalala sa Araw-araw na Gawain sa Bukid"
            AppLanguage.TAGLISH -> "📝 Daily Farm Activity Reminder"
            AppLanguage.ILOCANO -> "📝 Pakdaar ti Inaldaw nga Obra iti Talon"
            AppLanguage.CEBUANO -> "📝 Pahinumdom sa Adlaw-adlaw nga Buluhaton"
        }

        val text = when (currentLang) {
            AppLanguage.ENGLISH -> "Don't forget to log your daily farm activities, fertilizer applications, and crop health notes!"
            AppLanguage.TAGALOG -> "Huwag kalimutang itala ang iyong mga gawain sa bukid at paglalagay ng abono!"
            AppLanguage.TAGLISH -> "Don't forget to log your daily farm activities and fertilizer applications!"
            AppLanguage.ILOCANO -> "Dimo kalipatan nga isurat dagiti obram iti talon ken panag-ipaitaba!"
            AppLanguage.CEBUANO -> "Ayaw kalimti ang pag-tala sa imong mga buluhaton sa yuta ug pag-abuno!"
        }

        val bigText = when (currentLang) {
            AppLanguage.ENGLISH -> "Don't forget to log your daily farm activities! Record land measurements, fertilizer applications, soil analysis, and crop growth observations in NutriGuide."
            AppLanguage.TAGALOG -> "Huwag kalimutang itala ang iyong mga gawain sa bukid! I-record ang sukat ng lupa, paglalagay ng abono, pagsusuri ng lupa, at lagay ng pananim sa NutriGuide."
            AppLanguage.TAGLISH -> "Don't forget to log your daily farm activities! Record land measurement, fertilizer application, soil analysis, and crop health notes in NutriGuide."
            AppLanguage.ILOCANO -> "Dimo kalipatan nga isurat dagiti obram iti talon! Isagrap ti rukod ti talon, panag-ipaitaba, ken kasasaad ti mula iti NutriGuide."
            AppLanguage.CEBUANO -> "Ayaw kalimti ang pag-tala sa imong mga buluhaton sa yuta! I-record ang sukad sa yuta, pag-abuno, ug estado sa tanom sa NutriGuide."
        }

        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pendingIntent = PendingIntent.getActivity(
            context,
            1,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val builder = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentTitle(title)
            .setContentText(text)
            .setStyle(NotificationCompat.BigTextStyle().bigText(bigText))
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)

        try {
            with(NotificationManagerCompat.from(context)) {
                notify(ACTIVITY_NOTIFICATION_ID, builder.build())
            }
        } catch (e: SecurityException) {
            e.printStackTrace()
        }
    }
}

