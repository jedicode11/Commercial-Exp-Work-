package de.dimedis.mobileentry.fragments.menus

import android.view.*
import de.dimedis.mobileentry.databinding.FragmentMenuLogfileBinding
import de.dimedis.mobileentry.util.*
import java.lang.Exception

import android.os.Bundle
import android.widget.CompoundButton

class LogfileMenuFragment : AbsSettingsMenuFragment() {
    var binding: FragmentMenuLogfileBinding? = null

    class Builder {
        fun build(): LogfileMenuFragment {
            return LogfileMenuFragment()
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        super.onCreateView(inflater, container, savedInstanceState)
        binding = FragmentMenuLogfileBinding.inflate(inflater, container, false)
        binding!!.okCancel.ok.setOnClickListener {
            onClickOK() }
        binding!!.okCancel.cancel.setOnClickListener {
            onClickCancel() }
        return binding!!.root
    }

    override fun init() {
        super.init()
        binding!!.writeLocal.isChecked = mSettings!!.writeLogfile
        binding!!.writeLocal.setOnCheckedChangeListener { buttonView: CompoundButton?, isChecked: Boolean ->
            mSettings!!.writeLogfile = isChecked
            if (isChecked) try {
                Logger.clearLogFile()
            } catch (ex: Exception) {
                ex.printStackTrace()
            }
        }
    }

    companion object {
        fun builder(): Builder {
            return Builder()
        }
    }
}