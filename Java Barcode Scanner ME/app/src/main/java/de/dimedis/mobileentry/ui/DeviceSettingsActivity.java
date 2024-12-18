package de.dimedis.mobileentry.ui;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import androidx.annotation.NonNull;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import de.dimedis.mobileentry.R;
import de.dimedis.mobileentry.backend.BackendService;
import de.dimedis.mobileentry.backend.response.GetTicketHistoryResponse;

public class DeviceSettingsActivity extends StubActivity {
    static String TAG = "DeviceSettingsActivity";
    TextView sta;

    public static void start(@NonNull Context context) {
        Intent intent = new Intent(context, DeviceSettingsActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(newBase);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_phone);
        sta = (TextView) findViewById(R.id.status);
        sta.setText("OFF");
    }

    @Override
    public void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
        BackendService.getTicketHistory("123-good");
    }

    @Override
    public void onStop() {
        EventBus.getDefault().unregister(this);
        super.onStop();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventMainThread(GetTicketHistoryResponse event) {
        Log.i(TAG, "event:" + event.content.toString());
    }
}
