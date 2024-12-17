package de.dimedis.mobileentry.fragments

import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import de.dimedis.mobileentry.backend.BackendServiceUtil
import de.dimedis.mobileentry.backend.response.UserLoginByBarcodeResponse
import de.dimedis.mobileentry.bbapi.BarcodeManager
import de.dimedis.mobileentry.bbapi.BarcodeManager.BarcodeScannedEvent
import de.dimedis.mobileentry.databinding.FragmentLoginScanBinding
import de.dimedis.mobileentry.util.CommonUtils.isInternetConnected
import de.dimedis.mobileentry.util.SessionUtils.loginOffline
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

class LoginScanFragment : LoginFragment() {
    private var binding: FragmentLoginScanBinding? = null

    class Builder {
        fun build(): LoginScanFragment {
            return LoginScanFragment()
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        super.onCreateView(inflater, container, savedInstanceState)
        binding = FragmentLoginScanBinding.inflate(inflater, container, false)
        return binding!!.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }

    override fun getErrorMessage(): TextView {
        return binding!!.errorMessage
    }

    override fun onStart() {
        super.onStart()
        this.activity?.let { BarcodeManager.instance.open(it) }
    }

    override fun onStop() {
        super.onStop()
        this.activity?.let { BarcodeManager.instance.close(it) }
    }

    private fun loginByBarcode(barcode: String) {
        if (TextUtils.isEmpty(barcode)) {
            showError(true)
        } else {
            if (!isInternetConnected()) {
                loginOffline(barcode)
            } else {
                BackendServiceUtil.userLoginByBarcode(barcode)
                setVisibilityProgressBar(true)
            }
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    override fun onEventMainThread(event: UserLoginByBarcodeResponse) {
        super.onEventMainThread(event)
        restartScreenSaverTask()
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onEventMainThread(event: BarcodeScannedEvent) {
        event.barcode?.let { loginByBarcode(it) }
        restartScreenSaverTask()
    }

    companion object {
        fun builder(): Builder {
            return Builder()
        }
    }
}