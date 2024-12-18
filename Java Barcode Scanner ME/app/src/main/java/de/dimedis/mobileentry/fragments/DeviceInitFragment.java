package de.dimedis.mobileentry.fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import de.dimedis.mobileentry.backend.BackendService;
import de.dimedis.mobileentry.backend.response.GrabIdResponse;
import de.dimedis.mobileentry.databinding.FragmentDeviceInitBinding;
import de.dimedis.mobileentry.databinding.ViewDeviceinitDefmodeBinding;
import de.dimedis.mobileentry.ui.LoginActivity;
import de.dimedis.mobileentry.util.SoftKeyboard;

public class DeviceInitFragment extends BaseFragment {

    FragmentDeviceInitBinding binding;
    ViewDeviceinitDefmodeBinding placeBinding;
    static final String TAG = "DeviceInitFragment";


    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private final Bundle arguments;

        public Builder() {
            arguments = new Bundle();
        }

        public DeviceInitFragment build() {
            DeviceInitFragment fragment = new DeviceInitFragment();
            fragment.setArguments(arguments);
            return fragment;
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        binding = FragmentDeviceInitBinding.inflate(inflater, container, false);
        placeBinding = binding.place;
        binding.ok.setOnClickListener(view -> ok());
//        binding.cancel.setOnClickListener(view -> getChildFragmentManager().popBackStack());
        binding.cancel.setOnClickListener(view -> onCancelClick());
        binding.serverName.setText(getConfigPreferences().serverName());
        placeBinding.editId.setOnEditorActionListener((v, actionId, event) -> {
            final String deviceId = v.getText().toString();
            Log.w(TAG, "TEXT ID:" + deviceId);
            getConfigPreferences().setDeviceID(deviceId);
            ok();
            return false;
        });
        return binding.getRoot();
    }

    //error_message
    void setErrorMessageVisibility(boolean isVisible) {
        placeBinding.errorMessage.setVisibility(isVisible ? View.VISIBLE : View.INVISIBLE);
    }

    void ok() {
        SoftKeyboard.hideKeyboard(placeBinding.editId);
        final String deviceId = placeBinding.editId.getText().toString();
        getConfigPreferences().setDeviceID(deviceId);
        BackendService.grabId(deviceId, getConfigPreferences().currentLanguage());
    }

    void onCancelClick() {
        boolean status = getParentFragmentManager().popBackStackImmediate();
        Log.i(TAG, "status:" + status);
    }

    void showError() {
        setErrorMessageVisibility(true);
    }

    @Override
    public void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    public void onStop() {
        EventBus.getDefault().unregister(this);
        super.onStop();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventMainThread(GrabIdResponse event) {
        if (event.isResultOk()) {
            if (event.content.isStatusSuccess()) {
                LoginActivity.start(getContext());
                getActivity().finish();
            } else {
                showError();//showError(event.content.status);
                fragment(DeviceInitErrorFragment.builder().devId(Integer.toString(event.content.getDevice_id())).since(event.content.getOtherDeviceInstall_ts()).location(event.content.getOtherDeviceInstall_text()).status(event.content.status).session(event.content.getOtherUserSessionStart_text()).sessionSince(event.content.getOtherUserSessionStart_text()).build());
            }
        } else {
            showError();//event.error.code);
            fragment(LoginScanFragment.builder().build());
        }
    }
}
