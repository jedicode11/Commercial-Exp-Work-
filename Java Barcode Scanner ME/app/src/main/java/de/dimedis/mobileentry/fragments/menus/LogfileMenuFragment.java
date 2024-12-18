package de.dimedis.mobileentry.fragments.menus;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import de.dimedis.mobileentry.databinding.FragmentMenuLogfileBinding;
import de.dimedis.mobileentry.util.Logger;

public class LogfileMenuFragment extends AbsSettingsMenuFragment {

    FragmentMenuLogfileBinding binding;

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        public Builder() {
        }

        public LogfileMenuFragment build() {
            return new LogfileMenuFragment();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        binding = FragmentMenuLogfileBinding.inflate(inflater, container, false);
        binding.okCancel.ok.setOnClickListener(view -> onClickOK());
        binding.okCancel.cancel.setOnClickListener(view -> onClickCancel());

        return binding.getRoot();
    }

    @Override
    protected void init() {
        super.init();
        binding.writeLocal.setChecked(mSettings.isWriteLogfile());
        binding.writeLocal.setOnCheckedChangeListener((buttonView, isChecked) -> {
            mSettings.setWriteLogfile(isChecked);
            if (isChecked)
                try {
                    Logger.clearLogFile();
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
        });
    }

}
