package de.dimedis.mobileentry.fragments;

import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

import de.dimedis.lmlib.myinterfaces.Access;
import de.dimedis.mobileentry.R;
import de.dimedis.mobileentry.backend.BackendService;
import de.dimedis.mobileentry.backend.CommonArg;
import de.dimedis.mobileentry.backend.response.GetTicketHistoryResponse;
import de.dimedis.mobileentry.backend.response.GetTicketHistoryResponseContent;
import de.dimedis.mobileentry.backend.response.PerformEntryCheckoutResponseContent;
import de.dimedis.mobileentry.backend.response.RecordEntryResponse;
import de.dimedis.mobileentry.backend.response.VoidTicketResponse;
import de.dimedis.mobileentry.bbapi.BarcodeManager;
import de.dimedis.mobileentry.db.managers.DataBaseManager;
import de.dimedis.mobileentry.db.model.TicketHistoryItem;
import de.dimedis.mobileentry.model.Action;
import de.dimedis.mobileentry.model.Function;
import de.dimedis.mobileentry.model.LocalModeHelper;
import de.dimedis.mobileentry.model.Ticket;
import de.dimedis.mobileentry.ui.ScanActivity;
import de.dimedis.mobileentry.ui.view.BarcodeInputView;
import de.dimedis.mobileentry.util.AppContext;
import de.dimedis.mobileentry.util.CommonUtils;
import de.dimedis.mobileentry.util.LogType;
import de.dimedis.mobileentry.util.Logger;
import de.dimedis.mobileentry.util.PrefUtils;
import de.dimedis.mobileentry.util.UIUtils;
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.annotations.NonNull;
import io.reactivex.rxjava3.core.Observer;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class HistoryFragment2 extends BaseFragment {

    public static final String TICKET_CODE_EXTRA = "TICKET_CODE_EXTRA";
    private static final String REQUEST_ID_EXTRA = "REQUEST_ID_EXTRA";
    public static final String VOID_TICKET_EXTRA = "VOID_TICKET_EXTRA";
    private RecyclerView mTicketsRecyclerView;
    private TicketsAdapter mAdapter;
    private LinearLayout mProgress;
    private TextView mTicketInfoType;
    private TextView mTicketInfoArticleId;
    private BarcodeInputView mBarcodeInputView;
    private Button mForceEntry;
    private View mRoot;

    private String mCurrentTicketCode;

    private String mCurrentRequestId = "0";
    private Button mCancelButton;

    private boolean isVoidTicket = false;
    private Button mVoidTicket;
    private TextView mTitleText;
    private TextView mVoidTicketStatusText;
    private TextView mProgressText;

    @Override
    public void onResume() {
        super.onResume();
        BarcodeManager.getInstance().open(this.getActivity());
        Logger.i(Logger.FUNCTION_HISTORY_SWITCH_TAG, "History is opened");
        EventBus.getDefault().register(this);
    }

    @Override
    public void onPause() {
        super.onPause();
        UIUtils.hideSoftKeyboard(mRoot);
        BarcodeManager.getInstance().close(this.getActivity());
        Logger.i(Logger.FUNCTION_HISTORY_SWITCH_TAG, "History is closed");
        EventBus.getDefault().removeAllStickyEvents();
        EventBus.getDefault().unregister(this);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mRoot = inflater.inflate(R.layout.fragment_scan_history2, container, false);
        return mRoot;
    }

    @SuppressWarnings("unused")
    @Subscribe(threadMode = ThreadMode.MAIN)
//    @Subscribe
    public void onEventMainThread(BarcodeManager.BarcodeScannedEvent event) {
        Logger.i(HistoryFragment2.class.getSimpleName(), "Barcode scanned, code = " + event.barcode);
        mBarcodeInputView.setBarcode(event.barcode);
        mCurrentTicketCode = event.barcode;
        mProgress.setVisibility(View.VISIBLE);
        UIUtils.hideSoftKeyboard(mBarcodeInputView);
        startHistoryLoading();
    }

    @SuppressWarnings("unused")
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventMainThread(DataBaseManager.TicketsSelected event) {
        if (event.getTickets() == null || event.getTickets().size() == 0) {
            reportNoHistoryFoundForTicket();
        } else {
            refreshList(toActionsList(event.getTickets()));
        }
    }

    @SuppressWarnings("unused")
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventMainThread(RecordEntryResponse event) {
        if (event.isResultOk()) {
            mProgress.setVisibility(View.GONE);
            showOkVisualFeedback(event.content);
            closeFragment();
        } else {
            Toast.makeText(AppContext.get(), getLocalizedString(R.string.force_entry_failed), Toast.LENGTH_LONG).show();
            mProgressText.setText(getHistoryLoadingText());
            mProgress.setVisibility(View.GONE);
        }
    }

    private String getHistoryLoadingText() {
        return String.format("%s %s", getLocalizedString(R.string.SPINNER_LOADING), getLocalizedString(R.string.HISTORY_TITLE));
    }

    @SuppressWarnings("unused")
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventMainThread(GetTicketHistoryResponse event) {
        GetTicketHistoryResponseContent response = event.content;

        if (response == null || response.getListActions() == null || response.getListActions().size() == 0) {
            reportNoHistoryFoundForTicket();
        } else {
            try {
                String articleName = response.getTicket().articleName;
                String articleId = response.getTicket().articleId;

                mTicketInfoType.setText(articleName);
                mTicketInfoArticleId.setText(String.format("(%s)", articleId));
            } catch (Exception ignore) {
                Logger.e(TAG, "cant get article name and id");
            }
            refreshList(response.getListActions());
        }
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Bundle startArguments = getArguments();
        initCommonControls(view);
        initCancelButton(view);
        initVoidTicketButton(view, startArguments);
        initForceEntryButton(view);
        initBarCodeInputField(view);
        startProcessingBarCode(startArguments);
    }

    private void startProcessingBarCode(Bundle startArguments) {
        if (isVoidTicket) {
            mBarcodeInputView.setVisibility(View.VISIBLE);
            mProgress.setVisibility(View.GONE);
            return;
        }

        if (startArguments == null || startArguments.getString(TICKET_CODE_EXTRA, null) == null) {
            mBarcodeInputView.setVisibility(View.VISIBLE);
            mProgress.setVisibility(View.GONE);
        } else {
            startLoadingHistoryForTicket(startArguments);
        }
    }

    private void initVoidTicketButton(View view, Bundle startArguments) {
        isVoidTicket = startArguments != null && startArguments.getBoolean(VOID_TICKET_EXTRA);

        mTitleText.setText(isVoidTicket ? getLocalizedString(R.string.VOID_TICKET_TITLE) : getLocalizedString(R.string.HISTORY_TITLE));
        Logger.i(Logger.FUNCTION_HISTORY_SWITCH_TAG, isVoidTicket ? "history is in VOID ticket mode" : "history is in online history mode");
        mVoidTicket = (Button) view.findViewById(R.id.history_button_void_ticket);
        mVoidTicket.setVisibility(isVoidTicket ? View.VISIBLE : View.GONE);
        mVoidTicket.setOnClickListener(v -> voidTicketCode());
    }

    private void voidTicketCode() {
        if (mCurrentTicketCode == null || TextUtils.isEmpty(mCurrentTicketCode)) {
            return;
        }
        mVoidTicketStatusText.setVisibility(View.GONE);
        CommonArg arg = CommonArg.fromPreferences();
        BackendService.sBackendApi.voidTicket(arg.getLang(), arg.getCommKey(), arg.getDeviceSuid(), arg.getUserSession(), arg.getUserSuid(), arg.getUserName(), arg.getFair(), arg.getBorder(), mCurrentTicketCode).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new Observer<VoidTicketResponse>() {
            @Override
            public void onComplete() {
                mVoidTicketStatusText.setText(getLocalizedString(R.string.VOID_TICKET_SUCCESS_MESSAGE));
                mVoidTicketStatusText.setTextColor(ContextCompat.getColor(getActivity(), R.color.green_light));
                mVoidTicketStatusText.setVisibility(View.VISIBLE);
            }

            @Override
            public void onError(Throwable e) {
                mVoidTicketStatusText.setText(getLocalizedString(R.string.VOID_TICKET_FAIL_MESSAGE));
                mVoidTicketStatusText.setTextColor(Color.RED);
                mVoidTicketStatusText.setVisibility(View.VISIBLE);
            }

            @Override
            public void onSubscribe(@NonNull Disposable d) {
            }

            @Override
            public void onNext(VoidTicketResponse voidTicketResponse) {
                Logger.e(TAG, voidTicketResponse.toString());

                if (!voidTicketResponse.isResultOk()) {
                    onError(voidTicketResponse.throwable);
                }
            }
        });
    }

    private void initCancelButton(View view) {
        mCancelButton = (Button) view.findViewById(R.id.history_button_cancel);
        mCancelButton.setOnClickListener(v -> closeFragment());
    }

    private void initCommonControls(View view) {
        mTitleText = (TextView) view.findViewById(R.id.fragment_title);
        mVoidTicketStatusText = (TextView) view.findViewById(R.id.history_screen_ticket_void_status);
        mTicketsRecyclerView = (RecyclerView) view.findViewById(R.id.fragment_history_items_list);
        mTicketsRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mAdapter = new TicketsAdapter(null);
        mTicketsRecyclerView.setAdapter(mAdapter);
        mTicketInfoType = (TextView) view.findViewById(R.id.history_screen_ticket_type);
        mTicketInfoArticleId = (TextView) view.findViewById(R.id.history_screen_ticket_article_id);
        mProgress = (LinearLayout) view.findViewById(R.id.progress_bar_intermediate);
        mProgressText = (TextView) view.findViewById(R.id.progress_text);
        mProgressText.setText(getHistoryLoadingText());
    }

    private void initBarCodeInputField(View view) {
        mBarcodeInputView = (BarcodeInputView) view.findViewById(R.id.barcode_input_view);
        mBarcodeInputView.setOnBarcodeEnteredListener(barcode -> {
            Logger.i("History screen, manual code input", "Barcode entered manually, code = " + barcode);
            if (!TextUtils.isEmpty(barcode)) {
                mProgress.setVisibility(View.VISIBLE);
                UIUtils.hideSoftKeyboard(mBarcodeInputView);
                mCurrentTicketCode = barcode;
                startHistoryLoading();
            }
        });
    }

    private void initForceEntryButton(View rootView) {
        mForceEntry = (Button) rootView.findViewById(R.id.history_button_force_entry);

        if (Function.FORCE_ENTRY.isAvailable()) {
            mForceEntry.setVisibility(View.VISIBLE);
        } else {
            mForceEntry.setVisibility(View.GONE);
        }

        if (isVoidTicket) {
            mForceEntry.setVisibility(View.GONE);
            return;
        }

        mForceEntry.setOnClickListener(v -> {
            if (mCurrentTicketCode == null) {
                return;
            }

            mProgressText.setText(String.format("%s %s", getLocalizedString(R.string.SPINNER_PROGRESS), getLocalizedString(R.string.FORCE_ENTRY_BUTTON_LABEL)));
            if (PrefUtils.isLocalScanEnabled() || !CommonUtils.isInternetConnected()) {

                mProgress.setVisibility(View.VISIBLE);
                Access access = LocalModeHelper.recordEntry(mCurrentTicketCode);
                mProgress.setVisibility(View.GONE);
                PerformEntryCheckoutResponseContent responseContent = new PerformEntryCheckoutResponseContent();
                de.dimedis.lmlib.myinterfaces.Ticket ticket = access.getTicket();
                if (ticket != null) {
                    responseContent.ticket = new Ticket(access.getTicketCode(), ticket.getArticleId(), ticket.getArticleName());
                }

                de.dimedis.lmlib.myinterfaces.Action action = access.getAction();

                if (action != null) {
                    responseContent.action = new Action(action.getTimestamp(), action.getDateText(), action.getTimeText(), action.getDeviceSuid(), action.getDeviceName(), action.getType(), access.getAction().getMessage(), action.getTicketCode(), null);
                }

                showOkVisualFeedback(responseContent);
                closeFragment();
            } else {
                mProgress.setVisibility(View.VISIBLE);
                BackendService.recordEntry(mCurrentRequestId, mCurrentTicketCode);
            }
        });
    }

    private void showOkVisualFeedback(PerformEntryCheckoutResponseContent event) {
        String articleName = "";
        String articleId = "";
        String message = "Local scan, force entry";

        if (event != null && event.ticket != null) {
            if (event.action != null && event.action.message != null) {
                message = event.action.message;
            } else if (event.userMessage != null) {
                message = event.userMessage;
            }

            if (event.ticket.articleName != null) {
                articleName = event.ticket.articleName;
            }

            if (event.ticket.articleId != null) {
                articleId = event.ticket.articleId;
            }
        }

        EventBus.getDefault().postSticky(new ScanActivity.ShowScanCodeOkEvent(mCurrentTicketCode, articleName, articleId, message));
    }

    private void startHistoryLoading() {
        if (PrefUtils.isLocalScanEnabled() || !CommonUtils.isInternetConnected()) {
            reportNoHistoryFoundForTicket();
        } else {
            BackendService.getTicketHistory(mCurrentTicketCode);
        }
    }

    private void closeFragment() {
        getActivity().onBackPressed();
    }

    private void startLoadingHistoryForTicket(Bundle startArguments) {
        mCurrentTicketCode = startArguments.getString(TICKET_CODE_EXTRA);
        mCurrentRequestId = startArguments.getString(REQUEST_ID_EXTRA);
        if (mCurrentTicketCode != null) {
            startHistoryLoading();
        }
    }

    private @Nullable List<Action> toActionsList(@Nullable ArrayList<TicketHistoryItem> tickets) {

        if (tickets == null) return null;

        ArrayList<Action> actions = new ArrayList<>(tickets.size());

        for (TicketHistoryItem item : tickets) {
            actions.add(item.toAction());
        }

        return actions;
    }

    private void reportNoHistoryFoundForTicket() {
        TicketHistoryItem dummyItem = TicketHistoryItem.createDummyItem();
        dummyItem.getAction().setMessage(getLocalizedString(R.string.HISTORY_NOT_AVAILABLE_MESSAGE));
        ArrayList<TicketHistoryItem> dummyList = new ArrayList<>(1);
        dummyList.add(dummyItem);
        refreshList(toActionsList(dummyList));
    }

    private void refreshList(List<Action> actions) {
        mProgress.setVisibility(View.GONE);
        if (actions == null) {
            return;
        }
        for (Action action : actions) {
            Logger.w(TAG, "#### TICKET ACTION #### : " + action.ticketCode + " / " + action.location);
        }
        Collections.reverse(actions);
        mAdapter.setTickets(actions);
    }

    public class TicketsAdapter extends RecyclerView.Adapter {
        public class TicketViewHolder extends RecyclerView.ViewHolder {
            private final View mRootView;
            private final TextView mTicketDate;
            private final TextView mTicketTime;
            private final TextView mTicketDetails;
            private final TextView mTicketLocation;

            public TicketViewHolder(View view) {
                super(view);
                mRootView = view;
                mTicketDate = (TextView) mRootView.findViewById(R.id.history_item_date);
                mTicketTime = (TextView) mRootView.findViewById(R.id.history_item_time);
                mTicketDetails = (TextView) mRootView.findViewById(R.id.history_item_details);
                mTicketLocation = (TextView) mRootView.findViewById(R.id.history_item_location);
            }

            public void bind(TicketHistoryItem item, int position) {
                mRootView.setBackgroundColor(position % 2 == 0 ? Color.WHITE : getResources().getColor(R.color.grey_light)); // ContextCompat.getColor(getContext(), R.color.grey_light));
                mTicketDate.setText(item.getAction().getDateText());
                mTicketTime.setText(item.getAction().getTimeText());
                mTicketDetails.setText(TextUtils.isEmpty(item.getAction().getMessage()) ? item.getDenyReason() : item.getAction().getMessage());
                mTicketLocation.setText(item.getAction().getDeviceName());
                mRootView.setTag(item);
                try {
                    Logger.asyncLog(String.format("OnlineHistory - ticket: %s", item.getAction().getTicketCode()), String.format(Locale.getDefault(), "found (%d): %s", position, item), LogType.INFO);
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        }

        private List<Action> tickets;

        public TicketsAdapter(List<Action> tickets) {
            setTickets(tickets);
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_history_row, parent, false);
            return new TicketViewHolder(v);
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            ((TicketViewHolder) holder).bind(TicketHistoryItem.fromAction(null, getTickets().get(position)), position);
        }

        @Override
        public int getItemCount() {
            if (tickets == null || tickets.isEmpty()) {
                return 0;
            } else {
                return tickets.size();
            }
        }

        public void setTickets(List<Action> tickets) {
            if (tickets == null) return;
            this.tickets = new ArrayList<>(tickets);
            notifyDataSetChanged();
        }

        public List<Action> getTickets() {
            return tickets;
        }
    }
}
