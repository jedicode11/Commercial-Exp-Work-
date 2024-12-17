package de.dimedis.mobileentry.model

import com.google.gson.annotations.SerializedName

class Ticket(@field:SerializedName("ticket_code") val ticketCode: String, @field:SerializedName("article_id"
    ) val articleId: String, @field:SerializedName("article_name") val articleName: String) {
    override fun equals(other: Any?): Boolean {
        if (this === other) {
            return true
        }
        if (other == null || javaClass != other.javaClass) {
            return false
        }
        val ticket = other as Ticket
        return ticketCode == ticket.ticketCode
    }

    override fun hashCode(): Int {
        return ticketCode.hashCode()
    }

    override fun toString(): String {
        return "Ticket{ticketCode='$ticketCode', articleId='$articleId', articleName='$articleName'}"
    }

    companion object {
        fun fromLmLib(ticket: Ticket): Ticket {
            return Ticket(ticket.ticketCode, ticket.articleId, ticket.articleName)
        }
    }
}