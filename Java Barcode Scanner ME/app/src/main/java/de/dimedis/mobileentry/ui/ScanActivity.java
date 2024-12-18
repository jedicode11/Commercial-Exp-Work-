package de.dimedis.mobileentry.ui;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.CursorLoader;
import androidx.loader.content.Loader;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import de.dimedis.mobileentry.BuildConfig;
import de.dimedis.mobileentry.R;
import de.dimedis.mobileentry.backend.BackendService;
import de.dimedis.mobileentry.backend.response.BaseResponse;
import de.dimedis.mobileentry.backend.response.Keys;
import de.dimedis.mobileentry.backend.response.PerformCheckoutResponse;
import de.dimedis.mobileentry.backend.response.PerformEntryCheckoutResponseContent;
import de.dimedis.mobileentry.backend.response.PerformEntryResponse;
import de.dimedis.mobileentry.backend.response.UserPrefs;
import de.dimedis.mobileentry.bbapi.BarcodeManager;
import de.dimedis.mobileentry.db.content_provider.DataContentProvider;
import de.dimedis.mobileentry.db.managers.DataBaseManager;
import de.dimedis.mobileentry.db.model.TicketHistoryItem;
import de.dimedis.mobileentry.fragments.util.AlertFragment;
import de.dimedis.mobileentry.fragments.util.TicketUtils;
import de.dimedis.mobileentry.model.Action;
import de.dimedis.mobileentry.model.Function;
import de.dimedis.mobileentry.model.StatusManager;
import de.dimedis.mobileentry.model.Ticket;
import de.dimedis.mobileentry.ui.view.BarcodeInputView;
import de.dimedis.mobileentry.util.AppContext;
import de.dimedis.mobileentry.util.BarcodeUtil;
import de.dimedis.mobileentry.util.CommonUtils;
import de.dimedis.mobileentry.util.ConfigPrefHelper;
import de.dimedis.mobileentry.util.Logger;
import de.dimedis.mobileentry.util.PrefUtils;
import de.dimedis.mobileentry.util.SoundPlayerUtil;
import de.dimedis.mobileentry.util.SoundType;
import de.dimedis.mobileentry.util.UIHandler;
import de.dimedis.mobileentry.util.UIUtils;
import de.dimedis.mobileentry.util.UpdateUtil;
import de.dimedis.mobileentry.util.dynamicres.DynamicString;

public class ScanActivity extends BaseActivity implements LoaderManager.LoaderCallbacks<Cursor> {
    private static final String TAG = ScanActivity.class.getSimpleName();

    private static final String EXTRA_MODE = BuildConfig.APPLICATION_ID + ".Mode";
    private static final String STATE_MODE = "mode";
    private static final String STATE_SCAN_STATE = "scan_state";
    private static final String STATE_CHECKED_TICKET_CODE = "checked_ticket_code";

    private static final String STATUS_DENIED = "denied";
    private static final String STATUS_SUCCESS = "success";
    private Button mDetailsButton;

    private TextView mDefaultScanArea;

    private Button mBtnFunction1;
    private Button mBtnFunction2;
    private Button mBtnFunction3;
    private Button mBtnFunction4;
    private View mViewSpacer1;
    private View mViewSpacer2;
    private View mViewSpacer3;
    private String mLastTicketBeforeOnlineFail;

    private enum Mode {
        SCAN_ENTRY, SCAN_CHECKOUT
    }

    private enum State {
        DEFAULT, OK, ERROR
    }

    private final DefaultStateSwitchRunnable mDefaultStateSwitchRunnable = new DefaultStateSwitchRunnable();
    private ModelFragment mModelFragment;
    private Mode mMode;
    private State mState;
    private String mCheckedTicketCode;
    private View mRootLayout;
    private TextView mCodeCountTextView;
    private BarcodeInputView mBarcodeInputView;
    private FrameLayout mScanStateLayout;
    private TextView mModeTitleTextView;
    private ImageView mModeIconImageView;
    private View mScanStateDefaultLayout;
    private View mScanStateOkLayout;
    private View mScanStateErrorLayout;
    private ForegroundColorSpan mModeTitleColorSpan;
    private LinearLayout mContainer;
    private ProgressBar mProgressBar;

