package de.dimedis.mobileentry.fragments.drag_and_drop.helper;

public interface ItemTouchHelperAdapter {

    void onItemMove(int fromPosition, int toPosition);

    void onItemDismiss(int position);
}