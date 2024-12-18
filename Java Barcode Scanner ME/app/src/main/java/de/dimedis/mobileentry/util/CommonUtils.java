package de.dimedis.mobileentry.util;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.os.Build;
import android.os.Vibrator;
import android.text.TextUtils;

import androidx.annotation.NonNull;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.nio.file.Files;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class CommonUtils {

    private CommonUtils() {
    }

    public static boolean isInternetConnected() {
        ConnectivityManager connectivityManager = (ConnectivityManager) AppContext.get().getSystemService(Context.CONNECTIVITY_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            Network network = connectivityManager.getActiveNetwork();
            if (network == null) return false;
            NetworkCapabilities actNw = connectivityManager.getNetworkCapabilities(network);
            return actNw != null && actNw.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET);
        } else {
            return isInternetAvailable();
        }
    }

    public static boolean isInternetAvailable() {
        try {
            InetAddress inetAddress = getInetAddressByName("google.com");
            return inetAddress != null && inetAddress.getHostAddress() != null && !inetAddress.getHostAddress().isEmpty();
        } catch (Exception e) {
            return false;
        }
    }

    public static InetAddress getInetAddressByName(String name) {
        CompletableFuture<InetAddress> future = CompletableFuture.supplyAsync(() -> {
            try {
                return InetAddress.getByName(name);
            } catch (UnknownHostException e) {
                return null;
            }
        }, Executors.newSingleThreadExecutor());

        try {
            return future.get(10, TimeUnit.SECONDS);
        } catch (InterruptedException | ExecutionException | TimeoutException e) {
            return null;
        }
    }

    public static String getDefaultHomePackage() {
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_HOME);
        ResolveInfo resolveInfo = AppContext.get().getPackageManager().resolveActivity(intent, PackageManager.MATCH_DEFAULT_ONLY);
        return (resolveInfo != null && resolveInfo.activityInfo != null) ? resolveInfo.activityInfo.packageName : null;
    }

    public static void startLauncher() {
        startLauncher(null);
    }

    public static void startLauncher(String packageName) {
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_HOME);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        if (!TextUtils.isEmpty(packageName)) {
            intent.setPackage(packageName);
        }
        AppContext.get().startActivity(intent);
    }

    public static void setComponentEnabled(@NonNull Class<?> cls, boolean isEnabled) {
        Context context = AppContext.get();
        context.getPackageManager().setComponentEnabledSetting(new ComponentName(context, cls), isEnabled ? PackageManager.COMPONENT_ENABLED_STATE_ENABLED : PackageManager.COMPONENT_ENABLED_STATE_DISABLED, PackageManager.DONT_KILL_APP);
    }

    public static void copyFileFromAssets(@NonNull String assetName, @NonNull File destFile) throws IOException {
        try (InputStream is = AppContext.get().getAssets().open(assetName);
             OutputStream os = new BufferedOutputStream(Files.newOutputStream(destFile.toPath()))) {

            copyStream(is, os);
        }
    }

    static void copyStream(InputStream is, OutputStream os) throws IOException {
        final int BUF_SIZE = 8 * 1024;
        byte[] buf = new byte[BUF_SIZE];
        int len;
        while ((len = is.read(buf, 0, BUF_SIZE)) > 0) {
            os.write(buf, 0, len);
        }
    }

    public static String inputStreamToString(@NonNull InputStream is) throws IOException {
        BufferedInputStream bis = null;
        OutputStream os = null;
        ByteArrayOutputStream baos = null;
        try {
            bis = new BufferedInputStream(is);
            baos = new ByteArrayOutputStream();
            copyStream(bis, baos);
            return baos.toString();
        } catch (Throwable th) {
            th.printStackTrace();
        } finally {
            closeQuietly(bis);
            closeQuietly(baos);
        }
        return null;
    }

    public static void closeQuietly(Closeable closeable) {
        if (closeable != null) {
            try {
                closeable.close();
            } catch (IOException ignored) {
            }
        }
    }

    public static void vibrate() {
        Vibrator vibrator = (Vibrator) AppContext.get().getSystemService(Context.VIBRATOR_SERVICE);
        if (vibrator.hasVibrator()) {
            vibrator.vibrate(100);
        }
    }

    public static boolean hasBBApi() {
        return AppContext.hasBBApi();
    }
}
