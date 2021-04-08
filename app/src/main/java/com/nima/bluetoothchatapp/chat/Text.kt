package com.nima.bluetoothchatapp.chat

class Text constructor(
    private var content: Content<Message>,
    private var father: Father,
    private var child: Child
) : Message {

    override fun getContent(): Content<Message> {
        return content
    }

    override fun getFather(): Father {
        return father
    }

    override fun getChild(): Child {
        return child
    }
}