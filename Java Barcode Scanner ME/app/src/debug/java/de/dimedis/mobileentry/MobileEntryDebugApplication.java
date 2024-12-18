package de.dimedis.mobileentry;

import com.facebook.stetho.Stetho;

public class MobileEntryDebugApplication extends MobileEntryApplication {
    @Override
    public void onCreate() {
        super.onCreate();
        Stetho.initialize(Stetho.newInitializerBuilder(this).enableDumpapp(Stetho.defaultDumperPluginsProvider(this)).enableWebKitInspector(Stetho.defaultInspectorModulesProvider(this)).build());

        DebugUtils.init(this);
        MobileEntryApplication.mDebugUtilsInterface = DebugUtils.getInstance();
    }
}
