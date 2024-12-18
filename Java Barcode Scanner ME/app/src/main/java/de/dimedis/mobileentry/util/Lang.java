package de.dimedis.mobileentry.util;

import android.content.Context;
import android.content.res.Configuration;
import android.text.TextUtils;

import java.util.Locale;

public class Lang {
    public static void setLocation(Context context, String language) {
        Locale locale = TextUtils.isEmpty(language) ? Locale.ROOT : new Locale(language);
        Locale.setDefault(locale);
        Configuration config = new Configuration();
        config.locale = locale;
        context.getApplicationContext().getResources().updateConfiguration(config, null);
    }
}
