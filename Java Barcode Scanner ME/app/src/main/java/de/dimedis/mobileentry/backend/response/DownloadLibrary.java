package de.dimedis.mobileentry.backend.response;

import java.io.File;

import de.dimedis.mobileentry.util.PrefUtils;

public class DownloadLibrary {
    public File libFile;

    public DownloadLibrary(File newFile) {
        this.libFile = newFile;
        PrefUtils.setLibraryVersion(newFile);
    }
}
