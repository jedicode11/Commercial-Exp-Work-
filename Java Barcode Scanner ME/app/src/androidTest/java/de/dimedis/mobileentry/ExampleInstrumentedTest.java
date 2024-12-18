package de.dimedis.mobileentry;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.filters.SmallTest;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Map;
import java.util.concurrent.CountDownLatch;

import de.dimedis.mobileentry.backend.BackendService;
import de.dimedis.mobileentry.backend.response.ServerConnectResponse;
import de.dimedis.mobileentry.util.SoundPlayerUtil;
import de.dimedis.mobileentry.util.SoundType;

import static org.junit.Assert.*;

@RunWith(AndroidJUnit4.class)
public class ExampleInstrumentedTest {

    private static final String TAG = "ApplicationTest";
    private static final String TOKEN = "1234";

    private EventBus eventBus;
    private CountDownLatch signal;

    @Rule
    public ActivityScenarioRule<MainActivity> activityScenarioRule = new ActivityScenarioRule<>(MainActivity.class);

    @Before
    public void setUp() {
        eventBus = EventBus.getDefault();
        eventBus.register(this);
    }

    @Test
    public void useAppContext() {
        // Context of the app under test.
        Context appContext = ApplicationProvider.getApplicationContext();
        assertEquals("de.dimedis.mobileentry", appContext.getPackageName());
    }

    @SmallTest
    @Test
    public void testConnectToServer() throws InterruptedException {
        signal = new CountDownLatch(1);
        Log.d(TAG, "START testConnectToServer");

        BackendService.serverConnect(TOKEN);
        signal.await();

        Log.d(TAG, "FINISH testConnectToServer");
    }

    @SmallTest
    @Test
    public void testVibrationCode() {
        SoundPlayerUtil spu = SoundPlayerUtil.getInstance();

        spu.playSoundAsync(SoundType.TICKET_SCAN_STATUS_OK);
        sleep();

        spu.playSoundAsync(SoundType.TICKET_SCAN_STATUS_OK_REDUCED_TICKET);
        sleep();

        spu.playSoundAsync(SoundType.TICKET_SCAN_STATUS_NOT_OK);
        sleep();

        spu.playSoundAsync(SoundType.CODE_SCAN_OK_OR_MANUAL_ACCEPTED);
        sleep();

        spu.playSoundAsync(SoundType.CODE_SCAN_REFUSED_OR_ALREADY_PROCESSING);
        sleep();

        spu.playSoundAsync(SoundType.GENERAL_ERROR);
        sleep();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventMainThread(ServerConnectResponse event) {
        if (signal != null) {
            signal.countDown();
        }

        Log.d(TAG, "event: " + event);
        if (event.isResultOk() && event.content.isStatusSuccess()) {
            Log.i(TAG, event.content.serverName);
            String langContainer = new Gson().toJson(event.content.languages);

            assertNotNull("Languages container is null", langContainer);

            Map<String, String> mapLang = new Gson().fromJson(langContainer, new TypeToken<Map<String, String>>() {
            }.getType());
            for (Map.Entry<String, String> entry : mapLang.entrySet()) {
                Log.i(TAG, "LANGUAGE: " + entry.getKey() + " | " + entry.getValue());
            }
        } else {
            Log.i(TAG, "ERROR: " + event.result);
        }
    }

    private void sleep() {
        try {
            Thread.sleep(4000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            Log.e(TAG, "Sleep interrupted", e);
        }
    }
}
