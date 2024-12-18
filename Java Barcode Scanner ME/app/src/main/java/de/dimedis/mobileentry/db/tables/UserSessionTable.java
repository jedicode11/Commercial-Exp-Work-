package de.dimedis.mobileentry.db.tables;

import android.database.sqlite.SQLiteDatabase;

import de.dimedis.mobileentry.util.Logger;

public class UserSessionTable {
    public static final String TABLE_USER_SESSION = "TABLE_USER_SESSION";
    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_USER_ID = "user_id";
    public static final String COLUMN_LOGIN_TIMESTAMP = "COLUMN_LOGIN_TIMESTAMP";
    public static final String COLUMN_LOGOUT_TIMESTAMP = "COLUMN_LOGOUT_TIMESTAMP";
    public static final String COLUMN_STATUS = "COLUMN_STATUS";
    public static final String COLUMN_FULL_NAME = "COLUMN_FULL_NAME";
    public static final String COLUMN_NAME = "COLUMN_NAME";
    public static final String COLUMN_SESSION = "COLUMN_SESSION";
    public static final String COLUMN_FUNCTIONS = "COLUMN_FUNCTIONS";
    public static final String COLUMN_LOGOUT_SESSION = "COLUMN_LOGOUT_SESSION";
    public static final String COLUMN_PREFS = "COLUMN_PREFS";
    public static final String COLUMN_SESSION_RAW_VALUE = "COLUMN_SESSION_RAW_VALUE";
    public static final String COLUMN_AUTH_TYPE = "COLUMN_AUTH_TYPE";
    public static final String COLUMN_VALUE_1 = "COLUMN_VALUE_1";
    public static final String COLUMN_VALUE_2 = "COLUMN_VALUE_2";

    private static final String DATABASE_CREATE = "create table "
            + TABLE_USER_SESSION + "("
            + COLUMN_ID + " integer primary key autoincrement, "
            + COLUMN_USER_ID + " text, "
            + COLUMN_STATUS + " text, "
            + COLUMN_FULL_NAME + " text, "
            + COLUMN_NAME + " text, "
            + COLUMN_LOGOUT_SESSION + " text, "
            + COLUMN_SESSION_RAW_VALUE + " text, "
            + COLUMN_SESSION + " text, "
            + COLUMN_FUNCTIONS + " text, "
            + COLUMN_PREFS + " text, "
            + COLUMN_AUTH_TYPE + " text, "
            + COLUMN_VALUE_1 + " text, "
            + COLUMN_VALUE_2 + " text, "
            + COLUMN_LOGOUT_TIMESTAMP + " text, "
            + COLUMN_LOGIN_TIMESTAMP + " text " + ");";

    public static void onCreate(SQLiteDatabase database) {
        database.execSQL(DATABASE_CREATE);
    }

    public static void onUpgrade(SQLiteDatabase database, int oldVersion, int newVersion) {
        Logger.w(TicketsHistoryTable.class.getName(), "Upgrading database from version "
                + oldVersion + " to " + newVersion
                + ", which will destroy all old data");
        database.execSQL("DROP TABLE IF EXISTS " + TABLE_USER_SESSION);
        onCreate(database);
    }
}
