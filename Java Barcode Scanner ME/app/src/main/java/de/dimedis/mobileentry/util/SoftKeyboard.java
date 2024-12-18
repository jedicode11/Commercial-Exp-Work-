package de.dimedis.mobileentry.util;

import android.app.Service;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

public class SoftKeyboard {

    public static void showKeyboard(View view) {
        setShowKeyboard(view, true);
    }

    public static void hideKeyboard(View view) {
        setShowKeyboard(view, false);
    }

    public static void setShowKeyboard(View view, boolean isShow) {
        if (view == null) return;
        InputMethodManager imm = (InputMethodManager) view.getContext().getSystemService(Service.INPUT_METHOD_SERVICE);
        if (isShow) imm.showSoftInput(view, InputMethodManager.SHOW_IMPLICIT);
        else imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }
}
