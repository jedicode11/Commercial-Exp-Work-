package de.dimedis.mobileentry.util;

import android.content.Context;
import android.content.Intent;

import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import de.dimedis.mobileentry.ui.InitActivity;

public class UtilsService extends Worker {
    public UtilsService(@NonNull Context context, @NonNull WorkerParameters params) {
        super(context, params);
    }

    @NonNull
    @Override
    public Result doWork() {
        performCleanupTasks();
        return Result.success();
    }

    private void performCleanupTasks() {
        FileUtils.clearApplicationData(getApplicationContext());
        Intent intentNew = new Intent(getApplicationContext(), InitActivity.class);
        intentNew.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        getApplicationContext().startActivity(intentNew);
    }
}
