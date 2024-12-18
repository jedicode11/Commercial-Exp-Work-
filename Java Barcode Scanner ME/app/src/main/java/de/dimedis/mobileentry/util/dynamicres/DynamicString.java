package de.dimedis.mobileentry.util.dynamicres;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import com.google.gson.annotations.SerializedName;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import de.dimedis.mobileentry.BuildConfig;
import de.dimedis.mobileentry.ConfigPref;
import de.dimedis.mobileentry.backend.response.Localize;
import de.dimedis.mobileentry.util.ConfigPrefHelper;

public class DynamicString {
    static private final String TAG = "DynamicString";
    private Map<String, Localize> _localization;
    HashMap<String, String> resStrings = new HashMap<>();
    HashMap<Integer, String> resIds = new HashMap<>();
    Context mContext;
    ConfigPref pref;
    static DynamicString _DynamicString;

    DynamicString(Context mContext, ConfigPref pref) {
        this.mContext = mContext;
        this.pref = pref;

        Map<String, Localize> lang = ConfigPrefHelper.getLanguages();
        if (lang != null && !lang.isEmpty()) {
            init(lang);
            setLang(pref.currentLanguage());
        }
    }

    public void init(Map<String, Localize> localization) {
        _localization = localization;
    }

    public void setLang(String lang) {
        if (TextUtils.isEmpty(lang)) return;
        reflectionScan(Objects.requireNonNull(_localization.get(lang)));
    }

    void reflectionScan(Localize object) {
        Class<?> clazz = object.getClass();
        List<Field> fields = getAllFields(clazz);
        for (Field field : fields) {
            if (field.isAnnotationPresent(SerializedName.class) && field.isAnnotationPresent(SerializedResId.class)) {
                SerializedName seName = field.getAnnotation(SerializedName.class);
                SerializedResId resID = field.getAnnotation(SerializedResId.class);
                try {
                    boolean isAccess = field.isAccessible();
                    field.setAccessible(true);
                    Object objField = field.get(object);
                    if (objField == null) {
                        Log.w(TAG, "filed is null for: " + seName.value() + " setting default value: " + mContext.getString(resID.value()));
                        objField = mContext.getString(resID.value());
                    }
                    Log.w(TAG, ">>|" + seName.value() + "|=|" + objField + "|");
                    resStrings.put(seName.value(), objField.toString());
                    resIds.put(resID.value(), objField.toString());
                    field.setAccessible(isAccess);
                } catch (IllegalAccessException | NullPointerException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static List<Field> getAllFields(Class<?> type) {
        List<Field> result = new LinkedList<>(Arrays.asList(type.getDeclaredFields()));

        if (type.getSuperclass() != null) {
            result.addAll(getAllFields(type.getSuperclass()));
        }
        return result;
    }

    public String getString(int resId) {
        if (resIds.containsKey(resId)) {
            return resIds.get(resId);
        }
        return mContext.getString(resId);
    }

    public static void init(Context mContext, ConfigPref pref) {
        if (_DynamicString == null) _DynamicString = new DynamicString(mContext, pref);
    }

    public static DynamicString getInstance() {
        return _DynamicString;
    }

    public static void scan(View view) {
        if (view == null) return;
        ViewGroup gView = (ViewGroup) view;
        int count = gView.getChildCount();

        View tView;
        Object objectTag;
        for (int i = 0; i < count; i++) {
            tView = gView.getChildAt(i);
            if (tView instanceof ViewGroup) {
                scan(tView);
            }
            if (tView instanceof TextView) {
                boolean isEditText = tView instanceof EditText;
                objectTag = tView.getTag();
                Log.w("DynamicString", "TAG:" + objectTag);

                if (objectTag instanceof String) {
                    TextView tv = ((TextView) tView);

                    if (getInstance().resStrings.containsKey(objectTag)) {
                        if (!isEditText) {
                            tv.setText(getInstance().resStrings.get(objectTag));
                        } else {
                            tv.setHint(getInstance().resStrings.get(objectTag));
                        }
                    } else {
                        if (BuildConfig.DEBUG) {
                            Log.w("DynamicString", "*!!TAG:" + objectTag + " tx:" + tv.getText() + "| lM:" + getInstance().resStrings.size());
                            tv.setText(tv.getText());

                            for (Map.Entry<String, String> ss : getInstance().resStrings.entrySet()) {
                                Log.w("DynamicString", "MAP " + ss.getKey() + " v:" + ss.getValue());
                            }
                        }
                    }
                }
            }
        }
    }
}
