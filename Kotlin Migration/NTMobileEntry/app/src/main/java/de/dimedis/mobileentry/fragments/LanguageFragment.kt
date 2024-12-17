package de.dimedis.mobileentry.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import de.dimedis.mobileentry.ConfigPref
import de.dimedis.mobileentry.adapter.ItemAdapter
import de.dimedis.mobileentry.adapter.ItemAdapter.OnItemClick
import de.dimedis.mobileentry.databinding.FragmentLanguageBinding

class LanguageFragment : BaseFragment() {
    var binding: FragmentLanguageBinding? = null

    class Builder {
        fun build(): LanguageFragment {
            return LanguageFragment()
        }
    }

    lateinit var mapLanguage: List<ItemAdapter.Data>

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        super.onCreateView(inflater, container, savedInstanceState)
        binding = FragmentLanguageBinding.inflate(inflater, container, false)
        val adapter = ItemAdapter(languages)
        adapter.setOnItemClick(object : OnItemClick {
            override fun onClick(holder: ItemAdapter.ViewHolder?, dat: ItemAdapter.Data?, position: Int) {
                ConfigPref.currentLanguage = languages.toString()
                fragment(DeviceInitFragment.builder().build())
            }
        })
        binding?.language?.setHasFixedSize(true)
        binding?.language?.adapter = adapter
        binding?.language?.layoutManager = LinearLayoutManager(activity)
        return binding!!.root
    }

    val languages: List<ItemAdapter.Data>
        get() {
            mapLanguage = ArrayList()
            val gson = Gson()
            val container: String = ConfigPref.languages.toString()
            val mapLang = gson.fromJson<Map<String, String>>(container, object : TypeToken<Map<String, String>>() {}.type)
            for ((key, value) in mapLang) {
                Log.i(TAG, "LANG $key|$value")
                (mapLanguage as ArrayList<ItemAdapter.Data>).add(ItemAdapter.Data(key, value))
            }
            return mapLanguage as ArrayList<ItemAdapter.Data>
        }

    companion object {
        const val TAG = "LanguageFragment"
        fun builder(): Builder {
            return Builder()
        }
    }
}