package de.dimedis.mobileentry.backend.response;

import com.google.gson.annotations.SerializedName;

import de.dimedis.mobileentry.model.Action;
import de.dimedis.mobileentry.model.Ticket;

public class VoidTicketResponseContent extends BaseResponseContent {
    @SerializedName("ticket_code")
    public String ticketCode;

    @SerializedName("ticket")
    public Ticket ticket;

    @SerializedName("action")
    public Action action;
}
