package de.dimedis.mobileentry.util

interface DebugUtilsInterface {
    fun getDemoAccBarCode(): String?
    fun getDemoInitializeBarcode(): String?
    fun isDemoMode(): Boolean
    fun isDemoModeOn(): Boolean
    fun getDefaultToken(): String?
    fun isAdminMode(): Boolean
    interface Reinit {
        fun reinit()
        fun next(i: Int)
    }

    fun onTitleClick(reinit: Reinit?)
}