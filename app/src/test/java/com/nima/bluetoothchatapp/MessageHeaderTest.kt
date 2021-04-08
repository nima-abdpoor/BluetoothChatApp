package com.nima.bluetoothchatapp

import com.nima.bluetoothchatapp.chat.MessageAck
import com.nima.bluetoothchatapp.chat.MessageStatus
import junit.framework.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class MessageHeaderTest {
    private lateinit var randoms : List<String>
    @Before
    fun setup(){
        val generator  = RandomUIDGenerator()
        randoms = generator.generate(6)
    }
    @Test
    fun decodeStringToMessageAck(){
        assertEquals("00${randoms[0]}salam".decode(),MessageAck(true, MessageStatus.MessageStatusNone(0), randoms[0],"salam"))
        assertEquals("11${randoms[1]}".decode(),MessageAck(false, MessageStatus.MessageStatusSeen(0), randoms[1],""))
        assertEquals("01${randoms[2]}s".decode(),MessageAck(true, MessageStatus.MessageStatusSeen(0), randoms[2],"s"))
        assertEquals("10${randoms[3]}dddddddddddddddddddddddd".decode(),MessageAck(false, MessageStatus.MessageStatusNone(0), randoms[3],"dddddddddddddddddddddddd"))
        assertEquals("00${randoms[4]}1111".decode(),MessageAck(true, MessageStatus.MessageStatusNone(0), randoms[4],"1111"))
        assertEquals("11${randoms[5]}a.fasfadsf.".decode(),MessageAck(false, MessageStatus.MessageStatusSeen(0), randoms[5],"a.fasfadsf."))
    }


}