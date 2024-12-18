package de.dimedis.mobileentry.util;

import android.content.Context;
import android.content.res.Resources;
import android.util.TypedValue;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.StringRes;

import de.dimedis.mobileentry.util.dynamicres.DynamicString;

public class UIUtils {
    private static int sStatusBarHeight;

    private UIUtils() {
    }

    public static int dpToPixels(Context context, int dp) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, context.getResources().getDisplayMetrics());
    }

    public static void showSoftKeyboard(View view) {
        if (view != null && view.requestFocus()) {
            InputMethodManager imm = (InputMethodManager) AppContext.get().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.showSoftInput(view, InputMethodManager.SHOW_IMPLICIT);
        }
    }

    public static void hideSoftKeyboard(View view) {
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) AppContext.get().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    public static void showShortToast(@StringRes int textResId) {
        showShortToast(DynamicString.getInstance().getString(textResId)/*AppContext.get().getString(textResId)*/);
    }

    public static void showShortToast(@NonNull CharSequence text) {
        Toast.makeText(AppContext.get(), text, Toast.LENGTH_SHORT).show();
    }

    public static void showLongToast(@StringRes int textResId) {
        showLongToast(DynamicString.getInstance().getString(textResId));
    }

    public static void showLongToast(@NonNull CharSequence text) {
        Toast.makeText(AppContext.get(), text, Toast.LENGTH_LONG).show();
    }

    public static int getStatusBarHeight() {
        if (sStatusBarHeight == 0) {
            Resources res = AppContext.get().getResources();
            int resId = res.getIdentifier("status_bar_height", "dimen", "android");
            if (resId > 0) {
                sStatusBarHeight = res.getDimensionPixelSize(resId);
            }
        }
        return sStatusBarHeight;
    }
}
