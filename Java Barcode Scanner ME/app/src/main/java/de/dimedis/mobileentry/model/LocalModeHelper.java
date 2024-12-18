package de.dimedis.mobileentry.model;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Optional;
import java.util.jar.Attributes;
import java.util.jar.JarFile;
import java.util.jar.Manifest;

import dalvik.system.DexClassLoader;
import de.dimedis.lmlib.LocalModeImpl;
import de.dimedis.lmlib.myinterfaces.Access;
import de.dimedis.lmlib.myinterfaces.LocalMode;
import de.dimedis.mobileentry.ConfigPref;
import de.dimedis.mobileentry.MobileEntryApplication;
import de.dimedis.mobileentry.backend.response.Border;
import de.dimedis.mobileentry.util.AppContext;
import de.dimedis.mobileentry.util.CommonUtils;
import de.dimedis.mobileentry.util.ConfigPrefHelper;
import de.dimedis.mobileentry.util.Logger;
import de.dimedis.mobileentry.util.PrefUtils;

public class LocalModeHelper {
    private static final String TAG = LocalModeHelper.class.getSimpleName();
    private static final String LOCAL_MODE_CLASS_NAME = "de.dimedis.lmlib.impl.LocalModeImpl";
    private static final String LM_LIB_NAME = "lmlib.jar";
    private static LocalMode sLocalMode;

    private LocalModeHelper() {
    }

    public static Access performEntry(@NonNull String ticketCode) {
        return performEntry(new PerfData().initFromPref(), ticketCode);
    }

    public static Access performEntry(@NonNull PerfData pref, @NonNull String ticketCode) {

        LocalMode lm = getLocalMode();
        if (lm != null) {
            try {
                Access access = lm.performEntry(ticketCode, pref.getDeviceSuid(), pref.getUserSession(), pref.getUserSuid(), pref.getUserName(), pref.getFair(), pref.getBorder(), pref.getLang());
                Log.w(TAG, "access: " + access);
                return access;
            } catch (Exception e) {
                Logger.w(TAG, "Failed to perform entry", e);
            }
        }
        return null;
    }

    public static Access performCheckout(@NonNull String ticketCode) {
        return performCheckout(new PerfData().initFromPref(), ticketCode);
    }

    public static Access performCheckout(@NonNull PerfData pref, @NonNull String ticketCode) {
        LocalMode lm = getLocalMode();
        if (lm != null) {

            try {
                return lm.performCheckout(ticketCode, pref.getDeviceSuid(), pref.getUserSession(), pref.getUserSuid(), pref.getUserName(), pref.getFair(), pref.getBorder(), pref.getLang());
            } catch (Exception e) {
                Logger.w(TAG, "Failed to perform checkout", e);
            }
        }
        return null;
    }

    public static Access recordEntry(@NonNull String ticketCode) {
        LocalMode lm = getLocalMode();
        if (lm != null) {
            Border border = ConfigPrefHelper.getUsersBorder();
            if (border != null) {
                ConfigPref pref = MobileEntryApplication.getConfigPreferences();
                try {
                    return lm.recordEntry(ticketCode, pref.deviceSuid(), pref.userSession(), pref.userSuid(), pref.userName(), border.getFair(), border.getBorder(), pref.currentLanguage());
                } catch (Exception e) {
                    Logger.w(TAG, "Failed to record entry", e);
                }
            }
        }
        return null;
    }

    public static LocalMode getLocalMode() {
        if (sLocalMode == null) {
            sLocalMode = load();
        }
        return sLocalMode;
    }

