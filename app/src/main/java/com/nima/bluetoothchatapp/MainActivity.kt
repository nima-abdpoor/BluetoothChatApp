package com.nima.bluetoothchatapp

import android.Manifest
import android.app.ActionBar
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
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
import androidx.fragment.app.FragmentActivity
import java.util.*

class MainActivity : AppCompatActivity() {
    private val PERMISSION_CODE = 100
    private val REQUEST_ENABLE_BT = 101
    private var bluetoothAdapter: BluetoothAdapter? = null
    private var bluetoothDevice: BluetoothDevice? = null
    private var mChatService: BluetoothChatService? = null
    private var mConnectedDeviceName: String? = null
    private var mOutStringBuffer: StringBuffer? = null
    private val str: String = "BLUETOOTH_CHAT_APPLICATION"
    private lateinit var myBluetoothService: MyBluetoothService
    private val uuid: UUID = UUID.nameUUIDFromBytes(str.toByteArray())
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        checkForPermission()
        setBluetoothAdapter()
        enableBluetooth()
        val handler = object : Handler() {
            override fun handleMessage(msg: Message) {
                val bundle = msg.data
                Log.d("TAG", "handleMessage: ${bundle.getString("key")}")
                if (msg.what == 0) {
                    Log.d("TAG", "handleMessage: READ_TIME ${msg.obj}")
                }
            }
        }
        myBluetoothService = MyBluetoothService(handler)

    }

    private fun enableBluetooth() {
        if (bluetoothAdapter?.isEnabled == false) {
            val enableBtIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT)
        } else {
            setupChat()
            queryPairedDevices()
            //AcceptThread().start()
            discoverDevices()
            enableDiscoverability()
        }
    }

    private fun setupChat() {

        // Initialize the BluetoothChatService to perform bluetooth connections
        mChatService = BluetoothChatService(this, mHandler)

        // Initialize the buffer for outgoing messages
        mOutStringBuffer = StringBuffer("")
    }

    private fun setStatus(subTitle: CharSequence) {
        val activity: FragmentActivity = this
        val actionBar: ActionBar = activity.getActionBar() ?: return
        actionBar.subtitle = subTitle
    }

    private val mHandler: Handler = object : Handler() {
        override fun handleMessage(msg: Message) {
            when (msg.what) {
                Constants.MESSAGE_STATE_CHANGE -> when (msg.arg1) {
                    BluetoothChatService.STATE_CONNECTED -> {
                        setStatus(getString(R.string.app_name))
                        //mConversationArrayAdapter.clear()
                    }
                    BluetoothChatService.STATE_CONNECTING -> setStatus(getString(R.string.title_connecting))
                    BluetoothChatService.STATE_LISTEN, BluetoothChatService.STATE_NONE -> setStatus(
                        getString(R.string.title_not_connected)
                    )
                }
                Constants.MESSAGE_WRITE -> {
                    val writeBuf = msg.obj as ByteArray
                    // construct a string from the buffer
                    val writeMessage = String(writeBuf)
                    Log.d("TAG", "handleMessage: ME :: $writeMessage")
                    //mConversationArrayAdapter.add("Me:  $writeMessage")
                }
                Constants.MESSAGE_READ -> {
                    val readBuf = msg.obj as ByteArray
                    // construct a string from the valid bytes in the buffer
                    val readMessage = String(readBuf, 0, msg.arg1)
                    Log.d("TAG", "handleMessage: :: $readMessage")
                    //mConversationArrayAdapter.add(mConnectedDeviceName + ":  " + readMessage)
                }
                Constants.MESSAGE_DEVICE_NAME -> {
                    // save the connected device's name
                    mConnectedDeviceName = msg.data.getString(Constants.DEVICE_NAME)
                    Toast.makeText(
                        this@MainActivity, "Connected to "
                                + mConnectedDeviceName, Toast.LENGTH_SHORT
                    ).show()

                }
                Constants.MESSAGE_TOAST ->
                    Toast.makeText(
                        this@MainActivity, msg.data.getString(Constants.TOAST),
                        Toast.LENGTH_SHORT
                    ).show()
            }
        }
    }

    private fun enableDiscoverability() {
        val discoverableIntent: Intent =
            Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE).apply {
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
            if (device.address == "38:D4:0B:DA:FF:FF") {
                bluetoothDevice = device
                connectDevice(null, true)
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
                    Log.d(
                        "TAG", "Discover_Devices: deviceName : $deviceName ," +
                                " MAC : $deviceHardwareAddress"
                    )
                }
            }
        }
    }
    private fun connectDevice(data: Intent? = null, secure: Boolean) {
        // Get the device MAC address
//        val address = data.extras?.getString(DeviceListActivity.EXTRA_DEVICE_ADDRESS)
//        // Get the BluetoothDevice object
//        val device: BluetoothDevice = mBluetoothAdapter.getRemoteDevice(address)
        // Attempt to connect to the device
        mChatService!!.connect(bluetoothDevice, secure)
        sendMessage("salam")
    }
    private fun sendMessage(message: String) {
        // Check that we're actually connected before trying anything
//        if (mChatService!!.state != BluetoothChatService.STATE_CONNECTED) {
//            Toast.makeText(this, R.string.not_connected, Toast.LENGTH_SHORT).show()
//            return
//        }

        // Check that there's actually something to send
        if (message.isNotEmpty()) {
            // Get the message bytes and tell the BluetoothChatService to write
            val send = message.toByteArray()
            mChatService!!.write(send)
            Log.d("TAG", "sendMessage: sent1")
            // Reset out string buffer to zero and clear the edit text field
            mOutStringBuffer!!.setLength(0)
            //mOutEditText.setText(mOutStringBuffer)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(receiver)
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