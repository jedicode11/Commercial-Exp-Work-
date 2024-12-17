package de.dimedis
/**
 * Created by Softeq Development Corporation
 * http://www.softeq.com
 */
interface UserPref {
    //user_suid
    fun userSuid(): String?

    //user_name
    fun userName(): String?
    fun userFullName(): String?

    //Map<String, String>
    fun userPrefs(): String?

    //border
    fun usersBorder(): String?

    // List<String>
    fun listUserFunctions(): String?

    //user_session
    fun userSession(): String?
}