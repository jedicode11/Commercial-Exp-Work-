package de.dimedis.mobileentry.fragments.drag_and_drop.model;

import android.view.View;

import java.util.List;

public class PassObject {
    public View view;
    public Item item;
    public List<Item> srcList;

    public PassObject(View v, Item i, List<Item> s) {
        view = v;
        item = i;
        srcList = s;
    }
}