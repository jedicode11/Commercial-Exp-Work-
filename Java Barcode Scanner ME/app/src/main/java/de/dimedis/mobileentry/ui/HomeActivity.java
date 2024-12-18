package de.dimedis.mobileentry.ui;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;

import de.dimedis.mobileentry.util.PrefUtils;

public class HomeActivity extends BaseActivity {
    public static void start(@NonNull Context context) {
        Intent intent = new Intent(context, HomeActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Start the initialization, if needed.
        if (!PrefUtils.isInitCompleted()) {
            InitActivity.start(this);
            finish();
            return;
        }
        // Start the login, if needed.
        if (!PrefUtils.isLoginCompleted()) {
            LoginActivity.start(this);
            finish();
            return;
        }
        ScanActivity.start(this);
        finish();
    }

}
