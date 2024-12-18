package de.dimedis.mobileentry.util;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import de.dimedis.mobileentry.R;
import de.dimedis.mobileentry.db.model.TicketHistoryItem;

public class TicketsHistoryRecyclerViewAdapter extends AbstractRecyclerViewAdapter<RecyclerView.ViewHolder> {
    static final String TAG = "TicketsHistoryRecyclerViewAdapter";
    private Context mContext;

    private static final class HistoryItemViewHolder extends RecyclerView.ViewHolder {

        private final View mRootView;
        private final TextView mTicketText;

        public HistoryItemViewHolder(View view) {
            super(view);
            mRootView = view;
            mTicketText = (TextView) mRootView.findViewById(R.id.ticket_text);
        }

        public void bind(Context context, TicketHistoryItem item) {
            mTicketText.setText(item.toString());
            mRootView.setTag(item);
        }
    }

    public TicketsHistoryRecyclerViewAdapter(Context context, Cursor c) {
        super(c);
        mContext = context;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.fragment_scan_history_item, parent, false);
        return new HistoryItemViewHolder(v);
    }

    @Override
    public void onBindViewHolderCursor(RecyclerView.ViewHolder holderDefault, Cursor c) {
        int cursorPosition = c.getPosition();

        final HistoryItemViewHolder holder = (HistoryItemViewHolder) holderDefault;

        Logger.e(TAG, "CURSOR POSITION: " + cursorPosition);

        TicketHistoryItem ticketHistoryItem = TicketHistoryItem.fromCursor(c);
        holder.bind(mContext, ticketHistoryItem);
    }

    public TicketHistoryItem getTicketHistoryItem(View view) {
        return (TicketHistoryItem) view.getTag();
    }
}