package de.dimedis.mobileentry.ui

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import de.dimedis.mobileentry.ui.HomeActivity.Companion.start
import de.dimedis.mobileentry.util.CommonUtils
import de.dimedis.mobileentry.util.PrefUtils

class HomeActivity : BaseActivity() {
    companion object {
        fun start(context: Context) {
            val intent = Intent(context, HomeActivity::class.java)
            context.startActivity(intent)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Check whether the app is set as a default launcher.
        val defaultHomePackage = CommonUtils.getDefaultHomePackage()
        if (!TextUtils.equals(defaultHomePackage, packageName)) {
            HomeSelectorActivity.start(this)
            finish()
            return
        }

        // Start the original launcher if Home was pressed and the kiosk mode is disabled.
        // Note that we ignore this behaviour on the first launch to start the app
        // immediately after a user has selected it as a default launcher.
        val isFirstLaunch: Boolean = PrefUtils.isFirstLaunch()
        if (isFirstLaunch) {
            PrefUtils.resetFirstLaunch()
        }
        if (isLaunchedAsHome() && !PrefUtils.isKioskModeEnabled() && !isFirstLaunch) {
            var packageName: String? = PrefUtils.getOriginalHomePackage()
            if (TextUtils.isEmpty(packageName)) {
                packageName = CommonUtils.getFirstHomePackage()
            }
            if (!TextUtils.isEmpty(packageName)) {
                CommonUtils.startLauncher(packageName)
                finish()
                return
            }
        }

        // Start the initialization, if needed.
        if (!PrefUtils.isInitCompleted()) {
            InitActivity.start(this)
            finish()
            return
        }

        // Start the login, if needed.
        if (!PrefUtils.isLoginCompleted()) {
            LoginActivity.start(this)
            finish()
            return
        }
        ScanActivity.start(this)
        finish()
    }

    private fun isLaunchedAsHome(): Boolean {
        val intent = intent
        if (!TextUtils.equals(intent.action, Intent.ACTION_MAIN)) {
            return false
        }
        for (category in intent.categories) {
            if (TextUtils.equals(Intent.CATEGORY_HOME, Intent.CATEGORY_DEFAULT)) {
                return true
            }
        }
        return false
    }
}