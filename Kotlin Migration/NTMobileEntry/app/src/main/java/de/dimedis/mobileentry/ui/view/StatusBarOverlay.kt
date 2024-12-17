package de.dimedis.mobileentry.ui.view

import android.content.Context
import android.graphics.PixelFormat
import android.view.Gravity
import android.view.View
import android.view.WindowManager
import de.dimedis.mobileentry.util.AppContext
import de.dimedis.mobileentry.util.UIUtils


class StatusBarOverlay private constructor(context: Context) : View(context) {
    companion object {
        private var sInstance: StatusBarOverlay? = null
        fun create() {
            if (sInstance == null) {
                val context = AppContext.get()
                sInstance = StatusBarOverlay(context)
                val lp = WindowManager.LayoutParams(WindowManager.LayoutParams.MATCH_PARENT, UIUtils.getStatusBarHeight(),
                    WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
                    WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL or WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN,
                    PixelFormat.TRANSLUCENT)
                lp.gravity = Gravity.TOP
                (context.getSystemService(Context.WINDOW_SERVICE) as WindowManager).addView(
                    sInstance, lp
                )
            }
        }
    }
}