package de.dimedis.mobileentry.backend.response;

import com.google.gson.annotations.SerializedName;

import java.util.List;

import de.dimedis.mobileentry.model.Action;
import de.dimedis.mobileentry.model.Ticket;

public class GetTicketHistoryResponseContent extends BaseResponseContent {
    public static final String ACTIONS = "actions";
    public static final String TICKET_CODE = "ticket_code";
    public static final String TICKET = "ticket";


    @SerializedName(ACTIONS)
    private List<Action> listActions;

    @SerializedName(TICKET_CODE)
    private String ticket_code;

    @SerializedName(TICKET)
    private Ticket mTicket;

    public List<Action> getListActions() {
        return listActions;
    }

    public String getTicketCode() {
        return ticket_code;
    }

    public Ticket getTicket() {
        return mTicket;
    }

    @Override
    public String toString() {
        return "GetTicketHistoryResponseContent{" +
                "listActions=" + listActions +
                ", ticket_code='" + ticket_code + '\'' +
                ", mTicket=" + mTicket +
                '}';
    }
}
