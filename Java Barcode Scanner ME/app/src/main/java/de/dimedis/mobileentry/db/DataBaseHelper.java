package de.dimedis.mobileentry.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import de.dimedis.mobileentry.db.tables.TicketsHistoryTable;
import de.dimedis.mobileentry.db.tables.UserSessionTable;

public class DataBaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "mobile_entry_database.db";
    private static final int DATABASE_VERSION = 13;
    private static final int DATABASE_VERSION_V9 = 9;
    private static DataBaseHelper sInstance = null;

    public static synchronized DataBaseHelper instance(Context context) {
        if (null == sInstance) {
            sInstance = new DataBaseHelper(context.getApplicationContext());
        }
        return sInstance;
    }

    public DataBaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase database) {
        TicketsHistoryTable.onCreate(database);
        UserSessionTable.onCreate(database);
    }

    @Override
    public void onUpgrade(SQLiteDatabase database, int oldVersion, int newVersion) {
        if (oldVersion == DATABASE_VERSION_V9) {
            TicketsHistoryTable.onUpgrade(database, oldVersion, newVersion);
            UserSessionTable.onUpgrade(database, oldVersion, newVersion);
        }
    }

    @Override
    public void onOpen(SQLiteDatabase db) {
        super.onOpen(db);
    }
}
