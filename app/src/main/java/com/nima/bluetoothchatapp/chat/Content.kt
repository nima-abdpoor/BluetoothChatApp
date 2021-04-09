package com.nima.bluetoothchatapp.chat

data class Content(
    var id : Int,
    var chatId:String,
    var time : String,
    var uId:String,
    var content: String,
    var senderId : String,
    var isMe : Boolean,
    var status :MessageStatus
) {
    override fun toString(): String {
        return "Content(id=$id, chatId='$chatId', time='$time', uId='$uId', content='$content', senderId='$senderId', isMe=$isMe, status=$status)"
    }
}