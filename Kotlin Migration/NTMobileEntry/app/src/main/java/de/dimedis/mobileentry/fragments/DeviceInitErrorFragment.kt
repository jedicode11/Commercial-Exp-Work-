package de.dimedis.mobileentry.fragments

import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import de.dimedis.mobileentry.ConfigPref
import de.dimedis.mobileentry.backend.BackendServiceUtil
import de.dimedis.mobileentry.backend.response.StealIdResponse
import de.dimedis.mobileentry.databinding.FragmentDeviceInitErrorBinding
import de.dimedis.mobileentry.ui.LoginActivity
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

class DeviceInitErrorFragment : ProgressLoaderFragment() {
    var binding: FragmentDeviceInitErrorBinding? = null

    class Builder {
        private val arguments: Bundle = Bundle()

        fun status(`val`: String?): Builder {
            arguments.putString("status", `val`)
            return this
        }

        fun devId(`val`: String?): Builder {
            arguments.putString("devId", `val`)
            return this
        }

        fun since(`val`: String?): Builder {
            arguments.putString("since", `val`)
            return this
        }

        fun location(`val`: String?): Builder {
            arguments.putString("location", `val`)
            return this
        }

        fun user(`val`: String?): Builder {
            arguments.putString("user", `val`)
            return this
        }

        fun session(`val`: String?): Builder {
            arguments.putString("session", `val`)
            return this
        }

        fun sessionSince(`val`: String?): Builder {
            arguments.putString("sessionSince", `val`)
            return this
        }

        fun build(): DeviceInitErrorFragment {
            val fragment = DeviceInitErrorFragment()
            fragment.arguments = arguments
            return fragment
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        super.onCreateView(inflater, container, savedInstanceState)
        binding = FragmentDeviceInitErrorBinding.inflate(inflater, container, false)
        binding!!.ok.setOnClickListener {
            onOkClick() }
        binding!!.cancel.setOnClickListener {
            onCancelClick() }
        val args: Bundle? = arguments
        binding!!.serverName.text = ConfigPref.serverName
        binding!!.place.deviceId.text = args?.getString("devId")
        setTextOrHide(binding!!.place.linearLayout2, binding!!.place.since, args?.getString("since"))
        setTextOrHide(binding!!.place.linearLayout3, binding!!.place.location, args?.getString("location"))
        setTextOrHide(binding!!.place.linearLayout4, binding!!.place.user, args?.getString("user"))
        setTextOrHide(binding!!.place.linearLayout5, binding!!.place.sessionSince, args?.getString("sessionSince"))
        return binding!!.root
    }

    fun setTextOrHide(view: View, tView: TextView, txt: String?) {
        val isVisible: Boolean = !TextUtils.isEmpty(txt)
        view.visibility = if (isVisible) View.VISIBLE else View.GONE
        if (isVisible) tView.text = txt
    }

    fun ok() {
        ConfigPref.deviceID = binding!!.place.deviceId.text.toString()
        ConfigPref.currentLanguage?.let {
            BackendServiceUtil.stealId(binding!!.place.deviceId.text.toString(), it) }
    }

    fun onOkClick() {
        setVisibilityProgressBar(true)
        ok()
    }

    fun onCancelClick() {
        val status = parentFragmentManager.popBackStackImmediate()
        Log.i(TAG, "status:$status")
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
    fun onEventMainThread(event: StealIdResponse?) {
//        setVisibilityProgressBar(false);
        if (event != null) {
            responseProcess(event, object : ResponseImpl<StealIdResponse?>() {
                override fun resultOK(event: StealIdResponse?) {
                    context?.let { LoginActivity.start(it) }
                    activity!!.finish()
                }

                override fun onError(event: StealIdResponse?): Boolean {
                    fragment(LoginScanFragment.builder().build())
                    return false
                }
            })
        }
    }

    companion object {
        const val TAG = "DeviceInitFragment"
        fun builder(): Builder {
            return Builder()
        }
    }
}