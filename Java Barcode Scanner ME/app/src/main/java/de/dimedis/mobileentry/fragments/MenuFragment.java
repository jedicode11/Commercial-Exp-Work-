package de.dimedis.mobileentry.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;

import java.util.List;

import de.dimedis.mobileentry.MobileEntryApplication;
import de.dimedis.mobileentry.adapter.ItemMenuAdapter;
import de.dimedis.mobileentry.databinding.FragmentMenuBinding;
import de.dimedis.mobileentry.fragments.menus.ConfigFKeysFragment;
import de.dimedis.mobileentry.fragments.menus.DeleteLocalCodesMenuFragment;
import de.dimedis.mobileentry.fragments.menus.DeviceSettingsMenuFragment;
import de.dimedis.mobileentry.fragments.menus.ExitMenuFragment;
import de.dimedis.mobileentry.fragments.menus.LogfileMenuFragment;
import de.dimedis.mobileentry.fragments.menus.LogoutMenuFragment;
import de.dimedis.mobileentry.fragments.menus.OnlineSettingsFragment;
import de.dimedis.mobileentry.fragments.menus.PhoneMenuFragment;
import de.dimedis.mobileentry.fragments.menus.PushLocalCodesMenuFragment;
import de.dimedis.mobileentry.fragments.menus.ResetMenuFragment;
import de.dimedis.mobileentry.fragments.menus.UpdateFragment;
import de.dimedis.mobileentry.ui.ScanActivity;
import de.dimedis.mobileentry.util.DataBaseUtil;
import de.dimedis.mobileentry.util.DebugUtilsInterface;
import de.dimedis.mobileentry.util.Menu;

public class MenuFragment extends BaseFragment {

    FragmentMenuBinding binding;

    List<ItemMenuAdapter.Data> mapLanguage;

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        public Builder() {
        }

        public MenuFragment build() {
            return new MenuFragment();
        }
    }

    List<ItemMenuAdapter.Data> getItems() {
        return mapLanguage = new Menu(getContext()).getItems();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        binding = FragmentMenuBinding.inflate(inflater, container, false);
        binding.cancel.setOnClickListener(view -> getActivity().finish());
        binding.title.setOnClickListener(view -> MobileEntryApplication.getDemoConf().onTitleClick(new DebugUtilsInterface.Reinit() {

            @Override
            public void reinit() {
                reinit();
            }  // TODO Recursion fix maybe

            @Override
            public void next(int i) {
                DataBaseUtil.uploadCachedTickets();
                DataBaseUtil.uploadCachedOfflineSessions();
            }
        }));

        ItemMenuAdapter adapter = new ItemMenuAdapter(getItems());
        adapter.setOnItemClick((holder, data, position) -> {
            switch (data.fKeyInfo) {
                case SCAN_ENTRY:
                    ScanActivity.scanEntry(getActivity());
                    break;
                case SCAN_CHECKOUT:
                    ScanActivity.scanCheckout(getActivity());
                    break;
                case CHOOSE_BORDER:
                    fragment(LocationFragment.builder().build());
                    break;
                case HISTORY:
                    fragment(new HistoryFragment2());
                    break;
                case VOID_TICKET:
                    fragment(VoidTicketFragment.builder().build());
                    Bundle b = new Bundle();
                    b.putBoolean(HistoryFragment2.VOID_TICKET_EXTRA, true);
                    Fragment fragment = new HistoryFragment2();
                    fragment.setArguments(b);
                    fragment(fragment);
                    break;
                case PHONE:
                    fragment(PhoneMenuFragment.builder().build());
                    break;
                case FKEY_CONFIG:
                    fragment(new ConfigFKeysFragment());
                    break;
                case NETWORK_SETTINGS:
                    fragment(OnlineSettingsFragment.builder().build());
                    break;
                case VERSIONS:
                    fragment(UpdateFragment.builder().build());
                    break;
                case LOGFILE:
                    fragment(LogfileMenuFragment.builder().build());
                    break;
                case DEVICE_SETTINGS:
                    fragment(DeviceSettingsMenuFragment.builder().build());
                    break;
                case LOGOUT:
                    fragment(LogoutMenuFragment.builder().build());
                    break;
                case EXIT_APP:
                    fragment(ExitMenuFragment.builder().build());
                    break;
                case RESET_DEVICE:
                    fragment(ResetMenuFragment.builder().build());
                    break;
                case PUSH_LOCAL_CODES:
                    fragment(PushLocalCodesMenuFragment.builder().build());
                    break;
                case DELETE_LOCAL_CODES:
                    fragment(DeleteLocalCodesMenuFragment.builder().build());
                    break;
            }
        });
        binding.recyclerView.setHasFixedSize(true);
        binding.recyclerView.setAdapter(adapter);
        binding.recyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 2));

        return binding.getRoot();
    }
}
