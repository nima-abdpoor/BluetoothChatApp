package com.nima.bluetoothchatapp.chat

interface Message {
    fun content() : Content
    fun father() : Father
    fun child() : Child?
}