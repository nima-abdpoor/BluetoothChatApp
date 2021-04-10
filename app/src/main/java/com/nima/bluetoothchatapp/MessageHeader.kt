package com.nima.bluetoothchatapp

import com.nima.bluetoothchatapp.Constants.Companion.MessageStatusNone
import com.nima.bluetoothchatapp.Constants.Companion.MessageStatusSeen
import com.nima.bluetoothchatapp.Constants.Companion.MessageStatusSend
import com.nima.bluetoothchatapp.chat.MessageAck
import com.nima.bluetoothchatapp.chat.MessageStatus

fun String.decode(): MessageAck {
    var isMe = true
    var status: MessageStatus = MessageStatus.MessageStatusNone(0)
    var uId = "0000"
    var message = ""
    if (this.isNotEmpty()) {
        var m = this.substring(0, 1)
        isMe = m == "0"
        m = this.substring(1, 2)
        when (m) {
            "0" -> {
                status = MessageStatus.MessageStatusNone()
            }
            "1" -> {
                status = MessageStatus.MessageStatusSend()
            }
            "2"-> {
                status = MessageStatus.MessageStatusSeen()
            }
        }
        uId = this.substring(2, 6)
        message = this.substring(6)
    }
    return MessageAck(isMe, status, uId, message)
}

fun MessageAck.encode(): String {
    val sb = StringBuilder()
    this.apply {
        if (this.isMe) sb.append("0") else sb.append("1")
        when (this.status) {
            is MessageStatus.MessageStatusNone -> sb.append("0")
            is MessageStatus.MessageStatusSeen -> sb.append("2")
            is MessageStatus.MessageStatusSend -> sb.append("1")
        }
        sb.append(this.UID)
        sb.append(this.content)
    }
    return sb.toString()
}