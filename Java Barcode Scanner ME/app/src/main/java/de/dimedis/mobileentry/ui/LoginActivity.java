package de.dimedis.mobileentry.ui;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;

import de.dimedis.mobileentry.fragments.SoftwareVersionsFragment;

public class LoginActivity extends BaseFragmentActivity {

    public static void start(@NonNull Context context) {
        Intent intent = new Intent(context, LoginActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (savedInstanceState == null) {
            addFragment(SoftwareVersionsFragment.builder().build(), "init");
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}




