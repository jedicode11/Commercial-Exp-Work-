package de.dimedis.mobileentry.util

import android.os.Handler
import android.os.Looper

object UIHandler {
    private val sHandler = Handler(Looper.getMainLooper())
    fun get(): Handler {
        return sHandler
    }
}