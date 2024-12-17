package de.dimedis.mobileentry.db

import android.content.*
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import de.dimedis.mobileentry.db.tables.TicketsHistoryTable
import de.dimedis.mobileentry.db.tables.UserSessionTable

class DataBaseHelper(context: Context?) :
    SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {
    override fun onCreate(database: SQLiteDatabase) {
        TicketsHistoryTable.onCreate(database)
        UserSessionTable.onCreate(database)
    }

    override fun onUpgrade(database: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        if (oldVersion == DATABASE_VERSION_V9) {
        }
        TicketsHistoryTable.onUpgrade(database, oldVersion, newVersion)
        UserSessionTable.onUpgrade(database, oldVersion, newVersion)
    }

    override fun onOpen(db: SQLiteDatabase) {
        super.onOpen(db)
    }

    companion object {
        private const val DATABASE_NAME = "mobile_entry_database.db"
        private const val DATABASE_VERSION = 13
        private const val DATABASE_VERSION_V9 = 9
        private var sInstance: DataBaseHelper? = null
        @Synchronized
        fun instance(context: Context?): DataBaseHelper? {
            if (null == sInstance) {
                sInstance = DataBaseHelper(context!!.applicationContext)
            }
            return sInstance
        }
    }
}