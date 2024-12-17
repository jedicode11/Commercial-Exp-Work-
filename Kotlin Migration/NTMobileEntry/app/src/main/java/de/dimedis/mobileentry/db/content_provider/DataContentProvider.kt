package de.dimedis.mobileentry.db.content_provider

import android.content.*
import android.database.Cursor
import android.database.SQLException
import android.database.sqlite.SQLiteDatabase
import android.net.Uri
import de.dimedis.mobileentry.db.tables.TicketsHistoryTable
import de.dimedis.mobileentry.db.tables.UserSessionTable
import de.dimedis.mobileentry.util.Logger.e

class DataContentProvider : ContentProvider() {
    override fun onCreate(): Boolean {
        DataBaseHelper.instance(context)
        return true
    }

    override fun query(uri: Uri, projection: Array<String>?, selection: String?, selectionArgs: Array<String>?,
        sortOrder: String?): Cursor? {
        val db: SQLiteDatabase = DataBaseHelper.instance(context)!!.readableDatabase
        val cursor: Cursor? = when (sURIMatcher.match(uri)) {
            ALL_TICKETS -> {
                db.query(TicketsHistoryTable.TABLE_TICKETS_HISTORY, projection, selection, selectionArgs,
                    null, null, sortOrder)
            }
            ALL_SESSIONS, ONE_SESSION -> {
                db.query(UserSessionTable.TABLE_USER_SESSION, projection, selection, selectionArgs,
                    null, null, sortOrder)
            }
            else -> {
                throw IllegalArgumentException("Unknown URI: $uri")
            }
        }
        cursor?.setNotificationUri(context!!.contentResolver, uri)
        return cursor
    }

    override fun insert(uri: Uri, values: ContentValues?): Uri {
        val db: SQLiteDatabase = DataBaseHelper.instance(context)!!.writableDatabase
        val resolver = context!!.contentResolver
        val uriType = sURIMatcher.match(uri)
        val rowId: Long
        if (uriType == ALL_TICKETS) {
            rowId = db.insert(TicketsHistoryTable.TABLE_TICKETS_HISTORY, null, values)
            if (-1L != rowId) {
                val insertedUri = ContentUris.withAppendedId(TICKETS_URI, rowId)
                resolver.notifyChange(insertedUri, null)
                resolver.notifyChange(uri, null)
                return insertedUri
            }
            throw SQLException("Failed to insert row into $uri")
        }
        throw IllegalArgumentException("Unknown URI: $uri")
    }

    override fun delete(uri: Uri, selection: String?, selectionArgs: Array<String>?): Int {
        val db: SQLiteDatabase = DataBaseHelper.instance(context)!!.writableDatabase
        val resolver = context!!.contentResolver
        val uriType = sURIMatcher.match(uri)
        val rowsDeleted: Int
        when (uriType) {
            ALL_TICKETS -> {
                rowsDeleted = db.delete(TicketsHistoryTable.TABLE_TICKETS_HISTORY, null, null)
                if (rowsDeleted > 0) {
                    resolver.notifyChange(TICKETS_URI, null)
                }
            }
            ONE_TICKET -> {
                rowsDeleted = db.delete(TicketsHistoryTable.TABLE_TICKETS_HISTORY, selection, selectionArgs)
                e("### DB ###", "Rows deleted: " + rowsDeleted + " for id: " + selectionArgs!![0])
                if (rowsDeleted > 0) {
                    resolver.notifyChange(TICKETS_URI, null)
                }
            }
            else -> {
                throw IllegalArgumentException("Unknown URI: $uri")
            }
        }
        return rowsDeleted
    }

    override fun update(uri: Uri, values: ContentValues?, selection: String?, selectionArgs: Array<String>?): Int {
        val db: SQLiteDatabase = DataBaseHelper.instance(context)!!.writableDatabase
        val resolver = context!!.contentResolver
        val uriType = sURIMatcher.match(uri)
        val rowsUpdated: Int
        when (uriType) {
            ALL_TICKETS -> {
                rowsUpdated = db.update(TicketsHistoryTable.TABLE_TICKETS_HISTORY, values, selection, selectionArgs)
                if (rowsUpdated > 0) {
                    resolver.notifyChange(TICKETS_URI, null)
                }
            }
            ONE_SESSION -> {
                rowsUpdated = db.update(UserSessionTable.TABLE_USER_SESSION, values, selection, selectionArgs)
                if (rowsUpdated > 0) {
                    resolver.notifyChange(SESSIONS_URI, null)
                }
            }
            else -> {
                throw IllegalArgumentException("Unknown URI: $uri")
            }
        }
        return rowsUpdated
    }

    override fun getType(uri: Uri): String? {
        return null
    }

    companion object {
        /**
         * authority
         */
        const val AUTHORITY = "de.dimedis.mobileentry.db.content_provider"
        /**
         * content uri
         */
        val CONTENT_URI = Uri.Builder().scheme(ContentResolver.SCHEME_CONTENT).authority(AUTHORITY).build()
        /**
         * CONTENT URIS MATCHERS
         */
        private const val ALL_TICKETS = 10
        private const val ONE_TICKET = 20
        private const val ONE_SESSION = 30
        private const val ALL_SESSIONS = 40
        /**
         * PATHS TO CONTENT TABLES
         */
        private const val TICKETS_PATH = "tickets"
        private const val SESSIONS_PATH = "sessions"
        /**
         * CONTENT URIS
         */
        val TICKETS_URI = Uri.parse("content://$AUTHORITY/$TICKETS_PATH")
        val SESSIONS_URI = Uri.parse("content://$AUTHORITY/$SESSIONS_PATH")
        val TICKETS_URI_ONE = Uri.parse("content://$AUTHORITY/$TICKETS_PATH/$ONE_TICKET")
        val SESSIONS_URI_ONE = Uri.parse("content://$AUTHORITY/$SESSIONS_PATH/$ONE_SESSION")
        private val sURIMatcher = UriMatcher(UriMatcher.NO_MATCH)

        init {
            sURIMatcher.addURI(AUTHORITY, TICKETS_PATH, ALL_TICKETS)
            sURIMatcher.addURI(AUTHORITY, SESSIONS_PATH, ALL_SESSIONS)
            sURIMatcher.addURI(AUTHORITY, "$TICKETS_PATH/#", ONE_TICKET)
            sURIMatcher.addURI(AUTHORITY, "$SESSIONS_PATH/#", ONE_SESSION)
        }
    }
}