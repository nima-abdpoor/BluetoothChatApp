package com.nima.bluetoothchatapp.ui.fragment

import android.app.Activity
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.nima.bluetoothchatapp.Constants
import com.nima.bluetoothchatapp.R
import com.nima.bluetoothchatapp.adapter.ChatListAdapter
import com.nima.bluetoothchatapp.devices.BLDevice
import com.nima.bluetoothchatapp.viewmodel.ChatListViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ChatListFragment : Fragment(R.layout.fragment_chat_list), PairedDevicesDialogFragment.OnClick,
    ChatListAdapter.Interaction {

    private lateinit var chatListAdapter: ChatListAdapter
    private var bluetoothAdapter: BluetoothAdapter? = null
    private lateinit var emptyChatList: TextView
    private lateinit var pairedDevices: FloatingActionButton
    private lateinit var recycler: RecyclerView
    private val viewMode: ChatListViewModel by viewModels()
    private var blDevices: List<BLDevice?>? = null
    private val REQUEST_ENABLE_BT = 3

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        pairedDevices = view.findViewById(R.id.btn_mainActivity_pairedDevices)
        emptyChatList = view.findViewById(R.id.txt_chatListFragment_emptyList)
        recycler = view.findViewById(R.id.recycler_chatListF_items)
        getConnectedDevices()
        initRecyclerView()
        subscribeOnButtons()
        setBluetoothAdapter()
    }

    private fun initRecyclerView() {
        recycler.apply {
            layoutManager = LinearLayoutManager(requireContext())
            chatListAdapter = ChatListAdapter(this@ChatListFragment)
            adapter = chatListAdapter
        }
    }

    private fun showConnectedDevices(devices: List<BLDevice?>) {
        if (devices.isNotEmpty()) {
            submitDevices(devices)
        } else emptyChatList.visibility = View.VISIBLE
    }

    private fun submitDevices(devices: List<BLDevice?>) {
        chatListAdapter.submitList(devices)
    }

    private fun getConnectedDevices() {
        viewMode.getConnectedDevices().observe(viewLifecycleOwner) { device ->
            blDevices = device
            Log.d("TAG", "getConnectedDevices: $device")
            showConnectedDevices(device)
        }
    }

    private fun setBluetoothAdapter() {
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter()
        if (bluetoothAdapter == null) {
            Toast.makeText(
                requireContext(),
                "the device doesn't support Bluetooth!",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    private fun subscribeOnButtons() {
        pairedDevices.setOnClickListener {
            if (bluetoothAdapter?.isEnabled == false) {
                val enableBtIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
                startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT)
            }
            else showPairedDevices()
        }
    }

    private fun showPairedDevices() {
        val blDevices = queryPairedDevices()
        blDevices?.let {
            val pairedDevicesDialogFragment = PairedDevicesDialogFragment(this, it)
            pairedDevicesDialogFragment.show(childFragmentManager, "FromMainActivityToPaired")
        }
    }


    private fun insertDevice(item: BLDevice) {
        blDevices?.let {
            if (it.contains(item)) return else viewMode.insertConnectedDevice(item)
        }
        if (blDevices == null) {
            viewMode.insertConnectedDevice(item)
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
        findNavController().navigate(R.id.action_chatListFragment_to_chatFragment, bundle)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when(requestCode){
            REQUEST_ENABLE_BT ->{
                if (resultCode == Activity.RESULT_OK) showPairedDevices() else {
                    Toast.makeText(
                        requireContext(), R.string.bt_not_enabled_leaving,
                        Toast.LENGTH_SHORT
                    ).show()
                    activity?.finish()
                }
            }
        }
    }

    override fun pairedDeviceSelected(position: Int, item: BLDevice) {
        insertDevice(item)
        navigateToFragment(item)
    }


    override fun onItemSelected(position: Int, item: BLDevice) {
        navigateToFragment(item)
    }
}
