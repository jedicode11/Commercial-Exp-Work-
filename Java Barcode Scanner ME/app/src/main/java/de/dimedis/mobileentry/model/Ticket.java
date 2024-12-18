package de.dimedis.mobileentry.model;

import androidx.annotation.NonNull;

import com.google.gson.annotations.SerializedName;

public final class Ticket {
    @SerializedName("ticket_code")
    public final String ticketCode;
    @SerializedName("article_id")
    public final String articleId;
    @SerializedName("article_name")
    public final String articleName;

    public Ticket(String ticketCode, String articleId, String articleName) {
        this.ticketCode = ticketCode;
        this.articleId = articleId;
        this.articleName = articleName;
    }

    public static Ticket fromLmLib(@NonNull de.dimedis.lmlib.myinterfaces.Ticket ticket) {
        return new Ticket(ticket.getTicketCode(), ticket.getArticleId(), ticket.getArticleName());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Ticket ticket = (Ticket) o;
        return ticketCode.equals(ticket.ticketCode);
    }

    @Override
    public int hashCode() {
        return ticketCode.hashCode();
    }

    @Override
    public String toString() {
        return "Ticket{" + "ticketCode='" + ticketCode + '\'' + ", articleId='" + articleId + '\'' + ", articleName='" + articleName + '\'' + '}';
    }
}
