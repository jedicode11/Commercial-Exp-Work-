package de.dimedis.mobileentry.fragments.drag_and_drop.util;

import de.dimedis.mobileentry.fragments.drag_and_drop.model.Item;

public class EmptyTargetItem extends Item {
    public EmptyTargetItem() {
        functionName = "";
        ItemString = "";
    }

    public EmptyTargetItem(int position) {
        functionName = "";
        ItemString = "";

        setPosition(position);
    }
}
