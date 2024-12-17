package de.dimedis.mobileentry.db.managers

import android.content.ContentValues
import android.database.Cursor
import android.net.Uri
import android.os.AsyncTask
import android.util.Log
import de.dimedis.lmlib.SessionImpl
import de.dimedis.lmlib.myinterfaces.Session
import de.dimedis.mobileentry.ConfigPref
import de.dimedis.mobileentry.db.content_provider.DataContentProvider
import de.dimedis.mobileentry.db.model.TicketHistoryItem
import de.dimedis.mobileentry.db.tables.TicketsHistoryTable
import de.dimedis.mobileentry.db.tables.UserSessionTable
import de.dimedis.mobileentry.model.PerfData
import de.dimedis.mobileentry.util.AppContext
import de.dimedis.mobileentry.util.Logger
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.Observer
import io.reactivex.rxjava3.disposables.Disposable
import io.reactivex.rxjava3.schedulers.Schedulers
import org.greenrobot.eventbus.EventBus

class DataBaseManager private constructor() : Observer<Any> {
    fun clearAllTickets() {
        AppContext.get().contentResolver.delete(DataContentProvider.TICKETS_URI, null, null)
    }

    fun deleteTicketById(id: Int) {
        AppContext.get().contentResolver.delete(DataContentProvider.TICKETS_URI_ONE, BY_ID, arrayOf(id.toString()))
    }

    //---- Container  of Tickets
    class TicketHistoryContainer(item: TicketHistoryItem) {
        var arg: PerfData
        var tickets: ArrayList<TicketHistoryItem> = ArrayList()
        fun add(item: TicketHistoryItem) {
            tickets.add(item)
        }

        init {
            arg = item.getmPerfData()!!
        }

        fun getBody(): String? {
            return null
        }
    }

    @Throws(RuntimeException::class)
    fun getAllTickets(): Map<String, TicketHistoryContainer>? {
        val uri: Uri = DataContentProvider.TICKETS_URI
        var cursor: Cursor? = null
        return try {
            cursor = AppContext.get().contentResolver.query(uri, TicketsHistoryTable.projection,
                null, null, null)
            if (cursor == null || cursor.count == 0) {
                return null
            }

            val groupTackets = HashMap<String, TicketHistoryContainer>(cursor.count)
            var tmpTicketHistoryItem: TicketHistoryItem
            var tickets: TicketHistoryContainer?
            while (cursor.moveToNext()) {
                tmpTicketHistoryItem = TicketHistoryItem.fromCursor(cursor)
                val userSession: String? = tmpTicketHistoryItem.getmPerfData()?.userSession
                if (userSession != null) {
                    if (!groupTackets.containsKey(userSession)) {
                        groupTackets[userSession] = TicketHistoryContainer(tmpTicketHistoryItem).also { tickets = it }
                    } else {
                        tickets = groupTackets[userSession]
                    }
                    tickets!!.add(tmpTicketHistoryItem)
                }
            }
            groupTackets
        } catch (th: Throwable) {
            Log.e(TAG, "getAllTickets", th)
            null
        } finally {
            if (cursor != null && !cursor.isClosed) {
                cursor.close()
            }
        }
    }

    private fun saveTicketsArrayJsonAsync(tickets: List<TicketHistoryItem>) {
        val ticketAsyncTask = SaveTicketActionsAsyncTask(tickets)
        ticketAsyncTask.execute()
    }

    fun saveTicketActionsAsyncTask(ticket: TicketHistoryItem) {
        saveTicketsArrayJsonAsync(listOf(ticket))
    }

    fun saveTicket(ticket: TicketHistoryItem) {
        AppContext.get().contentResolver.insert(DataContentProvider.TICKETS_URI, ticket.toContentValues())
    }

    fun getTicketsHistory(mCurrentTicketCode: String?) {
        getTicketHistoryByCodeAsyncViaEvent(mCurrentTicketCode)
    }

    private fun getTicketHistoryByCodeAsyncViaEvent(mCurrentTicketCode: String?) {
        SelectTicketsAsync(mCurrentTicketCode).execute()
    }

