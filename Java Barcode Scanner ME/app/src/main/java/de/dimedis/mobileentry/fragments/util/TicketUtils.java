package de.dimedis.mobileentry.fragments.util;

import android.text.TextUtils;

import de.dimedis.mobileentry.util.PrefUtils;

public class TicketUtils {
    static final String TAG = "TicketUtils";

    private String mLastTicket;
    private long mLastTicketTime;

    private static TicketUtils sTicketUtils;

    void updateTicketTime(String code) {
        mLastTicket = code;
    }

    //check for double scan of ticket
    boolean checkTicket(String code) {
        boolean out = !TextUtils.isEmpty(mLastTicket) && mLastTicket.equals(code);
        if (!out) {
            mLastTicketTime = System.currentTimeMillis();
        }

        out = (System.currentTimeMillis() - mLastTicketTime) < PrefUtils.getScanDoubleDelay() && out;
        updateTicketTime(code);
        if ((System.currentTimeMillis() - mLastTicketTime) > PrefUtils.getScanDoubleDelay())
            mLastTicketTime = System.currentTimeMillis();
        return out;
    }

    //Refuse double scan of one code within 10 seconds
    public static boolean checkDoubleScanTicket(String code) {
        if (sTicketUtils == null) {
            sTicketUtils = new TicketUtils();
        }
        return sTicketUtils.checkTicket(code);
    }
}
