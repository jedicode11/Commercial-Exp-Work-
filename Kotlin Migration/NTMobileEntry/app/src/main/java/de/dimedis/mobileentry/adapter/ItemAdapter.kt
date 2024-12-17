package de.dimedis.mobileentry.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import de.dimedis.mobileentry.R

class ItemAdapter(private val mDataset: List<Data>) : RecyclerView.Adapter<ItemAdapter.ViewHolder>() {
    class Data(var code: String, var name: String)
    class ViewHolder(v: View) : RecyclerView.ViewHolder(v) {
        var mTextView: TextView
        var mImageView: ImageView

        init {
            mTextView = v.findViewById<View>(R.id.label) as TextView
            mImageView = v.findViewById<View>(R.id.icon) as ImageView
        }
    }

    // Create new views (invoked by the layout manager)
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        // create a new view
        val v = LayoutInflater.from(parent.context).inflate(R.layout.item_language, parent, false)
        return ViewHolder(v)
    }

    interface OnItemClick {
        fun onClick(holder: ViewHolder?, dat: Data?, position: Int)
    }

    var mOnItemClick: OnItemClick? = null
    fun setOnItemClick(onItemClick: OnItemClick?) {
        mOnItemClick = onItemClick
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.mTextView.text = mDataset[position].name
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