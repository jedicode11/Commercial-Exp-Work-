package de.dimedis.mobileentry.util

import android.app.Service
import android.view.View
import android.view.inputmethod.InputMethodManager

object SoftKeyboard {
    fun showKeyboard(view: View?) {
        setShowKeyboard(view, true)
    }

    fun hideKeyboard(view: View?) {
        setShowKeyboard(view, false)
    }

    fun setShowKeyboard(view: View?, isShow: Boolean) {
        if (view == null) return
        val imm = view.context.getSystemService(Service.INPUT_METHOD_SERVICE) as InputMethodManager
        if (isShow) imm.showSoftInput(view, InputMethodManager.SHOW_IMPLICIT)
        else imm.hideSoftInputFromWindow(view.windowToken, 0)
    }
}