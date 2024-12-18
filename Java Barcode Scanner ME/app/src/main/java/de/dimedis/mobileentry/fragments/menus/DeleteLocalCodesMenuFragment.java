package de.dimedis.mobileentry.fragments.menus;

import android.annotation.SuppressLint;
import android.database.Cursor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.loader.app.LoaderManager;
import androidx.loader.content.CursorLoader;
import androidx.loader.content.Loader;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import de.dimedis.mobileentry.R;
import de.dimedis.mobileentry.databinding.FragmentMenuDeleteLocalCodesBinding;
import de.dimedis.mobileentry.db.content_provider.DataContentProvider;
import de.dimedis.mobileentry.fragments.ProgressLoaderFragment;
import de.dimedis.mobileentry.model.StatusManager;
import de.dimedis.mobileentry.util.DataBaseUtil;


public class DeleteLocalCodesMenuFragment extends ProgressLoaderFragment implements LoaderManager.LoaderCallbacks<Cursor> {
    static final int LOADER_ID = 225;

    FragmentMenuDeleteLocalCodesBinding binding;
    StatusManager mStatusManager;

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        public Builder() {
        }

        public DeleteLocalCodesMenuFragment build() {
            return new DeleteLocalCodesMenuFragment();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        binding = FragmentMenuDeleteLocalCodesBinding.inflate(inflater, container, false);
        binding.cancel.setOnClickListener(view -> back());
        binding.sendCodes.setOnClickListener(view -> {
            if (StatusManager.getInstance().getStatus() == StatusManager.Status.ONLINE || StatusManager.getInstance().getStatus() == StatusManager.Status.LOCAL_SCAN) {
                setVisibilityProgressBar(true);
                DataBaseUtil.deleteCachedTickets();
            }
        });
        binding.title.setText(getLocalizedString(R.string.DELETE_CODES_TITLE));
        binding.label.setText(getLocalizedString(R.string.DELETE_CODES_INFO_TEXT));
        mStatusManager = StatusManager.getInstance();

        getLoaderManager().initLoader(LOADER_ID, null, this);

        return binding.getRoot();
    }

    @Override
    public void onResume() {

        super.onResume();
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new CursorLoader(getContext(), DataContentProvider.TICKETS_URI, new String[]{"COUNT(*)"},
                null, null, null);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        int codeCount = 0;
        if (cursor != null && cursor.moveToNext()) {//crash ? // java.lang.IllegalStateException: attempt to re-open an already-closed object: SQLiteQuery: SELECT COUNT(*) FROM TABLE_TICKETS_HISTORY
            codeCount = cursor.getInt(0);
        }

        if (codeCount > 0) {
            if (!mStatusManager.isOnline()) {
                binding.label.setText(getLocalizedString(R.string.DELETE_CODES_TITLE));
                binding.sendCodes.setVisibility(View.GONE);
            } else {
                binding.label.setText(getLocalizedString(R.string.DELETE_CODES_INFO_TEXT));
                binding.sendCodes.setVisibility(View.VISIBLE);
            }
            binding.codesInfo.setText(getLocalizedString(R.string.DELETE_CODES_CODES_ON_DEVICE_LABEL) + " " + codeCount);

        } else {
            binding.label.setText(getLocalizedString(R.string.DELETE_CODES_NOCODES_TEXT));
            binding.codesInfo.setText(getLocalizedString(R.string.DELETE_CODES_CODES_ON_DEVICE_LABEL) + " " + codeCount);
            binding.sendCodes.setVisibility(View.GONE);
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
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
    public void onEvent(DataBaseUtil.DeleteTicketsEvent event) {
        getActivity().runOnUiThread(() -> setVisibilityProgressBar(false));
    }
}

