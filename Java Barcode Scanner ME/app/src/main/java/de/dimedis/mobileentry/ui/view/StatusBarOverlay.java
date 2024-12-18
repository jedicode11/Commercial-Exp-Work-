package de.dimedis.mobileentry.ui.view;

import android.content.Context;
import android.graphics.PixelFormat;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;

import de.dimedis.mobileentry.util.AppContext;
import de.dimedis.mobileentry.util.UIUtils;

public class StatusBarOverlay extends View {
    private static StatusBarOverlay sInstance;

    private StatusBarOverlay(Context context) {
        super(context);
    }

    public static void create() {
        if (sInstance == null) {
            Context context = AppContext.get();
            sInstance = new StatusBarOverlay(context);
            WindowManager.LayoutParams lp = new WindowManager.LayoutParams(WindowManager.LayoutParams.MATCH_PARENT, UIUtils.getStatusBarHeight(), WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY, WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN, PixelFormat.TRANSLUCENT);
            lp.gravity = Gravity.TOP;
            ((WindowManager) context.getSystemService(Context.WINDOW_SERVICE)).addView(sInstance, lp);
        }
    }
}
