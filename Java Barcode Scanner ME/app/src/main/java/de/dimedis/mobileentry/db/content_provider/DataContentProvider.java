package de.dimedis.mobileentry.db.content_provider;

import android.content.ContentProvider;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;

import androidx.annotation.NonNull;

import de.dimedis.mobileentry.db.DataBaseHelper;
import de.dimedis.mobileentry.db.tables.TicketsHistoryTable;
import de.dimedis.mobileentry.db.tables.UserSessionTable;
import de.dimedis.mobileentry.util.Logger;

public class DataContentProvider extends ContentProvider {

    /**
     * authority
     */
    public static final String AUTHORITY = "de.dimedis.mobileentry.db.content_provider";

    /**
     * content uri
     */
    public final static Uri CONTENT_URI = new Uri.Builder().scheme(ContentResolver.SCHEME_CONTENT).authority(AUTHORITY).build();

    /**
     * CONTENT URIS MATCHERS
     */
    private static final int ALL_TICKETS = 10;
    private static final int ONE_TICKET = 20;
    private static final int ONE_SESSION = 30;
    private static final int ALL_SESSIONS = 40;
    /**
     * PATHS TO CONTENT TABLES
     */
    private static final String TICKETS_PATH = "tickets";
    private static final String SESSIONS_PATH = "sessions";

    /**
     * CONTENT URIS
     */
    public static final Uri TICKETS_URI = Uri.parse("content://" + AUTHORITY + "/" + TICKETS_PATH);
    public static final Uri SESSIONS_URI = Uri.parse("content://" + AUTHORITY + "/" + SESSIONS_PATH);
    public static final Uri TICKETS_URI_ONE = Uri.parse("content://" + AUTHORITY + "/" + TICKETS_PATH + "/" + ONE_TICKET);
    public static final Uri SESSIONS_URI_ONE = Uri.parse("content://" + AUTHORITY + "/" + SESSIONS_PATH + "/" + ONE_SESSION);

    private static final UriMatcher sURIMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    static {
        sURIMatcher.addURI(AUTHORITY, TICKETS_PATH, ALL_TICKETS);
        sURIMatcher.addURI(AUTHORITY, SESSIONS_PATH, ALL_SESSIONS);
        sURIMatcher.addURI(AUTHORITY, TICKETS_PATH + "/#", ONE_TICKET);
        sURIMatcher.addURI(AUTHORITY, SESSIONS_PATH + "/#", ONE_SESSION);
    }

    @Override
    public boolean onCreate() {
        DataBaseHelper.instance(getContext());
        return true;
    }

    @Override
    public Cursor query(@NonNull Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        final SQLiteDatabase db = DataBaseHelper.instance(getContext()).getReadableDatabase();
        int uriType = sURIMatcher.match(uri);
        Cursor cursor;

        switch (uriType) {
            case ALL_TICKETS: {
                cursor = db.query(TicketsHistoryTable.TABLE_TICKETS_HISTORY, projection, selection, selectionArgs, null, null, sortOrder);
                break;
            }

            case ALL_SESSIONS:

            case ONE_SESSION: {
                cursor = db.query(UserSessionTable.TABLE_USER_SESSION, projection, selection, selectionArgs, null, null, sortOrder);
                break;
            }

            default: {
                throw new IllegalArgumentException("Unknown URI: " + uri);
            }
        }

        if (null != cursor) {
            cursor.setNotificationUri(getContext().getContentResolver(), uri);
        }

        return cursor;
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        final SQLiteDatabase db = DataBaseHelper.instance(getContext()).getWritableDatabase();
        final ContentResolver resolver = getContext().getContentResolver();
        int uriType = sURIMatcher.match(uri);
        long rowId;

        if (uriType == ALL_TICKETS) {
            rowId = db.insert(TicketsHistoryTable.TABLE_TICKETS_HISTORY, null, values);

            if (-1 != rowId) {

                Uri insertedUri = ContentUris.withAppendedId(TICKETS_URI, rowId);
                resolver.notifyChange(insertedUri, null);
                resolver.notifyChange(uri, null);
                return insertedUri;
            }

            throw new SQLException("Failed to insert row into " + uri);
        }
        throw new IllegalArgumentException("Unknown URI: " + uri);
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        final SQLiteDatabase db = DataBaseHelper.instance(getContext()).getWritableDatabase();
        final ContentResolver resolver = getContext().getContentResolver();
        int uriType = sURIMatcher.match(uri);
        int rowsDeleted;

        switch (uriType) {
            case ALL_TICKETS: {
                rowsDeleted = db.delete(TicketsHistoryTable.TABLE_TICKETS_HISTORY, null, null);
                if (rowsDeleted > 0) {
                    resolver.notifyChange(TICKETS_URI, null);
                }
                break;
            }

            case ONE_TICKET: {
                rowsDeleted = db.delete(TicketsHistoryTable.TABLE_TICKETS_HISTORY, selection, selectionArgs);
                Logger.e("### DB ###", "Rows deleted: " + rowsDeleted + " for id: " + selectionArgs[0]);
                if (rowsDeleted > 0) {
                    resolver.notifyChange(TICKETS_URI, null);
                }
                break;
            }
            default: {
                throw new IllegalArgumentException("Unknown URI: " + uri);
            }
        }
        return rowsDeleted;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        final SQLiteDatabase db = DataBaseHelper.instance(getContext()).getWritableDatabase();
        final ContentResolver resolver = getContext().getContentResolver();
        int uriType = sURIMatcher.match(uri);
        int rowsUpdated;

        switch (uriType) {
            case ALL_TICKETS: {
                rowsUpdated = db.update(TicketsHistoryTable.TABLE_TICKETS_HISTORY, values, selection, selectionArgs);
                if (rowsUpdated > 0) {
                    resolver.notifyChange(TICKETS_URI, null);
                }
                break;
            }

            case ONE_SESSION:
                rowsUpdated = db.update(UserSessionTable.TABLE_USER_SESSION, values, selection, selectionArgs);
                if (rowsUpdated > 0) {
                    resolver.notifyChange(SESSIONS_URI, null);
                }
                break;

            default: {
                throw new IllegalArgumentException("Unknown URI: " + uri);
            }
        }

        return rowsUpdated;
    }

    @Override
    public String getType(Uri uri) {
        return null;
    }
}
