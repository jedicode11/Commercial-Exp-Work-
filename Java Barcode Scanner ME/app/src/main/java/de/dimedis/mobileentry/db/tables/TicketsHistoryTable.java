package de.dimedis.mobileentry.db.tables;

import android.database.sqlite.SQLiteDatabase;

import de.dimedis.mobileentry.util.Logger;

public class TicketsHistoryTable {

    public static final String TABLE_TICKETS_HISTORY = "TABLE_TICKETS_HISTORY";
    public static final String COLUMN_ID = "_id";

    public static final String COLUMN_LANG = "lang";
    public static final String COLUMN_COMM_KEY = "comm_key";
    public static final String COLUMN_DEVICESUID = "devicesuid";
    public static final String COLUMN_USERSESSION = "usersession";
    public static final String COLUMN_USERSUID = "usersuid";
    public static final String COLUMN_USERNAME = "username";
    public static final String COLUMN_FAIR = "fair";
    public static final String COLUMN_BORDER = "border";
    public static final String COLUMN_BODY = "body";


    public static final String[] projection = {
            COLUMN_ID,
            COLUMN_LANG,
            COLUMN_COMM_KEY,
            COLUMN_DEVICESUID,
            COLUMN_USERSESSION,
            COLUMN_USERSUID,
            COLUMN_USERNAME,
            COLUMN_FAIR,
            COLUMN_BORDER,
            COLUMN_BODY,
    };

    private static final String DATABASE_CREATE = "create table "
            + TABLE_TICKETS_HISTORY
            + "("
            + COLUMN_ID + " integer primary key autoincrement, "
            + COLUMN_LANG + " text, "
            + COLUMN_COMM_KEY + " text, "
            + COLUMN_DEVICESUID + " text, "
            + COLUMN_USERSESSION + " text, "
            + COLUMN_USERSUID + " text, "
            + COLUMN_USERNAME + " text, "
            + COLUMN_FAIR + " text, "
            + COLUMN_BORDER + " text, "
            + COLUMN_BODY + " text"
            + ");";


    public static void onCreate(SQLiteDatabase database) {
        database.execSQL(DATABASE_CREATE);
    }

    public static void onUpgrade(SQLiteDatabase database, int oldVersion, int newVersion) {
        Logger.w(TicketsHistoryTable.class.getName(),
                "Upgrading database from version "
                        + oldVersion + " to " + newVersion
                        + ", which will destroy all old data");
        database.execSQL("DROP TABLE IF EXISTS " + TABLE_TICKETS_HISTORY);
        onCreate(database);
    }
}
