package de.dimedis.mobileentry.util;

import android.os.Handler;
import android.os.Looper;

import androidx.annotation.NonNull;

public class UIHandler {
    private static final Handler sHandler = new Handler(Looper.getMainLooper());

    private UIHandler() {
    }

    @NonNull
    public static Handler get() {
        return sHandler;
    }
}
