package com.skyyo.samples.worker

import android.content.Context
import android.util.Log
import androidx.core.app.NotificationChannelCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.work.CoroutineWorker
import androidx.work.ForegroundInfo
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import com.skyyo.samples.R
import kotlinx.coroutines.delay
import java.util.concurrent.TimeUnit

private const val CHANNEL_ID = "foreground_worker_channel"
private const val CHANNEL_NAME = "Channel for expedited works"
private const val NOTIFICATION_ID = 12
private const val NOTIFICATION_TITLE = "Expedited work"
private const val NOTIFICATION_TEXT = "Work is running"
private const val NOTIFICATION_CANCEL_TEXT = "Abort"

open class ExpeditedWorker(context: Context, parameters: WorkerParameters): CoroutineWorker(context, parameters) {
    override suspend fun getForegroundInfo(): ForegroundInfo {
        val intent = WorkManager.getInstance(applicationContext).createCancelPendingIntent(id)
        createChannel()

        val notification = NotificationCompat.Builder(applicationContext, CHANNEL_ID)
            .setContentTitle(NOTIFICATION_TITLE)
            .setTicker(NOTIFICATION_TITLE)
            .setContentText(NOTIFICATION_TEXT)
            .setSmallIcon(R.drawable.ic_play)
            .setOngoing(true)
            .addAction(android.R.drawable.ic_delete, NOTIFICATION_CANCEL_TEXT, intent)
            .build()

        return ForegroundInfo(NOTIFICATION_ID, notification)
    }

    override suspend fun doWork(): Result {
        delay(TimeUnit.SECONDS.toMillis(40))
        Log.e("TAG", "completed")
        return Result.success()
    }

    private fun createChannel() {
        val channel = NotificationChannelCompat.Builder(
            CHANNEL_ID,
            NotificationManagerCompat.IMPORTANCE_HIGH
        ).setName(CHANNEL_NAME).build()
        NotificationManagerCompat.from(applicationContext).createNotificationChannel(channel)
    }
}