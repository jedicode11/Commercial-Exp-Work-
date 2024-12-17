package de.dimedis.mobileentry.fragments.menus

import android.os.Bundle
import android.util.Log
import android.view.DragEvent
import android.view.LayoutInflater
import android.view.View
import android.view.View.OnDragListener
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.LinearLayout
import android.widget.Toast
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import de.dimedis.mobileentry.R
import de.dimedis.mobileentry.fragments.BaseFragment
import de.dimedis.mobileentry.fragments.drag_and_drop.helper.SimpleItemTouchHelperCallback
import de.dimedis.mobileentry.fragments.drag_and_drop.util.EmptyTargetItem
import de.dimedis.mobileentry.fragments.drag_and_drop.util.FKeyType
import de.dimedis.mobileentry.fragments.drag_and_drop.util.RecyclerListAdapter
import de.dimedis.mobileentry.model.Function
import de.dimedis.mobileentry.model.Item
import de.dimedis.mobileentry.model.PassObject
import de.dimedis.mobileentry.util.ConfigPrefHelper
import de.dimedis.mobileentry.util.ConfigPrefHelper.getUserPrefs
import de.dimedis.mobileentry.util.ConfigPrefHelper.setUserPrefs
import de.dimedis.mobileentry.util.Logger

class ConfigFKeysFragment : BaseFragment() {
    private var adapterKeys: RecyclerListAdapter? = null
    private val itemsFunctions = ArrayList<Item>()
    private val itemsKeys = ArrayList<Item>()
    private var paneFunctions: FrameLayout? = null
    private var paneKeys: FrameLayout? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?, ): View? {
        return inflater.inflate(R.layout.fragment_config_fkeys, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val recyclerViewFunctions = view.findViewById<RecyclerView>(R.id.recycler_view_functions)
        val recyclerViewKeys = view.findViewById<RecyclerView>(R.id.recycler_view_keys)
        view.findViewById<View>(R.id.configure_fkeys_okay).setOnClickListener {
            saveAndExit() }
        view.findViewById<View>(R.id.configure_fkeys_cancel).setOnClickListener {
            closeFragment() }
        paneFunctions = view.findViewById(R.id.panel_functions)
        paneKeys = view.findViewById(R.id.panel_keys)
        paneFunctions?.setOnDragListener(myOnDragListener)
        paneKeys?.setOnDragListener(myOnDragListener)
        initItems()
        val adapterFunctions = RecyclerListAdapter(itemsFunctions, FKeyType.FUNCTIONS)
        adapterKeys = RecyclerListAdapter(itemsKeys, FKeyType.KEYS)
        adapterKeys!!.setOnDragListener(myOnDragListener)
        recyclerViewFunctions.setHasFixedSize(true)
        recyclerViewFunctions.adapter = adapterFunctions
        recyclerViewFunctions.layoutManager = GridLayoutManager(activity, 2, GridLayoutManager.VERTICAL, false)
        recyclerViewKeys.setHasFixedSize(true)
        recyclerViewKeys.adapter = adapterKeys
        recyclerViewKeys.layoutManager = GridLayoutManager(activity, 2, GridLayoutManager.VERTICAL, false)
        val callbackKeys: ItemTouchHelper.Callback = SimpleItemTouchHelperCallback(adapterKeys!!)
        val mItemTouchHelper = ItemTouchHelper(callbackKeys)
        mItemTouchHelper.attachToRecyclerView(recyclerViewKeys)
    }

    private fun saveAndExit() {
        val prefs = getUserPrefs()
        val list: List<Item> = adapterKeys!!.getList()
        for (i in list.indices) {
            var item: Item? = list[i]

            // NOTE: avoid saving empty config key, save just null
            if (item is EmptyTargetItem) {
                item = null
            }
            when (i) {
                0 -> prefs.keys.f1(item?.functionName)
                1 -> prefs.keys.f2(item?.functionName)
                2 -> prefs.keys.f3(item?.functionName)
                3 -> prefs.keys.f4(item?.functionName)
            }
        }
        setUserPrefs(prefs)
        closeFragment()
    }

    private fun initItems() {
        val functions = ConfigPrefHelper.getUserFunctions()
        val userKeys = getUserPrefs()
        if (functions.isEmpty()) {
            Logger.e(TAG, "functions list is empty")
            return
        }
        for (function in functions) {
            val info: Function? = Function.fromName(function)
            if (info == null) {
                Logger.e(TAG, "MISSED FUNCTION: $function")
                Toast.makeText(activity, "MISSED FUNCTION: $function", Toast.LENGTH_LONG).show()
                continue
            }
            if (info == Function.MENU || info == Function.FORCE_ENTRY || info == Function.UPDATE) {
                continue
            }
            itemsFunctions.add(Item(getLocalizedString(info.titleResId), info.iconResId, function))
        }
        val info1: Function? = Function.fromName(userKeys.keys.f1)
        val info2: Function? = Function.fromName(userKeys.keys.f2)
        val info3: Function? = Function.fromName(userKeys.keys.f3)
        val info4: Function? = Function.fromName(userKeys.keys.f4)
        if (info1 != null) {
            itemsKeys.add(Item(getLocalizedString(info1.titleResId),info1.iconResId,userKeys.keys.f1))
        } else {
            itemsKeys.add(EmptyTargetItem())
        }
        if (info2 != null) {
            itemsKeys.add(Item(getLocalizedString(info2.titleResId),info2.iconResId,userKeys.keys.f2))
        } else {
            itemsKeys.add(EmptyTargetItem())
        }
        if (info3 != null) {
            itemsKeys.add(Item(getLocalizedString(info3.titleResId),info3.iconResId,userKeys.keys.f3))
        } else {
            itemsKeys.add(EmptyTargetItem())
        }
        if (info4 != null) {
            itemsKeys.add(Item(getLocalizedString(info4.titleResId),info4.iconResId,userKeys.keys.f4))
        } else {
            itemsKeys.add(EmptyTargetItem())
        }
    }

    private var myOnDragListener = OnDragListener { v, event ->
        val area: String
        var item: Item? = null
        if (v === paneFunctions) {
            area = "area1"
        } else if (v === paneKeys) {
            area = "area2"
        } else {
            if (v is LinearLayout && v.getTag() != null && v.getTag() is Item) {
                item = v.getTag() as Item
                area = "fkey item: " + v.getId() + " view tag: " + item.functionName
            } else {
                area = "unknown: " + v.id + " view tag: " + v.tag
            }
        }
        when (event.action) {
            DragEvent.ACTION_DRAG_STARTED -> Log.d(TAG, "ACTION_DRAG_STARTED: $area\n")
            DragEvent.ACTION_DRAG_ENTERED -> Log.d(TAG, "ACTION_DRAG_ENTERED: $area\n")
            DragEvent.ACTION_DRAG_EXITED -> Log.d(TAG, "ACTION_DRAG_EXITED: $area\n")
            DragEvent.ACTION_DROP -> {
                Log.d(TAG, "ACTION_DROP: $area\n")
                if (area.equals("area1", ignoreCase = true)) {
                    return@OnDragListener false
                }
                val passObj = event.localState as PassObject
                val passedItem = passObj.item
                if (item != null && item.position == passedItem.position && item.functionName.equals(passedItem.functionName, ignoreCase = true)) {
                    return@OnDragListener false
                }
                if (item != null) {
                    adapterKeys!!.replace(item, passedItem)
                } else {
                    if (adapterKeys!!.itemCount == 4) {
                        return@OnDragListener false
                    }
                    adapterKeys!!.getList().add(passedItem)
                }
                adapterKeys!!.notifyDataSetChanged()
            }
            DragEvent.ACTION_DRAG_ENDED -> Log.d(TAG, "ACTION_DRAG_ENDED: $area\n")
            else -> {}
        }
        true
    }

    private fun closeFragment() {
        requireActivity().onBackPressed()
    }

    companion object {
        const val TAG = "ConfigFKeysFragment"
    }
}