    public static String getLocalLibVersionFromManifestFile(File fullPathToJar) {
        try {
            JarFile jar = new JarFile(fullPathToJar);
            final Manifest manifest = jar.getManifest();
            final Attributes mattr = manifest.getMainAttributes();
            String version = mattr.getValue("Implementation-Version");
            System.out.println("\\nVersion" + version + ":\\n");
            if (version == null) {
                throw new IOException("unable to read jar version from manifest");
            }
            return version;
        } catch (FileNotFoundException fex) {
            Log.e(TAG, "unable to find lib file: " + fullPathToJar.getAbsolutePath());
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return null;
    }

    static void initDefConfig() {
        InputStream is = null;
        try {
            is = AppContext.get().getAssets().open("offline-test-config.json");
            String conf = CommonUtils.inputStreamToString(is);
            ConfigPrefHelper.getPref().setOfflineConfig(conf);
            Log.w(TAG, "initDefConfig conf size: " + (conf != null ? conf.length() : "null") + " data:" + conf);
        } catch (Throwable th) {
            Log.e(TAG, "configLmLib... ", th);
        } finally {
            CommonUtils.closeQuietly(is);
        }
    }

    public static boolean configLmLib(LocalMode lm, String config) {
        try {
            if (TextUtils.isEmpty(config)) {
                Logger.e(TAG, "Config for lib empty");
                return false;
            }
            ObjectMapper mapper = new ObjectMapper();
            JsonNode actualObj = mapper.readTree(config);
            boolean test = actualObj.has("content") && actualObj.get("content").has("config");

            boolean isConfigured = lm.configure(actualObj);

            Log.w(TAG, "LocalModeHelper test: " + test);
            Log.w(TAG, "configLmLib isConfigured: " + isConfigured + " conf size: " + config.length() + " data:" + config);
            return isConfigured;
        } catch (Throwable th) {
            Log.e(TAG, "configLmLib... ", th);
        }
        return false;
    }

    public static boolean initWithNewPathLibrary(File libFile) {
        try {
            Logger.e("### local lib initialization ###", "local lib init started with: " + libFile);
//      TODO: investigate how is working that delete procedure (lmlib) ???
            File path = ContextCompat.getCodeCacheDir(AppContext.get()).getAbsoluteFile();
            if (path.isDirectory()) {
                File[] files = path.listFiles();
//      TODO: do we need to check is files isNull
                for (File fileDel : files) {
                    boolean status = fileDel.delete();
                    Log.i(TAG, "fileDel:" + fileDel.getName() + " status:" + status);
                }
            }

            PrefUtils.setLibraryVersion(libFile);

            LocalMode lm = load(libFile);
            if (lm == null) return false;

            boolean status = false;
            String offlineConfigServer = ConfigPrefHelper.getPref().offlineConfigServer();
            if (!TextUtils.isEmpty(offlineConfigServer)) {
                status = configLmLib(lm, offlineConfigServer);
                ConfigPrefHelper.getPref().setOfflineConfig(offlineConfigServer);
            }

            MobileEntryApplication.getConfigPreferences().setLmlibFilePath(libFile.getAbsolutePath());
            Log.i(TAG, "initWithNewPathLibrary  status:" + status);
            sLocalMode = lm;
            return true;
        } catch (Throwable e) {
            Logger.w(TAG, "Failed to load lmlib", e);
        }
        return false;
    }

    private static LocalMode firstInitLibrary() {
        try {
            Context context = AppContext.get();
            File destFile = new File(context.getObbDir(), LM_LIB_NAME);
            CommonUtils.copyFileFromAssets(LM_LIB_NAME, destFile);
            PrefUtils.setLibraryVersion(destFile);
            LocalMode lm = load(destFile);
            MobileEntryApplication.getConfigPreferences().setLmlibFilePath(destFile.getPath()); // was AbsolutePath
            // offlineConfigServer
            if (TextUtils.isEmpty(ConfigPrefHelper.getPref().offlineConfig())) {
                initDefConfig();
            }
            return lm;
        } catch (Exception e) {
            Logger.w(TAG, "Failed to load lmlib", e);
        }
        return null;
    }

    private static LocalMode load() {
        String path = MobileEntryApplication.getConfigPreferences().lmlibFilePath();
        LocalMode lm;
        if (TextUtils.isEmpty(path)) {
            lm = firstInitLibrary();
        } else {
            lm = load(new File(path));
            PrefUtils.setLibraryVersion(path);
        }
        String offlineConfig = Optional.ofNullable(ConfigPrefHelper.getPref().offlineConfigServer())
                .filter(s -> !s.isEmpty())
                .orElse(ConfigPrefHelper.getPref().offlineConfig());
        configLmLib(lm, offlineConfig);
        return lm;
    }

    private static LocalMode load(File destFile) {
        try {
            Context context = AppContext.get();
            DexClassLoader classLoader = new DexClassLoader(destFile.getAbsolutePath(), ContextCompat.getCodeCacheDir(context).getAbsolutePath(), null, context.getClassLoader());
            Class<?> clazz = classLoader.loadClass(LOCAL_MODE_CLASS_NAME);
            return new LocalModeImpl(clazz, clazz.newInstance());
        } catch (Exception e) {
            Logger.w(TAG, "Failed to load lmlib", e);
        }
        return null;
    }
}
