package com.nima.bluetoothchatapp

import android.Manifest
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothServerSocket
import android.bluetooth.BluetoothSocket
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import java.io.IOException
import java.util.*

class MainActivity : AppCompatActivity() {
    private val PERMISSION_CODE = 100
    private val REQUEST_ENABLE_BT = 101
    private var bluetoothAdapter: BluetoothAdapter? = null
    private var bluetoothDevice : BluetoothDevice? = null
    private val str :String = "BLUETOOTH_CHAT_APPLICATION"
    private lateinit var myBluetoothService : MyBluetoothService
    private val uuid : UUID = UUID.nameUUIDFromBytes(str.toByteArray())
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        checkForPermission()
        setBluetoothAdapter()
        enableBluetooth()
        val handler = object : Handler() {
            override fun handleMessage(msg: Message) {
                val bundle  = msg.data
                Log.d("TAG", "handleMessage: ${bundle.getString("key")}")
                if (msg.what == 0 ){
                    Log.d("TAG", "handleMessage: READ_TIME ${msg.obj}")
                }
            }
        }
        myBluetoothService =MyBluetoothService(handler)

    }

    private fun enableBluetooth() {
        if (bluetoothAdapter?.isEnabled == false) {
            val enableBtIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT)
        } else {
            queryPairedDevices()
            ConnectThread(bluetoothDevice!!).start()
            //AcceptThread().start()
            discoverDevices()
            enableDiscoverability()
        }
    }

    private fun enableDiscoverability() {
        val discoverableIntent: Intent = Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE).apply {
            putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 300)
        }
        startActivity(discoverableIntent)
    }

    private fun discoverDevices() {
        val filter = IntentFilter(BluetoothDevice.ACTION_FOUND)
        registerReceiver(receiver, filter)
    }

    private fun queryPairedDevices() {
        val pairedDevices: Set<BluetoothDevice>? = bluetoothAdapter?.bondedDevices
        pairedDevices?.forEach { device ->
            if(device.address == "04:B1:A1:8B:79:92"){
                bluetoothDevice = device
            }
            val deviceName = device.name
            val deviceHardwareAddress = device.address // MAC address
            Log.d("TAG", "queryPairedDevices: deviceName : $deviceName ," +
                    " MAC : $deviceHardwareAddress")
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
            if (ContextCompat.checkSelfPermission(baseContext,
                            Manifest.permission.ACCESS_BACKGROUND_LOCATION)
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(
                        this,
                        arrayOf(Manifest.permission.ACCESS_BACKGROUND_LOCATION),
                        PERMISSION_CODE)
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
                        discoverDevices()
                    }
                    RESULT_CANCELED -> {
                        finish()
                    }
                }
            }
        }
    }

    private val receiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            val action: String = intent.action.toString()
            when (action) {
                BluetoothDevice.ACTION_FOUND -> {
                    // Discovery has found a device. Get the BluetoothDevice
                    // object and its info from the Intent.
                    val device: BluetoothDevice? =
                            intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE)
                    val deviceName = device?.name
                    val deviceHardwareAddress = device?.address // MAC address
                    Log.d("TAG", "Discover_Devices: deviceName : $deviceName ," +
                            " MAC : $deviceHardwareAddress")
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(receiver)
    }
    private inner class AcceptThread : Thread() {
        private val mmServerSocket: BluetoothServerSocket? by lazy(LazyThreadSafetyMode.NONE) {
            bluetoothAdapter?.listenUsingInsecureRfcommWithServiceRecord(
                getString(R.string.app_name),
                uuid)
        }

        override fun run() {
            // Keep listening until exception occurs or a socket is returned.
            var shouldLoop = true
            while (shouldLoop) {
                val socket: BluetoothSocket? = try {
                    Log.d("AcceptThread", "accept called")
                    mmServerSocket?.accept()
                } catch (e: IOException) {
                    Log.e("AcceptThread", "Socket's accept() method failed", e)
                    shouldLoop = false
                    null
                }
                socket?.also {
                    Log.d("AcceptThread", "socket is not Null!")
                    val By = "salam".toByteArray()
                    myBluetoothService.ConnectedThread(it).start()
                    //myBluetoothService.ConnectedThread(it).write(By)
                    mmServerSocket?.close()
                    shouldLoop = false
                }
            }
        }

        // Closes the connect socket and causes the thread to finish.
        fun cancel() {
            try {
                mmServerSocket?.close()
            } catch (e: IOException) {
                Log.e("TAG", "Could not close the connect socket", e)
            }
        }
    }
    private inner class ConnectThread(device: BluetoothDevice) : Thread() {
        private val mmSocket: BluetoothSocket? by lazy(LazyThreadSafetyMode.NONE) {
            device.createRfcommSocketToServiceRecord(uuid)
        }

        public override fun run() {
            // Cancel discovery because it otherwise slows down the connection.
            bluetoothAdapter?.cancelDiscovery()
            try {
                mmSocket?.use { socket ->
                    // Connect to the remote device through the socket. This call blocks
                    // until it succeeds or throws an exception.
                    socket.connect()
                    Log.d("TAG", "Bluetooth_Socket: connected called")
                    // The connection attempt succeeded. Perform work associated with
                    // the connection in a separate thread.
                    //manageMyConnectedSocket(socket)
                    val By = "salam".toByteArray()
                    myBluetoothService.ConnectedThread(socket).start()
                   //myBluetoothService.ConnectedThread(socket).write(By)
                }
            }catch (e : IOException){
                Log.d("TAG", "Bluetooth_Socket: exception ${e.message}")
            }

        }

        // Closes the client socket and causes the thread to finish.
        fun cancel() {
            try {
                mmSocket?.close()
            } catch (e: IOException) {
                Log.e("TAG", "Could not close the client socket", e)
            }
        }
    }
}