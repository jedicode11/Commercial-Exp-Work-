package de.dimedis.mobileentry.fragments.menus

import android.view.*

import android.os.Bundle
import de.dimedis.mobileentry.SettingsController
import de.dimedis.mobileentry.fragments.BaseFragment
import de.dimedis.mobileentry.util.dynamicres.DynamicString

open class AbsSettingsMenuFragment : BaseFragment() {
    protected var mSettings: SettingsController? = null
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        DynamicString.scan(view)
        super.onViewCreated(view, savedInstanceState)
        init()
    }

    protected fun onClickOK() {
        mSettings!!.save()
        back()
    }

    protected fun onClickCancel() {
        back()
    }

    protected open fun init() {
        mSettings = activity?.let { SettingsController.getDefault(it) }
        mSettings!!.saveTemporary()
    }

    override fun onDetach() {
        super.onDetach()
        mSettings!!.loadTemporary()
    }
}