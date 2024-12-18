package de.dimedis.mobileentry.util;

import android.util.Log;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.RandomAccessFile;
import java.util.Date;

import de.dimedis.mobileentry.Config;

public class Logger {
    public static final String FUNCTION_HISTORY_SWITCH_TAG = "History function selected";
    public static final String FUNCTION_SCAN_SWITCH_TAG = "Scan function selected";

    private Logger() {
    }

    public static void i(String tag, String message) {
        Log.i(tag, message);
        asyncLog(tag, message, LogType.INFO);
    }

    public static void i(String tag, String message, Throwable cause) {
        Log.i(tag, message, cause);
        asyncLog(tag, message, LogType.INFO);
    }

    public static void w(String tag, String message) {
        Log.w(tag, message);
        asyncLog(tag, message, LogType.WARNING);
    }

    public static void w(String tag, String message, Throwable cause) {
        Log.w(tag, message, cause);
        asyncLog(tag, message, LogType.WARNING);
    }

    public static void e(String tag, String message) {
        Log.e(tag, message);
        asyncLog(tag, message, LogType.ERROR);
    }

    public static void e(String tag, String message, Throwable cause) {
        Log.e(tag, message, cause);
        asyncLog(tag, message, LogType.ERROR);
    }

    public static void asyncLog(final String tag, final String message, final LogType type) {
        if (!PrefUtils.isWriteLogfile()) {
            return;
        }
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    String prefix = getPrefix(type);
                    writeToFile(String.format("/%s/", tag.toUpperCase()), message);
                } catch (Throwable e) {
                    e.printStackTrace();
                }
            }

            private String getPrefix(LogType type) {
                String prefix = " [%s] ";
                return String.format(prefix, type.name());
            }
        }).start();
    }

    private static void writeToFile(String tag, String message) throws IOException {
        try (RandomAccessFile logFile = new RandomAccessFile(Config.LOG_FILE, "rw")) {
            Date date = new Date(System.currentTimeMillis());
            String time = Config.LOG_FILE_TIME_FORMAT.format(date);
            logFile.seek(logFile.length());
            String entry = time + "\t" + tag + "\t" + message + "\r\n";
            logFile.writeBytes(entry);
        }
    }

    public static long geLogfileSize() {
        File logFile = Config.LOG_FILE;
        long sizeInBytes = logFile.length();
        return sizeInBytes >>> 20;
    }

    public static void clearLogFile() throws IOException {
        PrintWriter writer = new PrintWriter(Config.LOG_FILE);
        writer.print("");
        writer.close();
    }
}
