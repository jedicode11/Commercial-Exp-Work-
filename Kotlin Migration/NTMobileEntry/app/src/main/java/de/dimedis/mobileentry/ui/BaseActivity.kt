package de.dimedis.mobileentry.ui

import android.content.*
import android.content.pm.PackageManager
import android.content.res.Resources
import android.os.Bundle
import android.text.TextUtils
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.zxing.client.android.Intents
import com.google.zxing.integration.android.IntentIntegrator
import de.dimedis.mobileentry.ConfigPref
import de.dimedis.mobileentry.R
import de.dimedis.mobileentry.SettingsController
import de.dimedis.mobileentry.bbapi.BarcodeManager
import de.dimedis.mobileentry.model.Function
import de.dimedis.mobileentry.ui.view.HeaderView
import de.dimedis.mobileentry.ui.view.HeaderView.OnMenuButtonClickedListener
import de.dimedis.mobileentry.util.AppContext
import de.dimedis.mobileentry.util.BarcodeUtil
import de.dimedis.mobileentry.util.CommonUtils.startLauncher
import de.dimedis.mobileentry.util.PrefUtils
import de.dimedis.mobileentry.util.UpdateUtil
import de.dimedis.mobileentry.util.dynamicres.DynamicString
import io.github.inflationx.viewpump.ViewPumpContextWrapper
import org.greenrobot.eventbus.EventBus

abstract class BaseActivity : AppCompatActivity(), SharedPreferences.OnSharedPreferenceChangeListener, SettingsController.SettingsControllerContainer {
    private val mSettingsController = SettingsController(this)
    private val mSystemDialogsReceiver = SystemDialogsReceiver()
    private var mHeaderView: HeaderView? = null
    override fun attachBaseContext(newBase: Context) {
        super.attachBaseContext(ViewPumpContextWrapper.wrap(newBase))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        PrefUtils.registerOnPrefChangeListener(this)
    }

    override fun onDestroy() {
        super.onDestroy()
        PrefUtils.unregisterOnPrefChangeListener(this)
    }

    override fun getBaseContext(): Context {
        return super.getBaseContext()
    }

    override fun getResources(): Resources {
        return super.getResources()
    }

    override fun onStart() {
        super.onStart()
        mSettingsController.onStart()
        mSystemDialogsReceiver.register()
    }

    override fun onStop() {
        super.onStop()
        mSettingsController.onStop()
        mSystemDialogsReceiver.unregister()
    }

    override fun setContentView(layoutResID: Int) {
        super.setContentView(layoutResID)
        DynamicString.scan(window.decorView)
        setupHeaderView()
    }

    private fun setupHeaderView() {
        mHeaderView = super.findViewById(R.id.header_view)
        if (mHeaderView == null) {
            return
        }
        mHeaderView!!.setDeviceId(ConfigPref.deviceID)
        mHeaderView!!.setBorderName(PrefUtils.getBorderName())
        if (Function.MENU.isAvailable) {
            mHeaderView!!.setOnMenuButtonClickedListener(object : OnMenuButtonClickedListener {
                override fun onMenuButtonClicked() {
                    MenuActivity.start(this@BaseActivity)
                }
            })
        } else {
            mHeaderView!!.setMenuButtonVisible(false)
        }
        mHeaderView!!.setCameraButtonVisible(PrefUtils.getCameraReaderValue())
    }

    override fun onResume() {
        super.onResume()
        if (mHeaderView != null) {
            mHeaderView!!.setCameraButtonVisible(PrefUtils.getCameraReaderValue())
        }
    }

    override fun onSharedPreferenceChanged(prefs: SharedPreferences, key: String) {
        if (mHeaderView != null && TextUtils.equals(key, PrefUtils.PREF_BORDER_NAME)) {
            mHeaderView!!.setBorderName(PrefUtils.getBorderName())
        } else if (mHeaderView != null && TextUtils.equals(key, SettingsController.KEY_CAM_READER_ENABLED)) {
            mHeaderView!!.setCameraButtonVisible(PrefUtils.getCameraReaderValue())
        }
    }

    override fun getSettingsController(): SettingsController {
        return mSettingsController
    }

    fun getLocalizedString(resId: Int): String? {
        return DynamicString.instance?.getString(resId)
    }

    private inner class SystemDialogsReceiver : BroadcastReceiver() {
        private val mFilter: IntentFilter = IntentFilter()

        init {
            mFilter.addAction(Intent.ACTION_CLOSE_SYSTEM_DIALOGS)
        }

        fun register() {
            AppContext.get().registerReceiver(this, mFilter)
        }

        fun unregister() {
            try {
                AppContext.get().unregisterReceiver(this)
            } catch (ignore: IllegalArgumentException) {
            }
        }

        override fun onReceive(context: Context, intent: Intent) {
            // Open Home screen to block the recent tasks in the kiosk mode.
            val reason = intent.getStringExtra("reason")
            if (TextUtils.equals(reason, "recentApps") && PrefUtils.isKioskModeEnabled()) {
                startLauncher()
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (data == null) {
            return
        }
        if (requestCode == IntentIntegrator.REQUEST_CODE) {
            var dataStr: String?
            if (data.getStringExtra(Intents.Scan.RESULT).also { dataStr = it } != null) {
                EventBus.getDefault().postSticky(dataStr?.let {
                    BarcodeManager.BarcodeScannedEvent(it) })
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data)
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            HeaderView.REQUEST -> {
                run {
                    if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                        BarcodeUtil.openCamReader(this)
                    } else {
                        Toast.makeText(baseContext, "The app was not allowed to use your camera", Toast.LENGTH_LONG).show()
                    }
                }
                run {
                    if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                        UpdateUtil.loadApp()
                    } else {
                        Toast.makeText(baseContext, "The app was not allowed to write", Toast.LENGTH_LONG).show()
                    }
                }
                run {
                    if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                        UpdateUtil.loadApp()
                    } else {
                        Toast.makeText(baseContext, "The app was not allowed to write", Toast.LENGTH_LONG).show()
                    }
                }
            }
            SettingsController.REQUEST -> {
                run {
                    if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                        UpdateUtil.loadApp()
                    } else {
                        Toast.makeText(baseContext, "The app was not allowed to write", Toast.LENGTH_LONG).show()
                    }
                }
                run {
                    if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                        UpdateUtil.loadApp()
                    } else {
                        Toast.makeText(baseContext, "The app was not allowed to write", Toast.LENGTH_LONG).show()
                    }
                }
            }
            SettingsController.STORAGE_REQUEST -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    UpdateUtil.loadApp()
                } else {
                    Toast.makeText(baseContext, "The app was not allowed to write", Toast.LENGTH_LONG).show()
                }
            }
        }
    }
}