package de.dimedis.mobileentry.fragments.menus

import android.database.Cursor
import android.view.*
import androidx.loader.app.LoaderManager
import androidx.loader.content.CursorLoader
import androidx.loader.content.Loader
import de.dimedis.mobileentry.databinding.FragmentMenuPushLocalCodesBinding
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe

import de.dimedis.mobileentry.db.content_provider.DataContentProvider
import org.greenrobot.eventbus.ThreadMode
import android.os.Bundle
import android.annotation.SuppressLint
import de.dimedis.mobileentry.R
import de.dimedis.mobileentry.fragments.ProgressLoaderFragment
import de.dimedis.mobileentry.model.StatusManager
import de.dimedis.mobileentry.util.DataBaseUtil

class PushLocalCodesMenuFragment : ProgressLoaderFragment(), LoaderManager.LoaderCallbacks<Cursor?> {
    var binding: FragmentMenuPushLocalCodesBinding? = null
    var mStatusManager: StatusManager? = null

    class Builder {
        fun build(): PushLocalCodesMenuFragment {
            return PushLocalCodesMenuFragment()
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        super.onCreateView(inflater, container, savedInstanceState)
        binding = FragmentMenuPushLocalCodesBinding.inflate(inflater, container, false)
        binding!!.cancel.setOnClickListener {
            back() }
        binding!!.sendCodes.setOnClickListener {
            if (StatusManager.getInstance()?.getStatus() == StatusManager.Status.ONLINE) {
                setVisibilityProgressBar(true)
                DataBaseUtil.uploadCachedTickets()
            }
        }
        binding!!.title.text = getLocalizedString(R.string.PUSH_CODE_TITLE)
        binding!!.label.text = getLocalizedString(R.string.PUSH_CODE_INFO_TEXT)
        mStatusManager = StatusManager.getInstance()
        loaderManager.initLoader(LOADER_ID, null, this)
        return binding!!.root
    }

    override fun onCreateLoader(id: Int, args: Bundle?): Loader<Cursor?> {
        return CursorLoader(requireContext(), DataContentProvider.TICKETS_URI, arrayOf("COUNT(*)"),
            null, null, null)
    }

    @SuppressLint("SetTextI18n")
    override fun onLoadFinished(loader: Loader<Cursor?>, cursor: Cursor?) {
        var codeCount = 0
        if (cursor != null && cursor.moveToNext()) {
            codeCount = cursor.getInt(0)
        }
        if (codeCount > 0) {
            if (!mStatusManager!!.isOnline()) {
                binding!!.label.text = getLocalizedString(R.string.PUSH_CODE_OFFLINE_TEXT)
                binding!!.sendCodes.visibility = View.GONE
            } else {
                binding!!.label.text = getLocalizedString(R.string.PUSH_CODE_INFO_TEXT)
                binding!!.sendCodes.visibility = View.VISIBLE
            }
            binding!!.codesInfo.text =
                getLocalizedString(R.string.PUSH_CODE_CODES_ON_DEVICE_LABEL) + " " + codeCount
        } else {
            binding!!.label.text = getLocalizedString(R.string.PUSH_CODE_NOCODES_TEXT)
            binding!!.codesInfo.text =
                getLocalizedString(R.string.PUSH_CODE_CODES_ON_DEVICE_LABEL) + " " + codeCount
            binding!!.sendCodes.visibility = View.GONE
        }
    }

    override fun onLoaderReset(loader: Loader<Cursor?>) {}
    override fun onStart() {
        super.onStart()
        EventBus.getDefault().register(this)
    }

    override fun onStop() {
        EventBus.getDefault().unregister(this)
        super.onStop()
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onEvent(event: DataBaseUtil.UploadTicketsEvent) {
        requireActivity().runOnUiThread {
            if (event.isUpload) {
                setVisibilityProgressBar(false)
            } else {
                requireActivity().finish()
            }
        }
    }

    companion object {
        const val LOADER_ID = 225
        fun builder(): Builder {
            return Builder()
        }
    }
}