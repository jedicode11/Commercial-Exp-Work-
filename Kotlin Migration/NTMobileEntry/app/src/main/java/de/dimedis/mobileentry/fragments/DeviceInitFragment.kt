package de.dimedis.mobileentry.fragments

import android.os.Bundle
import android.util.Log
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import de.dimedis.mobileentry.ConfigPref
import de.dimedis.mobileentry.backend.BackendServiceUtil
import de.dimedis.mobileentry.backend.response.GrabIdResponse
import de.dimedis.mobileentry.databinding.FragmentDeviceInitBinding
import de.dimedis.mobileentry.databinding.ViewDeviceinitDefmodeBinding
import de.dimedis.mobileentry.ui.LoginActivity
import de.dimedis.mobileentry.util.PrefUtils
import de.dimedis.mobileentry.util.SoftKeyboard
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

class DeviceInitFragment : BaseFragment() {
    var binding: FragmentDeviceInitBinding? = null
    var placeBinding: ViewDeviceinitDefmodeBinding? = null

    class Builder {
        private val arguments: Bundle = Bundle()

        fun build(): DeviceInitFragment {
            val fragment = DeviceInitFragment()
            fragment.arguments = arguments
            return fragment
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        super.onCreateView(inflater, container, savedInstanceState)
        binding = FragmentDeviceInitBinding.inflate(inflater, container, false)
        placeBinding = binding!!.place
        binding!!.ok.setOnClickListener {
            ok() }
        binding!!.cancel.setOnClickListener {
            onCancelClick() }
        binding!!.serverName.text = ConfigPref.serverName
        placeBinding!!.editId.setOnEditorActionListener(TextView.OnEditorActionListener { v: TextView, actionId: Int, event: KeyEvent? ->
            val deviceId: String = v.text.toString()
            Log.w(TAG, "TEXT ID:$deviceId")
            ConfigPref.deviceID = deviceId
            ok()
            false
        })
        return binding!!.root
    }

    //error_message
    fun setErrorMessageVisibility(isVisible: Boolean) {
        placeBinding!!.errorMessage.visibility =
            if (isVisible) View.VISIBLE else View.INVISIBLE
    }

    fun ok() {
        SoftKeyboard.hideKeyboard(placeBinding!!.editId)
        val deviceId = placeBinding!!.editId.getText().toString()
        ConfigPref.deviceID = deviceId
        ConfigPref.currentLanguage?.let { BackendServiceUtil.grabId(deviceId, it) }
    }

    fun onCancelClick() {
        val status = parentFragmentManager.popBackStackImmediate()
        Log.i(TAG, "status:$status")
    }

    fun showError() {
        setErrorMessageVisibility(true)
    }

    override fun onStart() {
        super.onStart()
        EventBus.getDefault().register(this)
    }

    override fun onStop() {
        EventBus.getDefault().unregister(this)
        super.onStop()
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onEventMainThread(event: GrabIdResponse) {
        if (event.isResultOk) {
            if (event.content?.isStatusSuccess() == true) {
                PrefUtils.setKioskModeEnabled(true)
                context?.let { LoginActivity.start(it) }
                requireActivity().finish()
            } else {
                showError() //showError(event.content.status);
                fragment(DeviceInitErrorFragment.builder()
                        .devId(event.content!!.getDevice_id().toString())
                        .since(event.content!!.getOtherDeviceInstall_ts())
                        .location(event.content!!.getOtherDeviceInstall_text())
                        .status(event.content!!.status)
                        .session(event.content!!.getOtherUserSessionStart_text())
                        .sessionSince(event.content!!.getOtherUserSessionStart_text())
                        .build())
            }
        } else {
            showError() //event.error.code);
            fragment(LoginScanFragment.builder().build())
        }
    }

    companion object {
        const val TAG = "DeviceInitFragment"
        fun builder(): Builder {
            return Builder()
        }
    }
}