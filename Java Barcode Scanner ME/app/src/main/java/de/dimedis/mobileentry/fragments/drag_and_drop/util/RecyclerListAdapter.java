package de.dimedis.mobileentry.fragments.drag_and_drop.util;

import android.content.ClipData;
import android.graphics.Color;
import android.os.Build;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import de.dimedis.mobileentry.R;
import de.dimedis.mobileentry.fragments.drag_and_drop.helper.ItemTouchHelperAdapter;
import de.dimedis.mobileentry.fragments.drag_and_drop.helper.ItemTouchHelperViewHolder;
import de.dimedis.mobileentry.fragments.drag_and_drop.model.Item;
import de.dimedis.mobileentry.fragments.drag_and_drop.model.PassObject;
import de.dimedis.mobileentry.util.AppContext;
import de.dimedis.mobileentry.util.Logger;

public class RecyclerListAdapter extends RecyclerView.Adapter<RecyclerListAdapter.ItemViewHolder>
        implements ItemTouchHelperAdapter {
    static final String TAG = "RecyclerListAdapter";
    private ArrayList<Item> mItems;
    private FKeyType type;
    private View.OnDragListener onDragListener;

    public RecyclerListAdapter(ArrayList<Item> items, FKeyType type) {
        mItems = items;
        setType(type);
    }

    @Override
    public ItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;
        view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_fkey_in_list, parent, false);
        return new ItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ItemViewHolder holder, final int position) {
        Item item = mItems.get(position);

        if (item instanceof EmptyTargetItem) {
            mItems.get(position).setPosition(position);
            bindTargetItem(holder, position);
        } else {
            bindFunctionItem(item, holder, position);
        }
    }

    private void bindFunctionItem(Item item, ItemViewHolder holder, final int position) {
        holder.iconView.setVisibility(View.VISIBLE);
        holder.textView.setVisibility(View.VISIBLE);
        holder.textView.setTag(item.functionName);
        holder.textView.setText(item.getItemString());
        holder.iconView.setImageResource(item.getIconRes());
        holder.itemView.setBackgroundColor(Color.parseColor(getColor(getType())));
        holder.itemParent.setOnDragListener(onDragListener);

        if (getType() == FKeyType.KEYS) {
            holder.itemParent.setTag(item);
        }

        holder.itemView.setOnLongClickListener(v -> {
            if (position >= mItems.size()) {
                Logger.e(TAG, "array out of bounds exception...");
                return false;
            }

            Item selectedItem = mItems.get(position);
            if (selectedItem instanceof EmptyTargetItem) {
                return false;
            }

            PassObject passObj = new PassObject(v, selectedItem, mItems);

            ClipData data = ClipData.newPlainText("", "");
            View.DragShadowBuilder shadowBuilder = new View.DragShadowBuilder(v);
            v.startDrag(data, shadowBuilder, passObj, 0);
            v.setBackgroundColor(Color.parseColor(getColor(getType())));
            return true;
        });
    }

    private void bindTargetItem(ItemViewHolder holder, final int positionInList) {
        setDashedBackground(holder.itemParent);

        holder.itemParent.setOnDragListener(onDragListener);
        holder.itemParent.setTag(getList().get(positionInList));
        holder.textView.setText("no text");
        holder.textView.setVisibility(View.INVISIBLE);
        holder.iconView.setVisibility(View.INVISIBLE);
    }

    public static void setDashedBackground(LinearLayout itemParent) {
        itemParent.setBackgroundColor(Color.WHITE);
        final int sdk = Build.VERSION.SDK_INT;
        if (sdk < Build.VERSION_CODES.LOLLIPOP) {
            itemParent.setBackgroundDrawable(AppContext.get().getResources().getDrawable(R.drawable.dashed_border));
        } else {
            itemParent.setBackground(AppContext.get().getResources().getDrawable(R.drawable.dashed_border, null));
        }
    }

    public static String getColor(FKeyType type) {
        return type == FKeyType.FUNCTIONS ? "#626262" : "#88b917";
    }

    @Override
    public void onItemDismiss(int position) {
        EmptyTargetItem tag = new EmptyTargetItem();
        tag.setPosition(position);
        mItems.set(position, tag);
        notifyDataSetChanged();
    }

    @Override
    public void onItemMove(int fromPosition, int toPosition) {
    }

    @Override
    public int getItemCount() {
        if (mItems == null) {
            return 0;
        }

        return mItems.size();
    }

    public List<Item> getList() {
        if (mItems == null) {
            mItems = new ArrayList<>();
        }
        return mItems;
    }

    public void setType(FKeyType type) {
        this.type = type;
    }

    public FKeyType getType() {
        return type;
    }

    public void setOnDragListener(View.OnDragListener onDragListener) {

        this.onDragListener = onDragListener;
    }

    public void replace(Item item, Item passedItem) {
        List<Item> list = getList();
        for (int i = 0; i < list.size(); i++) {
            Item itemInList = list.get(i);
            if (itemInList.functionName.equalsIgnoreCase(item.functionName) || itemInList instanceof EmptyTargetItem) {
                if (itemInList instanceof EmptyTargetItem && item instanceof EmptyTargetItem) {
                    if (item.getPosition() != i) {
                        continue;
                    }
                }
                Log.e(TAG, "setting item at position: " + i);
                passedItem.setPosition(i);
                list.set(i, passedItem);
                break;
            }
        }
    }

    public class ItemViewHolder extends RecyclerView.ViewHolder implements ItemTouchHelperViewHolder {
        public final TextView textView;
        public final ImageView iconView;
        public final LinearLayout itemParent;

        public ItemViewHolder(View itemView) {
            super(itemView);

            textView = (TextView) itemView.findViewById(R.id.text);
            iconView = (ImageView) itemView.findViewById(R.id.icon);
            itemParent = (LinearLayout) itemView.findViewById(R.id.item_parent);
        }

        @Override
        public void onItemSelected() {
            if (itemParent.getTag() != null && itemParent.getTag() instanceof EmptyTargetItem) {
                setDashedBackground(itemParent);
            } else {
                itemView.setBackgroundColor(Color.LTGRAY);
            }
        }

        @Override
        public void onItemClear() {
            setDashedBackground(itemParent);
            int i = 0;
            if (itemParent.getTag() != null && itemParent.getTag() instanceof Item) {
                i = ((Item) itemParent.getTag()).getPosition();
            }
            itemParent.setTag(new EmptyTargetItem(i));
        }
    }
}