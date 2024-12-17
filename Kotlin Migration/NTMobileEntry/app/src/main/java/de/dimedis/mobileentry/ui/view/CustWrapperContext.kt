package de.dimedis.mobileentry.ui.view

import android.content.*
import android.database.sqlite.SQLiteDatabase
import kotlin.Throws
import android.graphics.drawable.Drawable
import android.content.pm.PackageManager
import android.content.res.AssetManager
import android.content.res.Resources.Theme
import android.content.pm.ApplicationInfo
import android.database.sqlite.SQLiteDatabase.CursorFactory
import android.database.DatabaseErrorHandler
import android.graphics.Bitmap
import android.content.IntentSender.SendIntentException
import android.content.res.Configuration
import android.content.res.Resources
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.UserHandle
import android.view.Display
import java.io.*

class CustWrapperContext : Context() {
    override fun getAssets(): AssetManager? {
        return null
    }

    override fun getResources(): Resources? {
        return null
    }

    override fun getPackageManager(): PackageManager? {
        return null
    }

    override fun getContentResolver(): ContentResolver? {
        return null
    }

    override fun getMainLooper(): Looper? {
        return null
    }

    override fun getApplicationContext(): Context? {
        return null
    }

    override fun setTheme(resId: Int) {}
    override fun getTheme(): Theme? {
        return null
    }

    override fun getClassLoader(): ClassLoader? {
        return null
    }

    override fun getPackageName(): String? {
        return null
    }

    override fun getApplicationInfo(): ApplicationInfo? {
        return null
    }

    override fun getPackageResourcePath(): String? {
        return null
    }

    override fun getPackageCodePath(): String? {
        return null
    }

    override fun getSharedPreferences(name: String, mode: Int): SharedPreferences? {
        return null
    }

    override fun moveSharedPreferencesFrom(context: Context, s: String): Boolean {
        return false
    }

    override fun deleteSharedPreferences(s: String): Boolean {
        return false
    }

    @Throws(FileNotFoundException::class)
    override fun openFileInput(name: String): FileInputStream? {
        return null
    }

    @Throws(FileNotFoundException::class)
    override fun openFileOutput(name: String, mode: Int): FileOutputStream? {
        return null
    }

    override fun deleteFile(name: String): Boolean {
        return false
    }

    override fun getFileStreamPath(name: String): File? {
        return null
    }

    override fun getDataDir(): File? {
        return null
    }

    override fun getFilesDir(): File? {
        return null
    }

    override fun getNoBackupFilesDir(): File? {
        return null
    }

    override fun getExternalFilesDir(type: String?): File? {
        return null
    }

    override fun getExternalFilesDirs(type: String): Array<File?> {
        return arrayOfNulls(0)
    }

    override fun getObbDir(): File? {
        return null
    }

    override fun getObbDirs(): Array<File?> {
        return arrayOfNulls(0)
    }

    override fun getCacheDir(): File? {
        return null
    }

    override fun getCodeCacheDir(): File? {
        return null
    }

    override fun getExternalCacheDir(): File? {
        return null
    }

    override fun getExternalCacheDirs(): Array<File?> {
        return arrayOfNulls(0)
    }

    override fun getExternalMediaDirs(): Array<File?> {
        return arrayOfNulls(0)
    }

    override fun fileList(): Array<String?> {
        return arrayOfNulls(0)
    }

    override fun getDir(name: String, mode: Int): File? {
        return null
    }

    override fun openOrCreateDatabase(name: String, mode: Int, factory: CursorFactory): SQLiteDatabase? {
        return null
    }

    override fun openOrCreateDatabase(name: String, mode: Int, factory: CursorFactory, errorHandler: DatabaseErrorHandler?): SQLiteDatabase? {
        return null
    }

    override fun moveDatabaseFrom(context: Context, s: String): Boolean {
        return false
    }

    override fun deleteDatabase(name: String): Boolean {
        return false
    }

    override fun getDatabasePath(name: String): File? {
        return null
    }

    override fun databaseList(): Array<String?> {
        return arrayOfNulls(0)
    }

    override fun getWallpaper(): Drawable? {
        return null
    }

    override fun peekWallpaper(): Drawable? {
        return null
    }

    override fun getWallpaperDesiredMinimumWidth(): Int {
        return 0
    }

    override fun getWallpaperDesiredMinimumHeight(): Int {
        return 0
    }

    @Throws(IOException::class)
    override fun setWallpaper(bitmap: Bitmap) {
    }

    @Throws(IOException::class)
    override fun setWallpaper(data: InputStream) {
    }

    @Throws(IOException::class)
    override fun clearWallpaper() {
    }

    override fun startActivity(intent: Intent) {}
    override fun startActivity(intent: Intent, options: Bundle?) {}
    override fun startActivities(intents: Array<Intent>) {}
    override fun startActivities(intents: Array<Intent>, options: Bundle) {}
    @Throws(SendIntentException::class)
    override fun startIntentSender(intent: IntentSender, fillInIntent: Intent?, flagsMask: Int, flagsValues: Int, extraFlags: Int) {
    }

    @Throws(SendIntentException::class)
    override fun startIntentSender(intent: IntentSender, fillInIntent: Intent?, flagsMask: Int, flagsValues: Int, extraFlags: Int, options: Bundle?) {
    }

