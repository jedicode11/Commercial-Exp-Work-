package de.dimedis.mobileentry.fragments.util;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentManager;

import de.dimedis.mobileentry.R;
import de.dimedis.mobileentry.backend.response.ResponseError;
import de.dimedis.mobileentry.databinding.FragmentAlertBinding;
import de.dimedis.mobileentry.util.dynamicres.DynamicString;

public class AlertFragment extends DialogFragment {

    FragmentAlertBinding binding;

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        public Builder() {
        }

        public AlertFragment build() {
            return new AlertFragment();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        binding = FragmentAlertBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    private OnCancelClickedListener mOnCancelClickedListener;

    public static void show(ResponseError error, OnCancelClickedListener listener, FragmentManager fm) {
        String message = error != null ? error.message : DynamicString.getInstance().getString(R.string.message_unknown_error);
        AlertFragment.show(message, listener, fm);
    }

    public static void show(String message, OnCancelClickedListener listener, FragmentManager fm) {
    }

    void inject() {
        setStyle(DialogFragment.STYLE_NO_FRAME, android.R.style.Theme_DeviceDefault_Dialog_NoActionBar);
    }

    void onCancel() {
        if (mOnCancelClickedListener != null) {
            mOnCancelClickedListener.onCancelClicked();
        }
        dismiss();
    }

    public void setOnCancelClickedListener(OnCancelClickedListener listener) {
        mOnCancelClickedListener = listener;
    }

    public interface OnCancelClickedListener {
        void onCancelClicked();
    }
}
