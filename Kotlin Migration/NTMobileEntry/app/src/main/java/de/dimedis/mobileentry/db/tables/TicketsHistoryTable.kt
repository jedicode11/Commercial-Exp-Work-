package de.dimedis.mobileentry.db.tables

import android.database.sqlite.SQLiteDatabase
import de.dimedis.mobileentry.util.Logger.w

object TicketsHistoryTable {
    const val TABLE_TICKETS_HISTORY = "TABLE_TICKETS_HISTORY"
    const val COLUMN_ID = "_id"
    const val COLUMN_LANG = "lang"
    const val COLUMN_COMM_KEY = "comm_key"
    const val COLUMN_DEVICESUID = "devicesuid"
    const val COLUMN_USERSESSION = "usersession"
    const val COLUMN_USERSUID = "usersuid"
    const val COLUMN_USERNAME = "username"
    const val COLUMN_FAIR = "fair"
    const val COLUMN_BORDER = "border"
    const val COLUMN_BODY = "body"
    val projection = arrayOf(
        COLUMN_ID,
        COLUMN_LANG,
        COLUMN_COMM_KEY,
        COLUMN_DEVICESUID,
        COLUMN_USERSESSION,
        COLUMN_USERSUID,
        COLUMN_USERNAME,
        COLUMN_FAIR,
        COLUMN_BORDER,
        COLUMN_BODY
    )

    // Database creation SQL statement
    private const val DATABASE_CREATE = ("create table "
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
            + ");")

    fun onCreate(database: SQLiteDatabase) {
        database.execSQL(DATABASE_CREATE)
    }

    fun onUpgrade(database: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        w(TicketsHistoryTable::class.java.name, "Upgrading database from version "
                    + oldVersion + " to " + newVersion + ", which will destroy all old data")
        database.execSQL("DROP TABLE IF EXISTS $TABLE_TICKETS_HISTORY")
        onCreate(database)
    }
}