package de.dimedis.mobileentry.util

import android.content.Context
import androidx.core.content.ContextCompat
import de.dimedis.mobileentry.MobileEntryApplication
import de.dimedis.mobileentry.R
import de.dimedis.mobileentry.adapter.ItemMenuAdapter
import de.dimedis.mobileentry.model.Function
import de.dimedis.mobileentry.util.ConfigPrefHelper.getUserFunctions
import de.dimedis.mobileentry.util.dynamicres.DynamicString

class Menu(private val context: Context) {
    private val mapLanguage: MutableList<ItemMenuAdapter.Data> = ArrayList()
    private val userFunctions: List<String>?

    init {
        userFunctions = getUserFunctions()
        initMenu()
    }

    fun add(item: Function) {
        if (!(MobileEntryApplication.isAdminMode() && MobileEntryApplication.getDemoConf()!!.isDemoModeOn())) {
            if (userFunctions == null || !userFunctions.contains(item.name)) return
        }
        mapLanguage.add(ItemMenuAdapter.Data(if (item.iconResId != 0) ContextCompat.getDrawable(context, item.iconResId) else
                    ContextCompat.getDrawable(context, R.drawable.ic_circle), item, getString(item.titleResId)))
    }

    fun getString(res: Int): String? {
        return DynamicString.instance.getString(res)
    }

    fun initMenu() {
        for (key in Function.values()) {
            if (key === Function.MENU || key === Function.FORCE_ENTRY || key === Function.UPDATE) {
                continue
            }
            add(key)
        }
    }

    val items: List<ItemMenuAdapter.Data>
        get() = mapLanguage
}