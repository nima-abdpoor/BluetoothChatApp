package com.nima.bluetoothchatapp.ui.fragment

import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.nima.bluetoothchatapp.Constants
import com.nima.bluetoothchatapp.R
import com.nima.bluetoothchatapp.devices.BLDevice
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ChatListFragment: Fragment(R.layout.fragment_chat_list), PairedDevicesDialogFragment.OnClick {

    private var bluetoothAdapter: BluetoothAdapter? = null
    private lateinit var pairedDevices: FloatingActionButton

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        pairedDevices = view.findViewById(R.id.btn_mainActivity_pairedDevices)
        subscribeOnButtons()
        setBluetoothAdapter()
    }
    private fun setBluetoothAdapter() {
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter()
        if (bluetoothAdapter == null) {
            Toast.makeText(requireContext(), "the device doesn't support Bluetooth!", Toast.LENGTH_SHORT).show()
        }
    }

    private fun subscribeOnButtons() {
        pairedDevices.setOnClickListener {
            val blDevices = queryPairedDevices()
            blDevices?.let {
                val pairedDevicesDialogFragment = PairedDevicesDialogFragment(this, it)
                pairedDevicesDialogFragment.show(childFragmentManager, "FromMainActivityToPaired")
            }
        }
    }

    private fun queryPairedDevices(): List<BLDevice>? {
        val pairedDevices: Set<BluetoothDevice>? = bluetoothAdapter?.bondedDevices
        return pairedDevices?.map { BLDevice(it.name, it.address) }
    }

    private fun navigateToFragment(item: BLDevice) {
        val bundle = Bundle()
        bundle.putString(Constants.DEVICE_ADDRESS, item.deviceAddress)
        bundle.putString(Constants.DEVICE_NAME, item.deviceName)
        findNavController().navigate(R.id.action_chatListFragment_to_chatFragment,bundle)
    }

    override fun pairedDeviceSelected(position: Int, item: BLDevice) {
        navigateToFragment(item)
    }
}
