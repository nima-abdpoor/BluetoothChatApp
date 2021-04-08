package com.nima.bluetoothchatapp.chat

class Text(
    private val content: Content,
    private val father: Father,
    private val child: Child?
) :Message {
    override fun content(): Content {
        return content
    }

    override fun father(): Father {
        return father
    }

    override fun child(): Child? {
        return child
    }
}