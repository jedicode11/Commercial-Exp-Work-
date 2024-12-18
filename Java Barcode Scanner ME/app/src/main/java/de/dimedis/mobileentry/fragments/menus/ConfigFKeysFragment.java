package de.dimedis.mobileentry.fragments.menus;

import android.os.Bundle;
import android.util.Log;
import android.view.DragEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import de.dimedis.mobileentry.R;
import de.dimedis.mobileentry.backend.response.UserPrefs;
import de.dimedis.mobileentry.fragments.BaseFragment;
import de.dimedis.mobileentry.fragments.drag_and_drop.helper.SimpleItemTouchHelperCallback;
import de.dimedis.mobileentry.fragments.drag_and_drop.model.Item;
import de.dimedis.mobileentry.fragments.drag_and_drop.model.PassObject;
import de.dimedis.mobileentry.fragments.drag_and_drop.util.EmptyTargetItem;
import de.dimedis.mobileentry.fragments.drag_and_drop.util.FKeyType;
import de.dimedis.mobileentry.fragments.drag_and_drop.util.RecyclerListAdapter;
import de.dimedis.mobileentry.model.Function;
import de.dimedis.mobileentry.util.ConfigPrefHelper;
import de.dimedis.mobileentry.util.Logger;

public class ConfigFKeysFragment extends BaseFragment {
    static final String TAG = "ConfigFKeysFragment";
    private RecyclerListAdapter adapterKeys;

    private final ArrayList<Item> itemsFunctions = new ArrayList<>();
    private final ArrayList<Item> itemsKeys = new ArrayList<>();
    private FrameLayout paneFunctions;
    private FrameLayout paneKeys;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_config_fkeys, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        RecyclerView recyclerViewFunctions = view.findViewById(R.id.recycler_view_functions);
        RecyclerView recyclerViewKeys = view.findViewById(R.id.recycler_view_keys);

        view.findViewById(R.id.configure_fkeys_okay).setOnClickListener(v -> saveAndExit());

        view.findViewById(R.id.configure_fkeys_cancel).setOnClickListener(v -> closeFragment());

        paneFunctions = view.findViewById(R.id.panel_functions);
        paneKeys = view.findViewById(R.id.panel_keys);

        paneFunctions.setOnDragListener(myOnDragListener);
        paneKeys.setOnDragListener(myOnDragListener);

        initItems();

        RecyclerListAdapter adapterFunctions = new RecyclerListAdapter(itemsFunctions, FKeyType.FUNCTIONS);
        adapterKeys = new RecyclerListAdapter(itemsKeys, FKeyType.KEYS);
        adapterKeys.setOnDragListener(myOnDragListener);
        recyclerViewFunctions.setHasFixedSize(true);
        recyclerViewFunctions.setAdapter(adapterFunctions);
        recyclerViewFunctions.setLayoutManager(new GridLayoutManager(getActivity(), 2, GridLayoutManager.VERTICAL, false));

        recyclerViewKeys.setHasFixedSize(true);
        recyclerViewKeys.setAdapter(adapterKeys);
        recyclerViewKeys.setLayoutManager(new GridLayoutManager(getActivity(), 2, GridLayoutManager.VERTICAL, false));

