package de.dimedis.mobileentry.fragments

import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import de.dimedis.mobileentry.MobileEntryApplication
import de.dimedis.mobileentry.backend.response.BaseResponse
import de.dimedis.mobileentry.backend.response.BaseResponseContent
import de.dimedis.mobileentry.ui.BaseFragmentActivity
import de.dimedis.mobileentry.util.dynamicres.DynamicString

open class BaseFragment : Fragment() {
    var mActivity: Activity? = null
    override fun onDestroy() {
        super.onDestroy()
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is Activity) mActivity = context
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        DynamicString.scan(view)
        super.onViewCreated(view, savedInstanceState)
    }

    fun getMainActivity(): Activity? {
        return if (super.getActivity() != null) activity else mActivity
    }

    fun fragment(fragment: Fragment) {
        ((if (activity != null) activity else mActivity) as
                BaseFragmentActivity?)!!.fragment(fragment, true)
    }

    fun replaceFragment(fragment: Fragment) {
        (activity as BaseFragmentActivity?)!!.fragment(fragment, false)
    }

    fun getApp(): MobileEntryApplication {
        val ba = activity as BaseFragmentActivity?
        return ba?.application as MobileEntryApplication
    }

    fun back() {
        activity?.onBackPressed()
    }

    interface Response<T : BaseResponse<*>?> {
        fun resultOK(event: T)
        /**
         * @param event
         * @return true if need call onErrorCancelClick()
         */
        fun onError(event: T): Boolean
    }

    open class ResponseImpl<T : BaseResponse<*>?> : Response<T> {
        override fun resultOK(event: T) {}
        override fun onError(event: T): Boolean {
            return true
        }
    }

    protected fun responseProcess(event: BaseResponse<*>, rsponse: Response<*>) {
        if (event.isResultOk) {
            if (event.content is BaseResponseContent) {
                val brc = event.content as BaseResponseContent
                if (brc.isStatusSuccess()) {
                    rsponse.resultOK(event as Nothing)
                }
            }
        } else {
        }
    }

    open fun onErrorCancelClick() {}

//    get localized string
    fun getLocalizedString(resId: Int): String? {
        return DynamicString.instance?.getString(resId)
    }

    companion object {
        const val TAG = "BaseFragment"
    }
}