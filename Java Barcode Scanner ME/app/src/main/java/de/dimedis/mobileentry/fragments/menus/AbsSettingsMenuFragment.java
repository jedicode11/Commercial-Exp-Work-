package de.dimedis.mobileentry.fragments.menus;

import android.os.Bundle;
import android.view.View;

import androidx.annotation.Nullable;

import de.dimedis.mobileentry.SettingsController;
import de.dimedis.mobileentry.SettingsControllerImpl;
import de.dimedis.mobileentry.fragments.BaseFragment;
import de.dimedis.mobileentry.util.dynamicres.DynamicString;

public class AbsSettingsMenuFragment extends BaseFragment {
    protected SettingsController mSettings;

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        DynamicString.scan(view);
        super.onViewCreated(view, savedInstanceState);
        init();
    }

    protected void onClickOK() {
        mSettings.save();
        back();
    }

    protected void onClickCancel() {
        back();
    }

    protected void init() {
        mSettings = SettingsControllerImpl.getDefault(getActivity());
        mSettings.saveTemporary();
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mSettings.loadTemporary();
    }
}
