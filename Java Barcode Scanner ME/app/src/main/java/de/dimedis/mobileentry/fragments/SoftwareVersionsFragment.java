package de.dimedis.mobileentry.fragments;

import android.annotation.SuppressLint;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.loader.app.LoaderManager;
import androidx.loader.content.CursorLoader;
import androidx.loader.content.Loader;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import de.dimedis.mobileentry.R;
import de.dimedis.mobileentry.backend.response.DownloadLanguagesResponse;
import de.dimedis.mobileentry.backend.response.DownloadMyAvailableBordersResponse;
import de.dimedis.mobileentry.backend.response.DownloadOfflineConfigResponse;
import de.dimedis.mobileentry.backend.response.GetVersionsResponse;
import de.dimedis.mobileentry.backend.response.Versions;
import de.dimedis.mobileentry.databinding.FragmentSoftwareversionBinding;
import de.dimedis.mobileentry.databinding.SoftversionBinding;
import de.dimedis.mobileentry.db.content_provider.DataContentProvider;
import de.dimedis.mobileentry.service.HeartbeatService;
import de.dimedis.mobileentry.ui.InitActivity;
import de.dimedis.mobileentry.util.ConfigPrefHelper;
import de.dimedis.mobileentry.util.Logger;
import de.dimedis.mobileentry.util.UpdateUtil;
import de.dimedis.mobileentry.util.dynamicres.DynamicString;

public class SoftwareVersionsFragment extends ProgressLoaderFragment implements LoaderManager.LoaderCallbacks<Cursor> {
    static final String TAG = "SoftwareVersions";

    static final int LOADER_ID = 225;

    FragmentSoftwareversionBinding binding;
    SoftversionBinding softversionBinding;

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private final Bundle arguments;

        public Builder() {
            arguments = new Bundle();
        }

        public SoftwareVersionsFragment build() {
            SoftwareVersionsFragment fragment = new SoftwareVersionsFragment();
            fragment.setArguments(arguments);
            return fragment;
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
        UpdateUtil.updateSettings(() -> getMainActivity().runOnUiThread(() -> {
            checkUpdate();
            setUpdateButtonVisibility();
            setVisibilityProgressBar(false);
        }));
        HeartbeatService.scheduleService();
    }

    @Override
    public void onStop() {
        EventBus.getDefault().unregister(this);
        super.onStop();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        binding = FragmentSoftwareversionBinding.inflate(inflater, container, false);
        softversionBinding = binding.softVer;

        binding.startLogin.setOnClickListener(view -> {
            fragment(LoginScanFragment.builder().build());
        });

        getLoaderManager().initLoader(LOADER_ID, null, this);

        return binding.getRoot();
    }

