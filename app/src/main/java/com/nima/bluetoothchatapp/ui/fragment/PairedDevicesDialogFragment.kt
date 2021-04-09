package com.nima.bluetoothchatapp.ui.fragment

import android.os.Bundle
import android.view.View
import androidx.fragment.app.DialogFragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.nima.bluetoothchatapp.R
import com.nima.bluetoothchatapp.adapter.PairedDevicesAdapter
import com.nima.bluetoothchatapp.devices.BLDevice

class PairedDevicesDialogFragment(
    private val onClick : OnClick,
    private val blDevices: List<BLDevice>
    ): DialogFragment(R.layout.fragment_paired_devices),PairedDevicesAdapter.Interaction {
    private lateinit var pairedDevicesAdapter: PairedDevicesAdapter
    private lateinit var recycler : RecyclerView
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        recycler = view.findViewById(R.id.recycler_pairedDevice_models)
        initRecyclerView()
        pairedDevicesAdapter.submitList(blDevices)
    }
    private fun initRecyclerView() {
        recycler.apply {
            layoutManager = LinearLayoutManager(requireContext())
            pairedDevicesAdapter = PairedDevicesAdapter(this@PairedDevicesDialogFragment)
            adapter = pairedDevicesAdapter
        }
    }

    interface OnClick {
        fun pairedDeviceSelected(position: Int, item: BLDevice)
    }

    override fun onItemSelected(position: Int, item: BLDevice) {
        onClick.pairedDeviceSelected(position,item)
        dismiss()
    }
}