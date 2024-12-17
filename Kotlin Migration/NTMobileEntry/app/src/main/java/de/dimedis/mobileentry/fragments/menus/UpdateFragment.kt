package de.dimedis.mobileentry.fragments.menus

import android.annotation.SuppressLint
import android.database.Cursor
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.loader.app.LoaderManager
import androidx.loader.content.CursorLoader
import androidx.loader.content.Loader
import de.dimedis.mobileentry.ConfigPref
import de.dimedis.mobileentry.R
import de.dimedis.mobileentry.SettingsController
import de.dimedis.mobileentry.backend.BackendServiceUtil
import de.dimedis.mobileentry.backend.response.*
import de.dimedis.mobileentry.databinding.FragmentMenuUpdateBinding
import de.dimedis.mobileentry.databinding.SoftversionBinding
import de.dimedis.mobileentry.db.content_provider.DataContentProvider
import de.dimedis.mobileentry.fragments.ProgressLoaderFragment
import de.dimedis.mobileentry.fragments.util.AlertFragment
import de.dimedis.mobileentry.model.Function
import de.dimedis.mobileentry.model.LocalModeHelper.initWithNewPathLibrary
import de.dimedis.mobileentry.util.ConfigPrefHelper
import de.dimedis.mobileentry.util.ConfigPrefHelper.getVersionsFromServer
import de.dimedis.mobileentry.util.ConfigPrefHelper.setVersions
import de.dimedis.mobileentry.util.ICompleteOperationCallback
import de.dimedis.mobileentry.util.Logger
import de.dimedis.mobileentry.util.UpdateUtil.updateSettings
import de.dimedis.mobileentry.util.dynamicres.DynamicString
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

open class UpdateFragment : ProgressLoaderFragment(), LoaderManager.LoaderCallbacks<Cursor?> {
    var binding: FragmentMenuUpdateBinding? = null
    var softversionBinding: SoftversionBinding? = null

    class Builder {
        fun build(): UpdateFragment {
            return UpdateFragment()
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        super.onCreateView(inflater, container, savedInstanceState)
        binding = FragmentMenuUpdateBinding.inflate(inflater, container, false)
        softversionBinding = binding!!.softVer
        binding!!.cancel.setOnClickListener {
            back() }
        binding!!.updateButton.setOnClickListener {
            onUpdate() }
        loaderManager.initLoader(LOADER_ID, null, this)
        //        LoaderManager.getInstance(this); // added for the new way
        return binding!!.root
    }

