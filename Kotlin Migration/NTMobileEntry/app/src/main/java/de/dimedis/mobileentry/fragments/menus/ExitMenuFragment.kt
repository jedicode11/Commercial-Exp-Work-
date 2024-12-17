package de.dimedis.mobileentry.fragments.menus

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import de.dimedis.mobileentry.R
import de.dimedis.mobileentry.SettingsController
import de.dimedis.mobileentry.databinding.FragmentMenuConfirmationBinding
import de.dimedis.mobileentry.fragments.ProgressLoaderFragment

class ExitMenuFragment : ProgressLoaderFragment() {
    var binding: FragmentMenuConfirmationBinding? = null

    class Builder {
        fun build(): ExitMenuFragment {
            return ExitMenuFragment()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        super.onCreateView(inflater, container, savedInstanceState)
        binding = FragmentMenuConfirmationBinding.inflate(inflater, container, false)
        setVisibilityProgressBar(false)
        binding!!.title.text = getLocalizedString(R.string.EXIT_APP_TITLE)
        binding!!.label.text = getLocalizedString(R.string.EXIT_APP_QUESTION_TEXT)
        binding!!.okCancel.ok.setOnClickListener {
            onClickOk() }
        binding!!.okCancel.cancel.setOnClickListener {
            onClickCancel() }
        return binding!!.root
    }

    fun onClickOk() {
        setVisibilityProgressBar(true)
        activity?.let { SettingsController.getDefault(it).exit() }
    }

    fun onClickCancel() {
        back()
    }

    companion object {
        fun builder(): Builder {
            return Builder()
        }
    }
}