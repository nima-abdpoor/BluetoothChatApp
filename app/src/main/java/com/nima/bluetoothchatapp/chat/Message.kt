package com.nima.bluetoothchatapp.chat

interface Message {
    fun getContent() : Content
    fun getFather() : Father
    fun getChild() : Child
}