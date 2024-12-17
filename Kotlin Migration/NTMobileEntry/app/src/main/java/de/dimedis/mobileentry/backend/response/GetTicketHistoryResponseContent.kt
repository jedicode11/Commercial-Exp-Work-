package de.dimedis.mobileentry.backend.response

import com.google.gson.annotations.SerializedName
import de.dimedis.mobileentry.model.Action
import de.dimedis.mobileentry.model.Ticket

class GetTicketHistoryResponseContent : BaseResponseContent() {
    companion object {
        const val ACTIONS = "actions"
        const val TICKET_CODE = "ticket_code"
        const val TICKET = "ticket"
    }

    @SerializedName(ACTIONS)
    val listActions: List<Action?>? = null

    @SerializedName(TICKET_CODE)
    private val ticket_code: String? = null

    @SerializedName(TICKET)
    private val mTicket: Ticket? = null

    fun listActions(): List<Action?>? {
        return listActions
    }

    fun getTicketCode(): String? {
        return ticket_code
    }

    fun getTicket(): Ticket? {
        return mTicket
    }

    override fun toString(): String {
        return "GetTicketHistoryResponseContent{" +
                "listActions=" + listActions +
                ", ticket_code='" + ticket_code + '\'' +
                ", mTicket=" + mTicket +
                '}'
    }
}