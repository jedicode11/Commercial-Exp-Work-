package de.dimedis.mobileentry.util

import kotlin.Throws
import android.util.Log
import de.dimedis.mobileentry.Config
import java.io.IOException
import java.io.PrintWriter
import java.io.RandomAccessFile
import java.util.*

object Logger {
    const val FUNCTION_HISTORY_SWITCH_TAG = "History function selected"
    const val FUNCTION_SCAN_SWITCH_TAG = "Scan function selected"
    fun i(tag: String?, message: String) {
        Log.i(tag, message)
        asyncLog(tag, message, LogType.INFO)
    }

    fun i(tag: String?, message: String, cause: Throwable?) {
        Log.i(tag, message, cause)
        asyncLog(tag, message, LogType.INFO)
    }

    fun w(tag: String?, message: String) {
        Log.w(tag, message)
        asyncLog(tag, message, LogType.WARNING)
    }

    fun w(tag: String?, message: String, cause: Throwable?) {
        Log.w(tag, message, cause)
        asyncLog(tag, message, LogType.WARNING)
    }

    fun e(tag: String?, message: String) {
        Log.e(tag, message)
        asyncLog(tag, message, LogType.ERROR)
    }

    fun e(tag: String?, message: String, cause: Throwable?) {
        Log.e(tag, message, cause)
        asyncLog(tag, message, LogType.ERROR)
    }

    fun asyncLog(tag: String?, message: String, type: LogType) {
        if (!PrefUtils.isWriteLogfile()) {
            return
        }
        Thread(object : Runnable {
            override fun run() {
                try {
                    val prefix = getPrefix(type)
                    writeToFile(
                        String.format("/%s/", tag!!.uppercase(Locale.getDefault())),
                        message
                    )
                } catch (e: Throwable) {
                    e.printStackTrace()
                }
            }

            private fun getPrefix(type: LogType): String {
                val prefix = " [%s] "
                return kotlin.String.format(prefix, type.name)
            }
        }).start()
    }

    @Throws(IOException::class)
    private fun writeToFile(tag: String, message: String) {
        RandomAccessFile(Config.LOG_FILE, "rw").use { logFile ->
            val date = Date(System.currentTimeMillis())
            val time = Config.LOG_FILE_TIME_FORMAT.format(date)
            logFile.seek(logFile.length())
            val entry = """$time	$tag	$message"""
            logFile.writeBytes(entry)
        }
    }

    fun geLogfileSize(): Long {
        val logFile = Config.LOG_FILE
        val sizeInBytes = logFile.length()
        return sizeInBytes ushr 20
    }

    @Throws(IOException::class)
    fun clearLogFile() {
        val writer = PrintWriter(Config.LOG_FILE)
        writer.print("")
        writer.close()
    }
}