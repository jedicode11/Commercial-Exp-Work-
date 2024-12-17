package de.dimedis.mobileentry.fragments

import android.graphics.Color
import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import de.dimedis.mobileentry.R
import de.dimedis.mobileentry.backend.ApiService
import de.dimedis.mobileentry.backend.BackendServiceConst
import de.dimedis.mobileentry.backend.BackendServiceUtil
import de.dimedis.mobileentry.backend.CommonArg
import de.dimedis.mobileentry.backend.response.*
import de.dimedis.mobileentry.bbapi.BarcodeManager
import de.dimedis.mobileentry.db.managers.DataBaseManager
import de.dimedis.mobileentry.db.model.TicketHistoryItem
import de.dimedis.mobileentry.model.Action
import de.dimedis.mobileentry.model.Function
import de.dimedis.mobileentry.model.LocalModeHelper.recordEntry
import de.dimedis.mobileentry.model.Ticket
import de.dimedis.mobileentry.ui.ScanActivity.ShowScanCodeOkEvent
import de.dimedis.mobileentry.ui.view.BarcodeInputView
import de.dimedis.mobileentry.ui.view.BarcodeInputView.OnBarcodeEnteredListener
import de.dimedis.mobileentry.util.*
import de.dimedis.mobileentry.util.CommonUtils.isInternetConnected
import de.dimedis.mobileentry.util.Logger.asyncLog
import de.dimedis.mobileentry.util.Logger.i
import de.dimedis.mobileentry.util.Logger.w
import de.dimedis.mobileentry.util.PrefUtils.isLocalScanEnabled
import de.dimedis.mobileentry.util.UIUtils.hideSoftKeyboard
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Observer
import io.reactivex.rxjava3.disposables.Disposable
import io.reactivex.rxjava3.schedulers.Schedulers
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import java.util.*

class HistoryFragment2 : BaseFragment() {
    private var mTicketsRecyclerView: RecyclerView? = null
    private var mAdapter: TicketsAdapter? = null
    private var mProgress: LinearLayout? = null
    private var mTicketInfoType: TextView? = null
    private var mTicketInfoArticleId: TextView? = null
    private var mBarcodeInputView: BarcodeInputView? = null
    private var mForceEntry: Button? = null
    private var mRoot: View? = null
    private var mCurrentTicketCode: String? = null
    private var mCurrentRequestId: String? = "0"
    private var mCancelButton: Button? = null
    private var isVoidTicket = false
    private var mVoidTicket: Button? = null
    private var mTitleText: TextView? = null
    private var mVoidTicketStatusText: TextView? = null
    private var mProgressText: TextView? = null



    override fun onResume() {
        super.onResume()
        this.activity?.let { BarcodeManager.instance.open(it) }
        i(Logger.FUNCTION_HISTORY_SWITCH_TAG, "History is opened")
        EventBus.getDefault().register(this)
    }

