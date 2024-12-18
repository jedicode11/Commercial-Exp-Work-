package de.dimedis.mobileentry.db.managers;

import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

import androidx.annotation.Nullable;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.dimedis.lmlib.SessionImpl;
import de.dimedis.lmlib.myinterfaces.Session;
import de.dimedis.mobileentry.MobileEntryApplication;
import de.dimedis.mobileentry.db.DataBaseHelper;
import de.dimedis.mobileentry.db.content_provider.DataContentProvider;
import de.dimedis.mobileentry.db.model.TicketHistoryItem;
import de.dimedis.mobileentry.db.tables.TicketsHistoryTable;
import de.dimedis.mobileentry.db.tables.UserSessionTable;
import de.dimedis.mobileentry.model.PerfData;
import de.dimedis.mobileentry.util.AppContext;
import de.dimedis.mobileentry.util.Logger;
import io.reactivex.rxjava3.annotations.NonNull;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.core.Observer;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

public final class DataBaseManager implements Observer<Object> {
    static final String TAG = "DataBaseManager";
    public static final String BY_ID = TicketsHistoryTable.COLUMN_ID + "=?";
    private static DataBaseManager sInstance = null;

    public static final String[] projection_sessions = {
            UserSessionTable.COLUMN_ID,
            UserSessionTable.COLUMN_FULL_NAME,
            UserSessionTable.COLUMN_FUNCTIONS,
            UserSessionTable.COLUMN_NAME,
            UserSessionTable.COLUMN_PREFS,
            UserSessionTable.COLUMN_STATUS,
            UserSessionTable.COLUMN_USER_ID,
            UserSessionTable.COLUMN_SESSION,
            UserSessionTable.COLUMN_LOGOUT_SESSION,
            UserSessionTable.COLUMN_SESSION_RAW_VALUE,
            UserSessionTable.COLUMN_LOGIN_TIMESTAMP,
            UserSessionTable.COLUMN_LOGOUT_TIMESTAMP,
            UserSessionTable.COLUMN_AUTH_TYPE,
            UserSessionTable.COLUMN_VALUE_1,
            UserSessionTable.COLUMN_VALUE_2,
    };

    private DataBaseManager() {
    }

    public static synchronized DataBaseManager getInstance() {
        if (null == sInstance) {
            sInstance = new DataBaseManager();
        }
        return sInstance;
    }

    public void clearAllTickets() {
        AppContext.get().getContentResolver().delete(DataContentProvider.TICKETS_URI, null, null);
    }

    public void deleteTicketById(int id) {
        AppContext.get().getContentResolver().delete(DataContentProvider.TICKETS_URI_ONE, BY_ID, new String[]{String.valueOf(id)});
    }

    public static class TicketHistoryContainer {
        public PerfData arg;
        public ArrayList<TicketHistoryItem> tickets = new ArrayList<>();

        public void add(TicketHistoryItem item) {
            tickets.add(item);
        }

        public TicketHistoryContainer(TicketHistoryItem item) {
            arg = item.getmPerfData();
        }

        public String getBody() {
            return null;
        }
    }

    public @Nullable Map<String, TicketHistoryContainer> getAllTickets() {
        Uri uri = DataContentProvider.TICKETS_URI;
        try (Cursor cursor = AppContext.get().getContentResolver().query(uri, TicketsHistoryTable.projection, null, null, null)) {
            if (cursor == null || cursor.getCount() == 0) {
                return null;
            }

            HashMap<String, TicketHistoryContainer> groupTickets = new HashMap<>(cursor.getCount());
            while (cursor.moveToNext()) {
                TicketHistoryItem tmpTicketHistoryItem = TicketHistoryItem.fromCursor(cursor);
                String userSession = tmpTicketHistoryItem.getmPerfData().getUserSession();

                groupTickets.computeIfAbsent(userSession, k -> new TicketHistoryContainer(tmpTicketHistoryItem))
                        .add(tmpTicketHistoryItem);
            }
            return groupTickets;
        } catch (Throwable th) {
            Log.e(TAG, "getAllTickets", th);
            return null;
        }
    }

    public void saveTicketsArrayJsonAsync(List<TicketHistoryItem> tickets) {
        SaveTicketActionsAsyncTask ticketAsyncTask = new SaveTicketActionsAsyncTask();
        ticketAsyncTask.execute(tickets);
    }

