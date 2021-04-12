package com.nima.bluetoothchatapp.adapter

import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.core.content.res.ResourcesCompat
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.nima.bluetoothchatapp.R
import com.nima.bluetoothchatapp.chat.Message
import com.nima.bluetoothchatapp.chat.MessageStatus
import com.nima.bluetoothchatapp.toTime

class ChatAdapter :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private lateinit var parent : ViewGroup
    private var position: Int = 0
    val DIFF_CALLBACK = object : DiffUtil.ItemCallback<Message>() {

        override fun areItemsTheSame(oldItem: Message, newItem: Message): Boolean {
            return oldItem.content() == newItem.content()
        }

        override fun areContentsTheSame(oldItem: Message, newItem: Message): Boolean {
            return (oldItem.content().id == newItem.content().id)
        }

    }
    private val differ = AsyncListDiffer(this, DIFF_CALLBACK)


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        this.parent = parent
        return ChatViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.model_chat_item,
                parent,
                false
            ),
            position
        )
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is ChatViewHolder -> {
                holder.bind(differ.currentList[position])
            }
        }
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }

    fun submitList(list: List<Message?>) {
        differ.submitList(list)
    }

    class ChatViewHolder(
        itemView: View,
        private val pos: Int
    ) : RecyclerView.ViewHolder(itemView) {

        private lateinit var relativeLayout : RelativeLayout
        private lateinit var message : TextView
        private lateinit var state : ImageView
        private lateinit var datAndTime : TextView
        fun bind(item: Message) = with(itemView) {
            relativeLayout =findViewById(R.id.rl_chatItem_layout)
            message  =findViewById(R.id.txt_chatItem_content)
            state  =findViewById(R.id.img_chatItem_status)
            datAndTime  =findViewById(R.id.txt_chatItem_time)
            item.content().apply {
                if (isMe) {
                    state.visibility = View.VISIBLE
                    (relativeLayout.layoutParams as FrameLayout.LayoutParams).gravity = Gravity.END
                    relativeLayout.background = ResourcesCompat.getDrawable(resources,R.drawable.chat_message_host,null)
                    when(status){
                        is MessageStatus.MessageStatusNone -> {state.setImageResource(R.drawable.ic_status_none)}
                        is MessageStatus.MessageStatusSend -> {state.setImageResource(R.drawable.ic_status_sent)}
                        is MessageStatus.MessageStatusSeen -> {state.setImageResource(R.drawable.ic_status_seen)}
                    }
                }
                else{
                    (relativeLayout.layoutParams as FrameLayout.LayoutParams).gravity = Gravity.START
                    relativeLayout.background = ResourcesCompat.getDrawable(resources,R.drawable.chat_message_guest,null)
                    state.visibility = View.GONE
                }
                message.text = content
                datAndTime.text = time.toTime()
            }
        }
    }
}