    public static void scanEntry(Context context) {
        start(context, Mode.SCAN_ENTRY);
    }

    public static void scanCheckout(Context context) {
        start(context, Mode.SCAN_CHECKOUT);
    }

    public static void start(@NonNull Context context) {
        start(context, null);
    }

    private static void start(@NonNull Context context, Mode mode) {
        Intent intent = new Intent(context, ScanActivity.class);
        intent.putExtra(EXTRA_MODE, mode);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan);

        FragmentManager fm = getSupportFragmentManager();
        mModelFragment = (ModelFragment) fm.findFragmentByTag(ModelFragment.TAG);
        if (mModelFragment == null) {
            mModelFragment = new ModelFragment();
            fm.beginTransaction().add(mModelFragment, ModelFragment.TAG).commit();
            fm.executePendingTransactions();
        }
        mRootLayout = findViewById(R.id.layout_root);
        mCodeCountTextView = findViewById(R.id.tv_code_count);
        mBarcodeInputView = findViewById(R.id.barcode_input_view);
        mScanStateLayout = findViewById(R.id.frame_scan_state);
        mModeTitleTextView = findViewById(R.id.tv_mode_title);
        mModeIconImageView = findViewById(R.id.iv_mode_icon);
        mScanStateDefaultLayout = findViewById(R.id.layout_scan_state_default);
        mScanStateOkLayout = findViewById(R.id.layout_scan_state_ok);
        mScanStateErrorLayout = findViewById(R.id.layout_scan_state_error);
        mDefaultScanArea = findViewById(R.id.default_scan_area);
        mContainer = findViewById(R.id.container);
        mProgressBar = findViewById(R.id.progressBarScan);

        mBtnFunction1 = findViewById(R.id.btn_function_1);
        mBtnFunction2 = findViewById(R.id.btn_function_2);
        mBtnFunction3 = findViewById(R.id.btn_function_3);
        mBtnFunction4 = findViewById(R.id.btn_function_4);

        mViewSpacer1 = findViewById(R.id.spacer_fkeys_1);
        mViewSpacer2 = findViewById(R.id.spacer_fkeys_2);
        mViewSpacer3 = findViewById(R.id.spacer_fkeys_3);

        mScanStateDefaultLayout.setOnClickListener(v -> {
            if (PrefUtils.getCameraReaderValue()) {
                BarcodeUtil.openCamReader(ScanActivity.this);
            } else if (AppContext.hasBBApi()) {
                BarcodeManager.getInstance().setTriggerOn(this);
            }
        });

        mScanStateOkLayout.setOnClickListener(v -> {
            if (PrefUtils.getCameraReaderValue()) {
                BarcodeUtil.openCamReader(ScanActivity.this);
            } else if (AppContext.hasBBApi()) {
                BarcodeManager.getInstance().setTriggerOn(this);
            }
        });

        mScanStateErrorLayout.setOnClickListener(v -> {
            if (PrefUtils.getCameraReaderValue()) {
                BarcodeUtil.openCamReader(ScanActivity.this);
            } else if (AppContext.hasBBApi()) {
                BarcodeManager.getInstance().setTriggerOn(this);
            }
        });

        mBarcodeInputView.setOnBarcodeEnteredListener(barcode -> {
            Logger.i(TAG, "Barcode entered manually, code = " + barcode);
            checkTicket(barcode);
        });

        mDetailsButton = findViewById(R.id.btn_details);
        if (Function.HISTORY.isAvailable()) {
            mDetailsButton.setOnClickListener(v -> {
                if (!TextUtils.isEmpty(mCheckedTicketCode)) {
                    MenuActivity.startForFunction(ScanActivity.this, Function.HISTORY, mCheckedTicketCode);
                }
            });
        } else {
            mDetailsButton.setVisibility(View.GONE);
        }

