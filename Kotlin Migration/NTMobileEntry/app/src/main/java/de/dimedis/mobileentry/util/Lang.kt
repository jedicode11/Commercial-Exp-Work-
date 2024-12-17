package de.dimedis.mobileentry.util

import android.content.Context
import android.content.res.Configuration
import android.text.TextUtils
import java.util.*

object Lang {
    fun setLocation(context: Context, language: String?) {
        val locale = if (TextUtils.isEmpty(language)) Locale.ROOT else language?.let { Locale(it) }
        if (locale != null) {
            Locale.setDefault(locale)
        }
        val config = Configuration()
        config.locale = locale
        context.applicationContext.resources.updateConfiguration(config, null)
    }
}