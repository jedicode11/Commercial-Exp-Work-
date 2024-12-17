package de.dimedis.mobileentry.ui

import android.content.Context
import android.content.Intent
import android.database.Cursor
import android.graphics.Color
import android.graphics.PorterDuff
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.os.Handler
import android.text.SpannableString
import android.text.TextUtils
import android.text.style.ForegroundColorSpan
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.LinearLayout
import android.widget.ProgressBar
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.loader.app.LoaderManager
import androidx.loader.content.CursorLoader
import androidx.loader.content.Loader
import de.dimedis.mobileentry.BuildConfig
import de.dimedis.mobileentry.R
import de.dimedis.mobileentry.backend.BackendServiceConst
import de.dimedis.mobileentry.backend.BackendServiceUtil
import de.dimedis.mobileentry.backend.response.*
import de.dimedis.mobileentry.bbapi.BarcodeManager
import de.dimedis.mobileentry.databinding.ActivityScanBinding
import de.dimedis.mobileentry.db.content_provider.DataContentProvider
import de.dimedis.mobileentry.db.managers.DataBaseManager
import de.dimedis.mobileentry.db.model.TicketHistoryItem
import de.dimedis.mobileentry.fragments.util.AlertFragment
import de.dimedis.mobileentry.fragments.util.TicketUtils
import de.dimedis.mobileentry.model.Action
import de.dimedis.mobileentry.model.Function
import de.dimedis.mobileentry.model.StatusManager
import de.dimedis.mobileentry.model.Ticket
import de.dimedis.mobileentry.ui.view.BarcodeInputView
import de.dimedis.mobileentry.util.*
import de.dimedis.mobileentry.util.AppContext.hasBBApi
import de.dimedis.mobileentry.util.BarcodeUtil.openCamReader
import de.dimedis.mobileentry.util.CommonUtils.isInternetConnected
import de.dimedis.mobileentry.util.ConfigPrefHelper.getUserPrefs
import de.dimedis.mobileentry.util.Logger.i
import de.dimedis.mobileentry.util.PrefUtils.getPermScanModeValue
import de.dimedis.mobileentry.util.PrefUtils.isLocalScanEnabled
import de.dimedis.mobileentry.util.dynamicres.DynamicString
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

class ScanActivity : BaseActivity(), LoaderManager.LoaderCallbacks<Cursor?> {

    companion object {
        private val TAG = ScanActivity::class.java.simpleName

        private const val EXTRA_MODE = BuildConfig.APPLICATION_ID + ".Mode"
        private const val STATE_MODE = "mode"
        private const val STATE_SCAN_STATE = "scan_state"
        private const val STATE_CHECKED_TICKET_CODE = "checked_ticket_code"

        private const val STATUS_DENIED = "denied"
        private const val STATUS_SUCCESS = "success"
        fun scanEntry(context: Context) {
            start(context, Mode.SCAN_ENTRY)
        }

        fun scanCheckout(context: Context) {
            start(context, Mode.SCAN_CHECKOUT)
        }

        fun start(context: Context) {
            start(context, null)
        }

        private fun start(context: Context, mode: Mode?) {
            val intent = Intent(context, ScanActivity::class.java)
            intent.putExtra(EXTRA_MODE, mode)
            context.startActivity(intent)
        }

        fun builder(): Builder {
            return Builder()
        }
    }

    class Builder {
        fun build(): ScanActivity {
            return ScanActivity()
        }
    }

    private var mLastTicketBeforeOnlineFail: String? = null

    private enum class Mode {
        SCAN_ENTRY, SCAN_CHECKOUT
    }

    private enum class State {
        DEFAULT, OK, ERROR
    }

    private val mDefaultStateSwitchRunnable: DefaultStateSwitchRunnable = DefaultStateSwitchRunnable()
    private var mModelFragment: ModelFragment? = null

    private var mMode: Mode? = null
    private var mState: State? = null
    private var mCheckedTicketCode: String? = null

