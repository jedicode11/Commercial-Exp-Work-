package de.dimedis.mobileentry.fragments.util

import android.text.TextUtils
import de.dimedis.mobileentry.util.PrefUtils

class TicketUtils {
    private var mLastTicket: String? = null
    private var mLastTicketTime: Long = 0
    fun updateTicketTime(code: String?) {
        mLastTicket = code
    }

    //check for double scan of ticket
    fun checkTicket(code: String): Boolean {
        var out = (!TextUtils.isEmpty(mLastTicket)
                && mLastTicket == code)
        //   && (System.currentTimeMillis() - mLastTicketTime) < DOUBLE_SCAN_INTERVAL_MS;
        if (!out) {
            mLastTicketTime = System.currentTimeMillis()
        }
        out = System.currentTimeMillis() - mLastTicketTime < PrefUtils.getScanDoubleDelay() && out
        updateTicketTime(code)
        if (System.currentTimeMillis() - mLastTicketTime > PrefUtils.getScanDoubleDelay()) mLastTicketTime = System.currentTimeMillis()
        return out
    }

    companion object {
        const val TAG = "TicketUtils"
        private var sTicketUtils: TicketUtils? = null

        //Refuse double scan of one code within 10 seconds
        fun checkDoubleScanTicket(code: String?): Boolean? {
            if (sTicketUtils == null) {
                sTicketUtils = TicketUtils()
            }
            return code?.let { sTicketUtils!!.checkTicket(it) }
        }
    }
}