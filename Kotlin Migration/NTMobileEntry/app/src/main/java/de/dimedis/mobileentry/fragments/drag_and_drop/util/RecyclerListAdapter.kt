package de.dimedis.mobileentry.fragments.drag_and_drop.util

import android.content.ClipData
import android.graphics.Color
import android.os.Build
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import de.dimedis.mobileentry.R
import de.dimedis.mobileentry.fragments.drag_and_drop.helper.ItemTouchHelperAdapter
import de.dimedis.mobileentry.fragments.drag_and_drop.helper.ItemTouchHelperViewHolder
import de.dimedis.mobileentry.model.Item
import de.dimedis.mobileentry.model.PassObject
import de.dimedis.mobileentry.util.AppContext
import de.dimedis.mobileentry.util.Logger

class RecyclerListAdapter(items: ArrayList<Item>, type: FKeyType?) :
    RecyclerView.Adapter<RecyclerListAdapter.ItemViewHolder>(), ItemTouchHelperAdapter {
    private var mItems: ArrayList<Item> = ArrayList()
    private var type: FKeyType? = null
    private var onDragListener: View.OnDragListener? = null

    init {
        mItems = items
        setType(type)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int
    ): ItemViewHolder {
        val view: View =
            LayoutInflater.from(parent.context).inflate(R.layout.item_fkey_in_list, parent, false)
        return ItemViewHolder(view)
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        val item = mItems[position]
        if (item is EmptyTargetItem) {
            mItems[position].position = position
            bindTargetItem(holder, position)
        } else {
            bindFunctionItem(item, holder, position)
        }
    }

    private fun bindFunctionItem(item: Item, holder: ItemViewHolder, position: Int) {
        holder.iconView.visibility = View.VISIBLE
        holder.textView.visibility = View.VISIBLE
        holder.textView.tag = item.functionName
        holder.textView.text = item.getItemString
        holder.iconView.setImageResource(item.iconRes)
        holder.itemView.setBackgroundColor(Color.parseColor(getColor(getType()!!))
        )
        holder.itemParent.setOnDragListener(onDragListener)
        if (getType() == FKeyType.KEYS) {
            holder.itemParent.tag = item
        }
        holder.itemView.setOnLongClickListener { v: View ->
            if (position >= mItems.size) {
                Logger.e(TAG, "array out of bounds exception...")
                return@setOnLongClickListener false
            }
            val selectedItem = mItems[position]
            if (selectedItem is EmptyTargetItem) {
                return@setOnLongClickListener false
            }
            val passObj = PassObject(v, selectedItem, mItems)
            val data = ClipData.newPlainText("", "")
            val shadowBuilder = View.DragShadowBuilder(v)
            v.startDrag(data, shadowBuilder, passObj, 0)
            v.setBackgroundColor(Color.parseColor(getType()?.let { getColor(it) }))
            true
        }
    }

    private fun bindTargetItem(holder: ItemViewHolder, positionInList: Int) {
        setDashedBackground(holder.itemParent)
        holder.itemParent.setOnDragListener(onDragListener)
        holder.itemParent.tag = getList()[positionInList]
        holder.textView.text = "no text"
        holder.textView.visibility = View.INVISIBLE
        holder.iconView.visibility = View.INVISIBLE
    }

    override fun onItemDismiss(position: Int) {
        val tag = EmptyTargetItem()
        tag.position = position
        mItems[position] = tag
        notifyDataSetChanged()
    }

    override fun onItemMove(fromPosition: Int, toPosition: Int) {}
    override fun getItemCount(): Int {
        return mItems.size
    }

    fun getList(): MutableList<Item> {
        return mItems
    }

    fun setType(type: FKeyType?) {
        this.type = type
    }

    fun getType(): FKeyType? {
        return type
    }

    fun setOnDragListener(onDragListener: View.OnDragListener?) {
        this.onDragListener = onDragListener
    }

    fun replace(item: Item, passedItem: Item) {
        val list = getList()
        for (i in list.indices) {
            val itemInList = list[i]
            if (itemInList.functionName.equals(item.functionName,ignoreCase = true) || itemInList is EmptyTargetItem) {
                if (itemInList is EmptyTargetItem && item is EmptyTargetItem) {
                    if (item.getPosition != i) {
                        continue
                    }
                }
                Log.e(TAG, "setting item at position: $i")
                passedItem.position = i
                list[i] = passedItem
                break
            }
        }
    }

    inner class ItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView),
        ItemTouchHelperViewHolder {
        val textView: TextView
        val iconView: ImageView
        val itemParent: LinearLayout

        init {
            textView = itemView.findViewById<View>(R.id.text) as TextView
            iconView = itemView.findViewById<View>(R.id.icon) as ImageView
            itemParent = itemView.findViewById<View>(R.id.item_parent) as LinearLayout
        }

        override fun onItemSelected() {
            if (itemParent.tag != null && itemParent.tag is EmptyTargetItem) {
                setDashedBackground(itemParent)
            } else {
                itemView.setBackgroundColor(Color.LTGRAY)
            }
        }

        override fun onItemClear() {
            setDashedBackground(itemParent)
            var i = 0
            if (itemParent.tag != null && itemParent.tag is Item) {
                i = (itemParent.tag as Item).position
            }
            itemParent.tag = EmptyTargetItem(i)
        }
    }

    companion object {
        const val TAG = "RecyclerListAdapter"
        fun setDashedBackground(itemParent: LinearLayout) {
            itemParent.setBackgroundColor(Color.WHITE)
            val sdk = Build.VERSION.SDK_INT
            if (sdk < Build.VERSION_CODES.LOLLIPOP) {
                itemParent.setBackgroundDrawable(AppContext.get().resources.getDrawable(R.drawable.dashed_border))
            } else {  // TODO Deprecated
                itemParent.background = AppContext.get().resources.getDrawable(R.drawable.dashed_border, null)
            }
        }

        fun getColor(type: FKeyType): String {
            return if (type == FKeyType.FUNCTIONS) "#626262" else "#88b917"
        }
    }
}