package de.dimedis.mobileentry.ui;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import de.dimedis.mobileentry.BuildConfig;
import de.dimedis.mobileentry.fragments.HistoryFragment2;
import de.dimedis.mobileentry.fragments.LocationFragment;
import de.dimedis.mobileentry.fragments.MenuFragment;
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
import de.dimedis.mobileentry.model.Function;

public class MenuActivity extends BaseFragmentActivity {
    private static final String EXTRA_FUNCTION = BuildConfig.APPLICATION_ID + ".Function";
    private static final String EXTRA_TICKET_CODE = BuildConfig.APPLICATION_ID + ".TicketCode";

    public static void startForFunction(@NonNull Context context, @NonNull Function function) {
        startForFunction(context, function, null);
    }

    public static void startForFunction(@NonNull Context context, @NonNull Function function, String ticketCode) {
        start(context, function, ticketCode);
    }

    public static void start(@NonNull Context context) {
        start(context, null, null);
    }

    private static void start(@NonNull Context context, Function function, String ticketCode) {
        Intent intent = new Intent(context, MenuActivity.class);
        intent.putExtra(EXTRA_FUNCTION, function);
        intent.putExtra(EXTRA_TICKET_CODE, ticketCode);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (savedInstanceState != null) {
            return;
        }

        Intent intent = getIntent();
        Function function = (Function) intent.getSerializableExtra(EXTRA_FUNCTION);
        if (function == null) {
            addFragment(MenuFragment.builder().build(), "init");
            return;
        }

        switch (function) {
            case CHOOSE_BORDER:
                addFragment(LocationFragment.builder().build(), null);
                break;
            case LOGOUT:
                addFragment(LogoutMenuFragment.builder().build(), null);
                break;
            case FORCE_ENTRY:
            case HISTORY:
                showHistory(false, intent.getStringExtra(EXTRA_TICKET_CODE));
                break;
            case FKEY_CONFIG:
                addFragment(new ConfigFKeysFragment(), null);
                break;
            case NETWORK_SETTINGS:
                addFragment(OnlineSettingsFragment.builder().build(), null);
                break;
            case VOID_TICKET:
                showHistory(true, intent.getStringExtra(EXTRA_TICKET_CODE));
                break;
            case VERSIONS:
                //case UPDATE:
                addFragment(UpdateFragment.builder().build(), null);
                break;
            case LOGFILE:
                addFragment(LogfileMenuFragment.builder().build(), null);
                break;
            case DEVICE_SETTINGS:
                addFragment(DeviceSettingsMenuFragment.builder().build(), null);
                break;
            case EXIT_APP:
                addFragment(ExitMenuFragment.builder().build(), null);
                break;
            case PHONE:
                addFragment(PhoneMenuFragment.builder().build(), null);
                break;
            case RESET_DEVICE:
                addFragment(ResetMenuFragment.builder().build(), null);
                break;
            case PUSH_LOCAL_CODES:
                addFragment(PushLocalCodesMenuFragment.builder().build(), null);
                break;
            case DELETE_LOCAL_CODES:
                addFragment(DeleteLocalCodesMenuFragment.builder().build(), null);
                break;
        }
    }

    private void showHistory(boolean voidTicket, String ticketCode) {
        Bundle args = new Bundle();
        args.putBoolean(HistoryFragment2.VOID_TICKET_EXTRA, voidTicket);
        args.putString(HistoryFragment2.TICKET_CODE_EXTRA, ticketCode);
        Fragment fragment = new HistoryFragment2();
        fragment.setArguments(args);
        addFragment(fragment, null);
    }
}
