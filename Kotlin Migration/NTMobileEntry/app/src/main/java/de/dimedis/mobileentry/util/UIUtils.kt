package de.dimedis.mobileentry.util

import android.content.Context
import android.util.TypedValue
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.annotation.StringRes
import de.dimedis.mobileentry.util.AppContext.get

object UIUtils {
    private var sStatusBarHeight = 0
    fun dpToPixels(context: Context, dp: Int): Int {
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp.toFloat(), context.resources.displayMetrics).toInt()
    }

    fun showSoftKeyboard(view: View?) {
        if (view != null && view.requestFocus()) {
            val imm = AppContext.get().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.showSoftInput(view, InputMethodManager.SHOW_IMPLICIT)
        }
    }

    fun hideSoftKeyboard(view: View?) {
        if (view != null) {
            val imm = AppContext.get().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(view.windowToken, 0)
        }
    }

    fun showShortToast(@StringRes textResId: Int) {
//        showShortToast(DynamicString.instance?.getString(textResId))
    } /*AppContext.get().getString(textResId)*/

    fun showShortToast(text: CharSequence) {
        Toast.makeText(get(), text, Toast.LENGTH_SHORT).show()
    }

    fun showLongToast(@StringRes textResId: Int) {
        showLongToast(AppContext.get().getString(textResId));
//        showLongToast(DynamicString.instance?.getString(textResId))
    }

    fun showLongToast(text: CharSequence) {
        Toast.makeText(get(), text, Toast.LENGTH_LONG).show()
    }

    fun getStatusBarHeight(): Int {
        if (sStatusBarHeight == 0) {
            val res = get().resources
            val resId = res.getIdentifier("status_bar_height", "dimen", "android")
            if (resId > 0) {
                sStatusBarHeight = res.getDimensionPixelSize(resId)
            }
        }
        return sStatusBarHeight
    }
}