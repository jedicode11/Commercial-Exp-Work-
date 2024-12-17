package de.dimedis.mobileentry.db.tables

import android.database.sqlite.SQLiteDatabase
import de.dimedis.mobileentry.util.Logger

object UserSessionTable {
    const val TABLE_USER_SESSION = "TABLE_USER_SESSION"
    const val COLUMN_ID = "_id"
    const val COLUMN_USER_ID = "user_id"
    const val COLUMN_LOGIN_TIMESTAMP = "COLUMN_LOGIN_TIMESTAMP"
    const val COLUMN_LOGOUT_TIMESTAMP = "COLUMN_LOGOUT_TIMESTAMP"
    const val COLUMN_STATUS = "COLUMN_STATUS"
    const val COLUMN_FULL_NAME = "COLUMN_FULL_NAME"
    const val COLUMN_NAME = "COLUMN_NAME"
    const val COLUMN_SESSION = "COLUMN_SESSION"
    const val COLUMN_FUNCTIONS = "COLUMN_FUNCTIONS"
    const val COLUMN_LOGOUT_SESSION = "COLUMN_LOGOUT_SESSION"
    const val COLUMN_PREFS = "COLUMN_PREFS"
    const val COLUMN_SESSION_RAW_VALUE = "COLUMN_SESSION_RAW_VALUE"
    const val COLUMN_AUTH_TYPE = "COLUMN_AUTH_TYPE"
    const val COLUMN_VALUE_1 = "COLUMN_VALUE_1"
    const val COLUMN_VALUE_2 = "COLUMN_VALUE_2"

    // Database creation SQL statement
    private const val DATABASE_CREATE = ("create table "
            + TABLE_USER_SESSION
            + "("
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
            + COLUMN_LOGIN_TIMESTAMP + " text "
            + ");")

    fun onCreate(database: SQLiteDatabase) {
        database.execSQL(DATABASE_CREATE)
    }

    fun onUpgrade(database: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        Logger.w(TicketsHistoryTable::class.java.name, "Upgrading database from version $oldVersion to $newVersion, which will destroy all old data")
        database.execSQL("DROP TABLE IF EXISTS $TABLE_USER_SESSION")
        onCreate(database)
    }
}