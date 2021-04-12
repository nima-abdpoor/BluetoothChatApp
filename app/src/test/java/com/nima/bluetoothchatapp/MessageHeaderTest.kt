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
        randoms = generator.generate(12)
    }
    @Test
    fun decodeStringToMessageAck(){
        assertEquals("00${randoms[0]}salam".decode(),MessageAck(true, MessageStatus.MessageStatusNone(), randoms[0],"salam"))
        assertEquals("12${randoms[1]}".decode(),MessageAck(false, MessageStatus.MessageStatusSeen(), randoms[1],""))
        assertEquals("02${randoms[2]}s".decode(),MessageAck(true, MessageStatus.MessageStatusSeen(), randoms[2],"s"))
        assertEquals("10${randoms[3]}dddddddddddddddddddddddd".decode(),MessageAck(false, MessageStatus.MessageStatusNone(), randoms[3],"dddddddddddddddddddddddd"))
        assertEquals("00${randoms[4]}1111".decode(),MessageAck(true, MessageStatus.MessageStatusNone(), randoms[4],"1111"))
        assertEquals("11${randoms[5]}a.fasfadsf.".decode(),MessageAck(false, MessageStatus.MessageStatusSend(), randoms[5],"a.fasfadsf."))
    }

    @Before
    fun createRandomNumbers(){
        val generator  = RandomUIDGenerator()
        randoms = generator.generate(12)
    }
    @Test
    fun encodeMessageAckToString(){
        assertEquals("00${randoms[0]}nima",MessageAck(true,MessageStatus.MessageStatusNone(),randoms[0],"nima").encode())
        assertEquals("12${randoms[1]}abdpoor",MessageAck(false,MessageStatus.MessageStatusSeen(),randoms[1],"abdpoor").encode())
        assertEquals("01${randoms[2]}",MessageAck(true,MessageStatus.MessageStatusSend(),randoms[2],"").encode())
        assertEquals("10${randoms[3]}.",MessageAck(false,MessageStatus.MessageStatusNone(),randoms[3],".").encode())
        assertEquals("01${randoms[4]}aldsjflasfdjlasdkf",MessageAck(true,MessageStatus.MessageStatusSend(),randoms[4],"aldsjflasfdjlasdkf").encode())
        assertEquals("12${randoms[5]}_alsfj",MessageAck(false,MessageStatus.MessageStatusSeen(),randoms[5],"_alsfj").encode())
        assertEquals("02${randoms[6]}",MessageAck(true,MessageStatus.MessageStatusSeen(),randoms[6],"").encode())
        assertEquals("12${randoms[7]}",MessageAck(false,MessageStatus.MessageStatusSeen(),randoms[7],"").encode())
        assertEquals("00${randoms[8]}cddald",MessageAck(true,MessageStatus.MessageStatusNone(),randoms[8],"cddald").encode())
        assertEquals("12${randoms[9]}nima",MessageAck(false,MessageStatus.MessageStatusSeen(),randoms[9],"nima").encode())
        assertEquals("01${randoms[10]}v",MessageAck(true,MessageStatus.MessageStatusSend(),randoms[10],"v").encode())
        assertEquals("11${randoms[11]}salama",MessageAck(false,MessageStatus.MessageStatusSend(),randoms[11],"salama").encode())
    }
}