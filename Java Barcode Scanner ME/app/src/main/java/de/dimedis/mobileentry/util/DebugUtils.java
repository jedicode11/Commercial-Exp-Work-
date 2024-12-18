package de.dimedis.mobileentry.util;

import android.content.Context;
import android.widget.Toast;

import de.dimedis.mobileentry.BuildConfig;

public class DebugUtils {
    public static final boolean TEST_ADMIN_MODE = BuildConfig.DEBUG;
    public static int TEST_ADMIN_MODE_COUNT = 1;
    Context mContext;

    DebugUtils(Context context) {
        mContext = context;
    }

    private static DebugUtils ISTANCE_DEBUGUTILS;

    public static void initInstance(Context context) {
        if (ISTANCE_DEBUGUTILS == null) ISTANCE_DEBUGUTILS = new DebugUtils(context);
    }


    public static DebugUtils getInstance() {
        return ISTANCE_DEBUGUTILS;
    }

    Toast mToast;

    public interface Reinit {
        void reinit();
    }

    public void onTitleClick(Reinit reinit) {
        if (TEST_ADMIN_MODE) {
            if (TEST_ADMIN_MODE_COUNT >= 0) {
                if (mToast != null) mToast.cancel();
                mToast = Toast.makeText(mContext, "Admin: " + TEST_ADMIN_MODE_COUNT, Toast.LENGTH_SHORT);
                --TEST_ADMIN_MODE_COUNT;
                mToast.show();
            } else {
                if (null != reinit) reinit.reinit();
            }
        }
    }
}
