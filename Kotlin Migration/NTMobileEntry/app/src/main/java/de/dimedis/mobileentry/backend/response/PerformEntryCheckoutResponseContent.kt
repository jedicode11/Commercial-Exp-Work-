package de.dimedis.mobileentry.backend.response

import com.google.gson.annotations.SerializedName
import de.dimedis.mobileentry.model.Action
import de.dimedis.mobileentry.model.Ticket

object PerformEntryCheckoutResponseContent : BaseResponseContent() {
    @SerializedName("status_modifiers")
    var statusModifiers: List<String>? = null

    @SerializedName("ticket_code")
    var ticketCode: String? = null

    @SerializedName("deny_reason")
    var denyReason: String? = null
    var ticket: Ticket? = null
    var action: Action? = null

    fun fromLmLib(): PerformEntryCheckoutResponseContent {
        val content = PerformEntryCheckoutResponseContent
        content.status = status
        content.statusDescription = statusDescription
        content.userMessage = userMessage
        content.ticketCode = ticketCode
        content.denyReason = denyReason
        content.ticket = Ticket.fromLmLib(ticket!!)
        content.action = Action.fromLmLib(action)
        content.statusModifiers = statusModifiers
        return content
    }

    override fun toString(): String {
        return "PerformEntryCheckoutResponseContent{" +
                "statusModifiers=" + statusModifiers +
                ", ticketCode='" + ticketCode + '\'' +
                ", denyReason='" + denyReason + '\'' +
                ", ticket=" + ticket +
                ", action=" + action +
                '}'
    }

    fun isReducedTicket(): Boolean {
        return statusModifiers != null && statusModifiers!!.contains("reduced")
    }

}