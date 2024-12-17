package de.dimedis.mobileentry.fragments

import android.database.Cursor
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.loader.app.LoaderManager
import androidx.loader.content.CursorLoader
import androidx.loader.content.Loader
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import de.dimedis.mobileentry.R
import de.dimedis.mobileentry.db.content_provider.DataContentProvider
import de.dimedis.mobileentry.util.TicketsHistoryRecyclerViewAdapter

@Deprecated("")
class HistoryFragment : BaseFragment(), LoaderManager.LoaderCallbacks<Any> {
    private var historyList: RecyclerView? = null
    private var mAdapter: TicketsHistoryRecyclerViewAdapter? = null
    override fun onStart() {
        super.onStart()
        loaderManager.restartLoader(LOADER_TICKETS_HISTORY, null, this@HistoryFragment)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_scan_history2, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        historyList = view.findViewById<View>(R.id.fragment_history_items_list) as RecyclerView
        if (mAdapter == null) {
            mAdapter = TicketsHistoryRecyclerViewAdapter(requireActivity(), null)
        }
        historyList!!.adapter = mAdapter
        historyList!!.layoutManager = LinearLayoutManager(activity)
        loaderManager.initLoader(LOADER_ID, null, this)
    }

    override fun onCreateLoader(id: Int, args: Bundle?): Loader<Any> {
        return if (LOADER_TICKETS_HISTORY != id) {
            null!!
        } else CursorLoader(requireActivity(), DataContentProvider.TICKETS_URI, null, null, null, null) as Loader<Any>
    }

    override fun onLoadFinished(loader: Loader<Any>, data: Any) {
        if (LOADER_TICKETS_HISTORY == loader.id) {
            if (null != mAdapter) mAdapter!!.swapCursor(data as Cursor)
        }
    }

    override fun onLoaderReset(loader: Loader<Any>) {}

    companion object {
        const val LOADER_ID = 225
        private const val LOADER_TICKETS_HISTORY = 838325
    }
}