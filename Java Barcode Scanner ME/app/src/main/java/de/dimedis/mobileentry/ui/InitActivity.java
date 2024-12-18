package de.dimedis.mobileentry.ui;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;

import de.dimedis.mobileentry.fragments.InitializeFragment;
import de.dimedis.mobileentry.fragments.LanguageFragment;
import de.dimedis.mobileentry.util.PrefUtils;

public class InitActivity extends BaseFragmentActivity {
    static final String TAG = "InitActivity";

    public static void start(@NonNull Context context) {
        Intent intent = new Intent(context, InitActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState == null) {
            if (PrefUtils.isInitCompleted())
                addFragment(LanguageFragment.builder().build(), "lang");
            else addFragment(InitializeFragment.builder().build(), "init");
        }
    }
}