    public void saveTicketActionsAsyncTask(TicketHistoryItem ticket) {
        saveTicketsArrayJsonAsync(Collections.singletonList(ticket));
    }

    public void saveTicket(TicketHistoryItem ticket) {
        AppContext.get().getContentResolver().insert(DataContentProvider.TICKETS_URI, ticket.toContentValues());
    }

    public void getTicketsHistory(String mCurrentTicketCode) {
        getTicketHistoryByCodeAsyncViaEvent(mCurrentTicketCode);
    }

    public void getTicketHistoryByCodeAsyncViaEvent(String mCurrentTicketCode) {
        new SelectTicketsAsync(mCurrentTicketCode).execute();
    }

    public void saveOfflineUserSession(final Session session) {
        Observable.create(emitter -> {
                    ContentValues values = SessionImpl.SessionDB.toContentValues(session,
                            MobileEntryApplication.getConfigPreferences().loginBarcode(),
                            MobileEntryApplication.getConfigPreferences().login(),
                            MobileEntryApplication.getConfigPreferences().passwd(),
                            true);
                    DataBaseHelper.instance(AppContext.get()).getWritableDatabase().insertOrThrow(UserSessionTable.TABLE_USER_SESSION, null, values);
                })
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .subscribe(this);
    }

    @Override
    public void onComplete() {
    }

    @Override
    public void onError(Throwable e) {
        e.printStackTrace();
    }

    @Override
    public void onSubscribe(@NonNull Disposable d) {
    }

    @Override
    public void onNext(Object o) {
    }

    public ArrayList<SessionImpl.SessionDB> getAllUserSessions() {
        Uri uri = DataContentProvider.SESSIONS_URI;
        Cursor cursor = null;
        try {
            cursor = AppContext.get().getContentResolver().query(uri, projection_sessions, null, null, null);
            if (cursor == null || cursor.getCount() == 0) {
                return null;
            }

            ArrayList<SessionImpl.SessionDB> sessions = new ArrayList<>(cursor.getCount());
            while (cursor.moveToNext()) {
                SessionImpl.SessionDB sessionItem = SessionImpl.SessionDB.fromCursor(cursor);
                sessions.add(sessionItem);
            }
            return sessions;
        } finally {
            if (cursor != null && !cursor.isClosed()) {
                cursor.close();
            }
        }
    }

    public void reportUserLoggedOut(final String sessionId, final String userId, final Session logoutSession) {
        Observable.create(emitter -> updateRow(sessionId, userId, logoutSession)).subscribeOn(Schedulers.io()).observeOn(Schedulers.io()).subscribe(this);
    }

    // need for NTP
    public static long getCurrentTimeMillis() {
        return System.currentTimeMillis();
    }

    private void updateRow(String sessionId, String userId, Session logoutSession) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(UserSessionTable.COLUMN_LOGOUT_TIMESTAMP, getCurrentTimeMillis());
        contentValues.put(UserSessionTable.COLUMN_LOGOUT_SESSION, SessionImpl.SessionDB.logoutSessionToString(logoutSession));

        int rowsUpdated = DataBaseHelper.instance(AppContext.get()).getWritableDatabase().update(
                UserSessionTable.TABLE_USER_SESSION, contentValues,
                UserSessionTable.COLUMN_USER_ID + " = ? AND " + UserSessionTable.COLUMN_SESSION + " = ? ", new String[]{userId, sessionId});

