package de.dimedis.mobileentry.backend.response;

import androidx.annotation.NonNull;

import com.google.gson.annotations.SerializedName;

import java.util.List;

import de.dimedis.lmlib.myinterfaces.Access;
import de.dimedis.mobileentry.model.Action;
import de.dimedis.mobileentry.model.Ticket;

public class PerformEntryCheckoutResponseContent extends BaseResponseContent {
    @SerializedName("status_modifiers")
    public List<String> statusModifiers;
    @SerializedName("ticket_code")
    public String ticketCode;
    @SerializedName("deny_reason")
    public String denyReason;
    public Ticket ticket;
    public Action action;

    public static PerformEntryCheckoutResponseContent fromLmLib(@NonNull Access access) {
        PerformEntryCheckoutResponseContent content = new PerformEntryCheckoutResponseContent();
        content.status = access.getStatus();
        content.statusDescription = access.getStatusDescription();
        content.userMessage = access.getUserMessage();
        content.ticketCode = access.getTicketCode();
        content.denyReason = access.getDenyReason();
        content.ticket = Ticket.fromLmLib(access.getTicket());
        content.action = Action.fromLmLib(access.getAction());
        content.statusModifiers = access.getStatusModifiers();
        return content;
    }

    @Override
    public String toString() {
        return "PerformEntryCheckoutResponseContent{" +
                "statusModifiers=" + statusModifiers +
                ", ticketCode='" + ticketCode + '\'' +
                ", denyReason='" + denyReason + '\'' +
                ", ticket=" + ticket +
                ", action=" + action +
                '}';
    }

    public boolean isReducedTicket() {
        return statusModifiers != null && statusModifiers.contains("reduced");
    }
}
