package de.dimedis.mobileentry.fragments.util

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentManager
import de.dimedis.mobileentry.R
import de.dimedis.mobileentry.backend.response.ResponseError
import de.dimedis.mobileentry.databinding.FragmentAlertBinding
import de.dimedis.mobileentry.util.dynamicres.DynamicString

class AlertFragment : DialogFragment() {
    var binding: FragmentAlertBinding? = null

    class Builder {
        fun build(): AlertFragment {
            return AlertFragment()
        }
    }

    var titleRes = 0
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        super.onCreateView(inflater, container, savedInstanceState)
        binding = FragmentAlertBinding.inflate(inflater, container, false)
        return binding!!.root
    }

    private var mOnCancelClickedListener: OnCancelClickedListener? = null
    
    fun inject() {
        setStyle(STYLE_NO_FRAME, android.R.style.Theme_DeviceDefault_Dialog_NoActionBar)
    }

    fun onCancel() {
        if (mOnCancelClickedListener != null) {
            mOnCancelClickedListener!!.onCancelClicked()
        }
        dismiss()
    }

    fun setOnCancelClickedListener(listener: OnCancelClickedListener?) {
        mOnCancelClickedListener = listener
    }

    interface OnCancelClickedListener {
        fun onCancelClicked()
    }

    companion object {
        fun builder(): Builder {
            return Builder()
        }

        fun show(error: ResponseError?, listener: (() -> Unit)?, fm: FragmentManager) {
            
            val message = if (error != null) error.message else DynamicString.instance?.getString(R.string.message_unknown_error)
            if (listener != null) {
                show(message, listener, fm)
            }
        }

        fun show(message: String?, listener: (() -> Unit)?, fm: FragmentManager) {
//        AlertFragment ragment = AlertFragment.builder().message(message).build() // TODO Enable
//        fragment.setOnCancelClickedListener(listener);
//        fragment.setCancelable(false);
//        fragment.show(fm, null);
//        SoundPlayerUtil.getInstance().playSoundAsync(SoundType.GENERAL_ERROR)
        }
    }
}