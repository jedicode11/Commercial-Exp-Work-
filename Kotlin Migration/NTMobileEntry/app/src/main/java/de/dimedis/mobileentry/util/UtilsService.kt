package de.dimedis.mobileentry.util

import android.content.Context
import android.content.Intent
import android.os.Process
import androidx.work.Worker
import androidx.work.WorkerParameters
import de.dimedis.mobileentry.ui.InitActivity

class UtilsService(context: Context, params: WorkerParameters) : Worker(context, params) {
    override fun doWork(): Result {
        val pid = Process.myPid()
        Process.killProcess(pid)
        FileUtils.clearApplicationData(applicationContext)
        val intentNew = Intent(applicationContext, InitActivity::class.java)
        intentNew.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
        applicationContext.startActivity(intentNew)
        return Result.success()
    }
}