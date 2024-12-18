package de.dimedis.mobileentry.util;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;
import androidx.work.WorkRequest;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import de.dimedis.lmlib.SessionImpl;
import de.dimedis.lmlib.myinterfaces.Session;
import de.dimedis.mobileentry.Config;
import de.dimedis.mobileentry.ConfigPref;
import de.dimedis.mobileentry.MobileEntryApplication;
import de.dimedis.mobileentry.R;
import de.dimedis.mobileentry.backend.BackendService;
import de.dimedis.mobileentry.backend.BaseArg;
import de.dimedis.mobileentry.backend.response.OfflineSessionsBaseResponseContent;
import de.dimedis.mobileentry.backend.response.UserLoginResponseContent;
import de.dimedis.mobileentry.backend.response.UserPrefs;
import de.dimedis.mobileentry.bbapi.Constants;
import de.dimedis.mobileentry.db.managers.DataBaseManager;
import de.dimedis.mobileentry.model.LocalModeHelper;
import de.dimedis.mobileentry.model.OnUserOfflineLoginCompleted;
import de.dimedis.mobileentry.model.StatusManager;
import de.dimedis.mobileentry.service.HeartbeatService;
import de.dimedis.mobileentry.ui.LoginActivity;
import de.dimedis.mobileentry.ui.ScanActivity;
import de.dimedis.mobileentry.util.dynamicres.DynamicString;

public class SessionUtils {
    private static final String TAG = "SessionUtils";

    public static boolean sDeviceIsGoingToBeReset = true; // could be false

    public static void setUsersParam(UserLoginResponseContent con) {
        ConfigPref pref = MobileEntryApplication.getConfigPreferences();
        pref.setUserSuid(con.getUserSuid());
        pref.setUserName(con.getUserName());
        ConfigPrefHelper.setUserFunctions(con.getListUserFunctions());
        ConfigPrefHelper.setUserPrefs(con.getUserPrefs());
        pref.setUserFullName(con.getUserFullname());
        pref.setUserSession(con.getUserSession());
    }

    public static void setUsersParam(Session session) {
        ConfigPref pref = MobileEntryApplication.getConfigPreferences();
        pref.setUserSuid(session.getUserSuid());
        pref.setUserName(session.getUserName());
        ConfigPrefHelper.setUserFunctions(session.getUserFunctions());
        UserPrefs prefs = convertFromSessionPrefs(session.getUserPrefs());
        ConfigPrefHelper.setUserPrefs(prefs);
        pref.setUserFullName(session.getUserFullname());
        pref.setUserSession(session.getUserSession());
    }

    public static UserPrefs convertFromSessionPrefs(HashMap<String, HashMap<String, String>> userPrefs) {
        UserPrefs prefs = new UserPrefs();
        for (String key : userPrefs.keySet()) {
            HashMap<String, String> fkeys = userPrefs.get(key);
            if (key.equalsIgnoreCase(Constants.USER_PREFERENCES_FKEYS_KEY)) {
                for (String fKey_id : fkeys.keySet()) {
                    String function = fkeys.get(fKey_id);
                    if (!TextUtils.isEmpty(function)) {
                        switch (fKey_id) {
                            case Constants.FKEY_FUNCTION_1:
                                prefs.getKeys().setF1(function);
                                break;
                            case Constants.FKEY_FUNCTION_2:
                                prefs.getKeys().setF2(function);
                                break;
                            case Constants.FKEY_FUNCTION_3:
                                prefs.getKeys().setF3(function);
                                break;
                            case Constants.FKEY_FUNCTION_4:
                                prefs.getKeys().setF4(function);
                                break;
                        }
                    }
                }
            }
        }
        return prefs;
    }

    public static void loginOffline(String barcode) {
        doOfflineLogin(barcode);
    }

    public static void loginOffline(String user, String password) {
        doOfflineLogin(user, password);
    }

