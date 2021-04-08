package com.nima.bluetoothchatapp.chat

interface Message {
    fun getContent() : Content<Message>
    fun getFather() : Father
    fun getChild() : Child
}