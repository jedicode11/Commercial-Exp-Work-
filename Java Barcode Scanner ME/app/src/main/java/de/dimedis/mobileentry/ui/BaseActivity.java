package de.dimedis.mobileentry.ui;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.google.zxing.client.android.Intents;
import com.google.zxing.integration.android.IntentIntegrator;

import org.greenrobot.eventbus.EventBus;

import de.dimedis.mobileentry.MobileEntryApplication;
import de.dimedis.mobileentry.R;
import de.dimedis.mobileentry.SettingsController;
import de.dimedis.mobileentry.SettingsControllerImpl;
import de.dimedis.mobileentry.bbapi.BarcodeManager;
import de.dimedis.mobileentry.model.Function;
import de.dimedis.mobileentry.ui.view.HeaderView;
import de.dimedis.mobileentry.util.AppContext;
import de.dimedis.mobileentry.util.BarcodeUtil;
import de.dimedis.mobileentry.util.CommonUtils;
import de.dimedis.mobileentry.util.PrefUtils;
import de.dimedis.mobileentry.util.UpdateUtil;
import de.dimedis.mobileentry.util.dynamicres.DynamicString;
import io.github.inflationx.viewpump.ViewPumpContextWrapper;

public abstract class BaseActivity extends AppCompatActivity implements OnSharedPreferenceChangeListener, SettingsController.SettingsControllerContainer {

    private final SettingsControllerImpl mSettingsController = new SettingsControllerImpl(this);
    private final SystemDialogsReceiver mSystemDialogsReceiver = new SystemDialogsReceiver();
    protected HeaderView mHeaderView;

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(ViewPumpContextWrapper.wrap(newBase));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        PrefUtils.registerOnPrefChangeListener(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        PrefUtils.unregisterOnPrefChangeListener(this);
    }

    @Override
    public Context getBaseContext() {
        return super.getBaseContext();
    }

    @Override
    public Resources getResources() {
        return super.getResources();
    }


    @Override
    protected void onStart() {
        super.onStart();
        mSettingsController.onStart();
        mSystemDialogsReceiver.register();
    }

    @Override
    protected void onStop() {
        super.onStop();
        mSettingsController.onStop();
        mSystemDialogsReceiver.unregister();
    }

    @Override
    public void setContentView(int layoutResID) {
        super.setContentView(layoutResID);
        DynamicString.scan(getWindow().getDecorView());
        setupHeaderView();
    }

    private void setupHeaderView() {
        mHeaderView = super.findViewById(R.id.header_view);
        if (mHeaderView == null) {
            return;
        }
        mHeaderView.setDeviceId(MobileEntryApplication.getConfigPreferences().deviceID());
        mHeaderView.setBorderName(PrefUtils.getBorderName());
        if (Function.MENU.isAvailable()) {
            mHeaderView.setOnMenuButtonClickedListener(() -> MenuActivity.start(BaseActivity.this));
        } else {
            mHeaderView.setMenuButtonVisible(false);
        }
        mHeaderView.setCameraButtonVisible(PrefUtils.getCameraReaderValue());
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mHeaderView != null) {
            mHeaderView.setCameraButtonVisible(PrefUtils.getCameraReaderValue());
        }
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences prefs, String key) {
        if (mHeaderView != null && TextUtils.equals(key, PrefUtils.PREF_BORDER_NAME)) {
            mHeaderView.setBorderName(PrefUtils.getBorderName());
        } else if (mHeaderView != null && TextUtils.equals(key, SettingsControllerImpl.KEY_CAM_READER_ENABLED)) {
            mHeaderView.setCameraButtonVisible(PrefUtils.getCameraReaderValue());
        }
    }

    @Override
    public SettingsController getSettingsController() {
        return mSettingsController;
    }

    public String getLocalizedString(int resId) {
        return DynamicString.getInstance().getString(resId);
    }

    private static class SystemDialogsReceiver extends BroadcastReceiver {
        private final IntentFilter mFilter;

        public SystemDialogsReceiver() {
            mFilter = new IntentFilter();
            mFilter.addAction(Intent.ACTION_CLOSE_SYSTEM_DIALOGS);
        }

        @RequiresApi(api = Build.VERSION_CODES.TIRAMISU)
        public void register() {
            AppContext.get().registerReceiver(this, mFilter, Context.RECEIVER_NOT_EXPORTED);
        }

        public void unregister() {
            try {
                AppContext.get().unregisterReceiver(this);
            } catch (IllegalArgumentException ignore) {
                // Receiver not registered.
            }
        }

        @Override
        public void onReceive(Context context, Intent intent) {
            String reason = intent.getStringExtra("reason");
                CommonUtils.startLauncher();
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (data == null) {
            return;
        }
        if (requestCode == IntentIntegrator.REQUEST_CODE) {
            String dataStr;
            if ((dataStr = data.getStringExtra(Intents.Scan.RESULT)) != null) {
                EventBus.getDefault().postSticky(new BarcodeManager.BarcodeScannedEvent(dataStr));
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case HeaderView.REQUEST: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    BarcodeUtil.openCamReader(this);
                } else {
                    Toast.makeText(getBaseContext(), "The app was not allowed to use your camera", Toast.LENGTH_LONG).show();
                }
            }
            case SettingsControllerImpl.REQUEST: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    UpdateUtil.loadApp();
                } else {
                    Toast.makeText(getBaseContext(), "The app was not allowed to write", Toast.LENGTH_LONG).show();
                }
            }
            case SettingsControllerImpl.STORAGE_REQUEST: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    UpdateUtil.loadApp();
                } else {
                    Toast.makeText(getBaseContext(), "The app was not allowed to write", Toast.LENGTH_LONG).show();
                }
            }
        }
    }
}