    fun saveOfflineUserSession(session: Session) {
        Observable.create<Any> {
            val values: ContentValues = SessionImpl.SessionDB.toContentValues(session,
                ConfigPref.loginBarcode, ConfigPref.login, ConfigPref.passwd, true)
            DataBaseHelper.instance(AppContext.get())?.writableDatabase?.insertOrThrow(UserSessionTable.TABLE_USER_SESSION, null, values)
        }
            .subscribeOn(Schedulers.io())
            .observeOn(Schedulers.io())
            .subscribe(this)
    }

    override fun onComplete() {}
    override fun onError(e: Throwable) {
        e.printStackTrace()
    }

    override fun onSubscribe(d: Disposable) {}
    override fun onNext(o: Any) {}
    fun getAllUserSessions(): ArrayList<SessionImpl.SessionDB>? {
        val uri: Uri = DataContentProvider.SESSIONS_URI
        var cursor: Cursor? = null
        return try {
            cursor = AppContext.get().contentResolver.query(uri, projection_sessions, null,
                null, null)
            if (cursor == null || cursor.count == 0) {
                return null
            }
            val sessions: ArrayList<SessionImpl.SessionDB> = ArrayList(cursor.count)
            while (cursor.moveToNext()) {
                val sessionItem: SessionImpl.SessionDB = SessionImpl.SessionDB.fromCursor(cursor)
                sessions.add(sessionItem)
            }
            sessions
        } finally {
            if (cursor != null && !cursor.isClosed) {
                cursor.close()
            }
        }
    }

    fun reportUserLoggedOut(sessionId: String, userId: String, logoutSession: Session) {
        Observable.create<Any> {
            updateRow(sessionId, userId, logoutSession)
        }.subscribeOn(Schedulers.io()).observeOn(Schedulers.io()).subscribe(this)
    }

    private fun updateRow(sessionId: String, userId: String, logoutSession: Session) {
        val contentValues = ContentValues()
        contentValues.put(UserSessionTable.COLUMN_LOGOUT_TIMESTAMP, getCurrentTimeMillis())
        contentValues.put(UserSessionTable.COLUMN_LOGOUT_SESSION, SessionImpl.SessionDB.logoutSessionToString(logoutSession))
        val rowsUpdated: Int = DataBaseHelper.instance(AppContext.get())?.writableDatabase!!.update(UserSessionTable.TABLE_USER_SESSION, contentValues,
                UserSessionTable.COLUMN_USER_ID + " = ? AND " + UserSessionTable.COLUMN_SESSION + " = ? ", arrayOf(userId, sessionId))
        if (rowsUpdated < 1) { //fill empty session for logged out
            val values: ContentValues = SessionImpl.SessionDB.toContentValues(logoutSession, null, null, null, false)
            DataBaseHelper.instance(AppContext.get())?.writableDatabase!!.insertOrThrow(UserSessionTable.TABLE_USER_SESSION, null, values)
        }
    }

    fun deleteOfflineUserSessions() {
        val rows: Int = DataBaseHelper.instance(AppContext.get())?.writableDatabase!!.delete(UserSessionTable.TABLE_USER_SESSION, null, null)
        Logger.w("deleteOfflineUserSessions_####", "rows deleted: $rows all sessions")
    }

    fun deleteOfflineUserSessions(badSession: String) {
        val rows: Int = DataBaseHelper.instance(AppContext.get())?.writableDatabase!!.delete(UserSessionTable.TABLE_USER_SESSION,
                UserSessionTable.COLUMN_SESSION + " = ?", arrayOf(badSession))
        Logger.w("deleteOfflineUserSessions_####", "rows deleted: $rows for session: $badSession")
    }

    @Throws(RuntimeException::class)
    fun getSessionByUserSuid(session: String): ArrayList<*>? {
        val uri: Uri = DataContentProvider.SESSIONS_URI

        //SESSIONS_URI
        var cursor: Cursor? = null
        return try {
            cursor = AppContext.get().contentResolver.query(uri, projection_sessions,
                UserSessionTable.COLUMN_SESSION + " = ?", arrayOf(session), null)
            if (cursor == null || cursor.count == 0) {
                null
            } else ArrayList<Any>(cursor.count)
        } catch (ex: Exception) {
            Log.i(TAG, "getTicketByUserSession:", ex)
            null
        } finally {
            if (cursor != null && !cursor.isClosed) {
                cursor.close()
            }
        }
    }

