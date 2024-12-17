package de.dimedis.mobileentry.util

import android.content.Context
import android.database.Cursor
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import de.dimedis.mobileentry.R
import de.dimedis.mobileentry.db.model.TicketHistoryItem
import de.dimedis.mobileentry.util.Logger.e

class TicketsHistoryRecyclerViewAdapter(private val mContext: Context, c: Cursor?) : AbstractRecyclerViewAdapter<RecyclerView.ViewHolder>(c) {
    private class HistoryItemViewHolder(private val mRootView: View) : RecyclerView.ViewHolder(mRootView) {
        private val mTicketText: TextView = mRootView.findViewById<View>(R.id.ticket_text) as TextView

        fun bind(context: Context, item: TicketHistoryItem) {
            mTicketText.text = item.toString()
            mRootView.tag = item
        }
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.fragment_scan_history_item, parent, false)
        return HistoryItemViewHolder(v)
    }

    override fun onBindViewHolderCursor(holderDefault: RecyclerView.ViewHolder, cursor: Cursor?) {
        val cursorPosition = cursor!!.position
        val holder = holderDefault as HistoryItemViewHolder
        e(TAG, "CURSOR POSITION: $cursorPosition")
        val ticketHistoryItem = TicketHistoryItem.fromCursor(cursor)
        holder.bind(mContext, ticketHistoryItem)
    }

    fun getTicketHistoryItem(view: View): TicketHistoryItem {
        return view.tag as TicketHistoryItem
    }

    companion object {
        const val TAG = "TicketsHistoryRecyclerViewAdapter"
    }
}