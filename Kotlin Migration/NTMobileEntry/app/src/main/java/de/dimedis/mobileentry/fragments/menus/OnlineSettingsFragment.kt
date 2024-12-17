package de.dimedis.mobileentry.fragments.menus

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import de.dimedis.mobileentry.databinding.FragmentOnlinesettingsBinding

class OnlineSettingsFragment : AbsSettingsMenuFragment() {
    var binding: FragmentOnlinesettingsBinding? = null

    class Builder {
        fun build(): OnlineSettingsFragment {
            return OnlineSettingsFragment()
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        super.onCreateView(inflater, container, savedInstanceState)
        binding = FragmentOnlinesettingsBinding.inflate(inflater, container, false)
        binding!!.ok.setOnClickListener {
            onClickOK() }
        binding!!.cancel.setOnClickListener {
            onClickCancel() }
        return binding!!.root
    }

    override fun init() {
        super.init()
        binding!!.switch1.isChecked = mSettings!!.localScan
        binding!!.switch1.setOnCheckedChangeListener { buttonView, isChecked ->
            mSettings!!.localScan = (isChecked) }
    }

    companion object {
        fun builder(): Builder {
            return Builder()
        }
    }
}