package de.dimedis.mobileentry;

import android.os.Environment;

import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Locale;

public class Config {
    public static final String DISPATCHER_URL = "https://visit.dimedis.de/visit_vis-cgi/visit_vis/fairmate/dispatcher/nph-mobile-entry-3.cgi/Dim_fm::Ctrl::FairMateMobileEntry3";
    public static final String USERNAME = "fm_mobile_entry_3";
    public static final String PASSWORD = "opekTaBleuvgedWeifEs";
    public static final long SCREENSAVER = 10000L;
    public static final String DEFAULT_LANGUAGE = "en";
    private static final String DIMEDIS_LOG_FILE = "fairmate_logfile.txt";
    public static final File LOG_FILE = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), DIMEDIS_LOG_FILE);

    public static final DateFormat LOG_FILE_TIME_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss:SS", Locale.US);
    public static final DateFormat DB_DATE_TIME_FORMAT = new SimpleDateFormat("yyyyMMddHH:mm:ss.SSS", Locale.US);
    /**
     * Number of tickets to be uploaded to the server
     */
    public static final int DEFAULT_NUMBER_OF_TICKETS_PAGE = 50;

    private Config() {
    }
}
