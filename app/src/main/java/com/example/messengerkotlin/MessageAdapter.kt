package com.example.messengerkotlin

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder

class MessageAdapter(val currentUserId: String) :
    RecyclerView.Adapter<MessageAdapter.MessageViewHolder>() {

    private val TAG = "MessageAdapter"
    private val VIEW_TYPE_MY_MESSAGE = 100
    private val VIEW_TYPE_OTHER_MESSAGE = 200

    var messages: List<Message> = listOf()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MessageViewHolder {
        val layoutResId: Int?
        layoutResId = if (viewType == VIEW_TYPE_MY_MESSAGE) {
            R.layout.my_message_item_template
        } else {
            R.layout.other_message_item_template
        }
        val view = LayoutInflater.from(parent.context).inflate(layoutResId, parent, false)

        return MessageViewHolder(view)
    }

    override fun onBindViewHolder(holder: MessageViewHolder, position: Int) {
        val message = messages.get(position)
        holder.textViewMessage.text = message.text
    }

    override fun getItemViewType(position: Int): Int {
        if (messages.get(position).senderId.equals(currentUserId)) {
            Log.d(TAG, "getItemViewType: VIEW_TYPE_MY_MESSAGE = $VIEW_TYPE_MY_MESSAGE")
            return VIEW_TYPE_MY_MESSAGE
        } else {
            Log.d(TAG, "getItemViewType: VIEW_TYPE_OTHER_MESSAGE = $VIEW_TYPE_OTHER_MESSAGE")
            return VIEW_TYPE_OTHER_MESSAGE
        }
    }

    override fun getItemCount(): Int {
        return messages.size
    }

    class MessageViewHolder(itemView: View) : ViewHolder(itemView) {
        val textViewMessage = itemView.findViewById<TextView>(R.id.textViewMessage)
    }
}