package de.dimedis.mobileentry.fragments.menus;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import de.dimedis.mobileentry.R;
import de.dimedis.mobileentry.SettingsControllerImpl;
import de.dimedis.mobileentry.databinding.FragmentMenuConfirmationBinding;
import de.dimedis.mobileentry.fragments.ProgressLoaderFragment;

public class PhoneMenuFragment extends ProgressLoaderFragment {

    FragmentMenuConfirmationBinding binding;

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        public Builder() {
        }

        public PhoneMenuFragment build() {
            return new PhoneMenuFragment();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        binding = FragmentMenuConfirmationBinding.inflate(inflater, container, false);
        binding.title.setText(getLocalizedString(R.string.PHONE_TITLE));
        binding.label.setText(getLocalizedString(R.string.PHONE_QUESTION_TEXT));
        binding.okCancel.ok.setOnClickListener(view -> onClickOk());
        binding.okCancel.cancel.setOnClickListener(view -> onClickCancel());
        return binding.getRoot();
    }

    void onClickOk() {
        SettingsControllerImpl.getDefault(getActivity()).switchToPhone();
    }

    void onClickCancel() {
        back();
    }

}
