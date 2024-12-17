package de.dimedis.mobileentry.fragments.menus

import android.database.Cursor
import android.view.*
import androidx.loader.app.LoaderManager
import androidx.loader.content.Loader
import de.dimedis.mobileentry.databinding.FragmentMenuConfirmationBinding

import android.os.Bundle
import de.dimedis.mobileentry.R
import de.dimedis.mobileentry.SettingsController
import de.dimedis.mobileentry.fragments.ProgressLoaderFragment

class ResetMenuFragment : ProgressLoaderFragment(), LoaderManager.LoaderCallbacks<Cursor> {
    var binding: FragmentMenuConfirmationBinding? = null
    override fun onCreateLoader(id: Int, args: Bundle?): Loader<Cursor> {
        return null!!
    }

    override fun onLoadFinished(loader: Loader<Cursor>, data: Cursor) {}
    override fun onLoaderReset(loader: Loader<Cursor>) {}
    class Builder {
        fun build(): ResetMenuFragment {
            return ResetMenuFragment()
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        super.onCreateView(inflater, container, savedInstanceState)
        binding = FragmentMenuConfirmationBinding.inflate(inflater, container, false)
        setVisibilityProgressBar(false)
        binding!!.title.text = getLocalizedString(R.string.RESET_DEVICE_TITLE)
        binding!!.label.text = getLocalizedString(R.string.RESET_DEVICE_QUESTION_TEXT)
        binding!!.okCancel.ok.setOnClickListener {
            onClickOk() }
        binding!!.okCancel.cancel.setOnClickListener {
            onClickCancel() }
        return binding!!.root
    }

    fun onClickOk() {
        setVisibilityProgressBar(true)
        activity?.let { SettingsController.getDefault(it).reset(forceOfflineLogout = false, shouldResetDeviceState = false, isSessionInvalid = true) }
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