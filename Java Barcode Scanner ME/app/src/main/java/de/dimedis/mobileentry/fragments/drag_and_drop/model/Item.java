package de.dimedis.mobileentry.fragments.drag_and_drop.model;

public class Item {
    public String functionName;
    public String ItemString;
    private int iconRes;

    public Item() {
    }

    public Item(String t, int iconResId, String functionName) {
        ItemString = t;
        this.functionName = functionName;
        iconRes = iconResId;
    }

    public String getItemString() {
        return ItemString;
    }

    public int getIconRes() {
        return iconRes;
    }

    protected int position;

    public void setPosition(int position) {
        this.position = position;
    }

    public int getPosition() {
        return position;
    }
}