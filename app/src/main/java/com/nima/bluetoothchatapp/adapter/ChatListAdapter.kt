package com.nima.bluetoothchatapp.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.nima.bluetoothchatapp.R
import com.nima.bluetoothchatapp.devices.BLDevice

class ChatListAdapter(private val interaction: Interaction? = null) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private lateinit var parent : ViewGroup
    private var position: Int = 0
    val DIFF_CALLBACK = object : DiffUtil.ItemCallback<BLDevice>() {

        override fun areItemsTheSame(oldItem: BLDevice, newItem: BLDevice): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(oldItem: BLDevice, newItem: BLDevice): Boolean {
            return oldItem.deviceAddress == newItem.deviceAddress
        }

    }
    private val differ = AsyncListDiffer(this, DIFF_CALLBACK)


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        this.parent = parent
        return ChatListViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.model_chat_list,
                parent,
                false
            ),
            interaction,
            position
        )
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is ChatListViewHolder -> {
                holder.bind(differ.currentList[position])
            }
        }
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }

    fun submitList(list: List<BLDevice?>) {
        differ.submitList(list)
    }

    class ChatListViewHolder(
        itemView: View,
        private val interaction: Interaction?,
        private val pos: Int
    ) : RecyclerView.ViewHolder(itemView) {


        fun bind(item: BLDevice) = with(itemView) {
            itemView.setOnClickListener {
                interaction?.onItemSelected(bindingAdapterPosition, item)
            }
            val deviceName = itemView.findViewById<TextView>(R.id.txt_modelChatListF_deviceName)
//            val date = itemView.findViewById<TextView>(R.id.txt_modelPairedD_date)
            deviceName.text = item.deviceName
        }
    }

    interface Interaction {
        fun onItemSelected(position: Int, item: BLDevice)
    }
}