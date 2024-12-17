package de.dimedis.mobileentry.ui

import android.content.Context
import android.content.Intent
import android.os.Bundle
import de.dimedis.mobileentry.fragments.SoftwareVersionsFragment
import de.dimedis.mobileentry.util.PrefUtils

class LoginActivity : BaseFragmentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        PrefUtils.setKioskModeEnabled(true)
        if (savedInstanceState == null) {
            addFragment(SoftwareVersionsFragment.builder().build(), "init")
        }
    }

    override fun onBackPressed() {
        // Block Back button in the kiosk mode.
        if (!PrefUtils.isKioskModeEnabled()) {
            super.onBackPressed()
        }
    }

    companion object {
        fun start(context: Context) {
            val intent = Intent(context, LoginActivity::class.java)
            context.startActivity(intent)
        }
    }
}