    private var mModeTitleColorSpan: ForegroundColorSpan? = null
    private var mContainer: LinearLayout? = null
    private var mProgressBar: ProgressBar? = null


    private lateinit var binding: ActivityScanBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityScanBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val fm = supportFragmentManager
        mModelFragment = fm.findFragmentByTag(ModelFragment.TAG) as ModelFragment?
        if (mModelFragment == null) {
            mModelFragment = ModelFragment()
            fm.beginTransaction().add(mModelFragment!!, ModelFragment.TAG).commit()
            fm.executePendingTransactions()
        }

        mContainer = findViewById(R.id.container)
        mProgressBar = findViewById(R.id.progressBarScan)

        binding.layoutScanStateDefault.defaultScanArea.setOnClickListener {
            if (PrefUtils.getCameraReaderValue()) {
                openCamReader(this@ScanActivity)
            } else if (hasBBApi()) {
                BarcodeManager.instance.setTriggerOn(this)
            }
        }

        binding.layoutScanStateOk.scanReducedAsterisk.setOnClickListener {
            if (PrefUtils.getCameraReaderValue()) {
                openCamReader(this@ScanActivity)
            } else if (hasBBApi()) {
                BarcodeManager.instance.setTriggerOn(this)
            }
        }

        binding.layoutScanStateError.btnDetails.setOnClickListener {
            if (PrefUtils.getCameraReaderValue()) {
                openCamReader(this@ScanActivity)
            } else if (hasBBApi()) {
                BarcodeManager.instance.setTriggerOn(this)
            }
        }

        binding.barcodeInputView.setOnBarcodeEnteredListener(object :
            BarcodeInputView.OnBarcodeEnteredListener {
            override fun onBarcodeEntered(barcode: String?) {
                i(TAG, "Barcode entered manually, code = $barcode")
                if (barcode != null) {
                    checkTicket(barcode)
                }
            }
        })

        if (Function.HISTORY.isAvailable) {
            binding.layoutScanStateError.btnDetails.setOnClickListener {
                if (!TextUtils.isEmpty(mCheckedTicketCode)) {
                    MenuActivity.startForFunction(this@ScanActivity, Function.HISTORY, mCheckedTicketCode)
                }
            }
        } else {
            binding.layoutScanStateError.btnDetails.visibility = View.GONE
        }

        var mode: Mode?
        val state: State?
        if (savedInstanceState != null) {
            mode = savedInstanceState.getSerializable(STATE_MODE) as Mode?
            state = savedInstanceState.getSerializable(STATE_SCAN_STATE) as State?
            mCheckedTicketCode = savedInstanceState.getString(STATE_CHECKED_TICKET_CODE)
        } else {
            mode = intent.getSerializableExtra(EXTRA_MODE) as Mode?
            if (mode == null) {
                mode = Mode.SCAN_ENTRY
            }
            state = State.DEFAULT
        }

