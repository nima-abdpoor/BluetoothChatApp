package com.nima.bluetoothchatapp.ui.fragment

import android.app.ActionBar
import android.app.Activity
import android.bluetooth.BluetoothAdapter
import android.content.Intent
import android.os.*
import android.util.Log
import android.view.*
import android.view.inputmethod.EditorInfo
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.viewModels
import com.nima.bluetoothchatapp.*
import com.nima.bluetoothchatapp.adapter.ChatAdapter
import com.nima.bluetoothchatapp.chat.MessageAck
import com.nima.bluetoothchatapp.chat.MessageStatus
import com.nima.bluetoothchatapp.service.BluetoothChatService
import com.nima.bluetoothchatapp.viewmodel.ChatViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@AndroidEntryPoint
class ChatFragment : Fragment() {
    // Layout Views
    private var mConversationView: ListView? = null
    private var mOutEditText: EditText? = null
    private var mSendButton: Button? = null
    private var chatId = "-1"
    private lateinit var chatAdapter: ChatAdapter

    private lateinit var randomUIDGenerator: RandomUIDGenerator
    private val viewMode: ChatViewModel by viewModels()



    private var mConnectedDeviceName: String? = null
    private var mConnectedDeviceAddress: String? = null
    private var myDeviceAddress: String? = null


    private var mConversationArrayAdapter: ArrayAdapter<String>? = null
    private var mOutStringBuffer: StringBuffer? = null
    private var mBluetoothAdapter: BluetoothAdapter? = null
    private var mChatService: BluetoothChatService? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
        // Get local Bluetooth adapter
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter()
        chatId = requireArguments().getString(Constants.DEVICE_ADDRESS,"") ?: "-1"
        // If the adapter is null, then Bluetooth is not supported
        if (mBluetoothAdapter == null) {
            val activity: FragmentActivity = requireActivity()
            Toast.makeText(activity, "Bluetooth is not available", Toast.LENGTH_LONG).show()
            activity.finish()
        } else {
            mBluetoothAdapter?.let {
                myDeviceAddress = it.address
            }
        }
    }


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_bluetooth_chat, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        mConversationView = view.findViewById<View>(R.id.`in`) as ListView
        mOutEditText = view.findViewById<View>(R.id.edit_text_out) as EditText
        mSendButton = view.findViewById<View>(R.id.button_send) as Button
        init
        randomUIDGenerator = RandomUIDGenerator()
        getChatHistory()
    }

    private fun setupChat() {
        Log.d(TAG, "setupChat()")

        // Initialize the array adapter for the conversation thread
        mConversationArrayAdapter = ArrayAdapter(requireActivity(), R.layout.message)
        mConversationView!!.adapter = mConversationArrayAdapter

        // Initialize the compose field with a listener for the return key
        mOutEditText!!.setOnEditorActionListener(mWriteListener)

        // Initialize the send button with a listener that for click events
        mSendButton!!.setOnClickListener { // Send a message using content of the edit text widget
            val view: View? = view
            if (null != view) {
                val textView = view.findViewById<View>(R.id.edit_text_out) as TextView
                val message = textView.text.toString()
                val m = MessageAck(
                    false,
                    MessageStatus.MessageStatusNone(),
                    randomUIDGenerator.generate(),
                    message
                )
                sendMessage(m)
                //insertMessage(message, chatId, m.UID, myDeviceAddress!!, true, -1)
            }
        }

        // Initialize the BluetoothChatService to perform bluetooth connections
        mChatService = BluetoothChatService(
            activity,
            mHandler
        )

        // Initialize the buffer for outgoing messages
        mOutStringBuffer = StringBuffer("")
    }

    /**
     * Makes this device discoverable for 300 seconds (5 minutes).
     */
