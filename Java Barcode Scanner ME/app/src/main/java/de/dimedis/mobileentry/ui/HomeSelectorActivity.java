package de.dimedis.mobileentry.ui;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.squareup.phrase.Phrase;

import de.dimedis.mobileentry.BuildConfig;
import de.dimedis.mobileentry.R;
import de.dimedis.mobileentry.util.CommonUtils;
import de.dimedis.mobileentry.util.PrefUtils;

public class HomeSelectorActivity extends BaseActivity {

    public static void start(@NonNull Context context) {
        Intent intent = new Intent(context, HomeSelectorActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_selector);

        CharSequence prompt = Phrase.from(this, R.string.tv_home_selector_prompt).put("app_name", getString(R.string.app_name)).format();
        ((TextView) findViewById(R.id.tv_home_selector_prompt)).setText(prompt);
    }

    @Override
    protected void onResume() {
        super.onResume();

        String defaultHomePackage = CommonUtils.getDefaultHomePackage();
        if (!TextUtils.equals(defaultHomePackage, getPackageName())) {
            // Store the current default launcher, if any.
            if (!TextUtils.isEmpty(defaultHomePackage) && !TextUtils.equals(defaultHomePackage, "android")) {
                PrefUtils.setOriginalHomePackage(defaultHomePackage);
            }

            // Re-enable FakeHomeActivity to show the launcher selector.
            CommonUtils.setComponentEnabled(HomeActivity.class, true);
            CommonUtils.setComponentEnabled(FakeHomeActivity.class, true);
            CommonUtils.startLauncher();
            CommonUtils.setComponentEnabled(FakeHomeActivity.class, false);
            if (BuildConfig.DEBUG && Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                InitActivity.start(this);
            }
        } else {
            HomeActivity.start(this);
            finish();
        }
    }
}
