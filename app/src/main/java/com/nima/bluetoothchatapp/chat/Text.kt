package com.nima.bluetoothchatapp.chat

class Text(
    private val content: Content,
    private val father: Father,
    private val child: Child
) :Message {
    override fun getContent(): Content {
        return content
    }

    override fun getFather(): Father {
        return father
    }

    override fun getChild(): Child {
        return child
    }
}