//    private fun ensureDiscoverable() {
//        if (mBluetoothAdapter!!.scanMode !=
//            BluetoothAdapter.SCAN_MODE_CONNECTABLE_DISCOVERABLE
//        ) {
//            val discoverableIntent = Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE)
//            discoverableIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 300)
//            startActivity(discoverableIntent)
//        }
//    }
    private fun getChatHistory(){
        CoroutineScope(Dispatchers.Main).launch {
            viewMode.getAllMessages(chatId)?.collect {
                withContext(Dispatchers.Main){
                    it.forEach {
                        Log.d(TAG, "getChatHistory: $it")
                    }
                }
            }
        }
    }

    private fun insertMessage(
        writeMessage: String,
        chatId: String,
        uId: String,
        senderId: String,
        isMe: Boolean,
        fatherId: Int
    ) {
        viewMode.insertMessage(writeMessage, chatId, uId, senderId, isMe, fatherId)
    }

    private fun sendMessage(message: MessageAck) {
        if (mChatService!!.state != BluetoothChatService.STATE_CONNECTED) {
            Toast.makeText(activity, R.string.not_connected, Toast.LENGTH_SHORT).show()
            return
        }
        val m = message.content
        if (m.isNotEmpty()) {
            val send = message.encode().toByteArray()
            mChatService!!.write(send)
            mOutStringBuffer!!.setLength(0)
            mOutEditText!!.setText(mOutStringBuffer)
        }
    }

    private fun handleConnectStatus() {
        setStatus(getString(R.string.title_connected_to, mConnectedDeviceName))
        handleFailedMessages()
    }

    private fun handleFailedMessages() {
        CoroutineScope(Dispatchers.Main).launch {
            viewMode.getMyFailedMessages(chatId)?.collect { it ->
                if (it.isNotEmpty()){
                    it.forEach {
                        it?.let { message ->
                            sendMessage(
                                MessageAck(
                                    isMe = message.content().isMe,
                                    status = message.content().status,
                                    UID = message.content().uId,
                                    content = message.content().content
                                )
                            )
                        }
                    }
                }
            }
        }
    }

    private fun handleWriteMessage(mAck: MessageAck) {
        if (mAck.status == MessageStatus.MessageStatusNone()) {
            mConversationArrayAdapter!!.add("Me:  ${mAck.content}")
            insertMessage(mAck.content, chatId, mAck.UID, myDeviceAddress!!, true, -1)
        }
    }

    private fun handleReadMessage(readMessage: String) {
        val mAck = readMessage.decode()
        if (mAck.status == MessageStatus.MessageStatusSeen()) {
            updateMyMessageStatus(mAck)
        } else {
            Log.d(TAG, "handleReadMessage: $readMessage")
            storeTheirMessage(mAck)
            sendAck(mAck)
            mConversationArrayAdapter!!.add("$mConnectedDeviceName:  ${mAck.content}")
        }
    }

    private fun updateMyMessageStatus(mAck: MessageAck) {
        mAck.apply {
            viewMode.updateMyMessageStatus(status, UID, content)
        }
    }

    private fun storeTheirMessage(mAck: MessageAck) {
        insertMessage(
            writeMessage = mAck.content,
            chatId = chatId,
            uId = mAck.UID,
            senderId = mConnectedDeviceAddress!!,
            isMe = false,
            fatherId = -1
        )
    }

    private fun sendAck(message: MessageAck) {
        message.isMe = false
        message.status = MessageStatus.MessageStatusSeen()
        sendMessage(message)
    }

    /**
     * The action listener for the EditText widget, to listen for the return key
     */
    private val mWriteListener =
        TextView.OnEditorActionListener { view, actionId, event -> // If the action is a key-up event on the return key, send the message
            if (actionId == EditorInfo.IME_NULL && event.action == KeyEvent.ACTION_UP) {
                //val message = view.text.toString()
                //sendMessage(message)
            }
            true
        }

    /**
     * Updates the status on the action bar.
     *
     * @param resId a string resource ID
     */
    private fun setStatus(resId: Int) {
        val activity: FragmentActivity = activity ?: return
        val actionBar: ActionBar = activity.actionBar ?: return
        actionBar.setSubtitle(resId)
    }

    /**
     * Updates the status on the action bar.
     *
     * @param subTitle status
     */
    private fun setStatus(subTitle: CharSequence) {
        val activity: FragmentActivity = activity ?: return
        val actionBar: ActionBar = activity.actionBar ?: return
        actionBar.subtitle = subTitle
    }

    /**
     * The Handler that gets information back from the BluetoothChatService
     */
    private val mHandler: Handler = object : Handler(Looper.getMainLooper()) {
        override fun handleMessage(msg: Message) {
            when (msg.what) {
                Constants.MESSAGE_STATE_CHANGE -> when (msg.arg1) {
                    BluetoothChatService.STATE_CONNECTED -> {
                        handleConnectStatus()
                    }
                    BluetoothChatService.STATE_CONNECTING -> setStatus(R.string.title_connecting)
                    BluetoothChatService.STATE_LISTEN, BluetoothChatService.STATE_NONE -> setStatus(
                        R.string.title_not_connected
                    )
                }
                Constants.MESSAGE_WRITE -> {
                    val writeBuf = msg.obj as ByteArray
                    // construct a string from the buffer
                    val writeMessage = String(writeBuf)
                    val message = writeMessage.decode()
                    handleWriteMessage(message)
                }
                Constants.MESSAGE_READ -> {
                    val readBuf = msg.obj as ByteArray
                    // construct a string from the valid bytes in the buffer
                    val readMessage = String(readBuf, 0, msg.arg1)
                    handleReadMessage(readMessage)
                }
                Constants.MESSAGE_DEVICE_NAME -> {
                    // save the connected device's name
                    mConnectedDeviceName = msg.data.getString(Constants.DEVICE_NAME)
                    mConnectedDeviceAddress = msg.data.getString(Constants.DEVICE_ADDRESS)
                    chatId = mConnectedDeviceAddress ?: "-1"
                    Toast.makeText(
                        requireContext(), "Connected to "
                                + mConnectedDeviceName, Toast.LENGTH_SHORT
                    ).show()
                }
                Constants.MESSAGE_TOAST -> Toast.makeText(
                    requireContext(), msg.data.getString(Constants.TOAST),
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        when (requestCode) {
            REQUEST_CONNECT_DEVICE_SECURE ->                 // When DeviceListActivity returns with a device to connect
                if (resultCode == Activity.RESULT_OK) {
                    data?.let { connectDevice(it, true) }
                }
            REQUEST_CONNECT_DEVICE_INSECURE ->                 // When DeviceListActivity returns with a device to connect
                if (resultCode == Activity.RESULT_OK) {
                    data?.let { connectDevice(it, false) }
                }
            REQUEST_ENABLE_BT ->                 // When the request to enable Bluetooth returns
                if (resultCode == Activity.RESULT_OK) {
                    // Bluetooth is now enabled, so set up a chat session
                    setupChat()
                } else {
                    // User did not enable Bluetooth or an error occurred
                    Log.d(TAG, "BT not enabled")
                    Toast.makeText(
                        activity, R.string.bt_not_enabled_leaving,
                        Toast.LENGTH_SHORT
                    ).show()
                    activity?.finish()
                }
        }
    }

    private fun connectDevice(data: Intent, secure: Boolean) {
        val address = data.extras?.getString(DeviceListActivity.EXTRA_DEVICE_ADDRESS)
        val device = mBluetoothAdapter!!.getRemoteDevice(address)
        mChatService!!.connect(device, secure)
    }
    private fun connectDevice() {
        val device = mBluetoothAdapter!!.getRemoteDevice(chatId)
        mChatService!!.connect(device, true)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.chat_fragment_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.connect_scan -> {
//                val serverIntent = Intent(activity, DeviceListActivity::class.java)
//                startActivityForResult(serverIntent, REQUEST_CONNECT_DEVICE_SECURE)
                connectDevice()
                return true
            }
        }
        return false
    }
    override fun onStart() {
        super.onStart()
        if (!mBluetoothAdapter!!.isEnabled) {
            val enableIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
            startActivityForResult(enableIntent, REQUEST_ENABLE_BT)
        } else if (mChatService == null) {
            setupChat()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        if (mChatService != null) {
            mChatService!!.stop()
        }
    }

    override fun onResume() {
        super.onResume()
        if (mChatService != null) {
            if (mChatService!!.state == BluetoothChatService.STATE_NONE) {
                mChatService!!.start()
            }
        }
    }

    companion object {
        private const val TAG = "BluetoothChatFragment"

        // Intent request codes
        private const val REQUEST_CONNECT_DEVICE_SECURE = 1
        private const val REQUEST_CONNECT_DEVICE_INSECURE = 2
        private const val REQUEST_ENABLE_BT = 3
    }
}