    private static void doOfflineLogin(String user, String password) {
        BaseArg baseArg = new BaseArg();
        Session session = LocalModeHelper.getLocalMode().userLoginByUsernameAndPassword(user, password, baseArg.getDeviceSuid(), baseArg.getLang());
        Log.d(TAG, session.toString());
        EventBus.getDefault().post(new OnUserOfflineLoginCompleted(session));
    }

    private static void doOfflineLogin(String barcode) {
        BaseArg baseArg = new BaseArg();
        Session session = LocalModeHelper.getLocalMode().userLoginByBarcode(barcode, baseArg.getDeviceSuid(), baseArg.getLang());
        Log.d(TAG, session.toString());
        MobileEntryApplication.getConfigPreferences().setLoginBarcode(barcode);
        EventBus.getDefault().post(new OnUserOfflineLoginCompleted(session));
    }

    public static void storeOfflineSession(Session session) {
        try {
            DataBaseManager.getInstance().saveOfflineUserSession(session);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public static void saveCredentials(String login, String passwrd) {
        MobileEntryApplication.getConfigPreferences().setLogin(login);
        MobileEntryApplication.getConfigPreferences().setPasswd(passwrd);
    }

    public static void logout(boolean forceOfflineLogout) {
        logout(forceOfflineLogout, true, false);
    }

    public static void logout(boolean forceOfflineLogout, boolean shouldResetDeviceState, boolean isSessionInvalid) {
        PrefUtils.setLoginCompleted(false);

        sDeviceIsGoingToBeReset = shouldResetDeviceState;
        if (CommonUtils.isInternetConnected() && !forceOfflineLogout) {
            BackendService.userLogout();
        } else {
            ConfigPref pref = MobileEntryApplication.getConfigPreferences();
            Session logoutSession = LocalModeHelper.getLocalMode().userLogout(pref.userSession(), pref.userSuid(), pref.userName(), null);
            DataBaseManager.getInstance().reportUserLoggedOut(pref.userSession(), pref.userSuid(), logoutSession);
            doUserLogout(shouldResetDeviceState, AppContext.get());
        }
        try {
            if (isSessionInvalid) {
                Toast.makeText(AppContext.get(), DynamicString.getInstance().getString(R.string.MESSAGE_INVALID_SESSION), Toast.LENGTH_LONG).show();
            }
        } catch (Throwable th) {
            Log.e(TAG, "logout: Toast", th);
        }
    }

    public static void doUserLogout(boolean shouldResetDevice, Context context) {
        ConfigPrefHelper.userLogout();
        HeartbeatService.cancelService();
        if (shouldResetDevice) {
            if (StatusManager.getInstance().getStatus() == StatusManager.Status.ONLINE) {
                BackendService.resetDevice();
            }
            resetDeviceLocalState(context);
        } else {
            Intent intent = new Intent(context, LoginActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            context.startActivity(intent);
        }
    }

    public static void resetDeviceLocalState(Context context) {
        ConfigPrefHelper.resetDevice();

        DataBaseManager.getInstance().deleteOfflineUserSessions();


        WorkRequest uploadWorkRequest = new OneTimeWorkRequest.Builder(UtilsService.class).build();
        WorkManager.getInstance(context).enqueue(uploadWorkRequest);
        sDeviceIsGoingToBeReset = false;
    }

    public static String buildOfflineSessionsRequestBody(ArrayList<SessionImpl.SessionDB> sessions) {
        JSONObject data = new JSONObject();

        for (SessionImpl.SessionDB sessionDB : sessions) {
            try {
                JSONObject logoutSessionJson = new JSONObject(sessionDB.getLogoutSession() == null ? "{}" : sessionDB.getLogoutSession());
                JSONObject loginSessionJson = new JSONObject(sessionDB.getLoginSessionRaw() == null ? "{}" : sessionDB.getLoginSessionRaw());
                JSONObject userPreferences = new JSONObject(buildUserPrefsJsonString());
                JSONObject session = new JSONObject();
                session.put("user_suid", sessionDB.getUserSuid());
                session.put("user_name", sessionDB.getUserName());

                if (TextUtils.isEmpty(sessionDB.getAuthType())) {
                    if (!TextUtils.isEmpty(sessionDB.getVal1())) {
                        loginSessionJson.put("auth_type", "username_and_password");
                        loginSessionJson.put("auth_username", sessionDB.getVal1());
                    }
                } else {
                    loginSessionJson.put("auth_type", "barcode");
                    loginSessionJson.put("auth_barcode", sessionDB.getAuthType());
                }
                if (!TextUtils.isEmpty(sessionDB.getTimeStampLogin())) {
                    loginSessionJson.put("timestamp", getDbDateFormat(sessionDB.getTimeStampLogin()));
                    session.put("login", loginSessionJson);
                }
                logoutSessionJson.put("user_prefs", userPreferences);
                if (!TextUtils.isEmpty(sessionDB.getTimeStampLogout())) {
                    logoutSessionJson.put("timestamp", getDbDateFormat(sessionDB.getTimeStampLogout()));
                    session.put("logout", logoutSessionJson);
                }
                String userSession = getUserSessionId(loginSessionJson, logoutSessionJson);
                data.put(userSession, session);
            } catch (JSONException e) {
                Log.e(TAG, "error in buildOfflineSessionsRequestBody():", e);
            }
        }

        String body = data.toString();
        Logger.w(TAG, "json array: " + body);
        return body;
    }

    // las that
//    private static String getDbDateFormat(String timeStampLogout) {
//        Date date = new Date(Long.parseLong(timeStampLogout));
//        SimpleDateFormat dateFormat = new SimpleDateFormat(Config.DB_DATE_TIME_FORMAT.toString(), Locale.getDefault());
//        return dateFormat.format(date);
//    }
//    private static String getDbDateFormat(String timeStampLogout) {
//        Date date = new Date(Long.parseLong(timeStampLogout));
//        String time = Config.DB_DATE_TIME_FORMAT.format(date);
//        return time;
//    }

    private static String getDbDateFormat(String timeStampLogout) {
        try {
            Date date = new Date(Long.parseLong(timeStampLogout));
            return Config.DB_DATE_TIME_FORMAT.format(date);
        } catch (NumberFormatException e) {
            System.err.println("Invalid timestamp: " + timeStampLogout);
            return null;
        }
    }

    static String getUserSessionFromJson(JSONObject object) {
        return object != null ? object.optString("userSession") : null;
    }

    private static String getUserSessionId(JSONObject loginSessionJson, JSONObject logoutSessionJson) throws JSONException {
        String out = getUserSessionFromJson(loginSessionJson);
        return out != null ? out : getUserSessionFromJson(logoutSessionJson);
    }

    private static String buildUserPrefsJsonString() {
        return MobileEntryApplication.getConfigPreferences().userPrefs();
    }

    public static void prolongSession(OfflineSessionsBaseResponseContent content) {
        Logger.w(TAG, "Prolong session!");
        if (!TextUtils.isEmpty(content.user_fullname)) {
            MobileEntryApplication.getConfigPreferences().setUserFullName(content.user_fullname);
        }
        if (!TextUtils.isEmpty(content.user_name)) {
            MobileEntryApplication.getConfigPreferences().setUserName(content.user_name);
        }
        if (!TextUtils.isEmpty(content.user_session)) {
            MobileEntryApplication.getConfigPreferences().setUserSession(content.user_session);
        }
        if (!TextUtils.isEmpty(content.user_suid)) {
            MobileEntryApplication.getConfigPreferences().setUserSuid(content.user_suid);
        }

        ConfigPrefHelper.setUserFunctions(content.user_functions);
        ConfigPrefHelper.setUserPrefs(content.user_prefs);

        EventBus.getDefault().post(new ScanActivity.OnFKeysUpdateEvent());

        DataBaseManager.getInstance().deleteOfflineUserSessions();
        Logger.w(TAG, "deleteOfflineUserSessions");
    }
}