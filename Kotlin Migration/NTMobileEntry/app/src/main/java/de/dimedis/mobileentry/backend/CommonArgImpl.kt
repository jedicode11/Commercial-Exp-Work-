package de.dimedis.mobileentry.backend

interface CommonArgImpl : BaseArgImpl {
    fun getUserSession(): String?
    fun setUserSession(userSession: String?)
    fun getUserSuid(): String?
    fun setUserSuid(userSuid: String?)
    fun getUserName(): String?
    fun setUserName(userName: String?)
    fun getFair(): String?
    fun setFair(fair: String?)
    fun getBorder(): String?
    fun setBorder(border: String?)
}