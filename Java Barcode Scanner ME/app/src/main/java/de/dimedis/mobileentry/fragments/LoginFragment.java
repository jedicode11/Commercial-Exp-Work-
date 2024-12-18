package de.dimedis.mobileentry.fragments;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import de.dimedis.mobileentry.Config;
import de.dimedis.mobileentry.backend.response.UserLoginByBarcodeResponse;
import de.dimedis.mobileentry.model.OnUserOfflineLoginCompleted;
import de.dimedis.mobileentry.util.PrefUtils;
import de.dimedis.mobileentry.util.UIHandler;

public abstract class LoginFragment extends ProgressLoaderFragment {
    static final String TAG = "LoginFragment";

    protected abstract TextView getErrorMessage();

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setVisibilityProgressBar(false);

        view.setOnTouchListener((v, event) -> {
            Log.i(TAG, "onTouch event:" + event.getAction());
            restartScreenSaverTask();
            return false;
        });
    }

    private class ScreenSaver implements Runnable {
        @Override
        public void run() {
            fragment(SoftwareVersionsFragment.builder().build());    // TODO: fix & run
        }
    }

    ScreenSaver mScreenSaver = new ScreenSaver();

    void restartScreenSaverTask() {
        android.os.Handler handler = UIHandler.get();
        handler.removeCallbacks(mScreenSaver);
        handler.postDelayed(mScreenSaver, Config.SCREENSAVER);
    }

    void stopScreenSaverTask() {
        UIHandler.get().removeCallbacks(mScreenSaver);
    }

    @Override
    public void onStart() {
        Log.i(TAG, "onStart()");
        super.onStart();
        EventBus.getDefault().register(this);
        restartScreenSaverTask();
    }

    @Override
    public void onStop() {
        Log.i(TAG, "onStop()");
        stopScreenSaverTask();
        EventBus.getDefault().unregister(this);
        super.onStop();
    }

    @Override
    public void onPause() {
        super.onPause();
        EventBus.getDefault().removeAllStickyEvents();
    }

    void showError(boolean isVisible) {
        getErrorMessage().setVisibility(isVisible ? View.VISIBLE : View.INVISIBLE);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventMainThread(OnUserOfflineLoginCompleted event) {
        if (event.isSuccessfulLogin()) {
            PrefUtils.setLoginCompleted(true);
//            PrefUtils.setKioskModeEnabled(true);
            setVisibilityProgressBar(false);
            event.saveUserSessionPrefs();
            fragment(LocationFragment.builder().build());
        } else {
            showError(true);
            setVisibilityProgressBar(false);
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventMainThread(UserLoginByBarcodeResponse event) {
        if (event.isResultOk() && event.content.isStatusSuccess()) {
            PrefUtils.setLoginCompleted(true);
            fragment(LocationFragment.builder().build());
        } else {
            showError(true);
            if (event.content != null && !TextUtils.isEmpty(event.content.userMessage)) {
                getErrorMessage().setText(event.content.userMessage);
            }
            setVisibilityProgressBar(false);
        }
    }
}
