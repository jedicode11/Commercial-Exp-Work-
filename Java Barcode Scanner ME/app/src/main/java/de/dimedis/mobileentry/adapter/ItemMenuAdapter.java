package de.dimedis.mobileentry.adapter;

import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import de.dimedis.mobileentry.R;
import de.dimedis.mobileentry.model.Function;

public class ItemMenuAdapter extends RecyclerView.Adapter<ItemMenuAdapter.ViewHolder> {
    private List<Data> mDataset;

    public static class Data {
        public String name;
        public Function fKeyInfo;
        public Drawable icon;

        public Data(Function fKeyInfo, String name) {
            this(null, fKeyInfo, name);
        }

        public Data(Drawable icon, Function fKeyInfo, String name) {
            this.name = name;
            this.icon = icon;
            this.fKeyInfo = fKeyInfo;
        }
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView mTextView;
        public ImageView mImageView;

        public ViewHolder(View v) {
            super(v);
            mTextView = v.findViewById(R.id.label);
            mImageView = v.findViewById(R.id.icon);
        }
    }

    public ItemMenuAdapter(List<Data> mDataset) {
        this.mDataset = mDataset;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // create a new view
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_menu, parent, false);
        return new ViewHolder(v);
    }

    public static interface OnItemClick {
        void onClick(ViewHolder holder, Data dat, int position);
    }

    OnItemClick mOnItemClick;

    public void setOnItemClick(OnItemClick onItemClick) {
        this.mOnItemClick = onItemClick;
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        holder.mTextView.setText(mDataset.get(position).name);
        if (mDataset.get(position).icon != null) {
            holder.mImageView.setImageDrawable(mDataset.get(position).icon);
        } else {
            holder.mImageView.setImageResource(R.drawable.ic_circle);
        }

        if (mOnItemClick != null) {
            holder.itemView.setOnClickListener(v -> mOnItemClick.onClick(holder, getItem(position), position));
        }
    }

    @Override
    public int getItemCount() {
        return mDataset.size();
    }

    public Data getItem(int position) {
        return mDataset.get(position);
    }
}
