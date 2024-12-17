package de.dimedis.mobileentry.ui

import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.widget.TextView
import com.squareup.phrase.Phrase
import de.dimedis.mobileentry.BuildConfig
import de.dimedis.mobileentry.R
import de.dimedis.mobileentry.databinding.ActivityHomeSelectorBinding
import de.dimedis.mobileentry.util.CommonUtils
import de.dimedis.mobileentry.util.CommonUtils.startLauncher
import de.dimedis.mobileentry.util.PrefUtils

class HomeSelectorActivity : BaseActivity() {

    companion object {
        fun start(context: Context) {
            val intent = Intent(context, HomeSelectorActivity::class.java)
            context.startActivity(intent)
        }

        fun builder(): Builder {
            return Builder()
        }
    }

    class Builder {
        fun build(): HomeSelectorActivity {
            return HomeSelectorActivity()
        }
    }

        private lateinit var binding: ActivityHomeSelectorBinding

    override fun onCreate(savedInstanceState: Bundle?) {
                super.onCreate(savedInstanceState)
        binding = ActivityHomeSelectorBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val prompt = Phrase.from(this, R.string.tv_home_selector_prompt).put("app_name", getString(R.string.app_name)).format()
        binding.tvHomeSelectorPrompt.text = prompt

    }

    override fun onResume() {
        super.onResume()

        val defaultHomePackage = CommonUtils.getDefaultHomePackage()
        if (!TextUtils.equals(defaultHomePackage, packageName)) {
            // Store the current default launcher, if any.
            if (!TextUtils.isEmpty(defaultHomePackage) && !TextUtils.equals(defaultHomePackage, "android")) {
                PrefUtils.setOriginalHomePackage(defaultHomePackage)
            }

            // Re-enable FakeHomeActivity to show the launcher selector.
            CommonUtils.setComponentEnabled(HomeActivity::class.java, true)
            CommonUtils.setComponentEnabled(FakeHomeActivity::class.java, true)
            startLauncher()
            CommonUtils.setComponentEnabled(FakeHomeActivity::class.java, false)
            if (BuildConfig.DEBUG) {
                InitActivity.start(this)
            }
        } else {
            HomeActivity.start(this)
            finish()
        }
    }
}