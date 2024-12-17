package de.dimedis.mobileentry

import com.facebook.stetho.Stetho

abstract class MobileEntryDebugApplication : MobileEntryApplication() {
    override fun onCreate() {
        super.onCreate()
        Stetho.initialize(Stetho.newInitializerBuilder(this)
                .enableDumpapp(Stetho.defaultDumperPluginsProvider(this))
                .enableWebKitInspector(Stetho.defaultInspectorModulesProvider(this))
                .build())
        DebugUtils.init(this)
        mDebugUtilsInterface = DebugUtils.instance
    }
}