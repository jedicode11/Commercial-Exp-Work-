package de.dimedis.mobileentry.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import de.dimedis.mobileentry.MobileEntryApplication
import de.dimedis.mobileentry.adapter.ItemLocationAdapter
import de.dimedis.mobileentry.adapter.ItemMenuAdapter
import de.dimedis.mobileentry.databinding.FragmentMenuBinding
import de.dimedis.mobileentry.fragments.menus.*
import de.dimedis.mobileentry.model.Function
import de.dimedis.mobileentry.ui.ScanActivity
import de.dimedis.mobileentry.util.DataBaseUtil.uploadCachedOfflineSessions
import de.dimedis.mobileentry.util.DataBaseUtil.uploadCachedTickets
import de.dimedis.mobileentry.util.DebugUtilsInterface.Reinit
import de.dimedis.mobileentry.util.Menu

class MenuFragment : BaseFragment() {
    var binding: FragmentMenuBinding? = null
    var mapLanguage: List<ItemMenuAdapter.Data>? = null

    class Builder {
        fun build(): MenuFragment {
            return MenuFragment()
        }
    }

    val items: List<ItemMenuAdapter.Data>
        get() = Menu(requireContext()).items.also {
            mapLanguage = it
        }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        super.onCreateView(inflater, container, savedInstanceState)
        binding = FragmentMenuBinding.inflate(inflater, container, false)
        binding!!.cancel.setOnClickListener {
            requireActivity().finish() }
        binding!!.title.setOnClickListener {
            MobileEntryApplication.getDemoConf()!!.onTitleClick(object : Reinit {
                override fun reinit() {
                    reinit()
                }
                override fun next(i: Int) {
                    uploadCachedTickets()
                    uploadCachedOfflineSessions()
                }
            })
        }

        val adapter = ItemMenuAdapter(items)
        object : ItemLocationAdapter.OnItemClick {
            override fun onClick(holder: ItemLocationAdapter.ViewHolder, dat: ItemLocationAdapter.Data, position: Int) {
                when (dat.fKeyInfo) {
                    Function.SCAN_ENTRY -> ScanActivity.scanEntry(requireActivity())
                    Function.SCAN_CHECKOUT -> ScanActivity.scanCheckout(requireActivity())
                    Function.CHOOSE_BORDER -> fragment(LocationFragment.builder().build())
                    Function.HISTORY -> fragment(HistoryFragment2())
                    Function.VOID_TICKET -> {
                        fragment(VoidTicketFragment.builder().build())
                        val b = Bundle()
                        b.putBoolean(HistoryFragment2.VOID_TICKET_EXTRA, true)
                        val fragment: Fragment = HistoryFragment2()
                        fragment.arguments = b
                        fragment(fragment)
                    }
                    Function.PHONE -> fragment(PhoneMenuFragment.builder().build())
                    Function.FKEY_CONFIG -> fragment(ConfigFKeysFragment())
                    Function.NETWORK_SETTINGS -> fragment(OnlineSettingsFragment.builder().build())
                    Function.VERSIONS -> fragment(UpdateFragment.builder().build())
                    Function.LOGFILE -> fragment(LogfileMenuFragment.builder().build())
                    Function.DEVICE_SETTINGS -> fragment(DeviceSettingsMenuFragment.builder().build())
                    Function.LOGOUT -> fragment(LogoutMenuFragment.builder().build())
                    Function.EXIT_APP -> fragment(ExitMenuFragment.builder().build())
                    Function.RESET_DEVICE -> fragment(ResetMenuFragment.builder().build())
                    Function.PUSH_LOCAL_CODES -> fragment(PushLocalCodesMenuFragment.builder().build())
                    Function.DELETE_LOCAL_CODES -> fragment(DeleteLocalCodesMenuFragment.builder().build())
                }
            }
        }
        binding!!.recyclerView.setHasFixedSize(true)
        binding!!.recyclerView.adapter = adapter
        binding!!.recyclerView.layoutManager = GridLayoutManager(activity, 2)
        return binding!!.root
    }

    companion object {
        fun builder(): Builder {
            return Builder()
        }
    }
}