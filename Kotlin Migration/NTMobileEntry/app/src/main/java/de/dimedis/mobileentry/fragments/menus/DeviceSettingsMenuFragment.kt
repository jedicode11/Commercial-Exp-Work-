package de.dimedis.mobileentry.fragments.menus

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CompoundButton
import android.widget.SeekBar.OnSeekBarChangeListener
import android.widget.SeekBar
import de.dimedis.mobileentry.databinding.FragmentMenuDeviceSettingsBinding

class DeviceSettingsMenuFragment : AbsSettingsMenuFragment() {
    var binding: FragmentMenuDeviceSettingsBinding? = null

    class Builder {
        fun build(): DeviceSettingsMenuFragment {
            return DeviceSettingsMenuFragment()
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        super.onCreateView(inflater, container, savedInstanceState)
        binding = FragmentMenuDeviceSettingsBinding.inflate(inflater, container, false)
        binding!!.okCancel.ok.setOnClickListener {
            onClickOK() }
        binding!!.okCancel.cancel.setOnClickListener {
            onClickCancel() }
        return binding!!.root
    }

    override fun init() {
        super.init()
        binding!!.vibration.isChecked = mSettings!!.vibration
        binding!!.cameraReaderSetting.isChecked = mSettings!!.camReader
        binding!!.sound.max = mSettings!!.soundMax
        binding!!.sound.progress = mSettings!!.sound
        binding!!.brightness.max = mSettings!!.brightnessMax
        binding!!.brightness.progress = mSettings!!.brightness
        binding!!.permanentScanModeSetting.isChecked = mSettings!!.permScanMode
        binding!!.vibration.setOnCheckedChangeListener { buttonView: CompoundButton?, isChecked: Boolean ->
            mSettings!!.vibration = isChecked
        }
        binding!!.cameraReaderSetting.setOnCheckedChangeListener { buttonView: CompoundButton?, isChecked: Boolean ->
            mSettings!!.camReader = isChecked
        }
        binding!!.permanentScanModeSetting.setOnCheckedChangeListener { buttonView: CompoundButton?, isChecked: Boolean ->
            mSettings!!.permScanMode = isChecked
        }
        binding!!.sound.setOnSeekBarChangeListener(object : OnSeekBarChangeListener {

        override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
                if (fromUser) {
                    mSettings!!.sound = progress
                    seekBar.progress = progress
                }
            }
        override fun onStartTrackingTouch(seekBar: SeekBar?) {}
        override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })

        binding!!.brightness.setOnSeekBarChangeListener(object : OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                mSettings!!.brightness = progress
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })
    }

    companion object {
        private const val TAG = "Device Settings"
        fun builder(): Builder {
            return Builder()
        }
    }
}