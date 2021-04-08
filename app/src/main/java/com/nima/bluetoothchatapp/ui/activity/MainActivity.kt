package com.nima.bluetoothchatapp.ui.activity

import android.Manifest
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentTransaction
import com.nima.bluetoothchatapp.R
import com.nima.bluetoothchatapp.service.BluetoothChatService
import com.nima.bluetoothchatapp.ui.fragment.ChatFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private val PERMISSION_CODE = 100
    private val REQUEST_ENABLE_BT = 101
    private var bluetoothAdapter: BluetoothAdapter? = null
    private var bluetoothDevice: BluetoothDevice? = null
    private var mChatService: BluetoothChatService? = null
    private val str: String = "BLUETOOTH_CHAT_APPLICATION"
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        checkForPermission()
        setBluetoothAdapter()
        if (savedInstanceState == null){
            val transaction: FragmentTransaction = supportFragmentManager.beginTransaction()
            val fragment = ChatFragment()
            transaction.replace(R.id.sample_content_fragment,fragment)
            transaction.commit()
        }
    }

//    private fun setupChat() {
//
//        // Initialize the BluetoothChatService to perform bluetooth connections
//        mChatService = BluetoothChatService(this, mHandler)
//
//        // Initialize the buffer for outgoing messages
//        mOutStringBuffer = StringBuffer("")
//    }


    private fun enableDiscoverability() {
        val discoverableIntent: Intent =
            Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE).apply {
                putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 300)
            }
        startActivity(discoverableIntent)
    }

    private fun queryPairedDevices() {
        val pairedDevices: Set<BluetoothDevice>? = bluetoothAdapter?.bondedDevices
        pairedDevices?.forEach { device ->
            if (device.address == "38:D4:0B:DA:FF:FF") {
                bluetoothDevice = device
                //connectDevice(null, true)
            }
            val deviceName = device.name
            val deviceHardwareAddress = device.address // MAC address
            Log.d(
                "TAG", "queryPairedDevices: deviceName : $deviceName ," +
                        " MAC : $deviceHardwareAddress"
            )
        }
    }

    private fun setBluetoothAdapter() {
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter()
        if (bluetoothAdapter == null) {
            Toast.makeText(this, "the device doesn't support Bluetooth!", Toast.LENGTH_SHORT).show()
        }
    }

    private fun checkForPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            if (ContextCompat.checkSelfPermission(
                    baseContext,
                    Manifest.permission.ACCESS_BACKGROUND_LOCATION
                )
                != PackageManager.PERMISSION_GRANTED
            ) {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.ACCESS_BACKGROUND_LOCATION),
                    PERMISSION_CODE
                )
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        Log.d("TAG", "onActivityResult: $requestCode")
        when (requestCode) {
            REQUEST_ENABLE_BT -> {
                when (resultCode) {
                    RESULT_OK -> {
                        queryPairedDevices()
                    }
                    RESULT_CANCELED -> {
                        finish()
                    }
                }
            }
        }
    }


    override fun onResume() {
        super.onResume()

        // Performing this check in onResume() covers the case in which BT was
        // not enabled during onStart(), so we were paused to enable it...
        // onResume() will be called when ACTION_REQUEST_ENABLE activity returns.
        if (mChatService != null) {
            // Only if the state is STATE_NONE, do we know that we haven't started already
            if (mChatService!!.state == BluetoothChatService.STATE_NONE) {
                // Start the Bluetooth chat services
                mChatService!!.start()
            }
        }
    }
}