        setMode(mode!!)
        setState(state!!)
//      TODO Deprecated
        supportLoaderManager.initLoader(0, null, this)
    }


    override fun onResume() {
        i(Logger.FUNCTION_SCAN_SWITCH_TAG, "Opening Scan activity")
        super.onResume()
        EventBus.getDefault().register(this)
//        BarcodeManager.instance.close()
        BarcodeManager.instance.status(this)
        BarcodeManager.instance.open(this)
        updateModeTitleText()
        updateFunctionButtons()
        Log.i(TAG, "onResume() step2   ongoingRequestId: " + mModelFragment!!.ongoingRequestId)
    }

    override fun onPause() {
        i(Logger.FUNCTION_SCAN_SWITCH_TAG, "Closing Scan activity")
        super.onPause()
        EventBus.getDefault().removeAllStickyEvents()
        EventBus.getDefault().unregister(this) // >> to omDestroy()
        Log.i(TAG, "onPause() step2   ongoingRequestId: " + mModelFragment!!.ongoingRequestId)
        mModelFragment!!.ongoingRequestId = null
        BarcodeManager.instance.close(this)
        if (isFinishing) {
            UIHandler.get().removeCallbacks(mDefaultStateSwitchRunnable)
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        val mode = intent.getSerializableExtra(EXTRA_MODE) as Mode?
        mode?.let { setMode(it) }
    }

    private fun setMode(mode: Mode) {
        if (mMode == mode) {
            return
        }
        mMode = mode
        if (mode == Mode.SCAN_ENTRY) {
            i(Logger.FUNCTION_SCAN_SWITCH_TAG, "SCAN IS IN ENTRY MODE")

            binding.layoutRoot.setBackgroundColor(ContextCompat.getColor(this, R.color.white))
            binding.tvCodeCount.setTextColor(ContextCompat.getColor(this, R.color.grey_dark))
            binding.barcodeInputView.setShouldDrawBorder(true)
            binding.barcodeInputView.setKeyboardButtonBackgroundColor(R.color.grey_middle)
            binding.frameScanState.foreground = ContextCompat.getDrawable(this, R.drawable.grey_dark_border)
            binding.frameScanState.setBackgroundResource(0)
            binding.tvModeTitle.text = getLocalizedString(R.string.SCAN_ENTRY_TITLE)
            binding.tvModeTitle.setTextColor(ContextCompat.getColor(this, R.color.grey_dark))
            binding.tvModeTitle.setBackgroundColor(ContextCompat.getColor(this, R.color.grey_lighter))
            binding.ivModeIcon.setImageResource(R.drawable.ic_scan_entry_dark_grey)
            changeFButtonsColor()
        } else {
            i(Logger.FUNCTION_SCAN_SWITCH_TAG, "SCAN IS IN CHECKOUT MODE")
            binding.layoutRoot.setBackgroundColor(ContextCompat.getColor(this, R.color.blue))
            binding.barcodeInputView.setShouldDrawBorder(false)
            binding.barcodeInputView.setKeyboardButtonBackgroundColor(R.color.grey_dark)
            binding.frameScanState.foreground = ColorDrawable(Color.TRANSPARENT)
            binding.frameScanState.setBackgroundColor(ContextCompat.getColor(this, R.color.white))
            binding.tvModeTitle.text = getLocalizedString(R.string.SCAN_CHECKOUT_TITLE)
            binding.tvModeTitle.setTextColor(ContextCompat.getColor(this, R.color.white))
            binding.tvModeTitle.setBackgroundColor(ContextCompat.getColor(this, R.color.grey_dark))
            binding.ivModeIcon.setImageResource(R.drawable.ic_scan_checkout)
            changeFButtonsColor()
        }
        updateModeTitleText()
    }

    private fun changeFButtonsColor() {
        var bgColor = R.color.white
        var textColor = R.color.black
        var spacerColor = R.color.black
        if (mMode == Mode.SCAN_ENTRY) {
            bgColor = R.color.grey_dark
            textColor = R.color.white
            spacerColor = R.color.white
        }
        setButtonColors(binding.btnFunction1, bgColor, textColor)
        setButtonColors(binding.btnFunction2, bgColor, textColor)
        setButtonColors(binding.btnFunction3, bgColor, textColor)
        setButtonColors(binding.btnFunction4, bgColor, textColor)

        binding.spacerFkeys1.setBackgroundColor(ContextCompat.getColor(this, spacerColor))
        binding.spacerFkeys2.setBackgroundColor(ContextCompat.getColor(this, spacerColor))
        binding.spacerFkeys3.setBackgroundColor(ContextCompat.getColor(this, spacerColor))
        updateFunctionButtons()
    }

    private fun updateModeTitleText() {
        var title: String? =
            getLocalizedString(if (mMode == Mode.SCAN_ENTRY) R.string.SCAN_ENTRY_TITLE else R.string.SCAN_CHECKOUT_TITLE)
        // Add the status to the title, if it isn't Online.
        val statusManager = StatusManager.getInstance()
        if (!statusManager!!.isOnline()) {
            if (mModeTitleColorSpan == null) {
                mModeTitleColorSpan = ForegroundColorSpan(ContextCompat.getColor(this, R.color.red))
            }
            val spannableTitle = SpannableString("$title (" + statusManager.getStatus().getText(statusManager.getStatus()) + ")")
            spannableTitle.setSpan(mModeTitleColorSpan, title!!.length, spannableTitle.length, 0)
            title = spannableTitle.toString()
        }
        binding.tvModeTitle.text = title
    }

    private fun setState(state: State) {
        setState(state, null, false)
    }

    private fun setState(state: State, responseContent: PerformEntryCheckoutResponseContent?, force: Boolean) {
        binding.layoutScanStateError.btnDetails.visibility = if (Function.HISTORY.isAvailable && isInternetConnected()) View.VISIBLE else View.GONE
        if (mState == state && !force) {
            return
        }
        mState = state
        binding.layoutScanStateDefault.root.visibility = if (state == State.DEFAULT) View.VISIBLE else View.GONE
        binding.layoutScanStateOk.root.visibility = if (state == State.OK) View.VISIBLE else View.GONE
        binding.layoutScanStateOk.scanReducedAsterisk.visibility = if (responseContent!!.isReducedTicket() && state == State.OK) View.VISIBLE else View.GONE
        binding.layoutScanStateError.root.visibility = if (state == State.ERROR) View.VISIBLE else View.GONE
        if (responseContent.action != null) {
            if (state == State.OK) {
                binding.layoutScanStateOk.tvMessage.text = generateText(responseContent)
            } else if (state == State.ERROR) {
                binding.layoutScanStateError.tvMessage.text =generateText(responseContent)
            }
        }
        if (state == State.OK || state == State.ERROR) {
            val handler: Handler = UIHandler.get()
            handler.removeCallbacks(mDefaultStateSwitchRunnable)
            handler.postDelayed(mDefaultStateSwitchRunnable, (if (state == State.OK) PrefUtils.getScanOkSwitchDelay() else PrefUtils.getScanDeniedSwitchDelay()).toLong())
        } else {
            binding.barcodeInputView.clear()
        }
    }

    private fun generateText(responseContent: PerformEntryCheckoutResponseContent): String {
        var textMessage = "%s\n%s (%s)"
        // NOTE: empty by default for Force Entry from HistoryFragment2
        val articleId: String
        val articleName: String
        // NOTE: action is already checked for null in calling method
        var message: String? = responseContent.action!!.message
        var userMessage = responseContent.userMessage
        if (message == null || TextUtils.isEmpty(message)) {
            message = ""
        }
        if (userMessage == null || TextUtils.isEmpty(userMessage)) {
            userMessage = message
        }
        if (responseContent.ticket != null) {
            articleId = responseContent.ticket!!.articleId
            articleName = responseContent.ticket!!.articleName
        } else {
            articleId = "n/a"
            articleName = "n/a"
        }
        textMessage = String.format(textMessage, userMessage, articleName, articleId)
        return textMessage
    }

    private fun updateFunctionButtons() {
        val keys: Keys?
        val userPrefs = getUserPrefs()
        keys = userPrefs.keys
        setupFunctionButton(binding.btnFunction1, Function.fromName(keys.f1))
        setupFunctionButton(binding.btnFunction2, Function.fromName(keys.f2))
        setupFunctionButton(binding.btnFunction3, Function.fromName(keys.f3))
        setupFunctionButton(binding.btnFunction4, Function.fromName(keys.f4))
    }

    private fun setupFunctionButton(button: Button, function: Function?) {
        if (function == null) {
            button.visibility = View.INVISIBLE
            button.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0)
            button.text = ""
            button.setOnClickListener(null)
            return
        }
        button.visibility = View.VISIBLE
        button.text = getLocalizedString(function.titleResId)
        if (mMode == Mode.SCAN_CHECKOUT) {
            val icon = ContextCompat.getDrawable(this, function.iconResId)!!.mutate()
            icon.setColorFilter(Color.BLACK, PorterDuff.Mode.MULTIPLY)
            button.setCompoundDrawablesWithIntrinsicBounds(null, icon, null, null)
        } else {
            button.setCompoundDrawablesWithIntrinsicBounds(0, function.iconResId, 0, 0)
        }

        button.setOnClickListener {
            if (function.isAvailable) {
                when (function) {
                    Function.SCAN_ENTRY -> mMode = Mode.SCAN_ENTRY
                    Function.SCAN_CHECKOUT -> setMode(Mode.SCAN_CHECKOUT)
                    Function.MENU -> MenuActivity.start(this@ScanActivity)
                    else -> MenuActivity.startForFunction(this@ScanActivity, function)
                }
            } else {
                UIUtils.showLongToast(R.string.message_function_not_available)
            }
        }
    }

    private fun setButtonColors(button: Button?, backgroundColor: Int, textColor: Int) {
        button?.setBackgroundColor(ContextCompat.getColor(this, backgroundColor))
        button?.setTextColor(ContextCompat.getColor(this, textColor))
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putSerializable(STATE_MODE, mMode)
        outState.putSerializable(STATE_SCAN_STATE, mState)
        outState.putString(STATE_CHECKED_TICKET_CODE, mCheckedTicketCode)
    }

    override fun onBackPressed() {
        // Block Back button in the kiosk mode.
        if (!PrefUtils.isKioskModeEnabled()) {
            super.onBackPressed()
        }
    }

    override fun onCreateLoader(id: Int, args: Bundle?): Loader<Cursor?> {
        return CursorLoader(this, DataContentProvider.TICKETS_URI, arrayOf("COUNT(*)"), null, null, null)
    }

    override fun onLoadFinished(loader: Loader<Cursor?>, cursor: Cursor?) {
        var codeCount = 0
        if (cursor != null && cursor.moveToNext()) { //crash ? // java.lang.IllegalStateException: attempt to re-open an already-closed object: SQLiteQuery: SELECT COUNT(*) FROM TABLE_TICKETS_HISTORY
            codeCount = cursor.getInt(0)
        }
        val codeCountText: String? = if (codeCount == 1)
            DynamicString.instance.getString(R.string.SCAN_LOCAL_RECORDS_ONE)
            else
            DynamicString.instance.getString(R.string.SCAN_LOCAL_RECORDS_MANY)
        binding.tvCodeCount.text = "$codeCount $codeCountText"
    }

    override fun onLoaderReset(loader: Loader<Cursor?>) {}

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onEventMainThread(event: StatusManager.StatusChangedEvent?) {
        updateModeTitleText()
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onEventMainThread(event: ShowScanCodeOkEvent) {
        showOkAlert(event)
    }

    private fun showOkAlert(event: ShowScanCodeOkEvent) {
        Logger.e(TAG, "history force entry ok: " + event.ticketCode)
        val state = State.OK
        EventBus.getDefault().removeStickyEvent(event)
        val responseContent = PerformEntryCheckoutResponseContent
        responseContent.ticket = Ticket(event.ticketCode!!, event.ticketArticleId!!, event.ticketArticleName!!)
        responseContent.action = Action(null, null, null, null, null, null, event.message!!, event.ticketCode!!, null)
        setState(state, responseContent, true)
        SoundPlayerUtil.getInstance().playSoundAsync(SoundType.TICKET_SCAN_STATUS_OK)
        mCheckedTicketCode = event.ticketCode
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onEventMainThread(event: BarcodeManager.BarcodeScannedEvent) {
        i(TAG, "Barcode scanned, code = " + event.barcode)
        binding.barcodeInputView.setBarcode(event.barcode)
        event.barcode?.let { checkTicket(it) }
        EventBus.getDefault().removeAllStickyEvents()
    }

    private fun checkTicket(code: String) {
        if (TextUtils.isEmpty(code)) {
            return
        }
        // Check if the function to scan entry/checkout is available.
        if (mMode == Mode.SCAN_ENTRY && !Function.SCAN_ENTRY.isAvailable || mMode == Mode.SCAN_CHECKOUT && !Function.SCAN_CHECKOUT.isAvailable) {
            UIUtils.showLongToast(R.string.message_function_not_available)
            return
        }
        // Refuse the ticket if there is an ongoing request.
        if (!TextUtils.isEmpty(mModelFragment?.ongoingRequestId)) {
            i(TAG, "Refuse the ticket because there is an ongoing request")
            SoundPlayerUtil.getInstance().playSoundAsync(SoundType.CODE_SCAN_REFUSED_OR_ALREADY_PROCESSING)
            return
        }
        if (getPermScanModeValue() && TicketUtils.checkDoubleScanTicket(code)!!) {
            startScanAgainIfPermanentScanModeEnabled()
            return
        } else if (!getPermScanModeValue() && TicketUtils.checkDoubleScanTicket(code)!!) {
            return
        }
        mLastTicketBeforeOnlineFail = code
        if (isLocalScanEnabled() || !isInternetConnected() || StatusManager.getInstance()!!.getStatus() == StatusManager.Status.LOCAL_SCAN) {
            Log.d(TAG, "we are in local scan mode")
            checkTicketLocally(code)
            mLastTicketBeforeOnlineFail = ""
        } else {
            checkTicketOnline(code)
        }
    }

    private fun checkTicketOnline(barcode: String) {
        val requestId: String = BackendServiceUtil.getNextRequestId()
        i(TAG, "Check ticket online, code = $barcode, request_id = $requestId")
        if (StatusManager.getInstance()!!.isOnline()) {
            if (mMode == Mode.SCAN_ENTRY) {
                BackendServiceUtil.performEntry(requestId, barcode)
            } else {
                BackendServiceUtil.performCheckout(requestId, barcode)
            }
        }

        // Start the timer for the ongoing request to check the ticket locally
        // if the server response takes too long.
        UIHandler.get().postDelayed(OnlineTicketCheckTimeoutRunnable(requestId, barcode), PrefUtils.getOfflineDetectTimeout().toLong())
        mModelFragment!!.ongoingRequestId = requestId
    }

    private fun checkTicketLocally(barcode: String) {
        i(TAG, "Check ticket locally, code = $barcode")
        if (TextUtils.isEmpty(barcode)) {
            i(TAG, "Check ticket locally, code is empty")
            return
        }
        val access: TicketHistoryItem = if (mMode == Mode.SCAN_ENTRY) {
            TicketHistoryItem.performEntry(barcode)
        } else {
            TicketHistoryItem.performCheckout(barcode)
        }
        i(TAG, "Ticket is checked locally, result = $access")
        showTicketCheckResult(PerformEntryCheckoutResponseContent.fromLmLib())
        DataBaseManager.instance()!!.saveTicketActionsAsyncTask(access)
    }

    private fun showTicketCheckResult(content: PerformEntryCheckoutResponseContent) {
        val state = if (content.isStatusSuccess()) State.OK else State.ERROR
        setState(state, content, true)
        startScanAgainIfPermanentScanModeEnabled()
        if (state == State.OK) {
            if (content.isReducedTicket()) {
                SoundPlayerUtil.getInstance().playSoundAsync(SoundType.TICKET_SCAN_STATUS_OK_REDUCED_TICKET)
            } else {
                SoundPlayerUtil.getInstance().playSoundAsync(SoundType.TICKET_SCAN_STATUS_OK)
            }
        } else {
            SoundPlayerUtil.getInstance().playSoundAsync(SoundType.TICKET_SCAN_STATUS_NOT_OK)
        }
        mCheckedTicketCode = content.ticketCode
    }

    private fun startScanAgainIfPermanentScanModeEnabled() {
        if (getPermScanModeValue()) {
            if (PrefUtils.getCameraReaderValue()) {
                openCamReader(this@ScanActivity)
            } else if (hasBBApi()) {
                BarcodeManager.instance.setTriggerOn(this)
            }
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onEventMainThread(response: PerformEntryResponse) {
        processEntryCheckoutResponse(response)
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onEventMainThread(response: BarcodeManager.BarcodeScannerTimeoutNoDecodingEvent?) {
        startScanAgainIfPermanentScanModeEnabled()
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onEventMainThread(response: PerformCheckoutResponse) {
        processEntryCheckoutResponse(response)
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onEventMainThread(response: OnFKeysUpdateEvent?) {
        updateFunctionButtons()
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onEventMainThread(event: OnlineTicketCheckTimeoutEvent) {
        if (TextUtils.equals(event.requestId, mModelFragment!!.ongoingRequestId)) {
            i(TAG, "Timeout for online ticket check, request_id = " + event.requestId)
            mModelFragment!!.ongoingRequestId = null
            checkTicketLocally(event.barcode)
        } else {
            i(TAG, "Ignore timeout for online ticket check, request_id = " + event.requestId)
//            DataBaseUtil.uploadCachedTickets()
//            DataBaseUtil.uploadCachedOfflineSessions()
        }
    }

    private fun processEntryCheckoutResponse(response: BaseResponse<*>) {
        if (response.error != null && response.error!!.code == BackendServiceConst.ERROR_CODE_INVALID_BORDER) {
            AlertFragment.show(response.error, {
                mContainer!!.visibility = View.GONE
                mProgressBar!!.visibility = View.VISIBLE
                UpdateUtil.loadOfflineConfig()
            }, supportFragmentManager)
            return
        } else if (response.error != null && response.error!!.code == BackendServiceConst.ERROR_CODE_COMM_KEY_INVALID) {
            AlertFragment.show(response.error, null, supportFragmentManager)
            return
        }
        if (TextUtils.equals(response.requestId, mModelFragment!!.ongoingRequestId)) {
            i(TAG, "Received the response for online ticket check: $response")
            mModelFragment!!.ongoingRequestId = null
            if (response.isResultOk) {
                showTicketCheckResult((response.content as PerformEntryCheckoutResponseContent))
            } else {
                AlertFragment.show(response.error, null, supportFragmentManager)
                checkTicketLocally(mLastTicketBeforeOnlineFail!!)
            }
        } else {
            i(TAG, "Ignore the response for online ticket check: $response")
        }
    }

    open class ModelFragment : Fragment() {
        private var mOngoingRequestId: String? = null
        var ongoingRequestId: String?
            get() {
                Log.i(TAG, "ongoingRequestId:$mOngoingRequestId")
                return mOngoingRequestId
            }
            set(ongoingRequestId) {
                Log.i(TAG, "ongoingRequestId:$ongoingRequestId old_ongoingRequestId:$mOngoingRequestId")
                mOngoingRequestId = ongoingRequestId
            }

        override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)
            retainInstance = true // TODO Deprecated
        }

        companion object {
            val TAG: String = ModelFragment::class.java.simpleName
        }
    }

    private class OnlineTicketCheckTimeoutRunnable(private val mRequestId: String, private val mBarcode: String) : Runnable {
        override fun run() {
            EventBus.getDefault().post(OnlineTicketCheckTimeoutEvent(mRequestId, mBarcode))
        }
    }

    class OnlineTicketCheckTimeoutEvent(val requestId: String, val barcode: String)

    class OnFKeysUpdateEvent

    private inner class DefaultStateSwitchRunnable : Runnable {
        override fun run() {
            setState(State.DEFAULT)
            startScanAgainIfPermanentScanModeEnabled()
        }
    }

    class ShowScanCodeOkEvent(ticketCode: String?, articleName: String?, articleId: String?, message: String?) {
        var ticketCode: String? = null
        var ticketArticleName: String? = null
        var ticketArticleId: String? = null
        var message: String? = null

        init {
            this.ticketCode = ticketCode
            this.ticketArticleName = articleName
            this.ticketArticleId = articleId
            this.message = message
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onEventMainThread(event: UpdateUtil.LocationFragmentEvent) {
        mContainer!!.visibility = View.VISIBLE
        mProgressBar!!.visibility = View.GONE
        if (event.isLoaded) {
            MenuActivity.startForFunction(this@ScanActivity, Function.CHOOSE_BORDER)
        }
    }

}