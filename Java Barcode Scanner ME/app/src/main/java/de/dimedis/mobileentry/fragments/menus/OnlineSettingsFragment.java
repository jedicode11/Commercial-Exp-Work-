package de.dimedis.mobileentry.fragments.menus;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;

import de.dimedis.mobileentry.databinding.FragmentOnlinesettingsBinding;

public class OnlineSettingsFragment extends AbsSettingsMenuFragment {

    FragmentOnlinesettingsBinding binding;

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        public Builder() {
        }

        public OnlineSettingsFragment build() {
            return new OnlineSettingsFragment();
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        binding = FragmentOnlinesettingsBinding.inflate(inflater, container, false);
        binding.ok.setOnClickListener(view -> onClickOK());
        binding.cancel.setOnClickListener(view -> onClickCancel());

        return binding.getRoot();
    }

    @Override
    protected void init() {
        super.init();
        binding.switch1.setChecked(mSettings.isLocalScan());
        binding.switch1.setOnCheckedChangeListener((buttonView, isChecked) -> mSettings.setLocalScan(isChecked));
    }
}
