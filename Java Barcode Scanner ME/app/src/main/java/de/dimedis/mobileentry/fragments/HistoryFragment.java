package de.dimedis.mobileentry.fragments;

import android.database.Cursor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.CursorLoader;
import androidx.loader.content.Loader;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import de.dimedis.mobileentry.R;
import de.dimedis.mobileentry.db.content_provider.DataContentProvider;
import de.dimedis.mobileentry.util.TicketsHistoryRecyclerViewAdapter;

@Deprecated
public class HistoryFragment extends BaseFragment implements LoaderManager.LoaderCallbacks<Object> {
    static final int LOADER_ID = 225;

    private static final int LOADER_TICKETS_HISTORY = 838325;
    private RecyclerView historyList;
    private TicketsHistoryRecyclerViewAdapter mAdapter;

    @Override
    public void onStart() {
        super.onStart();
        getLoaderManager().restartLoader(LOADER_TICKETS_HISTORY, null, HistoryFragment.this);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_scan_history2, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        historyList = (RecyclerView) view.findViewById(R.id.fragment_history_items_list);
        if (mAdapter == null) {
            mAdapter = new TicketsHistoryRecyclerViewAdapter(getActivity(), null);
        }

        historyList.setAdapter(mAdapter);
        historyList.setLayoutManager(new LinearLayoutManager(getActivity()));

        getLoaderManager().initLoader(LOADER_ID, null, this);
    }

    @SuppressWarnings("unchecked")
    @Override
    public Loader<Object> onCreateLoader(int id, Bundle args) {
        if (LOADER_TICKETS_HISTORY != id) {
            return null;
        }
        return (Loader<Object>) (Loader<?>) new CursorLoader(getActivity(), DataContentProvider.TICKETS_URI, null, null, null, null);
    }

    @Override
    public void onLoadFinished(Loader<Object> loader, Object data) {
        if (LOADER_TICKETS_HISTORY == loader.getId()) {
            if (null != mAdapter) mAdapter.swapCursor((Cursor) data);
        }
    }

    @Override
    public void onLoaderReset(Loader<Object> loader) {

    }
}