    protected fun onUpdate() {
        binding!!.updateStarted.visibility = View.VISIBLE // (updateStartedTextView)
        binding!!.updateButton.visibility = View.GONE // updateButton
        if (isNeedUpdateBorders) {
            BackendServiceUtil.downloadMyAvailableBorders()
        }
        if (isLocalization) {
            BackendServiceUtil.downloadLanguages()
        }
        activity?.let { SettingsController.getDefault(it).update(context) }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onEventMainThread(event: DownloadLibrary) {
        binding!!.updateStarted.visibility = View.INVISIBLE
        if (event.libFile != null) {
            if (initWithNewPathLibrary(event.libFile!!)) {
                setVersions(getVersionsFromServer())
                checkUpdate() //fillVersionData();
            } else {
                event.libFile!!.delete()
                Log.e(TAG, "Library init failed ...")
                AlertFragment.show(getLocalizedString(R.string.library_initialization_failed), {}, requireFragmentManager())
            }
        } else {
            Log.e(TAG, "Library init failed ...")
            AlertFragment.show(getLocalizedString(R.string.library_initialization_failed), {}, requireFragmentManager())
        }
        setUpdateButtonVisibility()
    }

    protected var isVersions = false
    protected var isDownloadMyAvailableBorders = false
    protected var isDownloadOfflineConfig = false
    protected var isDownloadSettings = false
    protected var isLocalization = false

    override fun onStart() {
        super.onStart()
        EventBus.getDefault().register(this)
        updateSettings(object : ICompleteOperationCallback {
            override fun onOperationComplete() {
                getMainActivity()!!.runOnUiThread {
                    checkUpdate()
                    setUpdateButtonVisibility()
                    setVisibilityProgressBar(false)
                }
            }
        })
    }

    override fun onStop() {
        EventBus.getDefault().unregister(this)
        super.onStop()
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onEventMainThread(event: DownloadLanguagesResponse?) {
        if (event != null) {
            responseProcess(event, object : Response<DownloadLanguagesResponse?> {
                override fun resultOK(event: DownloadLanguagesResponse?) {
                    Logger.i(UpdateFragment::class.java.simpleName, "got download lang response")
                    ConfigPref.isLanguagesInit = (true)
                    DynamicString.instance.setLang(ConfigPref.currentLanguage)
                    DynamicString.scan(view)
                    isLocalization = true
                    checkForStartLogin()
                }

                override fun onError(event: DownloadLanguagesResponse?): Boolean {
                    Logger.i(UpdateFragment::class.java.simpleName, "got download lang response error")
                    isLocalization = true
                    checkForStartLogin()
                    return false
                }
            })
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onEventMainThread(event: GetVersionsResponse) {
        Log.i(TAG, "onEvent GetVersionsResponse:$event")
        responseProcess(event, object : ResponseImpl<GetVersionsResponse?>() {
            override fun resultOK(event: GetVersionsResponse?) {
                checkUpdate()
                setUpdateButtonVisibility()
                isVersions = true
                checkForStartLogin()
            }
        })
    }

    //DownloadMyAvailableBordersResponse
    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onEventMainThread(event: DownloadMyAvailableBordersResponse) {
        Log.i(TAG, "onEvent DownloadMyAvailableBordersResponse event:$event")
        responseProcess(event, object : ResponseImpl<DownloadMyAvailableBordersResponse?>() {
            override fun resultOK(event: DownloadMyAvailableBordersResponse?) {
                isDownloadMyAvailableBorders = true
                ConfigPref.isBordersInit = (true)
                checkForStartLogin()
            }
        })
    }

    ///DownloadOfflineConfigResponse
    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onEventMainThread(event: DownloadOfflineConfigResponse) {
        Log.i(TAG, "onEventMainThread DownloadOfflineConfigResponse event:$event")
        responseProcess(event, object : ResponseImpl<DownloadOfflineConfigResponse?>() {
            override fun resultOK(event: DownloadOfflineConfigResponse?) {
                isDownloadOfflineConfig = true
                checkForStartLogin()
            }
        })
    }

    fun checkForStartLogin() {
        if (isVersions and ConfigPref.isBordersInit and ConfigPref.isLanguagesInit and isDownloadOfflineConfig and isDownloadSettings) {
            checkUpdate()
            setUpdateButtonVisibility()
            setVisibilityProgressBar(false)
        }
    }

    fun checkUpdate() {
        softversionBinding!!.textInfo.visibility = if (ConfigPrefHelper.isUpdateAvailable()) View.VISIBLE else View.GONE
        _fillVersionData()
    }

    protected fun setUpdateButtonVisibility() {
        //if(BuildConfig.DEBUG){updateButton.setVisibility(1==1 ? View.VISIBLE : View.GONE);} else
        binding!!.updateButton.visibility = if (ConfigPrefHelper.isUpdateAvailable() && Function.UPDATE.isAvailable) View.VISIBLE else View.GONE
    }

    fun checkAndSetItem(textView: TextView, txt: String, textViewServer: TextView, txtServer: String?, isApp: Boolean): Boolean {
        textView.text = txt
        if (isApp && txtServer == null) {
            textView.text = getLocalizedString(R.string.SOFTWARE_VERSIONS_APP_VERSION_UNKNOWN_TEXT)
            textViewServer.visibility = View.GONE
            return false
        }
        return if (txtServer != txt) {
            textViewServer.text = txtServer
            textViewServer.visibility = View.VISIBLE
            if (txt < txtServer!!) {
                textViewServer.setTextAppearance(R.style.TextSmallRed)
            } else {
                textViewServer.setTextAppearance(R.style.TextSmall)
            }
            true
        } else {
            textViewServer.visibility = View.GONE
            false
        }
    }

    protected var isNeedUpdateApp = false
    protected var isNeedUpdateLibrary = false
    protected var isNeedUpdateLanguages = false
    protected var isNeedUpdateBorders = false
    protected var isNeedUpdateLocalConfig = false

    fun fillVersionsContext(versions: Versions, versionsServer: Versions) {
        isNeedUpdateApp = checkAndSetItem(softversionBinding!!.app, versions.app!!, softversionBinding!!.appNew, versionsServer.app, true) == true
        isNeedUpdateLibrary = checkAndSetItem(softversionBinding!!.lib, versions.library!!, softversionBinding!!.libNew, versionsServer.library, false) == true
        isNeedUpdateLanguages = checkAndSetItem(softversionBinding!!.lang, versions.languages!!, softversionBinding!!.langNew, versionsServer.languages, false) == true
        isNeedUpdateBorders = checkAndSetItem(softversionBinding!!.border, versions.myAvailableBorders!!, softversionBinding!!.borderNew, versionsServer.myAvailableBorders, false)
        isNeedUpdateLocalConfig = checkAndSetItem(softversionBinding!!.local, versions.localConfig!!, softversionBinding!!.localNew, versionsServer.localConfig, false)
        val v = view
        v?.invalidate()
    }

    fun _fillVersionData() {
        fillVersionsContext(ConfigPrefHelper.getVersions(), getVersionsFromServer())
    }

    override fun onResume() {
        super.onResume()
    }

    override fun onCreateLoader(id: Int, args: Bundle?): Loader<Cursor?> {
        return CursorLoader(requireContext(), DataContentProvider.TICKETS_URI, arrayOf("COUNT(*)"), null, null, null)
    }

    @SuppressLint("SetTextI18n")
    override fun onLoadFinished(loader: Loader<Cursor?>, cursor: Cursor?) {
        var codeCount = 0
        if (cursor != null && cursor.moveToNext()) {
            codeCount = cursor.getInt(0)
        }
        softversionBinding!!.softVer.text = codeCount.toString() // + " " + codeCountText);
    }

    override fun onLoaderReset(loader: Loader<Cursor?>) {}
    override fun onDestroy() {
        requireActivity().supportLoaderManager.destroyLoader(0)
        super.onDestroy()
    }

    companion object {
        const val TAG = "UpdateFragment"
        const val LOADER_ID = 225
        fun builder(): Builder {
            return Builder()
        }
    }
}