    override fun sendBroadcast(intent: Intent) {}
    override fun sendBroadcast(intent: Intent, receiverPermission: String?) {}
    override fun sendOrderedBroadcast(intent: Intent, receiverPermission: String?) {}
    override fun sendOrderedBroadcast(intent: Intent, receiverPermission: String?, resultReceiver: BroadcastReceiver?,
                                      scheduler: Handler?, initialCode: Int, initialData: String?, initialExtras: Bundle?) {
    }

    override fun sendBroadcastAsUser(intent: Intent, user: UserHandle) {}
    override fun sendBroadcastAsUser(intent: Intent, user: UserHandle, receiverPermission: String?) {
    }

    override fun sendOrderedBroadcastAsUser(intent: Intent, user: UserHandle, receiverPermission: String?, resultReceiver: BroadcastReceiver,
        scheduler: Handler?, initialCode: Int, initialData: String?, initialExtras: Bundle?) {
    }

    override fun sendStickyBroadcast(intent: Intent) {}
    override fun sendStickyOrderedBroadcast(intent: Intent, resultReceiver: BroadcastReceiver, scheduler: Handler?,
        initialCode: Int, initialData: String?, initialExtras: Bundle?) {
    }

    override fun removeStickyBroadcast(intent: Intent) {}
    override fun sendStickyBroadcastAsUser(intent: Intent, user: UserHandle) {}
    override fun sendStickyOrderedBroadcastAsUser(intent: Intent, user: UserHandle, resultReceiver: BroadcastReceiver,
        scheduler: Handler?, initialCode: Int, initialData: String?, initialExtras: Bundle?) {
    }

    override fun removeStickyBroadcastAsUser(intent: Intent, user: UserHandle) {}
    override fun registerReceiver(receiver: BroadcastReceiver?, filter: IntentFilter): Intent? {
        return null
    }

    override fun registerReceiver(broadcastReceiver: BroadcastReceiver?, intentFilter: IntentFilter, i: Int): Intent? {
        return null
    }

    override fun registerReceiver(receiver: BroadcastReceiver, filter: IntentFilter, broadcastPermission: String?,
        scheduler: Handler?): Intent? {
        return null
    }

    override fun registerReceiver(broadcastReceiver: BroadcastReceiver, intentFilter: IntentFilter, s: String?,
        handler: Handler?, i: Int): Intent? {
        return null
    }

    override fun unregisterReceiver(receiver: BroadcastReceiver) {}
    override fun startService(service: Intent): ComponentName? {
        return null
    }

    override fun startForegroundService(intent: Intent): ComponentName? {
        return null
    }

    override fun stopService(service: Intent): Boolean {
        return false
    }

    override fun bindService(service: Intent, conn: ServiceConnection, flags: Int): Boolean {
        return false
    }

    override fun unbindService(conn: ServiceConnection) {}
    override fun startInstrumentation(className: ComponentName, profileFile: String?, arguments: Bundle?): Boolean {
        return false
    }

    override fun getSystemService(name: String): Any? {
        return null
    }

    override fun getSystemServiceName(serviceClass: Class<*>): String? {
        return null
    }

    override fun checkPermission(permission: String, pid: Int, uid: Int): Int {
        return 0
    }

    override fun checkCallingPermission(permission: String): Int {
        return 0
    }

    override fun checkCallingOrSelfPermission(permission: String): Int {
        return 0
    }

    override fun checkSelfPermission(permission: String): Int {
        return 0
    }

    override fun enforcePermission(permission: String, pid: Int, uid: Int, message: String?) {}
    override fun enforceCallingPermission(permission: String, message: String?) {}
    override fun enforceCallingOrSelfPermission(permission: String, message: String?) {}
    override fun grantUriPermission(toPackage: String, uri: Uri, modeFlags: Int) {}
    override fun revokeUriPermission(uri: Uri, modeFlags: Int) {}
    override fun revokeUriPermission(s: String, uri: Uri, i: Int) {}
    override fun checkUriPermission(uri: Uri, pid: Int, uid: Int, modeFlags: Int): Int {
        return 0
    }

    override fun checkCallingUriPermission(uri: Uri, modeFlags: Int): Int {
        return 0
    }

    override fun checkCallingOrSelfUriPermission(uri: Uri, modeFlags: Int): Int {
        return 0
    }

    override fun checkUriPermission(uri: Uri?, readPermission: String?, writePermission: String?,
        pid: Int, uid: Int, modeFlags: Int): Int {
        return 0
    }

    override fun enforceUriPermission(uri: Uri, pid: Int, uid: Int, modeFlags: Int, message: String) {
    }

    override fun enforceCallingUriPermission(uri: Uri, modeFlags: Int, message: String) {}
    override fun enforceCallingOrSelfUriPermission(uri: Uri, modeFlags: Int, message: String) {}
    override fun enforceUriPermission(uri: Uri?, readPermission: String?, writePermission: String?,
        pid: Int, uid: Int, modeFlags: Int, message: String?) {
    }

    @Throws(PackageManager.NameNotFoundException::class)
    override fun createPackageContext(packageName: String, flags: Int): Context? {
        return null
    }

    @Throws(PackageManager.NameNotFoundException::class)
    override fun createContextForSplit(s: String): Context? {
        return null
    }

    override fun createConfigurationContext(overrideConfiguration: Configuration): Context? {
        return null
    }

    override fun createDisplayContext(display: Display): Context? {
        return null
    }

    override fun createDeviceProtectedStorageContext(): Context? {
        return null
    }

    override fun isDeviceProtectedStorage(): Boolean {
        return false
    }
}