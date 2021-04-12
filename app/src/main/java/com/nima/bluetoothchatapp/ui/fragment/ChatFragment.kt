package com.nima.bluetoothchatapp.ui.fragment

import android.annotation.SuppressLint
import android.app.Activity
import android.bluetooth.BluetoothAdapter
import android.content.Intent
import android.os.*
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
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
class ChatFragment : Fragment(R.layout.fragment_chat) {
    // Layout Views
    private lateinit var mOutEditText: EditText
    private lateinit var mSendButton: Button
    private lateinit var connectionState : Button
    private lateinit var recycler: RecyclerView
    private lateinit var noMessages: TextView

    private lateinit var chatAdapter: ChatAdapter

    private lateinit var randomUIDGenerator: RandomUIDGenerator

    private val viewMode: ChatViewModel by viewModels()

    private var mConnectedDeviceName: String? = null
    private var mConnectedDeviceAddress: String? = null
    private var myDeviceAddress: String? = null


    private var chatId = "-1"
    private var mOutStringBuffer: StringBuffer? = null
    private var mBluetoothAdapter: BluetoothAdapter? = null
    private var mChatService: BluetoothChatService? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        checkForBluetoothAdapter()
        chatId = requireArguments().getString(Constants.DEVICE_ADDRESS, "") ?: "-1"
        randomUIDGenerator = RandomUIDGenerator()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        mOutEditText = view.findViewById(R.id.edt_chatF_message)
        mSendButton = view.findViewById(R.id.button_send)
        connectionState = view.findViewById(R.id.btn_chatF_connectionState)
        recycler = view.findViewById(R.id.recycler_chatF_items)
        noMessages = view.findViewById(R.id.txt_chatF_noMessage)
        initRecyclerView()
        subscribeOnButtons()
        subscribeOnChatMessages()
    }

    @SuppressLint("HardwareIds")
    private fun checkForBluetoothAdapter() {
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter()
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

    private fun initRecyclerView() {
        recycler.apply {
            layoutManager = LinearLayoutManager(requireContext())
            chatAdapter = ChatAdapter()
            adapter = chatAdapter
        }
    }

    private fun subscribeOnButtons() {
        connectionState.setOnClickListener {
            connectDevice()
            it.visibility = View.GONE
        }
    }

    private fun subscribeOnChatMessages() {
        CoroutineScope(Dispatchers.Main).launch {
            viewMode.getAllMessages(chatId)?.collect { messages ->
                showMessages(messages)
                withContext(Dispatchers.Main) {
                    messages.forEach {
                        Log.d(TAG, "getChatHistory: $it")
                    }
                }
            }
        }
    }

    private fun changeStatus(state : String){
        connectionState.apply {
            text = state
            visibility = View.VISIBLE
        }
    }

    private fun showMessages(messages: List<com.nima.bluetoothchatapp.chat.Message?>) {
        if(messages.isNotEmpty()){
            showEmptyMessageView(false)
            chatAdapter.submitList(messages)
            recycler.smoothScrollToPosition(messages.size)
        }
        else if (messages.isEmpty()) showEmptyMessageView(true)
    }
    private fun showEmptyMessageView(show :Boolean){
        noMessages.isVisible = show
        recycler.isVisible = !show
    }

    private fun setupChat() {
        mSendButton.setOnClickListener {
            val view: View? = view
            if (null != view) {
                val textView = view.findViewById<View>(R.id.edt_chatF_message) as TextView
                val message = textView.text.toString()
                val m = MessageAck(
                    true,
                    MessageStatus.MessageStatusNone(),
                    randomUIDGenerator.generate(),
                    message
                )
                sendMessage(m)
            }
        }
        mChatService = BluetoothChatService(requireContext(), mHandler)
        mOutStringBuffer = StringBuffer("")
    }

    private fun insertMessage(
        writeMessage: String,
        chatId: String,
        uId: String,
        senderId: String,
        status :MessageStatus,
        isMe: Boolean,
        fatherId: Int
    ) {
        viewMode.insertMessage(writeMessage, chatId, uId, senderId, isMe, fatherId,status)
    }

    private fun sendMessage(message: MessageAck) {
        if (mChatService!!.state != BluetoothChatService.STATE_CONNECTED) {
            message.apply {
                if (content.isNotEmpty()){
                    insertMessage(content,chatId,UID,chatId,status,true,-1)
                    mOutEditText.setText("")
                }
            }
            return
        }
        writeMessage(message)
    }
    private fun writeMessage(message : MessageAck){
        if (message.content.isNotEmpty()) {
            val send = message.encode().toByteArray()
            mChatService!!.write(send)
            mOutStringBuffer!!.setLength(0)
            mOutEditText.setText(mOutStringBuffer)
        }
    }

    private fun handleConnectStatus() {
        connectionState.visibility = View.GONE
        handleFailedMessages()
    }

    private fun handleFailedMessages() {
        CoroutineScope(Dispatchers.IO).launch {
            val messages = viewMode.getMyFailedMessages(chatId)
            withContext(Dispatchers.Main) {
                if (messages.isNotEmpty()) {
                    messages.forEach {
                        Log.d(TAG, "handleFailedMessages: ${it?.content()}")
                        it?.let { message ->
                            sendMessage(
                                MessageAck(
                                    isMe = true,
                                    status = MessageStatus.MessageStatusSend(),
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
            insertMessage(mAck.content, chatId, mAck.UID, myDeviceAddress!!,MessageStatus.MessageStatusSend(), true, -1)
        }
    }

    private fun handleReadMessage(readMessage: String) {
        val mAck = readMessage.decode()
        if (mAck.status == MessageStatus.MessageStatusSeen()) {
            updateMyMessageStatus(mAck)
            Log.d(TAG, "handleReadMessage: haslkdfjladsf")
        } else {
            Log.d(TAG, "handleReadMessage: $readMessage")
            storeTheirMessage(mAck)
            sendAck(mAck)
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
            fatherId = -1,
            status = MessageStatus.MessageStatusSend()
        )
    }

    private fun sendAck(message: MessageAck) {
        message.isMe = true
        message.status = MessageStatus.MessageStatusSeen()
        sendMessage(message)
    }

    private val mHandler: Handler = object : Handler(Looper.getMainLooper()) {
        override fun handleMessage(msg: Message) {
            when (msg.what) {
                Constants.MESSAGE_STATE_CHANGE -> when (msg.arg1) {
                    BluetoothChatService.STATE_CONNECTED -> handleConnectStatus()
                    BluetoothChatService.STATE_CONNECTING -> changeStatus(resources.getString(R.string.title_connecting))
                    BluetoothChatService.STATE_LISTEN, BluetoothChatService.STATE_NONE -> changeStatus(resources.getString(R.string.title_not_connected))
                }
                Constants.MESSAGE_WRITE -> {
                    val writeBuf = msg.obj as ByteArray
                    val writeMessage = String(writeBuf)
                    val message = writeMessage.decode()
                    handleWriteMessage(message)
                }
                Constants.MESSAGE_READ -> {
                    val readBuf = msg.obj as ByteArray
                    val readMessage = String(readBuf, 0, msg.arg1)
                    handleReadMessage(readMessage)
                }
                Constants.MESSAGE_DEVICE_NAME -> {
                    mConnectedDeviceName = msg.data.getString(Constants.DEVICE_NAME)
                    mConnectedDeviceAddress = msg.data.getString(Constants.DEVICE_ADDRESS)
                    chatId = mConnectedDeviceAddress ?: "-1"
                    Toast.makeText(requireContext(), "Connected to "
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
            REQUEST_CONNECT_DEVICE_SECURE ->
                if (resultCode == Activity.RESULT_OK) {
                    data?.let { connectDevice() }
                }
            REQUEST_ENABLE_BT ->
                if (resultCode == Activity.RESULT_OK) {
                    setupChat()
                } else {
                    // User did not enable Bluetooth or an error occurred
                    Log.d(TAG, "BT not enabled")
                    Toast.makeText(
                        requireContext(), R.string.bt_not_enabled_leaving,
                        Toast.LENGTH_SHORT
                    ).show()
                    activity?.finish()
                }
        }
    }


    private fun connectDevice() {
        val device = mBluetoothAdapter!!.getRemoteDevice(chatId)
        mChatService?.connect(device, true)
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
        mChatService?.stop()
    }

    override fun onResume() {
        super.onResume()
        if (mChatService != null) {
            if (mChatService!!.state == BluetoothChatService.STATE_NONE) {
                mChatService!!.start()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        mChatService?.stop()
    }

    companion object {
        private const val TAG = "BluetoothChatFragment"

        // Intent request codes
        private const val REQUEST_CONNECT_DEVICE_SECURE = 1
        private const val REQUEST_ENABLE_BT = 3
    }
}