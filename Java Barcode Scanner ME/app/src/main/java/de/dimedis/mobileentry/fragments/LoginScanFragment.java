package de.dimedis.mobileentry.fragments;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import de.dimedis.mobileentry.backend.BackendService;
import de.dimedis.mobileentry.backend.response.UserLoginByBarcodeResponse;
import de.dimedis.mobileentry.bbapi.BarcodeManager;
import de.dimedis.mobileentry.databinding.FragmentLoginScanBinding;
import de.dimedis.mobileentry.util.CommonUtils;
import de.dimedis.mobileentry.util.SessionUtils;

public class LoginScanFragment extends LoginFragment {

    private FragmentLoginScanBinding binding;

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        public Builder() {
        }

        public LoginScanFragment build() {
            return new LoginScanFragment();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        binding = FragmentLoginScanBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    protected TextView getErrorMessage() {
        return binding.errorMessage;
    }

    @Override
    public void onStart() {
        super.onStart();
        BarcodeManager.getInstance().open(this.getActivity());
    }

    @Override
    public void onStop() {
        super.onStop();
        BarcodeManager.getInstance().close(this.getActivity());
    }

    private void loginByBarcode(String barcode) {
        if (TextUtils.isEmpty(barcode)) {
            showError(true);
        } else {
            if (!CommonUtils.isInternetConnected()) {
                SessionUtils.loginOffline(barcode);
            } else {
                BackendService.userLoginByBarcode(barcode);
                setVisibilityProgressBar(true);
            }
        }
    }

    @Override
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventMainThread(UserLoginByBarcodeResponse event) {
        super.onEventMainThread(event);
        restartScreenSaverTask();
    }

    @SuppressWarnings("unused")
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventMainThread(BarcodeManager.BarcodeScannedEvent event) {
        loginByBarcode(event.barcode);
        restartScreenSaverTask();
    }
}