    protected boolean isVersions;
    protected boolean isDownloadMyAvailableBorders;
    protected boolean isDownloadOfflineConfig;
    protected boolean isDownloadSettings;
    protected boolean isLocalization;

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventMainThread(DownloadLanguagesResponse event) {
        responseProcess(event, new Response<DownloadLanguagesResponse>() {
            @Override
            public void resultOK(DownloadLanguagesResponse event) {
                Logger.i(SoftwareVersionsFragment.class.getSimpleName(), "got download lang response");
                getConfigPreferences().setIsLanguagesInit(true);
                DynamicString.getInstance().setLang(getConfigPreferences().currentLanguage());
                DynamicString.scan(getView());
                isLocalization = true;
                checkForStartLogin();
            }

            @Override
            public boolean onError(DownloadLanguagesResponse event) {
                Logger.i(SoftwareVersionsFragment.class.getSimpleName(), "got download lang response error");
                isLocalization = true;
                checkForStartLogin();
                return false;
            }
        });
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventMainThread(GetVersionsResponse event) {
        Log.i(TAG, "onEvent GetVersionsResponse:" + event);
        responseProcess(event, new ResponseImpl<GetVersionsResponse>() {
            @Override
            public void resultOK(GetVersionsResponse event) {
                checkUpdate();
                setUpdateButtonVisibility();
                isVersions = true;
                checkForStartLogin();
            }
        });
    }

    //DownloadMyAvailableBordersResponse
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventMainThread(DownloadMyAvailableBordersResponse event) {
        Log.i(TAG, "onEvent DownloadMyAvailableBordersResponse event:" + event);
        responseProcess(event, new ResponseImpl<DownloadMyAvailableBordersResponse>() {
            @Override
            public void resultOK(DownloadMyAvailableBordersResponse event) {
                isDownloadMyAvailableBorders = true;
                getConfigPreferences().setIsBordersInit(true);
                checkForStartLogin();
            }
        });
    }

    ///DownloadOfflineConfigResponse
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventMainThread(DownloadOfflineConfigResponse event) {
        Log.i(TAG, "onEventMainThread DownloadOfflineConfigResponse event:" + event);
        responseProcess(event, new ResponseImpl<DownloadOfflineConfigResponse>() {
            @Override
            public void resultOK(DownloadOfflineConfigResponse event) {
                isDownloadOfflineConfig = true;
                checkForStartLogin();
            }
        });
    }

    void checkForStartLogin() {
        if (isVersions & getConfigPreferences().isBordersInit() & getConfigPreferences().isLanguagesInit() & isDownloadOfflineConfig & isDownloadSettings) {
            checkUpdate();
            setUpdateButtonVisibility();
            setVisibilityProgressBar(false);
        }
    }

    public void checkUpdate() {
        softversionBinding.textInfo.setVisibility(ConfigPrefHelper.isUpdateAvailable() ? View.VISIBLE : View.GONE);
        _fillVersionData();
    }

    protected void setUpdateButtonVisibility() {
    }

    boolean checkAndSetItem(TextView textView, String txt, TextView textViewServer, String txtServer, boolean isApp) {
        textView.setText(txt);
        if (isApp && txtServer == null) {
            textView.setText(getLocalizedString(R.string.SOFTWARE_VERSIONS_APP_VERSION_UNKNOWN_TEXT));
            textViewServer.setVisibility(View.GONE);
            return false;
        }
        if (!txtServer.equals(txt)) {
            String displayText = " " + txtServer; // Added space before txtServer
            textViewServer.setText(displayText);
            textViewServer.setVisibility(View.VISIBLE);

            if (txt.compareTo(txtServer) < 0) {
                textViewServer.setTextAppearance(R.style.TextSmallRed);
            } else {
                textViewServer.setTextAppearance(R.style.TextSmall);
            }
            return true;
        } else {
            textViewServer.setVisibility(View.GONE);
            return false;
        }
    }

    protected boolean isNeedUpdateApp = false;
    protected boolean isNeedUpdateLibrary = false;
    protected boolean isNeedUpdateLanguages = false;
    protected boolean isNeedUpdateBorders = false;
    protected boolean isNeedUpdateLocalConfig = false;

    void fillVersionsContext(Versions versions, Versions versionsServer) {


        isNeedUpdateApp = checkAndSetItem(softversionBinding.app, versions.getApp(), softversionBinding.appNew, versionsServer.getApp(), true);
        isNeedUpdateLibrary = checkAndSetItem(softversionBinding.lib, versions.getLibrary(), softversionBinding.libNew, versionsServer.getLibrary(), false);
        isNeedUpdateLanguages = checkAndSetItem(softversionBinding.lang, versions.getLanguages(), softversionBinding.langNew, versionsServer.getLanguages(), false);
        isNeedUpdateBorders = checkAndSetItem(softversionBinding.border, versions.getMyAvailableBorders(), softversionBinding.borderNew, versionsServer.getMyAvailableBorders(), false);
        isNeedUpdateLocalConfig = checkAndSetItem(softversionBinding.local, versions.getLocalConfig(), softversionBinding.localNew, versionsServer.getLocalConfig(), false);

        View v = getView();
        if (v != null) {
            v.invalidate();
        }
    }


    public void _fillVersionData() {
        fillVersionsContext(ConfigPrefHelper.getVersions(), ConfigPrefHelper.getVersionsFromServer());
    }

    @Override
    void onErrorCancelClick() {
        getActivity().finish();
        InitActivity.start(getContext());
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new CursorLoader(getContext(), DataContentProvider.TICKETS_URI, new String[]{"COUNT(*)"}, null, null, null);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        int codeCount = 0;
        if (cursor != null && cursor.moveToNext()) {
            codeCount = cursor.getInt(0);
        }
        softversionBinding.softVer.setText(Integer.toString(codeCount));// + " " + codeCountText);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
    }

    @Override
    public void onDestroy() {  // TODO Deprecated
        getActivity().getSupportLoaderManager().destroyLoader(0);
        super.onDestroy();
    }
}