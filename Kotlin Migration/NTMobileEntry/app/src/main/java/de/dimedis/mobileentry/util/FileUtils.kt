package de.dimedis.mobileentry.util

import android.content.Context
import androidx.core.content.ContextCompat
import java.io.File

object FileUtils {
    fun clearApplicationData(context: Context) {
        clearDirectories(context.cacheDir)
        clearDirectories(context.filesDir)
        clearDirectories(context.externalCacheDir)
        clearDirectories(context.getExternalFilesDir(null))
        clearDirectories(context.getExternalFilesDir(null)!!.parentFile)
        clearDirectories(context.cacheDir.parentFile)
        clearDirectories(*context.externalCacheDirs)
        clearDirectories(ContextCompat.getCodeCacheDir(context))
    }

    fun delete(file: File?): Boolean {
        if (file == null || !file.exists()) {
            return false
        }
        if (file.isDirectory) {
            for (currentFile in file.listFiles()) {
                val result = delete(currentFile)
                if (!result) {
                    return false
                }
            }
        }
        return file.delete()
    }

    fun clearDirectories(vararg directories: File?) {
        for (diretory in directories) {
            if (diretory == null) continue
            for (file in diretory.listFiles()) {
                val result = delete(file)
            }
        }
    }
}