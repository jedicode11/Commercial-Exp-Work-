package de.dimedis.mobileentry.util

import android.content.ContentProviderOperation
import android.content.OperationApplicationException
import android.os.AsyncTask
import android.os.RemoteException
import android.util.Log
import de.dimedis.lmlib.AccessImpl
import de.dimedis.mobileentry.Config
import de.dimedis.mobileentry.backend.ApiService
import de.dimedis.mobileentry.backend.BackendService
import de.dimedis.mobileentry.backend.BackendServiceConst
import de.dimedis.mobileentry.backend.response.BatchUploadResponse
import de.dimedis.mobileentry.db.content_provider.DataContentProvider
import de.dimedis.mobileentry.db.managers.DataBaseManager
import de.dimedis.mobileentry.db.managers.DataBaseManager.TicketHistoryContainer
import de.dimedis.mobileentry.db.model.TicketHistoryItem
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.ObservableEmitter
import io.reactivex.rxjava3.core.Observer
import io.reactivex.rxjava3.disposables.Disposable
import io.reactivex.rxjava3.schedulers.Schedulers
import org.greenrobot.eventbus.EventBus

object DataBaseUtil {
    const val TAG = "DataBaseUtil"
    var mOfflineSessionsSubscription: Observable<Any>? = null
    fun uploadCachedTickets() {
        UploadTicketsAsync().execute()
    }

    fun deleteCachedTickets() {
        ChachedTicketsAsync().execute()
    }

    fun deleteTicketsById(intArrayExtra: IntArray?) {
        if (intArrayExtra == null) {
            return
        }
        DeleteTicketsAsync(intArrayExtra).execute()
    }

    fun uploadCachedOfflineSessions() {
        if (mOfflineSessionsSubscription != null && !mOfflineSessionsSubscription!!.subscribe().isDisposed) {
            Log.w(TAG, "_ mOfflineSessionsSubscription in work ....")
            return
        }
        mOfflineSessionsSubscription = Observable.create { emitter: ObservableEmitter<Any> ->
            try {
                val sessions = DataBaseManager.instance()!!.getAllUserSessions()
                if (sessions == null || sessions.isEmpty()) {
                    Log.w(TAG, "UploadCachedOfflineSessions:Sessions empty")
                    emitter.onComplete()
                } else {
                    Log.w(TAG, "UploadCachedOfflineSessions:Sessions: " + sessions.size)
                    emitter.onNext(sessions)
                }
            } catch (ex: Exception) {
                emitter.onError(ex)
            }
        }
        (mOfflineSessionsSubscription as Observable<Any>).subscribeOn(Schedulers.io())
        mOfflineSessionsSubscription!!.observeOn(Schedulers.io()).subscribe(getObserverInstance())
    }

    private var sObserver: LocalObserver? = null
    private fun getObserverInstance(): LocalObserver {
        if (sObserver == null) {
            sObserver = LocalObserver()
        }
        return sObserver as LocalObserver
    }

    // private
    class LocalObserver : Observer<Any> {
        override fun onComplete() {
            handleSubscriptionCompleted()
        }

        private fun handleSubscriptionCompleted() {
            if (mOfflineSessionsSubscription != null) {
                mOfflineSessionsSubscription = null
            }
        }

        override fun onError(e: Throwable) {
            e.printStackTrace()
            handleSubscriptionCompleted()
        }

        override fun onSubscribe(d: Disposable) {}

        override fun onNext(sessions: Any) {
            Logger.i(TAG, "obj: $sessions")
            if (sessions is ArrayList<*>) {
                if (sessions.isEmpty()) {
                    onComplete()
                } else {
                    BackendServiceConst.mUploadSessionSubscription = (Disposable.disposed()) // Yasen
                }
            }
            onComplete()
        }
    }

//    internal
    class DeleteTicketsAsync(intArrayExtra: IntArray) : AsyncTask<Any?, Any?, Any?>() {
        private lateinit var ids: IntArray

        init {
            setIds(intArrayExtra)
        }

        override fun doInBackground(params: Array<Any?>): Any? {
            val ops = ArrayList<ContentProviderOperation>()
            for (id in ids) {
                try {
                    ops.add(ContentProviderOperation.newDelete(DataContentProvider.TICKETS_URI_ONE)
                        .withYieldAllowed(true)
                        .withSelection(DataBaseManager.BY_ID, arrayOf(id.toString()))
                        .build())
                } catch (ex: Exception) {
                    ex.printStackTrace()
                }
            }
            EventBus.getDefault().post(DeleteTicketsEvent(true))
            try {
                AppContext.get().contentResolver.applyBatch(DataContentProvider.AUTHORITY, ops)
            } catch (e: RemoteException) {
                e.printStackTrace()
            } catch (e: OperationApplicationException) {
                e.printStackTrace()
            }
            return null
        }

        fun setIds(ids: IntArray) {
            this.ids = ids
        }

