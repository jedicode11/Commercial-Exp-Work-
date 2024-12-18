package de.dimedis.mobileentry.model;

import android.text.TextUtils;

import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.annotation.StringRes;

import java.util.List;

import de.dimedis.mobileentry.R;
import de.dimedis.mobileentry.util.ConfigPrefHelper;

public enum Function {

    SCAN_ENTRY("scan_entry", R.string.SCAN_ENTRY_MENU_BUTTON_TEXT, R.drawable.ic_scan_entry), SCAN_CHECKOUT("scan_checkout", R.string.SCAN_CHECKOUT_MENU_BUTTON_TEXT, R.drawable.ic_scan_checkout),

    CHOOSE_BORDER("choose_border", R.string.CHOOSE_BORDER_MENU_BUTTON_TEXT, R.drawable.ic_border), HISTORY("history", R.string.HISTORY_MENU_BUTTON_TEXT, R.drawable.ic_history),

    VOID_TICKET("void_ticket", R.string.VOID_TICKET_MENU_BUTTON_TEXT, R.drawable.ic_void_ticket), PHONE("phone", R.string.PHONE_MENU_BUTTON_TEXT, R.drawable.ic_phone),

    FKEY_CONFIG("fkey_config", R.string.FKEY_CONFIG_MENU_BUTTON_TEXT, R.drawable.ic_f_key), NETWORK_SETTINGS("network_settings", R.string.NETWORK_SETTINGS_MENU_BUTTON_TEXT, R.drawable.ic_netvork_settings),

    VERSIONS("versions", R.string.UPDATE_BUTTON_LABEL, R.drawable.ic_versions), LOGFILE("logfile", R.string.LOGFILE_MENU_BUTTON_TEXT, R.drawable.ic_logfile),

    DEVICE_SETTINGS("device_settings", R.string.DEVICE_SETTINGS_MENU_BUTTON_TEXT, R.drawable.ic_device_setting), LOGOUT("logout", R.string.LOGOUT_MENU_BUTTON_TEXT, R.drawable.ic_logout),

    EXIT_APP("exit_app", R.string.EXIT_APP_MENU_BUTTON_TEXT, R.drawable.ic_exit_app), RESET_DEVICE("reset_device", R.string.RESET_DEVICE_MENU_BUTTON_TEXT, R.drawable.ic_reset),

    MENU("menu", R.string.MENU_TITLE, R.drawable.ic_menu), FORCE_ENTRY("force_entry", R.string.FORCE_ENTRY_BUTTON_LABEL, R.drawable.ic_force_entry),

    UPDATE("update", R.string.SOFTWARE_VERSIONS_MENU_BUTTON_TEXT, R.drawable.ic_versions), PUSH_LOCAL_CODES("push_batchupload", R.string.PUSH_CODE_TITLE, R.drawable.ic_batch_upload), DELETE_LOCAL_CODES("delete_local_codes", R.string.DELETE_CODES_TITLE, R.drawable.ic_delete);


    @NonNull
    public final String name;
    @StringRes
    public final int titleResId;
    @DrawableRes
    public final int iconResId;

    Function(@NonNull String name, @StringRes int titleResId, @DrawableRes int iconResId) {
        this.name = name;
        this.titleResId = titleResId;
        this.iconResId = iconResId;
    }

    public static Function fromName(String name) {
        for (Function function : values()) {
            if (TextUtils.equals(function.name, name)) {
                return function;
            }
        }
        return null;
    }

    public boolean isAvailable() {
        List<String> userFunctions = ConfigPrefHelper.getUserFunctions();
        if (userFunctions == null || userFunctions.isEmpty()) {
            // If the list of user functions is empty, we assume that a user was logged in
            // in the offline mode with some basic functions.
            return this == SCAN_ENTRY || this == CHOOSE_BORDER || this == SCAN_CHECKOUT || this == LOGOUT || this == MENU;
        }
        return userFunctions.contains(name);
    }
}
