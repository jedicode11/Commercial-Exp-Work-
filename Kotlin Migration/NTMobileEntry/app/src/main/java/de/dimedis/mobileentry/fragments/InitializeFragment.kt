package de.dimedis.mobileentry.fragments

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.ActivityCompat
import de.dimedis.mobileentry.ConfigPref
import de.dimedis.mobileentry.MobileEntryApplication
import de.dimedis.mobileentry.backend.ApiService
import de.dimedis.mobileentry.backend.BackendServiceUtil
import de.dimedis.mobileentry.backend.response.ServerConnectResponse
import de.dimedis.mobileentry.bbapi.BarcodeManager
import de.dimedis.mobileentry.databinding.FragmentInitailizeBinding
import de.dimedis.mobileentry.util.PrefUtils
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import java.util.*

class InitializeFragment : ProgressLoaderFragment() {

    companion object {
        fun builder(): Builder {
            return Builder()
        }

        private fun hasPermissions(context: Context?, vararg permissions: String): Boolean {
            if (context != null) {
                for (permission in permissions) {
                    if (ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                        return false
                    }
                }
            }
            return true
        }
    }

    var binding: FragmentInitailizeBinding? = null

    class Builder {
        fun build(): InitializeFragment {
            return InitializeFragment()
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        super.onCreateView(inflater, container, savedInstanceState)
        binding = FragmentInitailizeBinding.inflate(inflater, container, false)
//        binding!!.version.text = "v " + Utils.getVersionName(getContext(), true);
        binding!!.version.text = "v"
        return binding!!.root
    }

    override fun onStart() {
        super.onStart()
        EventBus.getDefault().register(this)
        this.activity?.let { BarcodeManager.instance.open(it) }
        if (MobileEntryApplication.isDemoMode()) {
            MobileEntryApplication.getDemoConf()?.getDemoInitializeBarcode()?.let {
                connectToServer(it)
            }
        }
        ActivityCompat.requestPermissions(requireActivity(), arrayOf(
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.MANAGE_EXTERNAL_STORAGE), 1)
    }

    override fun onStop() {
        super.onStop()
        EventBus.getDefault().unregister(this)
        this.activity?.let { BarcodeManager.instance.close(it) }
    }

    override fun onPause() {
        super.onPause()
        EventBus.getDefault().removeAllStickyEvents()
    }

    private fun connectToServer(barcode: String) {
        var token: String? = null
        var url: String? = null
        if (!TextUtils.isEmpty(barcode)) {
            val st = StringTokenizer(barcode, "@")
            if (st.hasMoreTokens()) {
                token = st.nextToken()
            }
            if (st.hasMoreTokens()) {
                url = st.nextToken()
            }
        }
        if (TextUtils.isEmpty(url)) {
            binding!!.errorMessage.visibility = View.VISIBLE
            return
        }
        //// FIXME: 11/25/2015 remove all debug features
        if (MobileEntryApplication.isDemoMode() && TextUtils.isEmpty(token)) {
            token = MobileEntryApplication.getDemoConf()?.getDefaultToken()
        }

        // HACK: When URL is changed we need to recreate REST Adapter
        if (PrefUtils.getRpcUrl() != url) {
            PrefUtils.setRpcUrl(url)
            ApiService.setupRestAdapter()
        }
        ConfigPref.customerToken = token
        BackendServiceUtil.serverConnect(token!!)
        setVisibilityProgressBar(true)
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onEventMainThread(event: ServerConnectResponse) {
        if (event.isResultOk && event.content!!.isStatusSuccess()) {
            PrefUtils.setInitCompleted(true)
            fragment(LanguageFragment.builder().build())
        } else {
            setVisibilityProgressBar(false)
            binding!!.errorMessage.visibility = View.VISIBLE
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onEventMainThread(event: BarcodeManager.BarcodeScannedEvent) {
        event.barcode?.let { connectToServer(it) }
    }
}