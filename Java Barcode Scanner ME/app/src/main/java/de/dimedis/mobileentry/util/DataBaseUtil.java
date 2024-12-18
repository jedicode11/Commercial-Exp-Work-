package de.dimedis.mobileentry.util;

import android.content.ContentProviderOperation;
import android.content.OperationApplicationException;
import android.os.AsyncTask;
import android.os.RemoteException;
import android.util.Log;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import de.dimedis.lmlib.SessionImpl;
import de.dimedis.mobileentry.Config;
import de.dimedis.mobileentry.backend.BackendService;
import de.dimedis.mobileentry.backend.response.BatchUploadResponse;
import de.dimedis.mobileentry.db.content_provider.DataContentProvider;
import de.dimedis.mobileentry.db.managers.DataBaseManager;
import de.dimedis.mobileentry.db.model.TicketHistoryItem;
import de.dimedis.mobileentry.model.PerfData;
import io.reactivex.rxjava3.annotations.NonNull;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.core.Observer;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class DataBaseUtil {
    static final String TAG = "DataBaseUtil";
    public static Observable<Object> mOfflineSessionsSubscription = null;

    public static void uploadCachedTickets() {
        new UploadTicketsAsync().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    public static void deleteCachedTickets() {
        new CachedTicketsAsync().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    public static void deleteTicketsById(int[] intArrayExtra) {
        if (intArrayExtra == null) {
            return;
        }
        new DeleteTicketsAsync(intArrayExtra).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    public static void uploadCachedOfflineSessions() {
        if (mOfflineSessionsSubscription != null && !mOfflineSessionsSubscription.subscribe().isDisposed()) {
            Log.w(TAG, "_ mOfflineSessionsSubscription in work ....");
            return;
        }
        mOfflineSessionsSubscription = Observable.create((emitter) -> {
            try {
                ArrayList<SessionImpl.SessionDB> sessions = DataBaseManager.getInstance().getAllUserSessions();
                if (sessions == null || sessions.isEmpty()) {
                    Log.w(TAG, "UploadCachedOfflineSessions:Sessions empty");
                    emitter.onComplete();
                } else {
                    Log.w(TAG, "UploadCachedOfflineSessions:Sessions: " + sessions.size());
                    emitter.onNext(sessions);
                    emitter.onComplete(); //added
                }
            } catch (Exception ex) {
                emitter.onError(ex);
            }
        });
        mOfflineSessionsSubscription.subscribeOn(Schedulers.io()).observeOn(Schedulers.io()).subscribe(getObserverInstance());
    }

    private static LocalObserver sObserver = null;

    private static LocalObserver getObserverInstance() {
        if (sObserver == null) {
            sObserver = new LocalObserver();
        }
        return sObserver;
    }

    private static class LocalObserver implements Observer<Object> {

        @Override
        public void onComplete() {
            handleSubscriptionCompleted();
        }

        private void handleSubscriptionCompleted() {
            if (mOfflineSessionsSubscription != null) {
                mOfflineSessionsSubscription = null;
            }
        }

        @Override
        public void onError(Throwable e) {
            e.printStackTrace();
            handleSubscriptionCompleted();
        }

        @Override
        public void onSubscribe(@NonNull Disposable d) {
        }

        @Override
        public void onNext(Object o) {
            Logger.i(TAG, "obj: " + o);
            if (o instanceof ArrayList) {
                ArrayList<SessionImpl.SessionDB> sessions = (ArrayList<SessionImpl.SessionDB>) o;
                if (sessions.isEmpty()) {
                    onComplete();
                } else {
                    BackendService.uploadOfflineSessions(sessions);
                }
            }
            onComplete();
        }
    }

    static class DeleteTicketsAsync extends AsyncTask<Void, Void, Void> {
        private int[] ids;

        public DeleteTicketsAsync(int[] intArrayExtra) {
            super();  // Calls the super constructor explicitly
            setIds(intArrayExtra);
        }

        @Override
        protected Void doInBackground(Void... params) {
            ArrayList<ContentProviderOperation> ops = new ArrayList<>();

            for (Integer id : ids) {
                try {
                    ops.add(ContentProviderOperation.newDelete(DataContentProvider.TICKETS_URI_ONE)
                            .withYieldAllowed(true)
                            .withSelection(DataBaseManager.BY_ID, new String[]{String.valueOf(id)})
                            .build());
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
            EventBus.getDefault().post(new DeleteTicketsEvent(true));
            try {
                AppContext.get().getContentResolver().applyBatch(DataContentProvider.AUTHORITY, ops);
            } catch (RemoteException | OperationApplicationException e) {
                e.printStackTrace();
            }
            return null;
        }

        public void setIds(int[] ids) {
            this.ids = ids;
        }

        public int[] getIds() {
            return ids;
        }
    }

    static class CachedTicketsAsync extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... params) {
            Map<String, DataBaseManager.TicketHistoryContainer> tickets = DataBaseManager.getInstance().getAllTickets();
            if (tickets != null) {
                for (Map.Entry<String, DataBaseManager.TicketHistoryContainer> entry : tickets.entrySet()) {
                    processTickets(entry);
                }
            }
            return null;
        }

        private void processTickets(Map.Entry<String, DataBaseManager.TicketHistoryContainer> entry) {
            DataBaseManager.TicketHistoryContainer ticketsEntry = entry.getValue();
            if (ticketsEntry.tickets == null || ticketsEntry.tickets.size() == 0) {
                return;
            }
            int end = Math.min(Config.DEFAULT_NUMBER_OF_TICKETS_PAGE, ticketsEntry.tickets.size());

            List<TicketHistoryItem> ticketHistoryItems = ticketsEntry.tickets.subList(0, end);

            int[] ids = new int[ticketHistoryItems.size()];
            for (int i = 0; i < ticketHistoryItems.size(); i++) {
                TicketHistoryItem item = ticketHistoryItems.get(i);
                ids[i] = item.getColumnId();
            }
            DataBaseUtil.deleteTicketsById(ids);
        }
    }

    static class UploadTicketsAsync extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... params) {
            Map<String, DataBaseManager.TicketHistoryContainer> tickets = DataBaseManager.getInstance().getAllTickets();
            if (tickets != null) {
                for (Map.Entry<String, DataBaseManager.TicketHistoryContainer> entry : tickets.entrySet()) {
                    while (uploadTickets(entry)) {
                        Logger.e(TAG + "_### tickets uploading ###", "tickets batch uploaded");
                    }
                }
            } else {
                EventBus.getDefault().post(new UploadTicketsEvent(true));
            }
            return null;
        }

        private boolean uploadTickets(Map.Entry<String, DataBaseManager.TicketHistoryContainer> entry) {
            DataBaseManager.TicketHistoryContainer ticketsEntry = entry.getValue();
            if (ticketsEntry.tickets == null || ticketsEntry.tickets.isEmpty()) {
                return false;
            }
            int end = Math.min(Config.DEFAULT_NUMBER_OF_TICKETS_PAGE, ticketsEntry.tickets.size());
            List<TicketHistoryItem> ticketHistoryItems = ticketsEntry.tickets.subList(0, end);
            int[] ids = new int[ticketHistoryItems.size()];

            for (int i = 0; i < ticketHistoryItems.size(); i++) {
                TicketHistoryItem item = ticketHistoryItems.get(i);
                ids[i] = item.getColumnId();
            }
            String body = transformTicketsToBatchBody(ticketHistoryItems);

            Logger.e(TAG + "_" + ticketHistoryItems.size() + " #####", body);

            upload(ticketsEntry, body, ids);

            ticketHistoryItems.clear();

            return true;
        }

        private String transformTicketsToBatchBody(List<TicketHistoryItem> ticketHistoryItems) {
            StringBuilder sb = new StringBuilder(100000);
            sb.append("{\"actions\":[");
            int size = ticketHistoryItems.size();
            for (int i = 0; i < size; i++) {
                TicketHistoryItem item = ticketHistoryItems.get(i);
                sb.append(item.getActionJson());
                if (i != size - 1) {
                    sb.append(",");
                }
            }
            sb.append("]}");
            return sb.toString();
        }

        Disposable mSubscription;

        private void upload(DataBaseManager.TicketHistoryContainer argTC, String body, final int[] ticketIdsToDeleteOnSuccess) {
            PerfData arg = argTC.arg;//CommonArg.fromPreferences();
            BackendService.sBackendApi.batchUpload2(arg.getLang(), arg.getCommKey(), arg.getDeviceSuid(), arg.getUserSession(), arg.getUserSuid(), arg.getUserName(), arg.getFair(), arg.getBorder(), body).subscribeOn(Schedulers.io()).observeOn(Schedulers.io()).subscribe(new Observer<BatchUploadResponse>() {
                @Override
                public void onComplete() {
                    Logger.e(TAG + "_#### onCompleted", "Batch upload is finished");
                }

                @Override
                public void onError(Throwable e) {
                    if (e != null) {
                        EventBus.getDefault().post(new UploadTicketsEvent(false));
                        e.printStackTrace();
                    }
                    Logger.e(TAG + "_Error", "error uploading local codes");
                }

                @Override
                public void onSubscribe(@NonNull Disposable d) {
                    mSubscription = d;
                }

                @Override
                public void onNext(BatchUploadResponse batchUploadResponse) {
                    if (!batchUploadResponse.isResultOk()) {
                        onError(batchUploadResponse.throwable);
                    } else {
                        DataBaseUtil.deleteTicketsById(ticketIdsToDeleteOnSuccess);
                        EventBus.getDefault().post(new UploadTicketsEvent(true));
                    }
                }
            });
        }
    }

    public static class UploadTicketsEvent {
        public final Boolean isUpload;

        public UploadTicketsEvent(boolean isUpload) {
            this.isUpload = isUpload;
        }
    }

    public static class DeleteTicketsEvent {
        public final Boolean isUpload;

        public DeleteTicketsEvent(boolean isUpload) {
            this.isUpload = isUpload;
        }
    }
}
