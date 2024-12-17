package de.dimedis.mobileentry.fragments

import de.dimedis.mobileentry.databinding.FragmentVoidticketBinding

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

class VoidTicketFragment : BaseFragment() {
    var binding: FragmentVoidticketBinding? = null
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        binding = FragmentVoidticketBinding.inflate(inflater, container, false)
        binding!!.ok.setOnClickListener {
            back() }
        binding!!.cancel.setOnClickListener {
            back() }
        return binding!!.root
    }

    class Builder {
        fun build(): VoidTicketFragment {
            return VoidTicketFragment()
        }
    }

    companion object {
        fun builder(): Builder {
            return Builder()
        }
    }
}