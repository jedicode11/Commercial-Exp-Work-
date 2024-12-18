package de.dimedis.mobileentry;

import android.content.Context;
import android.os.Build;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.multidex.BuildConfig;

import de.dimedis.mobileentry.util.CommonUtils;
import de.dimedis.mobileentry.util.DebugUtilsInterface;

public class DebugUtils implements DebugUtilsInterface {

    public static final boolean TEST_ADMIN_MODE = BuildConfig.DEBUG;
    private static final String YAHOR_NEXUS7_SERIAL_NUMBER = "06d9e1e4";
    private static final String ELEPHAN_SERIAL_NUMBER = "0123456789ABCDEF";
    public static final String TEST_DEFAULT_TOKEN = "Fe2rf";
    public static final boolean DEMO_MODE = !CommonUtils.hasBBApi();
    public static int TEST_ADMIN_MODE_COUNT = 3;

    public static final String DEMO_INITIALIZE_BARCODE = TEST_DEFAULT_TOKEN + "@" + Config.DISPATCHER_URL;

    private static DebugUtils instance;
    private final Context mContext;
    private Toast mToast;

    private DebugUtils(@NonNull Context context) {
        this.mContext = context.getApplicationContext();
    }

    public static synchronized void init(@NonNull Context context) {
        if (instance == null) {
            instance = new DebugUtils(context);
        }
    }

    public static DebugUtilsInterface getInstance() {
        if (instance == null) {
            throw new IllegalStateException("DebugUtils is not initialized. Call init(context) first.");
        }
        return instance;
    }

    @Override
    public boolean isAdminMode() {
        return TEST_ADMIN_MODE;
    }

    @Override
    public String getDemoAccBarCode() {
        switch (Build.SERIAL) {
            case YAHOR_NEXUS7_SERIAL_NUMBER:
            case ELEPHAN_SERIAL_NUMBER:
                return "68ba185d101e27c699ac241978b1bbf6#dim.u.cava.2015111113-52-33.54096.31863.1&fmme_klaus";
            default:
                throw new IllegalStateException("Debug mode, unsupported device: " + Build.SERIAL);
        }
    }

    @Override
    public String getDemoInitializeBarcode() {
        return DEMO_INITIALIZE_BARCODE;
    }

    @Override
    public boolean isDemoMode() {
        return DEMO_MODE;
    }

    @Override
    public boolean isDemoModeOn() {
        return TEST_ADMIN_MODE_COUNT < 0;
    }

    @Override
    public String getDefaultToken() {
        return TEST_DEFAULT_TOKEN;
    }

    @Override
    public void onTitleClick(Reinit reinit) {
        if (isAdminMode()) {
            if (TEST_ADMIN_MODE_COUNT >= 0) {
                showToast("Admin: " + TEST_ADMIN_MODE_COUNT);
                TEST_ADMIN_MODE_COUNT--;
            } else if (reinit != null) {
                if (TEST_ADMIN_MODE_COUNT == -1) {
                    reinit.reinit();
                    TEST_ADMIN_MODE_COUNT--;
                } else {
                    reinit.next(TEST_ADMIN_MODE_COUNT);
                }
            }
        }
    }

    private void showToast(String message) {
        if (mToast != null) {
            mToast.cancel();
        }
        mToast = Toast.makeText(mContext, message, Toast.LENGTH_SHORT);
        mToast.show();
    }
}
