package com.nima.bluetoothchatapp.ui.fragment

import android.os.Bundle
import android.view.View
import androidx.fragment.app.DialogFragment
import com.nima.bluetoothchatapp.R
import com.nima.bluetoothchatapp.service.BLDevice

class PairedDevicesDialogFragment(
    private val onClick : OnClick,
    private val blDevices: List<BLDevice>
    ): DialogFragment(R.layout.fragment_paired_devices) {
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }

    interface OnClick {
        fun onCancel(blDevice: BLDevice)
    }
}