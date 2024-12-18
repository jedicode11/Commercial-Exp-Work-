package de.dimedis.mobileentry.fragments;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.Settings;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.StringTokenizer;

import de.dimedis.mobileentry.MobileEntryApplication;
import de.dimedis.mobileentry.backend.BackendService;
import de.dimedis.mobileentry.backend.response.ServerConnectResponse;
import de.dimedis.mobileentry.bbapi.BarcodeManager;
import de.dimedis.mobileentry.databinding.FragmentInitailizeBinding;
import de.dimedis.mobileentry.fragments.util.Utils;
import de.dimedis.mobileentry.util.PrefUtils;

public class InitializeFragment extends ProgressLoaderFragment {

    FragmentInitailizeBinding binding;

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        public Builder() {
        }

        public InitializeFragment build() {
            return new InitializeFragment();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        binding = FragmentInitailizeBinding.inflate(inflater, container, false);
        binding.version.setText("v " + Utils.getVersionName(getContext(), true));
        return binding.getRoot();
    }


    private static boolean hasPermissions(Context context, String... permissions) {
        if (context != null && permissions != null) {
            for (String permission : permissions) {
                if (ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
            }
        }
        return true;
    }

    @RequiresApi(api = Build.VERSION_CODES.R)
    @Override
    public void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
        BarcodeManager.getInstance().open(this.requireActivity());
        if (MobileEntryApplication.isDemoMode()) {
            connectToServer(MobileEntryApplication.getDemoConf().getDemoInitializeBarcode());
        }
        // SDK 23-29 Permission
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(mActivity.getApplicationContext(), Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this.requireActivity(), new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.MANAGE_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
            }
        }
//        SDK 30+ Permission
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            if (!Environment.isExternalStorageManager()) {
                try {
                    Intent intent = new Intent(Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION);
                    intent.addCategory("android.intent.category.DEFAULT");
                    intent.setData(Uri.parse(String.format("package:%s", requireActivity().getApplicationContext().getPackageName())));
                    mActivity.startActivityIfNeeded(intent, 101);
                } catch (Exception exception) {
                    Intent intent = new Intent();
                    intent.setAction(Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION);
                    mActivity.startActivityIfNeeded(intent, 101);
                }
            }
        }
    }

    public void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);
        BarcodeManager.getInstance().close(this.getActivity());
    }

    @Override
    public void onPause() {
        super.onPause();
        EventBus.getDefault().removeAllStickyEvents();
    }

    private void connectToServer(String barcode) {
        String token = null, url = null;
        if (!TextUtils.isEmpty(barcode)) {
            StringTokenizer st = new StringTokenizer(barcode, "@");
            if (st.hasMoreTokens()) {
                token = st.nextToken();
            }
            if (st.hasMoreTokens()) {
                url = st.nextToken();
            }
        }
        if (TextUtils.isEmpty(url)) {
            binding.errorMessage.setVisibility(View.VISIBLE);
            return;
        }
        if (MobileEntryApplication.isDemoMode() && TextUtils.isEmpty(token)) {
            token = MobileEntryApplication.getDemoConf().getDefaultToken();
        }
        if (!PrefUtils.getRpcUrl().equals(url)) {
            PrefUtils.setRpcUrl(url);
            BackendService.setupRestAdapter();
        }
        getConfigPreferences().setCustomerToken(token);
        BackendService.serverConnect(token);
        setVisibilityProgressBar(true);
    }

    @SuppressWarnings("unused")
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventMainThread(ServerConnectResponse event) {
        if (event.isResultOk() && event.content.isStatusSuccess()) {
            PrefUtils.setInitCompleted(true);
            fragment(LanguageFragment.builder().build());
        } else {
            setVisibilityProgressBar(false);
            binding.errorMessage.setVisibility(View.VISIBLE);
        }
    }

    @SuppressWarnings("unused")
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventMainThread(BarcodeManager.BarcodeScannedEvent event) {
        connectToServer(event.barcode);
    }
}
