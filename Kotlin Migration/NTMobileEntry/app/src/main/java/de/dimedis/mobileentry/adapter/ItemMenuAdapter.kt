package de.dimedis.mobileentry.adapter

import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import de.dimedis.mobileentry.R
import de.dimedis.mobileentry.model.Function


class ItemMenuAdapter(private val mDataset: List<Data>) : RecyclerView.Adapter<ItemMenuAdapter.ViewHolder>() {
    class Data(var icon: Drawable?, var fKeyInfo: Function?, var name: String?) {
        constructor(fKeyInfo: Function?, name: String?) : this(null, fKeyInfo, name)
    }

    class ViewHolder(v: View) : RecyclerView.ViewHolder(v) {
        var mTextView: TextView
        var mImageView: ImageView

        init {
            mTextView = v.findViewById(R.id.label)
            mImageView = v.findViewById(R.id.icon)
        }
    }

    // Create new views (invoked by the layout manager)
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        // create a new view
        val v: View = LayoutInflater.from(parent.context).inflate(R.layout.item_menu, parent, false)
        return ViewHolder(v)
    }

    interface OnItemClick {
        fun onClick(holder: ViewHolder?, dat: Data?, position: Int)
    }

    var mOnItemClick: OnItemClick? = null
    fun setOnItemClick(onItemClick: OnItemClick) {
        mOnItemClick = onItemClick
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.mTextView.text = mDataset[position].name
        if (mDataset[position].icon != null) {
            holder.mImageView.setImageDrawable(mDataset[position].icon)
        } else {
            holder.mImageView.setImageResource(R.drawable.ic_circle)
        }
        if (mOnItemClick != null) {
            holder.itemView.setOnClickListener {
                mOnItemClick!!.onClick(holder, getItem(position), position)
            }
        }
    }

    override fun getItemCount(): Int {
        return mDataset.size
    }

    fun getItem(position: Int): Data {
        return mDataset[position]
    }
}