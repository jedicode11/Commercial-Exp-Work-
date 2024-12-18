package de.dimedis.mobileentry.db.model;

import android.content.ContentValues;
import android.database.Cursor;

import androidx.annotation.NonNull;

import com.google.gson.Gson;

import de.dimedis.lmlib.AccessImpl;
import de.dimedis.lmlib.ActionJson;
import de.dimedis.mobileentry.db.tables.TicketsHistoryTable;
import de.dimedis.mobileentry.model.Action;
import de.dimedis.mobileentry.model.LocalModeHelper;
import de.dimedis.mobileentry.model.PerfData;

public class TicketHistoryItem {

    private Integer columnId;
    private PerfData mPerfData;
    private ActionJson mActionJson;
    private AccessImpl mAccessImpl;

    public PerfData getmPerfData() {
        return mPerfData;
    }

    public void setmPerfData(PerfData mPerfData) {
        this.mPerfData = mPerfData;
    }

    public AccessImpl getAccessImpl() {
        return mAccessImpl;
    }

    public void setAccessImpl(AccessImpl mAccessImpl) {
        this.mAccessImpl = mAccessImpl;
        setActionJson(mAccessImpl);
    }

    public TicketHistoryItem() {
    }

    public String denyReason;

    public String getDenyReason() {
        return denyReason;
    }

    public void setDenyReason(String denyReason) {
        this.denyReason = denyReason;
    }

    void initFieldFromPref() {
        mPerfData = new PerfData().initFromPref();
    }

    public static TicketHistoryItem performEntry(@NonNull String ticketCode) {
        TicketHistoryItem item = new TicketHistoryItem();
        item.initFieldFromPref();
        item.setAccessImpl((AccessImpl) LocalModeHelper.performEntry(item.getmPerfData(), ticketCode));
        return item;
    }

    public static TicketHistoryItem performCheckout(@NonNull String ticketCode) {
        TicketHistoryItem item = new TicketHistoryItem();
        item.initFieldFromPref();
        item.setAccessImpl((AccessImpl) LocalModeHelper.performCheckout(item.getmPerfData(), ticketCode));
        return item;
    }

    public ContentValues toContentValues() {
        ContentValues contentValues = new ContentValues();
        contentValues.put(TicketsHistoryTable.COLUMN_LANG, mPerfData.getLang());
        contentValues.put(TicketsHistoryTable.COLUMN_COMM_KEY, mPerfData.getCommKey());
        contentValues.put(TicketsHistoryTable.COLUMN_DEVICESUID, mPerfData.getDeviceSuid());
        contentValues.put(TicketsHistoryTable.COLUMN_USERSESSION, mPerfData.getUserSession());
        contentValues.put(TicketsHistoryTable.COLUMN_USERSUID, mPerfData.getUserSuid());
        contentValues.put(TicketsHistoryTable.COLUMN_USERNAME, mPerfData.getUserName());
        contentValues.put(TicketsHistoryTable.COLUMN_FAIR, mPerfData.getFair());
        contentValues.put(TicketsHistoryTable.COLUMN_BORDER, mPerfData.getBorder());
        contentValues.put(TicketsHistoryTable.COLUMN_BODY, getActionJson());
        return contentValues;
    }

    public static TicketHistoryItem fromCursor(Cursor cursor) {
        return new TicketHistoryItem(cursor);
    }

    public TicketHistoryItem(Cursor cursor) {
        setColumnId(cursor.getInt(cursor.getColumnIndexOrThrow(TicketsHistoryTable.COLUMN_ID)));
        mPerfData = new PerfData();
        mPerfData.setLang(cursor.getString(cursor.getColumnIndexOrThrow(TicketsHistoryTable.COLUMN_LANG)));
        mPerfData.setCommKey(cursor.getString(cursor.getColumnIndexOrThrow(TicketsHistoryTable.COLUMN_COMM_KEY)));
        mPerfData.setDeviceSuid(cursor.getString(cursor.getColumnIndexOrThrow(TicketsHistoryTable.COLUMN_DEVICESUID)));
        mPerfData.setUserSession(cursor.getString(cursor.getColumnIndexOrThrow(TicketsHistoryTable.COLUMN_USERSESSION)));
        mPerfData.setUserSuid(cursor.getString(cursor.getColumnIndexOrThrow(TicketsHistoryTable.COLUMN_USERSUID)));
        mPerfData.setUserName(cursor.getString(cursor.getColumnIndexOrThrow(TicketsHistoryTable.COLUMN_USERNAME)));
        mPerfData.setFair(cursor.getString(cursor.getColumnIndexOrThrow(TicketsHistoryTable.COLUMN_FAIR)));
        mPerfData.setBorder(cursor.getString(cursor.getColumnIndexOrThrow(TicketsHistoryTable.COLUMN_BORDER)));
        setActionJson(cursor.getString(cursor.getColumnIndexOrThrow(TicketsHistoryTable.COLUMN_BODY)));
    }

    public static TicketHistoryItem fromAction(String userSUID, Action action) {
        TicketHistoryItem item = new TicketHistoryItem();
        PerfData pf = new PerfData();
        item.setmPerfData(pf);
        ActionJson actionJson = new ActionJson();
        item.setAction(actionJson);
        actionJson.setTicketCode(action.ticketCode);
        actionJson.setUserSuid(userSUID);
        actionJson.setDeviceSuid(action.deviceSuid);
        actionJson.setDeviceName(action.deviceName);
        actionJson.setType(action.type);
        actionJson.setMessage(action.message);
        actionJson.setTimeStamp(action.timestamp);

        actionJson.setDateText(action.dateText);
        actionJson.setTimeText(action.timeText);
        actionJson.setFair(action.location);
        //--\
        item.setDenyReason("");
        return item;
    }

    public Action toAction() {
        return new Action(mActionJson.getTimestamp(), getDateText(), mActionJson.getTimeText(), mActionJson.getDeviceSuid(),
                mActionJson.getDeviceName(), mActionJson.getType(), mActionJson.getMessage(), mActionJson.getTicketCode(), null);
    }

    public String getActionJson() {
        return new Gson().toJson(mActionJson);
    }

    public ActionJson getAction() {
        return mActionJson;
    }

    public void setAction(ActionJson actionJson) {
        mActionJson = actionJson;
    }

    public void setActionJson(AccessImpl access) {
        mActionJson = new ActionJson(mPerfData.getUserSuid(), access);
    }

    public void setActionJson(String json) {
        mActionJson = new Gson().fromJson(json, ActionJson.class);
    }

    public static TicketHistoryItem createDummyItem() {
        TicketHistoryItem item = new TicketHistoryItem();
        item.setAction(new ActionJson());
        return item;
    }


    public Integer getColumnId() {
        return columnId;
    }

    public void setColumnId(Integer columnId) {
        this.columnId = columnId;
    }


    public String getDateText() {
        return mActionJson.getDateText();
    }

}
