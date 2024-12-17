package de.dimedis.mobileentry.backend.response

import com.google.gson.annotations.SerializedName
import de.dimedis.mobileentry.model.Action
import de.dimedis.mobileentry.model.Ticket

class VoidTicketResponseContent : BaseResponseContent() {
    @SerializedName("ticket_code")
    var ticketCode: String? = null

    @SerializedName("ticket")
    var ticket: Ticket? = null

    @SerializedName("action")
    var action: Action? = null
}