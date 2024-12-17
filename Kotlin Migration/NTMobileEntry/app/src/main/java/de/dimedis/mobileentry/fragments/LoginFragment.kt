package de.dimedis.mobileentry.fragments


import android.text.TextUtils
import android.widget.TextView
import org.greenrobot.eventbus.ThreadMode
import android.os.Bundle
import android.util.Log
import android.view.MotionEvent
import android.view.View
import de.dimedis.mobileentry.Config
import de.dimedis.mobileentry.backend.response.UserLoginByBarcodeResponse
import de.dimedis.mobileentry.model.OnUserOfflineLoginCompleted
import de.dimedis.mobileentry.util.PrefUtils
import de.dimedis.mobileentry.util.UIHandler
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe

abstract class LoginFragment : ProgressLoaderFragment() {
    companion object {
        const val TAG = "LoginFragment"
    }

    protected abstract fun getErrorMessage(): TextView

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setVisibilityProgressBar(false)

        view.setOnTouchListener { v: View?, event: MotionEvent ->
            Log.i(TAG, "onTouch event:" + event.action)
            restartScreenSaverTask()
            false
        }
    }

    inner class ScreenSaver : Runnable {
        override fun run() {
            fragment(SoftwareVersionsFragment.builder().build())
        }
    }

    var mScreenSaver = ScreenSaver()

    fun restartScreenSaverTask() {
        val handler = UIHandler.get()
        handler.removeCallbacks(mScreenSaver)
        handler.postDelayed(mScreenSaver, Config.SCREENSAVER)
    }

    fun stopScreenSaverTask() {
        UIHandler.get().removeCallbacks(mScreenSaver)
    }

    override fun onStart() {
        Log.i(TAG, "onStart()")
        super.onStart()
        EventBus.getDefault().register(this)
        restartScreenSaverTask()
    }

    override fun onStop() {
        Log.i(TAG, "onStop()")
        stopScreenSaverTask()
        EventBus.getDefault().unregister(this)
        super.onStop()
    }

    override fun onPause() {
        super.onPause()
        EventBus.getDefault().removeAllStickyEvents()
    }

    fun showError(isVisible: Boolean) {
        getErrorMessage().visibility = if (isVisible) View.VISIBLE else View.INVISIBLE
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onEventMainThread(event: OnUserOfflineLoginCompleted) {
        if (event.isSuccessfulLogin()) {
            PrefUtils.setLoginCompleted(true)
            PrefUtils.setKioskModeEnabled(true)
            setVisibilityProgressBar(false)
            event.saveUserSessionPrefs()
            fragment(LocationFragment.builder().build())
        } else {
            showError(true)
            setVisibilityProgressBar(false)
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    open fun onEventMainThread(event: UserLoginByBarcodeResponse) {
        if (event.isResultOk && event.content!!.isStatusSuccess()) {
            PrefUtils.setLoginCompleted(true)
            PrefUtils.setKioskModeEnabled(true)
            fragment(LocationFragment.builder().build())
        } else {
            showError(true)
            if (event.content != null && !TextUtils.isEmpty(event.content!!.userMessage)) {
                getErrorMessage().text = event.content!!.userMessage
            }
            setVisibilityProgressBar(false)
        }
    }
}