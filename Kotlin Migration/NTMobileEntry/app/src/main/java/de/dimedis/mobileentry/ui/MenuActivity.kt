package de.dimedis.mobileentry.ui

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import de.dimedis.mobileentry.BuildConfig
import de.dimedis.mobileentry.fragments.HistoryFragment2
import de.dimedis.mobileentry.fragments.LocationFragment
import de.dimedis.mobileentry.fragments.MenuFragment
import de.dimedis.mobileentry.fragments.menus.*
import de.dimedis.mobileentry.model.Function

class MenuActivity : BaseFragmentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (savedInstanceState != null) {
            return
        }
        val intent = intent
        val function = intent.getSerializableExtra(EXTRA_FUNCTION) as Function?
        if (function == null) {
            addFragment(MenuFragment.builder().build(), "init")
            return
        }
        when (function) {
            Function.CHOOSE_BORDER -> addFragment(LocationFragment.builder().build(), null)
            Function.LOGOUT -> addFragment(LogoutMenuFragment.builder().build(), null)
            Function.FORCE_ENTRY, Function.HISTORY -> showHistory(false, intent.getStringExtra(EXTRA_TICKET_CODE)!!)
            Function.FKEY_CONFIG -> addFragment(ConfigFKeysFragment(), null)
            Function.NETWORK_SETTINGS -> addFragment(OnlineSettingsFragment.builder().build(), null)
            Function.VOID_TICKET -> showHistory(true, intent.getStringExtra(EXTRA_TICKET_CODE)!!)
            Function.VERSIONS -> addFragment(UpdateFragment.builder().build(), null)
            Function.LOGFILE -> addFragment(LogfileMenuFragment.builder().build(), null)
            Function.DEVICE_SETTINGS -> addFragment(DeviceSettingsMenuFragment.builder().build(), null)
            Function.EXIT_APP -> addFragment(ExitMenuFragment.builder().build(), null)
            Function.PHONE -> addFragment(PhoneMenuFragment.builder().build(), null)
            Function.RESET_DEVICE -> addFragment(ResetMenuFragment.builder().build(), null)
            Function.PUSH_LOCAL_CODES -> addFragment(PushLocalCodesMenuFragment.builder().build(), null)
            Function.DELETE_LOCAL_CODES -> addFragment(DeleteLocalCodesMenuFragment.builder().build(), null)
            else -> { }
        }
    }

    private fun showHistory(voidTicket: Boolean, ticketCode: String) {
        val args = Bundle()
        args.putBoolean(HistoryFragment2.VOID_TICKET_EXTRA, voidTicket)
        args.putString(HistoryFragment2.TICKET_CODE_EXTRA, ticketCode)
        val fragment: Fragment = HistoryFragment2()
        fragment.arguments = args
        addFragment(fragment, null)
    }

    companion object {
        private const val EXTRA_FUNCTION = BuildConfig.APPLICATION_ID + ".Function"
        private const val EXTRA_TICKET_CODE = BuildConfig.APPLICATION_ID + ".TicketCode"
        @JvmOverloads
        fun startForFunction(context: Context, function: Function, ticketCode: String? = null) {
            start(context, function, ticketCode)
        }

        fun start(context: Context) {
            start(context, null, null)
        }

        private fun start(context: Context, function: Function?, ticketCode: String?) {
            val intent = Intent(context, MenuActivity::class.java)
            intent.putExtra(EXTRA_FUNCTION, function)
            intent.putExtra(EXTRA_TICKET_CODE, ticketCode)
            context.startActivity(intent)
        }
    }
}