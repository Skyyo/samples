package com.skyyo.samples.worker

import android.content.Context
import androidx.work.WorkerParameters

class ForegroundWorker(context: Context, parameters: WorkerParameters): ExpeditedWorker(context, parameters) {
    override suspend fun doWork(): Result {
        setForeground(getForegroundInfo())
        return super.doWork()
    }
}