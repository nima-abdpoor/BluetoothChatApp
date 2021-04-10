package com.nima.bluetoothchatapp.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.nima.bluetoothchatapp.R
import com.nima.bluetoothchatapp.chat.Message

class ChatAdapter() :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private lateinit var parent : ViewGroup
    private var position: Int = 0
    val DIFF_CALLBACK = object : DiffUtil.ItemCallback<Message>() {

        override fun areItemsTheSame(oldItem: Message, newItem: Message): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(oldItem: Message, newItem: Message): Boolean {
            return oldItem.content().id == newItem.content().id
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

    fun submitList(list: List<Message>) {
        differ.submitList(list)
    }

    class ChatViewHolder(
        itemView: View,
        private val pos: Int
    ) : RecyclerView.ViewHolder(itemView) {


        fun bind(item: Message) = with(itemView) {

        }
    }
}