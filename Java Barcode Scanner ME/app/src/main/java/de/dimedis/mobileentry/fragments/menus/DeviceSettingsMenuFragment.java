package de.dimedis.mobileentry.fragments.menus;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SeekBar;

import androidx.annotation.NonNull;

import de.dimedis.mobileentry.databinding.FragmentMenuDeviceSettingsBinding;

public class DeviceSettingsMenuFragment extends AbsSettingsMenuFragment {
    private static final String TAG = "Device Settings";

    FragmentMenuDeviceSettingsBinding binding;

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        public Builder() {
        }

        public DeviceSettingsMenuFragment build() {
            return new DeviceSettingsMenuFragment();
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        binding = FragmentMenuDeviceSettingsBinding.inflate(inflater, container, false);
        binding.okCancel.ok.setOnClickListener(view -> onClickOK());
        binding.okCancel.cancel.setOnClickListener(view -> onClickCancel());

        return binding.getRoot();
    }

    @Override
    protected void init() {
        super.init();
        binding.vibration.setChecked(mSettings.isVibration());
        binding.cameraReaderSetting.setChecked(mSettings.isCamReader());
        binding.sound.setMax(mSettings.getSoundMax());
        binding.sound.setProgress(mSettings.getSound());
        binding.brightness.setMax(mSettings.getBrightnessMax());
        binding.brightness.setProgress(mSettings.getBrightness());
        binding.permanentScanModeSetting.setChecked(mSettings.isPermScanMode());
        binding.vibration.setOnCheckedChangeListener((buttonView, isChecked) -> mSettings.setVibration(isChecked));
        binding.cameraReaderSetting.setOnCheckedChangeListener((buttonView, isChecked) -> mSettings.setCamReader(isChecked));
        binding.permanentScanModeSetting.setOnCheckedChangeListener((buttonView, isChecked) -> mSettings.setPermScanMode(isChecked));
        binding.sound.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress,
                                          boolean fromUser) {
                if (fromUser) {
                    mSettings.setSound(progress);
                    seekBar.setProgress(progress);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });

        binding.brightness.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                mSettings.setBrightness(progress);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });
    }
}
