package de.dimedis.mobileentry.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import de.dimedis.mobileentry.R;

public class ItemAdapter extends RecyclerView.Adapter<ItemAdapter.ViewHolder> {
    private List<Data> mDataset;

  public static class Data{
    public String name;
      public String code;

      public Data(String code, String name) {
          this.name = name;
          this.code = code;
      }
  }


    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView mTextView;
        public ImageView mImageView;

        public ViewHolder(View v) {
            super(v);
            mTextView = (TextView)v.findViewById(R.id.label);
            mImageView = (ImageView)v.findViewById(R.id.icon);
        }
    }


    public ItemAdapter(List<Data> mDataset) {
        this.mDataset = mDataset;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent,
                                                   int viewType) {
        // create a new view
        View v  = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_language, parent, false);

        ViewHolder vh = new ViewHolder(v);
        return vh;
    }


public static interface  OnItemClick{
        void onClick(ViewHolder holder,Data dat, int position);
    }
    OnItemClick mOnItemClick;
    public void   setOnItemClick(OnItemClick onItemClick){
         this.mOnItemClick=onItemClick;
     }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) { // TODO Error
        holder.mTextView.setText(mDataset.get(position).name);
        if(mOnItemClick!=null) {
            holder.itemView.setOnClickListener(v -> mOnItemClick.onClick(holder, getItem(position), position)
            );
        };

    }

    @Override
    public int getItemCount() {
        return mDataset.size();
    }


    public Data getItem(int position) {
              return mDataset.get(position);

    }
}
