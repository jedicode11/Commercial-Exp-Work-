package de.dimedis.mobileentry.backend

interface BaseArgImpl {
    fun getLang(): String?
    fun setLang(lang: String?)
    fun getCommKey(): String?
    fun setCommKey(commKey: String?)
    fun getDeviceSuid(): String?
    fun setDeviceSuid(deviceSuid: String?)
}