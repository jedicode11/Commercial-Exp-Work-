package de.dimedis.mobileentry.ui;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.google.zxing.client.android.Intents;
import com.google.zxing.integration.android.IntentIntegrator;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import de.dimedis.mobileentry.R;
import de.dimedis.mobileentry.bbapi.BarcodeManager;
import de.dimedis.mobileentry.util.dynamicres.DynamicString;
import io.github.inflationx.viewpump.ViewPumpContextWrapper;

public class BaseFragmentActivity extends BaseActivity {
    static final String TAG = "BaseActivity";


    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(ViewPumpContextWrapper.wrap(newBase));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    @Override
    public void setContentView(int layoutResID) {
        super.setContentView(layoutResID);
        DynamicString.scan(getWindow().getDecorView());
    }

    @Override
    public void onBackPressed() {
        Log.i(TAG, "onBackPressed():" + getSupportFragmentManager().getBackStackEntryCount());
        if (getSupportFragmentManager().getBackStackEntryCount() == 0) {
            finish();
        } else {
            super.onBackPressed();
        }
    }

    public void addFragment(Fragment fragment) {
        addFragment(fragment, null);
    }

    public void addFragment(Fragment fragment, String tag) {
        tag = tag == null ? fragment.getClass().getSimpleName() : tag;

        Log.i(TAG, "---- addFragment([ " + tag + " ]- isVisible: " + fragment.isVisible() + " isAdded: " + fragment.isAdded() + " isRemoving:" + fragment.isRemoving());

        getSupportFragmentManager().beginTransaction().add(R.id.content, fragment, tag).commit();
    }

    public void fragment(Fragment fragment) {
        fragment(fragment, true);
    }

    public void fragment(Fragment fragment, boolean isAddtoStack) {

        String tag = fragment.getTag();
        tag = tag == null ? fragment.getClass().getSimpleName() : tag;

        Log.i(TAG, "==== fragment -[ " + tag + " ]- isVisible: " + fragment.isVisible() + " isAdded: " + fragment.isAdded() + " isRemoving:" + fragment.isRemoving());

        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        Fragment fr = getSupportFragmentManager().findFragmentByTag(tag);

        if (fr != null) {
            Log.i(TAG, "fragment fr isVisible: " + fr.isVisible() + " isAdded: " + fr.isAdded() + " isRemoving:" + fr.isRemoving());
            boolean status = getSupportFragmentManager().popBackStackImmediate(tag, 0);
            fragmentTransaction.show(fr);
            fragmentTransaction.commit();
            Log.i(TAG, "in stack> tag:" + tag + " status:" + status);
            return;
        }
        if (fragment.isRemoving()) {
            Log.i(TAG, "isRemoving");
        }

        if (fragment.isVisible() || fragment.isAdded()) {
            fragmentTransaction.show(fragment);
            fragmentTransaction.commit();
            return;
        }


        fragmentTransaction.replace(R.id.content, fragment, tag);


        if (isAddtoStack) fragmentTransaction.addToBackStack(tag);
        fragmentTransaction.commit();

        Log.i(TAG, "fragment:" + getSupportFragmentManager().getBackStackEntryCount());
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //handler=null;
    }

    @Override
    @Subscribe(threadMode = ThreadMode.MAIN)
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
}