    override fun onPause() {
        super.onPause()
        hideSoftKeyboard(mRoot)
        this.activity?.let { BarcodeManager.instance.close(it) }
        i(Logger.FUNCTION_HISTORY_SWITCH_TAG, "History is closed")
        EventBus.getDefault().removeAllStickyEvents()
        EventBus.getDefault().unregister(this)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        mRoot = inflater.inflate(R.layout.fragment_scan_history2, container, false)
        return mRoot
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onEventMainThread(event: BarcodeManager.BarcodeScannedEvent) {
        i(HistoryFragment2::class.java.simpleName, "Barcode scanned, code = " + event.barcode)
        mBarcodeInputView?.getBarcode()
        mCurrentTicketCode = event.barcode
        mProgress?.visibility = View.VISIBLE
        hideSoftKeyboard(mBarcodeInputView)
        startHistoryLoading()
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onEventMainThread(event: DataBaseManager.TicketsSelected) {
        if (event.tickets == null || event.tickets!!.size == 0) {
            reportNoHistoryFoundForTicket()
        } else {
            refreshList(toActionsList(event.tickets))
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onEventMainThread(event: RecordEntryResponseContent) {
        if (event.isResultOk) {
            mProgress!!.visibility = View.GONE
            event.content?.let { showOkVisualFeedback(it) }
            closeFragment()
        } else {
            Toast.makeText(AppContext.get(), getLocalizedString(R.string.force_entry_failed), Toast.LENGTH_LONG).show()
            mProgressText!!.text = getHistoryLoadingText()
            mProgress!!.visibility = View.GONE
        }
    }

    private fun getHistoryLoadingText(): String {
        return String.format("%s %s", getLocalizedString(R.string.SPINNER_LOADING), getLocalizedString(R.string.HISTORY_TITLE))
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onEventMainThread(event: GetTicketHistoryResponse) {
        val response = event.content
        if (response?.listActions == null || response.listActions.isEmpty()) {
            reportNoHistoryFoundForTicket()
        } else {
            try {
                val articleName = response.getTicket()?.articleName
                val articleId = response.getTicket()?.articleId
                mTicketInfoType!!.text = articleName
                mTicketInfoArticleId!!.text = String.format("(%s)", articleId)
            } catch (ignore: Exception) {
                Logger.e(TAG, "cant get article name and id")
            }
            refreshList(response.listActions)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val startArguments = arguments
        initCommonControls(view)
        initCancelButton(view)
        initVoidTicketButton(view, startArguments)
        initForceEntryButton(view)
        initBarCodeInputField(view)
        startProcessingBarCode(startArguments)
    }

    private fun startProcessingBarCode(startArguments: Bundle?) {
        if (isVoidTicket) {
            mBarcodeInputView!!.visibility = View.VISIBLE
            mProgress!!.visibility = View.GONE
            return
        }
        if (startArguments?.getString(TICKET_CODE_EXTRA, null) == null) {
            mBarcodeInputView!!.visibility = View.VISIBLE
            mProgress!!.visibility = View.GONE
        } else {
            startLoadingHistoryForTicket(startArguments)
        }
    }

    private fun initVoidTicketButton(view: View, startArguments: Bundle?) {
        isVoidTicket = startArguments != null && startArguments.getBoolean(VOID_TICKET_EXTRA)
        mTitleText!!.text = if (isVoidTicket) getLocalizedString(R.string.VOID_TICKET_TITLE) else getLocalizedString(R.string.HISTORY_TITLE)
        i(Logger.FUNCTION_HISTORY_SWITCH_TAG, if (isVoidTicket) "history is in VOID ticket mode" else "history is in online history mode")
        mVoidTicket = view.findViewById<View>(R.id.history_button_void_ticket) as Button
        mVoidTicket!!.visibility = if (isVoidTicket) View.VISIBLE else View.GONE
        mVoidTicket!!.setOnClickListener {
            voidTicketCode() }
    }

    private fun voidTicketCode() {
        if (mCurrentTicketCode == null || TextUtils.isEmpty(mCurrentTicketCode)) {
            return
        }
        mVoidTicketStatusText!!.visibility = View.GONE
        val arg: CommonArg = CommonArg.fromPreferences()
        BackendServiceConst.sBackendApi?.voidTicket(
            arg.lang,
            arg.commKey,
            arg.deviceSuid,
            arg.userSession,
            arg.userSuid,
            arg.userName,
            arg.fair,
            arg.border,
            mCurrentTicketCode)?.subscribeOn(Schedulers.io())?.observeOn(AndroidSchedulers.mainThread())
            ?.subscribe(object : Observer<VoidTicketResponse> {
                override fun onComplete() {
                    mVoidTicketStatusText!!.text = getLocalizedString(R.string.VOID_TICKET_SUCCESS_MESSAGE)
                    mVoidTicketStatusText!!.setTextColor(ContextCompat.getColor(activity!!, R.color.green_light))
                    mVoidTicketStatusText!!.visibility = View.VISIBLE
                }

                override fun onError(e: Throwable) {
                    mVoidTicketStatusText!!.text = getLocalizedString(R.string.VOID_TICKET_FAIL_MESSAGE)
                    mVoidTicketStatusText!!.setTextColor(Color.RED)
                    mVoidTicketStatusText!!.visibility = View.VISIBLE
                }

                override fun onSubscribe(d: Disposable) {
                }

                override fun onNext(voidTicketResponse: VoidTicketResponse) {
                    Logger.e(TAG, voidTicketResponse.toString())
                    if (!voidTicketResponse.isResultOk) {
                        onError(voidTicketResponse.throwable!!)
                    }
                }
            })
    }

    private fun initCancelButton(view: View) {
        mCancelButton = view.findViewById<View>(R.id.history_button_cancel) as Button
        mCancelButton!!.setOnClickListener { closeFragment() }
    }

    private fun initCommonControls(view: View) {
        mTitleText = view.findViewById<View>(R.id.fragment_title) as TextView
        mVoidTicketStatusText = view.findViewById<View>(R.id.history_screen_ticket_void_status) as TextView
        mTicketsRecyclerView = view.findViewById<View>(R.id.fragment_history_items_list) as RecyclerView
        mTicketsRecyclerView!!.layoutManager = LinearLayoutManager(activity)
        mAdapter = TicketsAdapter(null)
        mTicketsRecyclerView!!.adapter = mAdapter
        mTicketInfoType = view.findViewById<View>(R.id.history_screen_ticket_type) as TextView
        mTicketInfoArticleId = view.findViewById<View>(R.id.history_screen_ticket_article_id) as TextView
        mProgress = view.findViewById<View>(R.id.progress_bar_intermediate) as LinearLayout
        mProgressText = view.findViewById<View>(R.id.progress_text) as TextView
        mProgressText!!.text = getHistoryLoadingText()
    }

    private fun initBarCodeInputField(view: View) {
        mBarcodeInputView = view.findViewById<View>(R.id.barcode_input_view) as BarcodeInputView
        mBarcodeInputView!!.setOnBarcodeEnteredListener(object : OnBarcodeEnteredListener {
            override fun onBarcodeEntered(barcode: String?) {
                i("History screen, manual code input", "Barcode entered manually, code = $barcode")
                if (!TextUtils.isEmpty(barcode)) {
                    mProgress!!.visibility = View.VISIBLE
                    hideSoftKeyboard(mBarcodeInputView)
                    mCurrentTicketCode = barcode
                    startHistoryLoading()
                }
            }
        })
    }

    private fun initForceEntryButton(rootView: View) {
        mForceEntry = rootView.findViewById<View>(R.id.history_button_force_entry) as Button
        if (Function.FORCE_ENTRY.isAvailable) {
            mForceEntry!!.visibility = View.VISIBLE
        } else {
            mForceEntry!!.visibility = View.GONE
        }
        if (isVoidTicket) {
            mForceEntry!!.visibility = View.GONE
            return
        }
        mForceEntry!!.setOnClickListener {
            if (mCurrentTicketCode == null) {
                return@setOnClickListener
            }
            mProgressText!!.text = String.format("%s %s", getLocalizedString(R.string.SPINNER_PROGRESS), getLocalizedString(R.string.FORCE_ENTRY_BUTTON_LABEL))
            if (isLocalScanEnabled() || !isInternetConnected()) {
                mProgress!!.visibility = View.VISIBLE
                val access = recordEntry(mCurrentTicketCode!!)
                mProgress!!.visibility = View.GONE
                val responseContent = PerformEntryCheckoutResponseContent
                val ticket = access!!.getTicket()
                if (ticket != null) {
                    responseContent.ticket = Ticket(access.getTicketCode()!!, ticket.getArticleId()!!, ticket.getArticleName()!!)
                }
                val action = access.getAction()
                if (action != null) {
                    responseContent.action = Action(
                        action.getTimestamp(),
                        action.getDateText(),
                        action.getTimeText(),
                        action.getDeviceSuid(),
                        action.getDeviceName(),
                        action.getType(),
                        access.getAction()!!.getMessage(),
                        action.getTicketCode(), null)
                }
                showOkVisualFeedback(responseContent)
                closeFragment()
            } else {
                mProgress!!.visibility = View.VISIBLE
                BackendServiceUtil.recordEntry(mCurrentRequestId!!, mCurrentTicketCode!!)
            }
        }
    }

    fun showOkVisualFeedback(event: PerformEntryCheckoutResponseContent) {
        var articleName = ""
        var articleId = ""
        var message: String? = "Local scan, force entry"
        if (event.ticket != null) {
            if (event.action != null && event.action!!.message != null) {
                message = event.action!!.message
            } else if (event.userMessage != null) {
                message = event.userMessage
            }
            articleName = event.ticket!!.articleName
            articleId = event.ticket!!.articleId
        }
        EventBus.getDefault().postSticky(ShowScanCodeOkEvent(mCurrentTicketCode, articleName, articleId, message))
    }

    private fun startHistoryLoading() {
        if (isLocalScanEnabled() || !isInternetConnected()) {
            reportNoHistoryFoundForTicket()
        } else {
            BackendServiceUtil.getTicketHistory(mCurrentTicketCode!!)
        }
    }

    private fun closeFragment() {
        requireActivity().onBackPressed()
    }

    private fun startLoadingHistoryForTicket(startArguments: Bundle) {
        mCurrentTicketCode = startArguments.getString(TICKET_CODE_EXTRA)
        mCurrentRequestId = startArguments.getString(REQUEST_ID_EXTRA)
        if (mCurrentTicketCode != null) {
            startHistoryLoading()
        }
    }

    private fun toActionsList(tickets: ArrayList<TicketHistoryItem?>?): List<Action?>? {
        if (tickets == null) return null
        val actions = ArrayList<Action?>(tickets.size)
        for (item in tickets) {
            actions.add(item!!.toAction())
        }
        return actions
    }

    private fun reportNoHistoryFoundForTicket() {
        val dummyItem = TicketHistoryItem.createDummyItem()
        dummyItem.getAction()!!.setMessage(getLocalizedString(R.string.HISTORY_NOT_AVAILABLE_MESSAGE))
        val dummyList = ArrayList<TicketHistoryItem?>(1)
        dummyList.add(dummyItem)
        refreshList(toActionsList(dummyList))
    }

    private fun refreshList(actions: List<Action?>?) {
        mProgress!!.visibility = View.GONE
        if (actions == null) {
            return
        }
        for (action in actions) {
            w(TAG, "#### TICKET ACTION #### : " + action!!.ticketCode + " / " + action.location)
        }
        Collections.reverse(actions)
        mAdapter!!.setTickets(actions)
    }

    inner class TicketsAdapter(tickets: List<Action?>?) :
        RecyclerView.Adapter<RecyclerView.ViewHolder>() {
        inner class TicketViewHolder(private val mRootView: View) : RecyclerView.ViewHolder(
            mRootView) {
            private val mTicketDate: TextView = mRootView.findViewById<View>(R.id.history_item_date) as TextView
            private val mTicketTime: TextView = mRootView.findViewById<View>(R.id.history_item_time) as TextView
            private val mTicketDetails: TextView = mRootView.findViewById<View>(R.id.history_item_details) as TextView
            private val mTicketLocation: TextView = mRootView.findViewById<View>(R.id.history_item_location) as TextView

            fun bind(item: TicketHistoryItem, position: Int) {
                mRootView.setBackgroundColor(if (position % 2 == 0) Color.WHITE else resources.getColor(R.color.grey_light))
                mTicketDate.text = item.getAction()!!.getDateText()
                mTicketTime.text = item.getAction()!!.getTimeText()
                mTicketDetails.text = if (TextUtils.isEmpty(item.getAction()!!.getMessage())) item.denyReason else item.getAction()!!.getMessage()
                mTicketLocation.text = item.getAction()!!.getDeviceName()
                mRootView.tag = item
                try {
                    asyncLog(String.format("OnlineHistory - ticket: %s", item.getAction()!!.getTicketCode()),
                        String.format(Locale.getDefault(), "found (%d): %s", position, item), LogType.INFO)
                } catch (ex: java.lang.Exception) {
                    ex.printStackTrace()
                }
            }
        }

        private var tickets: List<Action?>? = null

        init {
            setTickets(tickets)
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
            val v = LayoutInflater.from(parent.context).inflate(R.layout.item_history_row, parent, false)
            return TicketViewHolder(v)
        }

        override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
            (holder as TicketViewHolder?)!!.bind(TicketHistoryItem.fromAction(null, getTickets()!![position]!!), position)
        }

        override fun getItemCount(): Int {
            return if (tickets == null || tickets!!.isEmpty()) {
                0
            } else {
                tickets!!.size
            }
        }

        fun setTickets(tickets: List<Action?>?) {
            if (tickets == null) return
            this.tickets = ArrayList(tickets)
            notifyDataSetChanged()
        }

        fun getTickets(): List<Action?>? {
            return tickets
        }
    }

    companion object {
        const val TICKET_CODE_EXTRA = "TICKET_CODE_EXTRA"
        private const val REQUEST_ID_EXTRA = "REQUEST_ID_EXTRA"
        const val VOID_TICKET_EXTRA = "VOID_TICKET_EXTRA"
    }
}