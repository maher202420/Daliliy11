package com.example.utils

import android.content.Context
import androidx.work.Data
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager

object NotificationHelper {
    fun scheduleNotification(context: Context, title: String, message: String) {
        val workData = Data.Builder()
            .putString("title", title)
            .putString("message", message)
            .build()

        val notificationWorkRequest = OneTimeWorkRequestBuilder<NotificationWorker>()
            .setInputData(workData)
            .build()

        WorkManager.getInstance(context).enqueue(notificationWorkRequest)
    }
}
