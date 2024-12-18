package de.dimedis.mobileentry.util;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;

import androidx.core.content.ContextCompat;

import java.io.File;

public class FileUtils {

    @TargetApi(Build.VERSION_CODES.R)
    public static void clearApplicationData(Context context) {
        clearDirectories(context.getCacheDir());
        clearDirectories(context.getFilesDir());
        clearDirectories(context.getExternalCacheDir());
        clearDirectories(context.getExternalFilesDir(null));
        clearDirectories(context.getExternalFilesDir(null).getParentFile());
        clearDirectories(context.getCacheDir().getParentFile());
        clearDirectories(context.getExternalCacheDirs());
        clearDirectories(ContextCompat.getCodeCacheDir(context));
    }

    public static boolean delete(File file) {
        if ((file == null) || !file.exists()) {
            return false;
        }
        if (file.isDirectory()) {
            for (File currentFile : file.listFiles()) {
                boolean result = delete(currentFile);
                if (!result) {
                    return false;
                }
            }
        }
        return file.delete();
    }

    public static void clearDirectories(File... directories) {
        for (File directory : directories) {
            if (directory == null) continue;
            for (File file : directory.listFiles()) {
                boolean result = delete(file);
                if (!result) {
                    return;
                }
            }
        }
    }
}
