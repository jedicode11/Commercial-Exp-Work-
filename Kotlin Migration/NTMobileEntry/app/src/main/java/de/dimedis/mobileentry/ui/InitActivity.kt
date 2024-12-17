package de.dimedis.mobileentry.ui

import android.content.Context
import android.content.Intent
import android.os.Bundle
import de.dimedis.mobileentry.fragments.InitializeFragment
import de.dimedis.mobileentry.fragments.LanguageFragment
import de.dimedis.mobileentry.util.PrefUtils

class InitActivity : BaseFragmentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (savedInstanceState == null) {
            if (PrefUtils.isInitCompleted()) {
                addFragment(LanguageFragment.builder().build(), "lang")
            } else {
                addFragment(InitializeFragment.builder().build(), "init")
            }
        }
    }

    companion object {
        const val TAG = "InitActivity"
        fun start(context: Context) {
            val intent = Intent(context, InitActivity::class.java)
            context.startActivity(intent)
        }
    }
}