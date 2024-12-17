package de.dimedis.mobileentry.util
/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2014 Matthieu Harlé
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
import android.database.ContentObserver
import android.database.Cursor
import android.database.DataSetObserver
import android.os.Handler
import android.widget.Filter
import android.widget.FilterQueryProvider
import android.widget.Filterable
import androidx.recyclerview.widget.RecyclerView

abstract class AbstractRecyclerViewAdapter<VH : RecyclerView.ViewHolder>(cursor: Cursor?) :
    RecyclerView.Adapter<VH>(), Filterable, CursorFilter.CursorFilterClient {
    private var mDataValid = false
    private var mRowIDColumn = 0
    private var mCursor: Cursor? = null
    private var mChangeObserver: ChangeObserver? = null
    private var mDataSetObserver: DataSetObserver? = null
    private var mCursorFilter: CursorFilter? = null
    private var mFilterQueryProvider: FilterQueryProvider? = null

    init {
        init(cursor!!)
    }

    fun init(c: Cursor) {
        val cursorPresent = true
        mCursor = c
        mDataValid = cursorPresent
        mRowIDColumn = if (cursorPresent) c.getColumnIndexOrThrow("_id") else -1
        mChangeObserver = ChangeObserver()
        mDataSetObserver = MyDataSetObserver()
        if (cursorPresent) {
            if (mChangeObserver != null) c.registerContentObserver(mChangeObserver)
            if (mDataSetObserver != null) c.registerDataSetObserver(mDataSetObserver)
        }
    }

    override fun onBindViewHolder(holder: VH, i: Int) {
        check(mDataValid) { "this should only be called when the cursor is valid" }
        check(mCursor!!.moveToPosition(i)) { "couldn't move cursor to position $i" }
        onBindViewHolderCursor(holder, mCursor)
    }

    abstract fun onBindViewHolderCursor(holder: VH, cursor: Cursor?)
    override fun getItemCount(): Int {
        return if (mDataValid && mCursor != null) {
            mCursor!!.count
        } else {
            0
        }
    }

    override fun getItemId(position: Int): Long {
        return if (mDataValid && mCursor != null) {
            if (mCursor!!.moveToPosition(position)) {
                mCursor!!.getLong(mRowIDColumn)
            } else {
                0
            }
        } else {
            0
        }
    }

    override fun getCursor(): Cursor {
        return mCursor!!
    }

    /**
     * Change the underlying cursor to a new cursor. If there is an existing cursor it will be
     * closed.
     *
     * @param cursor The new cursor to be used
     */

    override fun changeCursor(cursor: Cursor) {
        val old = swapCursor(cursor)
        old?.close()
    }

    /**
     * Swap in a new Cursor, returning the old Cursor.  Unlike
     * [.changeCursor], the returned old Cursor is *not*
     * closed.
     *
     * @param newCursor The new cursor to be used.
     * @return Returns the previously set Cursor, or null if there wasa not one.
     * If the given new Cursor is the same instance is the previously set
     * Cursor, null is also returned.
     */

    fun swapCursor(newCursor: Cursor?): Cursor? {
        if (newCursor === mCursor) {
            return null
        }
        val oldCursor = mCursor
        if (oldCursor != null) {
            if (mChangeObserver != null) oldCursor.unregisterContentObserver(mChangeObserver)
            if (mDataSetObserver != null) oldCursor.unregisterDataSetObserver(mDataSetObserver)
        }
        mCursor = newCursor
        if (newCursor != null) {
            if (mChangeObserver != null) newCursor.registerContentObserver(mChangeObserver)
            if (mDataSetObserver != null) newCursor.registerDataSetObserver(mDataSetObserver)
            mRowIDColumn = newCursor.getColumnIndexOrThrow("_id")
            mDataValid = true
            // notify the observers about the new cursor
            notifyDataSetChanged()
        } else {
            mRowIDColumn = -1
            mDataValid = false
            // notify the observers about the lack of a data set
            // notifyDataSetInvalidated();
            notifyItemRangeRemoved(0, itemCount)
        }
        return oldCursor
    }

    /**
     *
     * Converts the cursor into a CharSequence. Subclasses should override this
     * method to convert their results. The default implementation returns an
     * empty String for null values or the default String representation of
     * the value.
     *
     * @param cursor the cursor to convert to a CharSequence
     * @return a CharSequence representing the value
     */
    override fun convertToString(cursor: Cursor): CharSequence {
        return cursor.toString()
    }

    /**
     * Runs a query with the specified constraint. This query is requested
     * by the filter attached to this adapter.
     *
     *
     * The query is provided by a
     * [FilterQueryProvider].
     * If no provider is specified, the current cursor is not filtered and returned.
     *
     *
     * After this method returns the resulting cursor is passed to [.changeCursor]
     * and the previous cursor is closed.
     *
     *
     * This method is always executed on a background thread, not on the
     * application's main thread (or UI thread.)
     *
     *
     * Contract: when constraint is null or empty, the original results,
     * prior to any filtering, must be returned.
     *
     * @param constraint the constraint with which the query must be filtered
     * @return a Cursor representing the results of the new query
     * @see .getFilter
     * @see .getFilterQueryProvider
     * @see .setFilterQueryProvider
     */
    override fun runQueryOnBackgroundThread(constraint: CharSequence?): Cursor {
        return if (mFilterQueryProvider != null) {
            mFilterQueryProvider!!.runQuery(constraint)
        } else mCursor!!
    }

    override fun getFilter(): Filter {
        if (mCursorFilter == null) {
            mCursorFilter = CursorFilter(this)
        }
        return mCursorFilter!!
    }

    /**
     * Returns the query filter provider used for filtering. When the
     * provider is null, no filtering occurs.
     *
     * @return the current filter query provider or null if it does not exist
     * @see .setFilterQueryProvider
     * @see .runQueryOnBackgroundThread
     */
    fun getFilterQueryProvider(): FilterQueryProvider? {
        return mFilterQueryProvider
    }

    /**
     * Sets the query filter provider used to filter the current Cursor.
     * The provider's
     * [FilterQueryProvider.runQuery]
     * method is invoked when filtering is requested by a client of
     * this adapter.
     *
     * @param filterQueryProvider the filter query provider or null to delete it
     * @see .getFilterQueryProvider
     * @see .runQueryOnBackgroundThread
     */
    fun setFilterQueryProvider(filterQueryProvider: FilterQueryProvider?) {
        mFilterQueryProvider = filterQueryProvider
    }

    /**
     * Called when the [ContentObserver] on the cursor receives a change notification.
     * Can be implemented by sub-class.
     *
     * @see ContentObserver.onChange
     */
    protected fun onContentChanged() {}

    inner class ChangeObserver : ContentObserver(Handler()) {
        override fun deliverSelfNotifications(): Boolean {
            return true
        }

        override fun onChange(selfChange: Boolean) {
            onContentChanged()
        }
    }

    private inner class MyDataSetObserver : DataSetObserver() {
        override fun onChanged() {
            mDataValid = true
            notifyDataSetChanged()
        }

        override fun onInvalidated() {
            mDataValid = false
            // notifyDataSetInvalidated();
            notifyItemRangeRemoved(0, itemCount)
        }
    }
    /**
     *
     * The CursorFilter delegates most of the work to the CursorAdapter.
     * Subclasses should override these delegate methods to run the queries
     * and convert the results into String that can be used by auto-completion
     * widgets.
     */
}

internal class CursorFilter(var mClient: CursorFilterClient) : Filter() {
    internal interface CursorFilterClient {
        fun convertToString(cursor: Cursor): CharSequence
        fun runQueryOnBackgroundThread(constraint: CharSequence?): Cursor
        fun getCursor(): Cursor
        fun changeCursor(cursor: Cursor)
    }

    override fun convertResultToString(resultValue: Any): CharSequence {
        return mClient.convertToString(resultValue as Cursor)
    }

    override fun performFiltering(constraint: CharSequence): FilterResults {
        val cursor = mClient.runQueryOnBackgroundThread(constraint)
        val results = FilterResults()
        results.count = cursor.count
        results.values = cursor
        return results
    }

    override fun publishResults(constraint: CharSequence, results: FilterResults) {
        val oldCursor = mClient.getCursor()
        if (results.values != null && results.values !== oldCursor) {
            mClient.changeCursor(results.values as Cursor)
        }
    }
}