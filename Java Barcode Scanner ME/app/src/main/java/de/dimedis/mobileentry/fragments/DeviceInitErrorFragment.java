package de.dimedis.mobileentry.fragments;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import de.dimedis.mobileentry.backend.BackendService;
import de.dimedis.mobileentry.backend.response.StealIdResponse;
import de.dimedis.mobileentry.databinding.FragmentDeviceInitErrorBinding;
import de.dimedis.mobileentry.ui.LoginActivity;

public class DeviceInitErrorFragment extends ProgressLoaderFragment {
    static final String TAG = "DeviceInitFragment";

    FragmentDeviceInitErrorBinding binding;

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private final Bundle arguments;

        public Builder() {
            arguments = new Bundle();
        }

        public Builder status(String val) {
            arguments.putString("status", val);
            return this;
        }

        public Builder devId(String val) {
            arguments.putString("devId", val);
            return this;
        }

        public Builder since(String val) {
            arguments.putString("since", val);
            return this;
        }

        public Builder location(String val) {
            arguments.putString("location", val);
            return this;
        }

        public Builder user(String val) {
            arguments.putString("user", val);
            return this;
        }

        public Builder session(String val) {
            arguments.putString("session", val);
            return this;
        }

        public Builder sessionSince(String val) {
            arguments.putString("sessionSince", val);
            return this;
        }

        public DeviceInitErrorFragment build() {
            DeviceInitErrorFragment fragment = new DeviceInitErrorFragment();
            fragment.setArguments(arguments);
            return fragment;
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        binding = FragmentDeviceInitErrorBinding.inflate(inflater, container, false);
        binding.ok.setOnClickListener(view -> onOkClick());
        binding.cancel.setOnClickListener(view -> onCancelClick());

        Bundle args = getArguments();
        binding.serverName.setText(getConfigPreferences().serverName());
        binding.place.deviceId.setText(args.getString("devId"));
        setTextOrHide(binding.place.linearLayout2, binding.place.since, args.getString("since"));
        setTextOrHide(binding.place.linearLayout3, binding.place.location, args.getString("location"));
        setTextOrHide(binding.place.linearLayout4, binding.place.user, args.getString("user"));
        setTextOrHide(binding.place.linearLayout5, binding.place.sessionSince, args.getString("sessionSince"));
        return binding.getRoot();
    }

    void setTextOrHide(View view, TextView tView, String txt) {
        boolean isVisible = !TextUtils.isEmpty(txt);
        view.setVisibility(isVisible ? View.VISIBLE : View.GONE);
        if (isVisible) tView.setText(txt);
    }

    void ok() {
        getConfigPreferences().setDeviceID(binding.place.deviceId.getText().toString());
        BackendService.stealId(binding.place.deviceId.getText().toString(), getConfigPreferences().currentLanguage());
    }

    void onOkClick() {
        setVisibilityProgressBar(true);
        ok();
    }

    void onCancelClick() {
        boolean status = getParentFragmentManager().popBackStackImmediate();
        Log.i(TAG, "status:" + status);
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
    public void onEventMainThread(StealIdResponse event) {
        responseProcess(event, new ResponseImpl<StealIdResponse>() {
            @Override
            public void resultOK(StealIdResponse event) {
                LoginActivity.start(getContext());
                getActivity().finish();
            }

            @Override
            public boolean onError(StealIdResponse event) {
                fragment(LoginScanFragment.builder().build());
                return false;
            }
        });
    }
}