        fun getIds(): IntArray {
            return ids
        }
    }

//    internal
    class ChachedTicketsAsync : AsyncTask<Any?, Any?, Any?>() {
        override fun doInBackground(params: Array<Any?>): Any? {
            val tickets: Map<String, TicketHistoryContainer>? = DataBaseManager.instance()!!.getAllTickets()
            if (tickets != null) for (entry in tickets.entries) {
                tickets(entry)
            }
            return null
        }

        /*ArrayList<TicketHistoryItem> tickets*/
        fun tickets(entry: Map.Entry<String, TicketHistoryContainer>) {
            val ticketsEntry = entry.value
            if (ticketsEntry.tickets.size == 0) {
                return
            }
            val end = if (Config.DEFAULT_NUMBER_OF_TICKETS_PAGE > ticketsEntry.tickets.size) ticketsEntry.tickets.size else Config.DEFAULT_NUMBER_OF_TICKETS_PAGE
            val ticketHistoryItems: List<TicketHistoryItem?> = ticketsEntry.tickets.subList(0, end)
            val ids = IntArray(ticketHistoryItems.size)
            for (i in ticketHistoryItems.indices) {
                val item = ticketHistoryItems[i]
                ids[i] = item?.columnId!!
            }
            deleteTicketsById(ids)
        }
    }


    class UploadTicketsAsync : AsyncTask<Any?, Any?, Any?>() {
        override fun doInBackground(params: Array<Any?>): Any? {
            val tickets: Map<String, TicketHistoryContainer>? = DataBaseManager.instance()!!.getAllTickets()
            if (tickets != null) {
                for (entry in tickets.entries) {
                    while (uploadTickets(entry)) {
                        Logger.e(TAG + "_### tickets uploading ###", "tickets batch uploaded")
                    }
                }
            } else {
                EventBus.getDefault().post(UploadTicketsEvent(true))
            }
            return null
        }

        fun uploadTickets(entry: Map.Entry<String, TicketHistoryContainer>): Boolean {
            val ticketsEntry = entry.value
            if (ticketsEntry.tickets.size == 0) {
                return false
            }
            val end = if (Config.DEFAULT_NUMBER_OF_TICKETS_PAGE > ticketsEntry.tickets.size) ticketsEntry.tickets.size else Config.DEFAULT_NUMBER_OF_TICKETS_PAGE
            val ticketHistoryItems = ticketsEntry.tickets.subList(0, end)
            val ids = IntArray(ticketHistoryItems.size)
            for (i in ticketHistoryItems.indices) {
                val item = ticketHistoryItems[i]
                ids[i] = item.columnId!!
            }
            val body = transformTicketsToBatchBody(ticketHistoryItems)
            Logger.e(TAG + "_" + ticketHistoryItems.size + " #####", body)
            upload(ticketsEntry, body, ids)
            ticketHistoryItems.clear()
            return true
        }

        private fun transformTicketsToBatchBody(ticketHistoryItems: List<TicketHistoryItem?>): String {
            val sb = StringBuilder(100000)
            sb.append("{\"actions\":[")
            val size = ticketHistoryItems.size
            for (i in 0 until size) {
                val item = ticketHistoryItems[i]
                sb.append(item?.setActionJson(AccessImpl()))
                if (i != size - 1) {
                    sb.append(",")
                }
            }
            sb.append("]}")
            return sb.toString()
        }

        var mSubsctiption: Disposable? = null
        private fun upload(argTC: TicketHistoryContainer, body: String, ticketIdsToDeleteOnSuccess: IntArray) {
            val arg = argTC.arg
            BackendServiceConst.sBackendApi?.batchUpload2(
                arg.lang,
                arg.commKey,
                arg.deviceSuid,
                arg.userSession,
                arg.userSuid,
                arg.userName,
                arg.fair,
                arg.border,
                body
            )?.subscribeOn(Schedulers.io()
            )?.observeOn(Schedulers.io())?.subscribe(object : Observer<BatchUploadResponse> {
                override fun onComplete() {
                    Logger.e(TAG + "_#### onCompleted", "Batch upload is finished")
                }

                override fun onError(e: Throwable?) {
                    if (e != null) {
                        EventBus.getDefault().post(UploadTicketsEvent(false))
                        e.printStackTrace()
                    }
                    Logger.e(TAG + "_Error", "error uploading local codes")
                }

                override fun onSubscribe(d: Disposable) {
                    mSubsctiption = d
                }

                override fun onNext(batchUploadResponse: BatchUploadResponse) {
                    if (!batchUploadResponse.isResultOk) {
                        onError(batchUploadResponse.throwable)
                    } else {
                        deleteTicketsById(ticketIdsToDeleteOnSuccess)
                        EventBus.getDefault().post(UploadTicketsEvent(true))
                    }
                }
            })
        }
    }

    class UploadTicketsEvent(val isUpload: Boolean)
    class DeleteTicketsEvent(val isUpload: Boolean)
}