        Mode mode;
        State state;
        if (savedInstanceState != null) {
            mode = (Mode) savedInstanceState.getSerializable(STATE_MODE);
            state = (State) savedInstanceState.getSerializable(STATE_SCAN_STATE);
            mCheckedTicketCode = savedInstanceState.getString(STATE_CHECKED_TICKET_CODE);
        } else {
            mode = (Mode) getIntent().getSerializableExtra(EXTRA_MODE);
            if (mode == null) {
                mode = Mode.SCAN_ENTRY;
            }
            state = State.DEFAULT;
        }
        //noinspection ConstantConditions
        setMode(mode);
        setState(state);
        getSupportLoaderManager().initLoader(0, null, this);
    }

    @Override
    protected void onResume() {
        Logger.i(Logger.FUNCTION_SCAN_SWITCH_TAG, "Opening Scan activity");
        super.onResume();
        EventBus.getDefault().register(this);
        //   BarcodeManager.getInstance().close();
        BarcodeManager.getInstance().status(this);
        BarcodeManager.getInstance().open(this);
        updateModeTitleText();
        updateFunctionButtons();
        Log.i(TAG, "onResume() step2   ongoingRequestId: " + mModelFragment.getOngoingRequestId());
    }

    @Override
    protected void onPause() {
        Logger.i(Logger.FUNCTION_SCAN_SWITCH_TAG, "Closing Scan activity");
        super.onPause();
        EventBus.getDefault().removeAllStickyEvents();
        EventBus.getDefault().unregister(this);// >> to omDestroy()
        Log.i(TAG, "onPause() step2   ongoingRequestId: " + mModelFragment.getOngoingRequestId());
        mModelFragment.setOngoingRequestId(null);
        BarcodeManager.getInstance().close(this);

        if (isFinishing()) {
            UIHandler.get().removeCallbacks(mDefaultStateSwitchRunnable);
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);

        Mode mode = (Mode) intent.getSerializableExtra(EXTRA_MODE);
        if (mode != null) {
            setMode(mode);
        }
    }

    private void setMode(@NonNull Mode mode) {
        if (mMode == mode) {
            return;
        }
        mMode = mode;
        if (mode == Mode.SCAN_ENTRY) {
            Logger.i(Logger.FUNCTION_SCAN_SWITCH_TAG, "SCAN IS IN ENTRY MODE");
            mRootLayout.setBackgroundColor(ContextCompat.getColor(this, R.color.white));
            mCodeCountTextView.setTextColor(ContextCompat.getColor(this, R.color.grey_dark));
            mBarcodeInputView.setShouldDrawBorder(true);
            mBarcodeInputView.setKeyboardButtonBackgroundColor(R.color.grey_middle);
            mScanStateLayout.setForeground(ContextCompat.getDrawable(this, R.drawable.grey_dark_border));
            mScanStateLayout.setBackgroundResource(0);
            mModeTitleTextView.setText(getLocalizedString(R.string.SCAN_ENTRY_TITLE));
            mModeTitleTextView.setTextColor(ContextCompat.getColor(this, R.color.grey_dark));
            mModeTitleTextView.setBackgroundColor(ContextCompat.getColor(this, R.color.grey_lighter));
            mModeIconImageView.setImageResource(R.drawable.ic_scan_entry_dark_grey);
            changeFButtonsColor();
        } else {
            Logger.i(Logger.FUNCTION_SCAN_SWITCH_TAG, "SCAN IS IN CHECKOUT MODE");
            mRootLayout.setBackgroundColor(ContextCompat.getColor(this, R.color.blue));
            mBarcodeInputView.setShouldDrawBorder(false);
            mBarcodeInputView.setKeyboardButtonBackgroundColor(R.color.grey_dark);
            mScanStateLayout.setForeground(new ColorDrawable(Color.TRANSPARENT));
            mScanStateLayout.setBackgroundColor(ContextCompat.getColor(this, R.color.white));
            mModeTitleTextView.setText(getLocalizedString(R.string.SCAN_CHECKOUT_TITLE));
            mModeTitleTextView.setTextColor(ContextCompat.getColor(this, R.color.white));
            mModeTitleTextView.setBackgroundColor(ContextCompat.getColor(this, R.color.grey_dark));
            mModeIconImageView.setImageResource(R.drawable.ic_scan_checkout);
            changeFButtonsColor();
        }
        updateModeTitleText();
    }

    private void changeFButtonsColor() {
        int bgColor = R.color.white;
        int textColor = R.color.black;
        int spacerColor = R.color.black;

        if (mMode == Mode.SCAN_ENTRY) {
            bgColor = R.color.grey_dark;
            textColor = R.color.white;
            spacerColor = R.color.white;
        }

        setButtonColors(mBtnFunction1, bgColor, textColor);
        setButtonColors(mBtnFunction2, bgColor, textColor);
        setButtonColors(mBtnFunction3, bgColor, textColor);
        setButtonColors(mBtnFunction4, bgColor, textColor);

        mViewSpacer1.setBackgroundColor(ContextCompat.getColor(this, spacerColor));
        mViewSpacer2.setBackgroundColor(ContextCompat.getColor(this, spacerColor));
        mViewSpacer3.setBackgroundColor(ContextCompat.getColor(this, spacerColor));
        updateFunctionButtons();
    }

    private void updateModeTitleText() {
        CharSequence title = getLocalizedString(mMode == Mode.SCAN_ENTRY ? R.string.SCAN_ENTRY_TITLE : R.string.SCAN_CHECKOUT_TITLE);
        StatusManager statusManager = StatusManager.getInstance();
        if (!statusManager.isOnline()) {
            if (mModeTitleColorSpan == null) {
                mModeTitleColorSpan = new ForegroundColorSpan(ContextCompat.getColor(this, R.color.red));
            }
            SpannableString spannableTitle = new SpannableString(title + " (" + statusManager.getStatus().getText(statusManager.getStatus()) + ")");
            spannableTitle.setSpan(mModeTitleColorSpan, title.length(), spannableTitle.length(), 0);
            title = spannableTitle;
        }
        mModeTitleTextView.setText(title);
    }

    private void setState(@NonNull State state) {
        setState(state, null, false);
    }

    private void setState(@NonNull State state, @Nullable PerformEntryCheckoutResponseContent responseContent, boolean force) {
        mDetailsButton.setVisibility(Function.HISTORY.isAvailable() && CommonUtils.isInternetConnected() ? View.VISIBLE : View.GONE);
        if (mState == state && !force) {
            return;
        }
        mState = state;
        mScanStateDefaultLayout.setVisibility(state == State.DEFAULT ? View.VISIBLE : View.GONE);
        mScanStateOkLayout.setVisibility(state == State.OK ? View.VISIBLE : View.GONE);
        mScanStateOkLayout.findViewById(R.id.scan_reduced_asterisk).setVisibility((responseContent != null && responseContent.isReducedTicket() && state == State.OK) ? View.VISIBLE : View.GONE);
        mScanStateErrorLayout.setVisibility(state == State.ERROR ? View.VISIBLE : View.GONE);

        if (responseContent != null && responseContent.action != null) {
            if (state == State.OK) {
                ((TextView) mScanStateOkLayout.findViewById(R.id.tv_message)).setText(generateText(responseContent));
            } else if (state == State.ERROR) {
                ((TextView) mScanStateErrorLayout.findViewById(R.id.tv_message)).setText(generateText(responseContent));
            }
        }
        if (state == State.OK || state == State.ERROR) {
            // Post Runnable to switch to Default state after some delay without scanning codes.
            Handler handler = UIHandler.get();
            handler.removeCallbacks(mDefaultStateSwitchRunnable);
            handler.postDelayed(mDefaultStateSwitchRunnable, state == State.OK ? PrefUtils.getScanOkSwitchDelay() : PrefUtils.getScanDeniedSwitchDelay());
        } else {
            mBarcodeInputView.clear();
        }
    }

    private String generateText(PerformEntryCheckoutResponseContent responseContent) {
    /*
            https://jira.softeq.com/browse/MENTRY-12
            At the moment, for message the retval of element action.message is used - here we have to see if 'user_message' should be used if available.
            'ticket_info.type' is not filled at all - this should be assembled with
            ticket.article_name (ticket.article_id)
    */
        String textMessage = "%s\n%s (%s)";
        // NOTE: empty by default for Force Entry from HistoryFragment2
        String articleId;
        String articleName;
        // NOTE: action is already checked for null in calling method
        String message = responseContent.action.message;
        String userMessage = responseContent.userMessage;

        if (message == null || TextUtils.isEmpty(message)) {
            message = "";
        }
        if (userMessage == null || TextUtils.isEmpty(userMessage)) {
            userMessage = message;
        }
        if (responseContent.ticket != null) {
            articleId = responseContent.ticket.articleId == null ? "n/a" : responseContent.ticket.articleId;
            articleName = responseContent.ticket.articleName == null ? "n/a" : responseContent.ticket.articleName;
        } else {
            articleId = "n/a";
            articleName = "n/a";
        }
        textMessage = String.format(textMessage, userMessage, articleName, articleId);
        return textMessage;
    }

    private void updateFunctionButtons() {
        Keys keys = null;
        UserPrefs userPrefs = ConfigPrefHelper.getUserPrefs();
        if (userPrefs != null) {
            keys = userPrefs.getKeys();
        }
        setupFunctionButton(mBtnFunction1, keys != null ? Function.fromName(keys.getF1()) : null);
        setupFunctionButton(mBtnFunction2, keys != null ? Function.fromName(keys.getF2()) : null);
        setupFunctionButton(mBtnFunction3, keys != null ? Function.fromName(keys.getF3()) : null);
        setupFunctionButton(mBtnFunction4, keys != null ? Function.fromName(keys.getF4()) : null);
    }

    private void setupFunctionButton(@NonNull Button button, final Function function) {
        if (function == null) {
//            button.setVisibility(View.INVISIBLE);
            button.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
            button.setText("");
            button.setOnClickListener(null);
            return;
        }
        button.setVisibility(View.VISIBLE);
        button.setText(getLocalizedString(function.titleResId));

        if (mMode == Mode.SCAN_CHECKOUT) {
            Drawable icon = ContextCompat.getDrawable(this, function.iconResId).mutate();
            icon.setColorFilter(Color.BLACK, PorterDuff.Mode.MULTIPLY);
            button.setCompoundDrawablesWithIntrinsicBounds(null, icon, null, null);
        } else {
            button.setCompoundDrawablesWithIntrinsicBounds(0, function.iconResId, 0, 0);
        }

        button.setOnClickListener(v -> {
            if (function.isAvailable()) {
                switch (function) {
                    case SCAN_ENTRY:
                        setMode(Mode.SCAN_ENTRY);
                        break;
                    case SCAN_CHECKOUT:
                        setMode(Mode.SCAN_CHECKOUT);
                        break;
                    case MENU:
                        MenuActivity.start(ScanActivity.this);
                        break;
                    default:
                        MenuActivity.startForFunction(ScanActivity.this, function);
                }
            } else {
                UIUtils.showLongToast(R.string.message_function_not_available);
            }
        });
    }

    private void setButtonColors(Button button, int backgroundColor, int textColor) {
        button.setBackgroundColor(ContextCompat.getColor(this, backgroundColor));
        button.setTextColor(ContextCompat.getColor(this, textColor));
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putSerializable(STATE_MODE, mMode);
        outState.putSerializable(STATE_SCAN_STATE, mState);
        outState.putString(STATE_CHECKED_TICKET_CODE, mCheckedTicketCode);
    }

    @Override
    public void onBackPressed() {
        // Block Back button in the kiosk mode.
//        if (!PrefUtils.isKioskModeEnabled()) {
        super.onBackPressed();
//        }
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new CursorLoader(this, DataContentProvider.TICKETS_URI, new String[]{"COUNT(*)"}, null, null, null);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        int codeCount = 0;
        if (cursor != null && cursor.moveToNext()) {//crash ? // java.lang.IllegalStateException: attempt to re-open an already-closed object: SQLiteQuery: SELECT COUNT(*) FROM TABLE_TICKETS_HISTORY
            codeCount = cursor.getInt(0);
        }

        CharSequence codeCountText = codeCount == 1 ? DynamicString.getInstance().getString(R.string.SCAN_LOCAL_RECORDS_ONE) : DynamicString.getInstance().getString(R.string.SCAN_LOCAL_RECORDS_MANY);
        mCodeCountTextView.setText(codeCount + " " + codeCountText);
    }

    @Override
    public void onLoaderReset(@NonNull Loader<Cursor> loader) {
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventMainThread(StatusManager.StatusChangedEvent event) {
        updateModeTitleText();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventMainThread(ShowScanCodeOkEvent event) {
        showOkAlert(event);
    }

    private void showOkAlert(ShowScanCodeOkEvent event) {
        Logger.e(TAG, "history force entry ok: " + event.getTicketCode());
        State state = State.OK;
        EventBus.getDefault().removeStickyEvent(event);
        PerformEntryCheckoutResponseContent responseContent = new PerformEntryCheckoutResponseContent();
        responseContent.ticket = new Ticket(event.getTicketCode(), event.getTicketArticleId(), event.getTicketArticleName());
        responseContent.action = new Action(null, null, null, null, null, null, event.getMessage(), event.getTicketCode(), null);
        setState(state, responseContent, true);
        SoundPlayerUtil.getInstance().playSoundAsync(SoundType.TICKET_SCAN_STATUS_OK);
        mCheckedTicketCode = event.getTicketCode();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventMainThread(BarcodeManager.BarcodeScannedEvent event) {
        Logger.i(TAG, "Barcode scanned, code = " + event.barcode);
        mBarcodeInputView.setBarcode(event.barcode);
        checkTicket(event.barcode);
        EventBus.getDefault().removeAllStickyEvents();
    }

    private void checkTicket(String code) {
        if (TextUtils.isEmpty(code)) {
            return;
        }
        // Check if the function to scan entry/checkout is available.
        if ((mMode == Mode.SCAN_ENTRY && !Function.SCAN_ENTRY.isAvailable()) || (mMode == Mode.SCAN_CHECKOUT && !Function.SCAN_CHECKOUT.isAvailable())) {
            UIUtils.showLongToast(R.string.message_function_not_available);
            return;
        }
        // Refuse the ticket if there is an ongoing request.
        if (!TextUtils.isEmpty(mModelFragment.getOngoingRequestId())) {
            Logger.i(TAG, "Refuse the ticket because there is an ongoing request");
            SoundPlayerUtil.getInstance().playSoundAsync(SoundType.CODE_SCAN_REFUSED_OR_ALREADY_PROCESSING);
            return;
        }
        if (PrefUtils.getPermScanModeValue() && TicketUtils.checkDoubleScanTicket(code)) {
            startScanAgainIfPermanentScanModeEnabled();
            return;
        } else if (!PrefUtils.getPermScanModeValue() && TicketUtils.checkDoubleScanTicket(code)) {
            return;
        }
        mLastTicketBeforeOnlineFail = code;
        if (PrefUtils.isLocalScanEnabled() || !CommonUtils.isInternetConnected() || StatusManager.getInstance().getStatus().equals(StatusManager.Status.LOCAL_SCAN)) {
            Log.d(TAG, "we are in local scan mode");
            checkTicketLocally(code);
            mLastTicketBeforeOnlineFail = "";
        } else {
            checkTicketOnline(code);
        }
    }


    private void checkTicketOnline(@NonNull String barcode) {
        String requestId = BackendService.getNextRequestId();
        Logger.i(TAG, "Check ticket online, code = " + barcode + ", request_id = " + requestId);
        if (StatusManager.getInstance().isOnline()) {
            if (mMode == Mode.SCAN_ENTRY) {
                BackendService.performEntry(requestId, barcode);
            } else {
                BackendService.performCheckout(requestId, barcode);
            }
        }

        // Start the timer for the ongoing request to check the ticket locally
        // if the server response takes too long.
        UIHandler.get().postDelayed(new OnlineTicketCheckTimeoutRunnable(requestId, barcode), PrefUtils.getOfflineDetectTimeout());
        mModelFragment.setOngoingRequestId(requestId);
    }

    private void checkTicketLocally(@NonNull String barcode) {
        Logger.i(TAG, "Check ticket locally, code = " + barcode);

        if (TextUtils.isEmpty(barcode)) {
            Logger.i(TAG, "Check ticket locally, code is empty");
            return;
        }

        TicketHistoryItem access;
        if (mMode == Mode.SCAN_ENTRY) {
            access = TicketHistoryItem.performEntry(barcode);
        } else {
            access = TicketHistoryItem.performCheckout(barcode);
        }
        if (access != null) {
            Logger.i(TAG, "Ticket is checked locally, result = " + access);
            showTicketCheckResult(PerformEntryCheckoutResponseContent.fromLmLib(access.getAccessImpl()));
            DataBaseManager.getInstance().saveTicketActionsAsyncTask(access);//saveTicketJsonAsync(access.toJsonString());
        } else {
            AlertFragment.show(getLocalizedString(R.string.message_lmlib_error), null, getSupportFragmentManager());
        }
    }

    private void showTicketCheckResult(@NonNull PerformEntryCheckoutResponseContent content) {
        State state = content.isStatusSuccess() ? State.OK : State.ERROR;
        setState(state, content, true);
        startScanAgainIfPermanentScanModeEnabled();
        if (state == State.OK) {
            if (content.isReducedTicket()) {
                SoundPlayerUtil.getInstance().playSoundAsync(SoundType.TICKET_SCAN_STATUS_OK_REDUCED_TICKET);
            } else {
                SoundPlayerUtil.getInstance().playSoundAsync(SoundType.TICKET_SCAN_STATUS_OK);
            }
        } else {
            SoundPlayerUtil.getInstance().playSoundAsync(SoundType.TICKET_SCAN_STATUS_NOT_OK);
        }
        mCheckedTicketCode = content.ticketCode;
    }

    private void startScanAgainIfPermanentScanModeEnabled() {
        if (PrefUtils.getPermScanModeValue()) {
            if (PrefUtils.getCameraReaderValue()) {
                BarcodeUtil.openCamReader(ScanActivity.this);
            } else if (AppContext.hasBBApi()) {
                BarcodeManager.getInstance().setTriggerOn(this);
            }
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventMainThread(PerformEntryResponse response) {
        processEntryCheckoutResponse(response);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventMainThread(BarcodeManager.BarcodeScannerTimeoutNoDecodingEvent response) {
        startScanAgainIfPermanentScanModeEnabled();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventMainThread(PerformCheckoutResponse response) {
        processEntryCheckoutResponse(response);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventMainThread(OnFKeysUpdateEvent response) {
        updateFunctionButtons();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventMainThread(OnlineTicketCheckTimeoutEvent event) {
        if (TextUtils.equals(event.requestId, mModelFragment.getOngoingRequestId())) {
            Logger.i(TAG, "Timeout for online ticket check, request_id = " + event.requestId);
            mModelFragment.setOngoingRequestId(null);
            checkTicketLocally(event.barcode);
        } else {
            Logger.i(TAG, "Ignore timeout for online ticket check, request_id = " + event.requestId);
//            DataBaseUtil.uploadCachedTickets();
//            DataBaseUtil.uploadCachedOfflineSessions();
        }
    }

    private void processEntryCheckoutResponse(@NonNull BaseResponse response) {
        if (response.error != null && response.error.code.equals(BackendService.ERROR_CODE_INVALID_BORDER)) {

            AlertFragment.show(response.error, () -> {
                mContainer.setVisibility(View.GONE);
                mProgressBar.setVisibility(View.VISIBLE);
                UpdateUtil.loadOfflineConfig();
            }, getSupportFragmentManager());
            return;
        } else if (response.error != null && response.error.code.equals(BackendService.ERROR_CODE_COMM_KEY_INVALID)) {
            AlertFragment.show(response.error, null, getSupportFragmentManager());
            return;
        }
        if (TextUtils.equals(response.requestId, mModelFragment.getOngoingRequestId())) {
            Logger.i(TAG, "Received the response for online ticket check: " + response);

            mModelFragment.setOngoingRequestId(null);
            if (response.isResultOk()) {
                showTicketCheckResult((PerformEntryCheckoutResponseContent) response.content);
            } else {
//                AlertFragment.show(response.error, null, getSupportFragmentManager());
                checkTicketLocally(mLastTicketBeforeOnlineFail);
            }
        } else {
            Logger.i(TAG, "Ignore the response for online ticket check: " + response);
        }
    }

    public static class ModelFragment extends Fragment {
        public static final String TAG = ModelFragment.class.getSimpleName();

        protected String mOngoingRequestId;

        public String getOngoingRequestId() {
            Log.i(TAG, "ongoingRequestId:" + mOngoingRequestId);
            return mOngoingRequestId;
        }

        public void setOngoingRequestId(String ongoingRequestId) {
            Log.i(TAG, "ongoingRequestId:" + ongoingRequestId + " old_ongoingRequestId:" + mOngoingRequestId);
            this.mOngoingRequestId = ongoingRequestId;
        }

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setRetainInstance(true);   // TODO Deprecated
        }
    }

    private static class OnlineTicketCheckTimeoutRunnable implements Runnable {
        private final String mRequestId;
        private final String mBarcode;

        public OnlineTicketCheckTimeoutRunnable(String requestId, String barcode) {
            mRequestId = requestId;
            mBarcode = barcode;
        }

        @Override
        public void run() {
            EventBus.getDefault().post(new OnlineTicketCheckTimeoutEvent(mRequestId, mBarcode));
        }
    }

    private static class OnlineTicketCheckTimeoutEvent {
        public final String requestId;
        public final String barcode;

        public OnlineTicketCheckTimeoutEvent(String requestId, String barcode) {
            this.requestId = requestId;
            this.barcode = barcode;
        }
    }

    public static class OnFKeysUpdateEvent {
    }

    private class DefaultStateSwitchRunnable implements Runnable {
        @Override
        public void run() {
            setState(State.DEFAULT);
//            startScanAgainIfPermanentScanModeEnabled();
        }
    }

    public static class ShowScanCodeOkEvent {
        private String ticketCode;
        private String ticketArticleName;
        private String ticketArticleId;
        private String message;

        public ShowScanCodeOkEvent(String ticketCode, String articleName, String articleId, String message) {
            setTicketCode(ticketCode);
            setTicketArticleName(articleName);
            setTicketArticleId(articleId);
            setMessage(message);
        }

        public void setTicketCode(String ticketCode) {
            this.ticketCode = ticketCode;
        }

        public String getTicketCode() {
            return ticketCode;
        }

        public void setTicketArticleName(String ticketArticleName) {
            this.ticketArticleName = ticketArticleName;
        }

        public String getTicketArticleName() {
            return ticketArticleName;
        }

        public void setTicketArticleId(String ticketArticleId) {
            this.ticketArticleId = ticketArticleId;
        }

        public String getTicketArticleId() {
            return ticketArticleId;
        }

        public void setMessage(String message) {

            this.message = message;
        }

        public String getMessage() {
            return message;
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventMainThread(UpdateUtil.LocationFragmentEvent event) {
        mContainer.setVisibility(View.VISIBLE);
        mProgressBar.setVisibility(View.GONE);
        if (event.isLoaded) {
            MenuActivity.startForFunction(ScanActivity.this, Function.CHOOSE_BORDER);
        }
    }
}
