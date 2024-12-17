package de.dimedis.mobileentry.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import de.dimedis.mobileentry.adapter.ItemLocationAdapter
import de.dimedis.mobileentry.databinding.FragmentLocationBinding
import de.dimedis.mobileentry.ui.ScanActivity
import de.dimedis.mobileentry.util.ConfigPrefHelper
import de.dimedis.mobileentry.util.ConfigPrefHelper.setUsersBorder

class LocationFragment : BaseFragment() {
    var binding: FragmentLocationBinding? = null

    class Builder {
        fun build(): LocationFragment {
            return LocationFragment()
        }
    }

    lateinit var mapLanguage: MutableList<ItemLocationAdapter.Data>

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        super.onCreateView(inflater, container, savedInstanceState)
        binding = FragmentLocationBinding.inflate(inflater, container, false)
        val adapter = ItemLocationAdapter(getLanguages())
        object : ItemLocationAdapter.OnItemClick {
            override fun onClick(holder: ItemLocationAdapter.ViewHolder, dat: ItemLocationAdapter.Data, position: Int) {
                setUsersBorder(dat.code)
                ScanActivity.scanEntry(requireContext())
                requireActivity().finish()
            }
        }

        binding!!.location.setHasFixedSize(true)
        binding!!.location.adapter = adapter
        binding!!.location.layoutManager = LinearLayoutManager(activity)
        return binding!!.root
    }

    fun getLanguages(): List<ItemLocationAdapter.Data> {
        mapLanguage = ArrayList()
        val borders = ConfigPrefHelper.getBorders()
        for (entry in borders) {
            Log.i(TAG, "LANG " + entry.borderName + "|" + entry.fairName)
            mapLanguage.add(ItemLocationAdapter.Data(entry))
        }
        return mapLanguage
    }

    companion object {
        const val TAG = "LanguageFragment"
        fun builder(): Builder {
            return Builder()
        }
    }
}