        if (rowsUpdated < 1) {   //fill empty session for logged out
            ContentValues values = SessionImpl.SessionDB.toContentValues(logoutSession,
                    null,
                    null,
                    null,
                    false
            );
            DataBaseHelper.instance(AppContext.get()).getWritableDatabase().insertOrThrow(UserSessionTable.TABLE_USER_SESSION, null, values);
        }
    }

    public void deleteOfflineUserSessions() {
        int rows = DataBaseHelper.instance(AppContext.get()).getWritableDatabase().delete(UserSessionTable.TABLE_USER_SESSION, null, null);
        Logger.w("deleteOfflineUserSessions_####", "rows deleted: " + rows + " all sessions");
    }

    public void deleteOfflineUserSessions(String badSession) {
        int rows = DataBaseHelper.instance(AppContext.get()).getWritableDatabase().delete(
                UserSessionTable.TABLE_USER_SESSION, UserSessionTable.COLUMN_SESSION + " = ?", new String[]{badSession});
        Logger.w("deleteOfflineUserSessions_####", "rows deleted: " + rows + " for session: " + badSession);
    }

    public @Nullable
    ArrayList<?> getSessionByUserSuid(String session) throws RuntimeException {
        Uri uri = DataContentProvider.SESSIONS_URI;

        Cursor cursor = null;

        try {
            cursor = AppContext.get().getContentResolver().query(uri,
                    projection_sessions, UserSessionTable.COLUMN_SESSION + " = ?", new String[]{session}, null);
            if (cursor == null || cursor.getCount() == 0) {
                return null;
            }
            return new ArrayList<>(cursor.getCount());

        } catch (Exception ex) {
            Log.i(TAG, "getTicketByUserSession:", ex);
            return null;
        } finally {
            if (cursor != null && !cursor.isClosed()) {
                cursor.close();
            }
        }
    }

    public @Nullable
    ArrayList<TicketHistoryItem> getTicketByUserSuid(String userSuid) throws RuntimeException {
        Uri uri = DataContentProvider.TICKETS_URI;
        Cursor cursor = null;

        try {
            cursor = AppContext.get().getContentResolver().query(uri,
                    projection_sessions, TicketsHistoryTable.COLUMN_USERSUID + " = ?", new String[]{userSuid}, null);
            if (cursor == null || cursor.getCount() == 0) {
                return null;
            }

            ArrayList<TicketHistoryItem> tickets = new ArrayList<>(cursor.getCount());
            while (cursor.moveToNext()) {
                tickets.add(TicketHistoryItem.fromCursor(cursor));
            }
            return tickets;

        } catch (Exception ex) {
            Log.i(TAG, "getTicketByUserSuid:", ex);
            return null;
        } finally {
            if (cursor != null && !cursor.isClosed()) {
                cursor.close();
            }
        }
    }

    public static class SelectTicketsAsync extends AsyncTask {
        // TODO AsyncTask
        private String ticketCode;

        public SelectTicketsAsync(String ticketCode) {
            setTicketCode(ticketCode);
        }

        @Override
        protected Object doInBackground(Object[] params) {
            Cursor cursor = null;

            try {
                cursor = AppContext.get().getContentResolver().query(DataContentProvider.TICKETS_URI,
                        TicketsHistoryTable.projection, null, null, null);
                if (cursor == null || cursor.getCount() == 0) {
                    return null;
                }

                ArrayList<TicketHistoryItem> tickets = new ArrayList<>(cursor.getCount());

                while (cursor.moveToNext()) {
                    TicketHistoryItem ticketHistoryItem = TicketHistoryItem.fromCursor(cursor);
                    tickets.add(ticketHistoryItem);
                }

                return tickets;
            } catch (Throwable th) {
                Log.e(TAG, "SelectTicketsAsync", th);
                return null;
            } finally {
                if (cursor != null && !cursor.isClosed()) {
                    cursor.close();
                }
            }
        }

        @Override
        protected void onPostExecute(Object o) {
            if (o instanceof ArrayList) {
                EventBus.getDefault().post(new TicketsSelected((ArrayList<TicketHistoryItem>) o));
            } else {
                EventBus.getDefault().post(new TicketsSelected(null));
            }
        }

        public void setTicketCode(String ticketCode) {
            this.ticketCode = ticketCode;
        }

        public String getTicketCode() {
            return ticketCode;
        }
    }

    //<Params, Progress, Result>
    public class SaveTicketActionsAsyncTask extends AsyncTask<List<TicketHistoryItem>, Void, Void> {

        @SafeVarargs
        @Override
        protected final Void doInBackground(List<TicketHistoryItem>... params) {
            saveItems(params[0]);
            return null;
        }

        private void saveItems(List<TicketHistoryItem> tickets) {
            if (tickets == null || tickets.isEmpty()) {
                return;
            }

            for (TicketHistoryItem ticket : tickets) {
                try {
                    saveTicket(ticket);
                } catch (Throwable throwable) {
                    throwable.printStackTrace();
                }
            }
        }
    }

    public static class TicketsSelected {
        private ArrayList<TicketHistoryItem> tickets;

        public TicketsSelected(ArrayList<TicketHistoryItem> o) {
            setTickets(o);
        }

        public void setTickets(ArrayList<TicketHistoryItem> tickets) {
            this.tickets = tickets;
        }

        public ArrayList<TicketHistoryItem> getTickets() {
            return tickets;
        }
    }
}
