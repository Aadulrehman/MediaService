package com.hazel.mediaservice

import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.work.Worker
import androidx.work.WorkerParameters

class MusicCompletionWorker(appContext: Context, workerParams: WorkerParameters) : Worker(appContext, workerParams) {
    @SuppressLint("MissingPermission")
    override fun doWork(): Result {
        val channelId = "music_completion_channel"
        createNotificationChannel(channelId)

        val notification = NotificationCompat.Builder(applicationContext, channelId)
            .setContentTitle("Music Finished")
            .setContentText("The music has finished playing.")
            .setSmallIcon(R.drawable.music)
            .build()

        val notificationManager = NotificationManagerCompat.from(applicationContext)
        notificationManager.notify(1, notification)

        return Result.success()
    }
    private fun createNotificationChannel(channelId: String) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "Music Completion"
            val descriptionText = "Notification for music completion"
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(channelId, name, importance).apply {
                description = descriptionText
            }

            val notificationManager =
                applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }
}