        ItemTouchHelper.Callback callbackKeys = new SimpleItemTouchHelperCallback(adapterKeys);
        ItemTouchHelper mItemTouchHelper = new ItemTouchHelper(callbackKeys);
        mItemTouchHelper.attachToRecyclerView(recyclerViewKeys);
    }

    private void saveAndExit() {
        UserPrefs prefs = ConfigPrefHelper.getUserPrefs();
        List<Item> list = adapterKeys.getList();

        for (int i = 0; i < list.size(); i++) {
            Item item = list.get(i);

            if (item instanceof EmptyTargetItem) {
                item = null;
            }

            switch (i) {
                case 0:
                    prefs.getKeys().setF1(item == null ? null : item.functionName);
                    break;
                case 1:
                    prefs.getKeys().setF2(item == null ? null : item.functionName);
                    break;
                case 2:
                    prefs.getKeys().setF3(item == null ? null : item.functionName);
                    break;
                case 3:
                    prefs.getKeys().setF4(item == null ? null : item.functionName);
                    break;
            }
        }

        ConfigPrefHelper.setUserPrefs(prefs);
        closeFragment();
    }

    private void initItems() {
        List<String> functions = ConfigPrefHelper.getUserFunctions();
        UserPrefs userKeys = ConfigPrefHelper.getUserPrefs();

        if (functions == null || functions.isEmpty()) {
            Logger.e(TAG, "functions list is empty");
            return;
        }

        for (String function : functions) {
            Function info = Function.fromName(function);
            if (info == null) {
                Logger.e(TAG, "MISSED FUNCTION: " + function);
                Toast.makeText(getActivity(), "MISSED FUNCTION: " + function, Toast.LENGTH_LONG).show();
                continue;
            }
            if (info == Function.MENU || info == Function.FORCE_ENTRY || info == Function.UPDATE) {
                continue;
            }
            itemsFunctions.add(new Item(getLocalizedString(info.titleResId), info.iconResId, function));
        }

        Function info1 = Function.fromName(userKeys.getKeys().getF1());
        Function info2 = Function.fromName(userKeys.getKeys().getF2());
        Function info3 = Function.fromName(userKeys.getKeys().getF3());
        Function info4 = Function.fromName(userKeys.getKeys().getF4());

        if (info1 != null) {
            itemsKeys.add(new Item(getLocalizedString(info1.titleResId), info1.iconResId, userKeys.getKeys().getF1()));
        } else {
            itemsKeys.add(new EmptyTargetItem());
        }
        if (info2 != null) {
            itemsKeys.add(new Item(getLocalizedString(info2.titleResId), info2.iconResId, userKeys.getKeys().getF2()));
        } else {
            itemsKeys.add(new EmptyTargetItem());
        }
        if (info3 != null) {
            itemsKeys.add(new Item(getLocalizedString(info3.titleResId), info3.iconResId, userKeys.getKeys().getF3()));
        } else {
            itemsKeys.add(new EmptyTargetItem());
        }
        if (info4 != null) {
            itemsKeys.add(new Item(getLocalizedString(info4.titleResId), info4.iconResId, userKeys.getKeys().getF4()));
        } else {
            itemsKeys.add(new EmptyTargetItem());
        }
    }

    View.OnDragListener myOnDragListener = new View.OnDragListener() {

        @Override
        public boolean onDrag(View v, DragEvent event) {
            String area;
            Item item = null;
            if (v == paneFunctions) {
                area = "area1";
            } else if (v == paneKeys) {
                area = "area2";
            } else {
                if (v instanceof LinearLayout && v.getTag() != null && v.getTag() instanceof Item) {
                    item = (Item) v.getTag();
                    area = "fkey item: " + v.getId() + " view tag: " + item.functionName;
                } else {
                    area = "unknown: " + v.getId() + " view tag: " + v.getTag();
                }
            }

            switch (event.getAction()) {
                case DragEvent.ACTION_DRAG_STARTED:
                    Log.d(TAG, "ACTION_DRAG_STARTED: " + area + "\n");
                    break;
                case DragEvent.ACTION_DRAG_ENTERED:
                    Log.d(TAG, "ACTION_DRAG_ENTERED: " + area + "\n");
                    break;
                case DragEvent.ACTION_DRAG_EXITED:
                    Log.d(TAG, "ACTION_DRAG_EXITED: " + area + "\n");
                    break;
                case DragEvent.ACTION_DROP:
                    Log.d(TAG, "ACTION_DROP: " + area + "\n");

                    if (area.equalsIgnoreCase("area1")) {
                        return false;
                    }

                    PassObject passObj = (PassObject) event.getLocalState();
                    Item passedItem = passObj.item;

                    if (item != null && item.getPosition() == passedItem.getPosition() && item.functionName.equalsIgnoreCase(passedItem.functionName)) {
                        return false;
                    }

                    if (item != null) {
                        adapterKeys.replace(item, passedItem);
                    } else {
                        if (adapterKeys.getItemCount() == 4) {
                            return false;
                        }
                        adapterKeys.getList().add(passedItem);
                    }

                    adapterKeys.notifyDataSetChanged();

                    break;
                case DragEvent.ACTION_DRAG_ENDED:
                    Log.d(TAG, "ACTION_DRAG_ENDED: " + area + "\n");

                default:
                    break;
            }

            return true;
        }
    };

    private void closeFragment() {
        getActivity().onBackPressed();
    }
}
