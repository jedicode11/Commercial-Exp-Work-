package de.dimedis.mobileentry.util;

import android.content.Context;

import java.util.ArrayList;
import java.util.List;

import de.dimedis.mobileentry.MobileEntryApplication;
import de.dimedis.mobileentry.R;
import de.dimedis.mobileentry.adapter.ItemMenuAdapter;
import de.dimedis.mobileentry.model.Function;
import de.dimedis.mobileentry.util.dynamicres.DynamicString;


public class Menu {

    private Context context;
    private List<ItemMenuAdapter.Data> mapLanguage = new ArrayList<>();
    private List<String> userFunctions;


    public Menu(Context context) {
        this.context = context;
        userFunctions = ConfigPrefHelper.getUserFunctions();
        initMenu();
    }

    public void add(Function item) {
        //// FIXME: 11/25/2015 remove all debug features
        if (!(MobileEntryApplication.isAdminMode() && MobileEntryApplication.getDemoConf().isDemoModeOn())) {
            if (userFunctions == null || !userFunctions.contains(item.name)) return;
        }

        mapLanguage.add(new ItemMenuAdapter.Data(item.iconResId != 0 ? context.getResources().getDrawable(item.iconResId) : context.getResources().getDrawable(R.drawable.ic_circle),
                item, getString(item.titleResId)));
    }

    String getString(int res) {
        return DynamicString.getInstance().getString(res);
    }

    public void initMenu() {
        for (Function key : Function.values()) {
            if (key == Function.MENU || key == Function.FORCE_ENTRY || key == Function.UPDATE) {
                continue;
            }
            add(key);
        }
    }

    public List<ItemMenuAdapter.Data> getItems() {
        return mapLanguage;
    }
}