    @Throws(RuntimeException::class)
    fun getTicketByUserSuid(userSuid: String): ArrayList<TicketHistoryItem>? {
        val uri: Uri = DataContentProvider.TICKETS_URI
        var cursor: Cursor? = null
        return try {
            cursor = AppContext.get().contentResolver.query(uri, projection_sessions,
                TicketsHistoryTable.COLUMN_USERSUID + " = ?", arrayOf(userSuid), null)
            if (cursor == null || cursor.count == 0) {
                return null
            }
            val tickets: ArrayList<TicketHistoryItem> = ArrayList<TicketHistoryItem>(cursor.count)
            while (cursor.moveToNext()) {
                tickets.add(TicketHistoryItem.fromCursor(cursor))
            }
            tickets
        } catch (ex: Exception) {
            Log.i(TAG, "getTicketByUserSuid:", ex)
            null
        } finally {
            if (cursor != null && !cursor.isClosed) {
                cursor.close()
            }
        }
    }

    inner class SelectTicketsAsync(ticketCode: String?) : AsyncTask<Any?, Any?, Any?>() {
        // TODO AsyncTask
        private var ticketCode: String? = null

        init {
            setTicketCode(ticketCode)
        }

        override fun doInBackground(vararg params: Any?): Any? {
            //  Uri uri = DataContentProvider.TICKETS_URI;
            var cursor: Cursor? = null
            return try {
                cursor = AppContext.get().contentResolver.query(
                    DataContentProvider.TICKETS_URI,
                    TicketsHistoryTable.projection, null, null, null
                )
                if (cursor == null || cursor.count == 0) {
                    return null
                }
                val tickets: ArrayList<TicketHistoryItem> = ArrayList(cursor.count)
                while (cursor.moveToNext()) {
                    val ticketHistoryItem: TicketHistoryItem = TicketHistoryItem.fromCursor(cursor)
                    tickets.add(ticketHistoryItem)
                }
                tickets
            } catch (th: Throwable) {
                Log.e(TAG, "SelectTicketsAsync", th)
                null
            } finally {
                if (cursor != null && !cursor.isClosed) {
                    cursor.close()
                }
            }
        }

        override fun onPostExecute(o: Any?) {
            if (o != null && o is ArrayList<*>) {
                EventBus.getDefault().post(TicketsSelected(o as ArrayList<TicketHistoryItem?>))
            } else {
                EventBus.getDefault().post(TicketsSelected(null))
            }
        }

        fun setTicketCode(ticketCode: String?) {
            this.ticketCode = ticketCode
        }

        fun getTicketCode(): String? {
            return ticketCode
        }
    }

    //<Params, Progress, Result>
    inner class SaveTicketActionsAsyncTask(tickets: List<TicketHistoryItem>) :
        AsyncTask<Any?, Any?, Any?>() {
        var tickets: List<TicketHistoryItem?>?

        init {
            this.tickets = tickets
        }

        override fun doInBackground(vararg params: Any?): Any {
            saveItems()
            return true
        }

        private fun saveItems() { //List<TicketHistoryItem> tickets
            if (tickets == null || tickets!!.isEmpty()) {
                return
            }
            for (ticket in tickets!!) {
                try {
                    if (ticket != null) {
                        saveTicket(ticket)
                    }
                } catch (throwable: Throwable) {
                    throwable.printStackTrace()
                }
            }
        }
    }

    inner class TicketsSelected(o: ArrayList<TicketHistoryItem?>?) {
        var tickets: ArrayList<TicketHistoryItem?>? = null

        init {
            setTickets(o)
        }

        @JvmName("setTickets1")
        fun setTickets(tickets: ArrayList<TicketHistoryItem?>?) {
            this.tickets = tickets
        }

        @JvmName("getTickets1")
        fun getTickets(): ArrayList<TicketHistoryItem?>? {
            return tickets
        }
    }

    companion object {
        const val TAG = "DataBaseManager"
        val BY_ID: String = TicketsHistoryTable.COLUMN_ID + "=?"
        private var sInstance: DataBaseManager? = null
        val projection_sessions = arrayOf(
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
            UserSessionTable.COLUMN_VALUE_2
        )

        @Synchronized
        fun instance(): DataBaseManager? {
            if (null == sInstance) {
                sInstance = DataBaseManager()
            }
            return sInstance
        }

        // need for NTP
        fun getCurrentTimeMillis(): Long {
            return System.currentTimeMillis()
        }
    }
}