package com.nima.bluetoothchatapp.ui.fragment

import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.os.Bundle
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.nima.bluetoothchatapp.Constants
import com.nima.bluetoothchatapp.R
import com.nima.bluetoothchatapp.devices.BLDevice
import com.nima.bluetoothchatapp.viewmodel.ChatListViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ChatListFragment: Fragment(R.layout.fragment_chat_list), PairedDevicesDialogFragment.OnClick {

    private var bluetoothAdapter: BluetoothAdapter? = null
    private lateinit var emptyChatList : TextView
    private lateinit var pairedDevices: FloatingActionButton
    private val viewMode: ChatListViewModel by viewModels()
    private var blDevices: List<BLDevice?>? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        getConnectedDevices()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        pairedDevices = view.findViewById(R.id.btn_mainActivity_pairedDevices)
        emptyChatList = view.findViewById(R.id.txt_chatListFragment_emptyList)
        showConnectedDevices()
        subscribeOnButtons()
        setBluetoothAdapter()
    }

    private fun showConnectedDevices() {
        blDevices?.let {
            if (blDevices!!.isNotEmpty()){
                submitDevices(it)
            }else emptyChatList.visibility = View.VISIBLE
        }
        if (blDevices ==null) emptyChatList.visibility = View.VISIBLE
    }

    private fun submitDevices(devices: List<BLDevice?>) {

    }

    private fun getConnectedDevices() {
        val devices = viewMode.getConnectedDevices()
        devices?.let {
            blDevices = it
        }
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
