package com.nima.bluetoothchatapp.mapper

import com.nima.bluetoothchatapp.Constants.Companion.MessageStatusNone
import com.nima.bluetoothchatapp.Constants.Companion.MessageStatusSeen
import com.nima.bluetoothchatapp.Constants.Companion.MessageStatusSend
import com.nima.bluetoothchatapp.Constants.Companion.MessageTypeNone
import com.nima.bluetoothchatapp.Constants.Companion.MessageTypeText
import com.nima.bluetoothchatapp.chat.*
import com.nima.bluetoothchatapp.database.entities.ChatMessage
import javax.inject.Inject

class ChatMessageMapper @Inject constructor() : EntityMapper<ChatMessage, Message?> {
    override fun mapFromEntity(entity: ChatMessage): Message? {
        return when(entity.type){
            MessageTypeText ->Text(
                content = Content(
                    id  = entity.id,
                    time = entity.time,
                    content = entity.content,
                    senderId = entity.senderId,
                    isMe = entity.isMe,
                    status = when(entity.status){
                        "1" -> MessageStatus.MessageStatusSend(entity.id)
                        "2" -> MessageStatus.MessageStatusSeen(entity.id)
                        else -> MessageStatus.MessageStatusNone(entity.id)
                    }
                ),
                father = Father(entity.fatherId),
                child = null
            )
            else -> null
        }
    }

    override fun mapToEntity(domainModel: Message?): ChatMessage? {
        domainModel?.let { message ->
            return ChatMessage(
                id = message.content().id,
                senderId = message.content().senderId,
                time = message.content().time,
                content = message.content().content,
                isMe = message.content().isMe,
                status = when(message.content().status){
                    is MessageStatus.MessageStatusNone -> MessageStatusNone
                    is MessageStatus.MessageStatusSeen -> MessageStatusSeen
                    is MessageStatus.MessageStatusSend -> MessageStatusSend
                },
                type = if (message is Text) MessageTypeText else MessageTypeNone,
                fatherId = message.father().id
            )
        }
        return null
    }

    fun mapFromEntityList(response: List<ChatMessage>): List<Message?> =
        response.map { mapFromEntity(it) }

    fun mapToEntityList(response: List<Message>): List<ChatMessage?> =
        response.map { mapToEntity(it) }
}