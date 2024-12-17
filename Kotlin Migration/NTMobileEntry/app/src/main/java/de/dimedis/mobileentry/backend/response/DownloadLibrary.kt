package de.dimedis.mobileentry.backend.response

import de.dimedis.mobileentry.util.PrefUtils
import java.io.File

class DownloadLibrary(var libFile: File?) {
    init {
        libFile?.let { PrefUtils.setLibraryVersion(it) }
    }
}