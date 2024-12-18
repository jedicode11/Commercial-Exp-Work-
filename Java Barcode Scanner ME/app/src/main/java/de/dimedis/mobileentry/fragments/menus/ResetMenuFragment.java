package de.dimedis.mobileentry.fragments.menus;

import android.database.Cursor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.Loader;

import de.dimedis.mobileentry.R;
import de.dimedis.mobileentry.SettingsControllerImpl;
import de.dimedis.mobileentry.databinding.FragmentMenuConfirmationBinding;
import de.dimedis.mobileentry.fragments.ProgressLoaderFragment;

public class ResetMenuFragment extends ProgressLoaderFragment implements LoaderManager.LoaderCallbacks<Cursor> {

    FragmentMenuConfirmationBinding binding;

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        public Builder() {
        }

        public ResetMenuFragment build() {
            return new ResetMenuFragment();
        }
    }

    @NonNull
    @Override
    public Loader<Cursor> onCreateLoader(int id, @Nullable Bundle args) {
        return null;
    }

    @Override
    public void onLoadFinished(@NonNull Loader<Cursor> loader, Cursor data) {
    }

    @Override
    public void onLoaderReset(@NonNull Loader<Cursor> loader) {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        binding = FragmentMenuConfirmationBinding.inflate(inflater, container, false);
        binding.title.setText(getLocalizedString(R.string.RESET_DEVICE_TITLE));
        binding.label.setText(getLocalizedString(R.string.RESET_DEVICE_QUESTION_TEXT));
        binding.okCancel.ok.setOnClickListener(view -> onClickOk());
        binding.okCancel.cancel.setOnClickListener(view -> back());
        return binding.getRoot();
    }

    void onClickOk() {
        setVisibilityProgressBar(true);
        SettingsControllerImpl.getDefault(getActivity()).reset(false, false, false);
    }
}
