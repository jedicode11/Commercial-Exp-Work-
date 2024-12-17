package de.dimedis.mobileentry.db.model

import android.content.ContentValues
import android.database.Cursor
import com.google.gson.Gson
import de.dimedis.lmlib.AccessImpl
import de.dimedis.lmlib.ActionJson
import de.dimedis.mobileentry.db.tables.TicketsHistoryTable
import de.dimedis.mobileentry.model.Action
import de.dimedis.mobileentry.model.LocalModeHelper
import de.dimedis.mobileentry.model.PerfData

class TicketHistoryItem {
    var columnId: Int? = null
    private var mPerfData: PerfData? = null
    private var mActionJson: ActionJson? = null
    private var mAccessImpl: AccessImpl? = null
    fun getmPerfData(): PerfData? {
        return mPerfData
    }

    fun setmPerfData(mPerfData: PerfData?) {
        this.mPerfData = mPerfData
    }

    var accessImpl: AccessImpl?
        get() = mAccessImpl
        set(mAccessImpl) {
            this.mAccessImpl = mAccessImpl
//            actionJson = mAccessImpl.toString()
        }

    constructor()

    var denyReason: String? = null
    fun initFieldFromPref() {
        mPerfData = PerfData().initFromPref()
    }

    fun toContentValues(): ContentValues {
        val contentValues = ContentValues()
        contentValues.put(TicketsHistoryTable.COLUMN_LANG, mPerfData?.lang)
        contentValues.put(TicketsHistoryTable.COLUMN_COMM_KEY, mPerfData?.commKey)
        contentValues.put(TicketsHistoryTable.COLUMN_DEVICESUID, mPerfData?.deviceSuid)
        contentValues.put(TicketsHistoryTable.COLUMN_USERSESSION, mPerfData?.userSession)
        contentValues.put(TicketsHistoryTable.COLUMN_USERSUID, mPerfData?.userSuid)
        contentValues.put(TicketsHistoryTable.COLUMN_USERNAME, mPerfData?.userName)
        contentValues.put(TicketsHistoryTable.COLUMN_FAIR, mPerfData?.fair)
        contentValues.put(TicketsHistoryTable.COLUMN_BORDER, mPerfData?.border)
        return contentValues
    }

    constructor(cursor: Cursor) {
        columnId = cursor.getInt(cursor.getColumnIndexOrThrow(TicketsHistoryTable.COLUMN_ID))
        mPerfData = PerfData()
        mPerfData?.lang = cursor.getString(cursor.getColumnIndexOrThrow(TicketsHistoryTable.COLUMN_LANG))
        mPerfData?.commKey = cursor.getString(cursor.getColumnIndexOrThrow(TicketsHistoryTable.COLUMN_COMM_KEY))
        mPerfData?.deviceSuid = cursor.getString(cursor.getColumnIndexOrThrow(TicketsHistoryTable.COLUMN_DEVICESUID))
        mPerfData?.userSession = cursor.getString(cursor.getColumnIndexOrThrow(TicketsHistoryTable.COLUMN_USERSESSION))
        mPerfData?.userSuid = cursor.getString(cursor.getColumnIndexOrThrow(TicketsHistoryTable.COLUMN_USERSUID))
        mPerfData?.userName = cursor.getString(cursor.getColumnIndexOrThrow(TicketsHistoryTable.COLUMN_USERNAME))
        mPerfData?.fair = cursor.getString(cursor.getColumnIndexOrThrow(TicketsHistoryTable.COLUMN_FAIR))
        mPerfData?.border = cursor.getString(cursor.getColumnIndexOrThrow(TicketsHistoryTable.COLUMN_BORDER))
        setActionJson(cursor.getString(cursor.getColumnIndexOrThrow(TicketsHistoryTable.COLUMN_BODY)));
    }

    fun toAction(): Action {
        return Action(
            mActionJson!!.getTimestamp(), getDateText(),
            mActionJson!!.getTimeText(),
            mActionJson!!.getDeviceSuid(),
            mActionJson!!.getDeviceName(),
            mActionJson!!.getType(),
            mActionJson!!.getMessage(),
            mActionJson!!.getTicketCode(), null)
    }

    fun getAction(): ActionJson? {
        return mActionJson
    }

    fun setAction(actionJson: ActionJson?) {
        mActionJson = actionJson
    }

    fun setActionJson(access: AccessImpl?) {
        mActionJson = access?.let { ActionJson(mPerfData?.userSuid, it) }
    }

    fun setActionJson(json: String?) {
        mActionJson = Gson().fromJson(json, ActionJson::class.java)
    }

    fun getDateText(): String? {
        return mActionJson?.getDateText()
    }

    companion object {
        fun performEntry(ticketCode: String): TicketHistoryItem {
            val item = TicketHistoryItem()
            item.initFieldFromPref()
            item.accessImpl = item.getmPerfData()?.let { LocalModeHelper.performEntry(it, ticketCode) } as AccessImpl
            return item
        }

        fun performCheckout(ticketCode: String): TicketHistoryItem {
            val item = TicketHistoryItem()
            item.initFieldFromPref()
            item.accessImpl = item.getmPerfData()?.let { LocalModeHelper.performCheckout(it, ticketCode) } as AccessImpl
            return item
        }

        fun fromCursor(cursor: Cursor): TicketHistoryItem {
            return TicketHistoryItem(cursor)
        }

        fun fromAction(userSUID: String?, action: Action): TicketHistoryItem {
            val item = TicketHistoryItem()
            val pf = PerfData()
            item.setmPerfData(pf)
            val actionJson = ActionJson()
            item.setAction(actionJson)
            actionJson.setTicketCode(action.ticketCode)
            actionJson.userSuid = (userSUID)
            actionJson.setDeviceSuid(action.deviceSuid)
            actionJson.setDeviceName(action.deviceName)
            actionJson.setType(action.type)
            actionJson.setMessage(action.message)
            actionJson.setTimeStamp(action.timestamp)
            actionJson.setDateText(action.dateText)
            actionJson.setTimeText(action.timeText)
            actionJson.setFair(action.location)
            //--\
            item.denyReason = ""
            return item
        }

        fun createDummyItem(): TicketHistoryItem {
            val item = TicketHistoryItem()
            item.setAction(ActionJson())
            return item
        }
    }
}