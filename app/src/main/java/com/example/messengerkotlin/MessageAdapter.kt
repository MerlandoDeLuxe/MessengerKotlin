package com.example.messengerkotlin

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder

class MessageAdapter () : RecyclerView.Adapter<MessageAdapter.MessageViewHolder>() {
    private val VIEW_TYPE_MY_MESSAGE = 100
    private val VIEW_TYPE_OTHER_MESSAGE = 200

    var currentUserId: String? = null

    constructor(currentUserId: String) :this () {
        this.currentUserId = currentUserId
    }

    var messages: List<Message> = mutableListOf()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MessageViewHolder {
        val layoutResId = if (viewType == VIEW_TYPE_MY_MESSAGE) {
            R.layout.my_message_item_template
        } else {
            R.layout.other_message_item_template
        }
        val view = LayoutInflater.from(parent.context)
            .inflate(
                layoutResId,
                parent,
                false
            )
        return MessageViewHolder(view)
    }

    override fun onBindViewHolder(holder: MessageViewHolder, position: Int) {
        val message = messages.get(position)
        holder.textViewMessage.text = message.text
    }

    override fun getItemCount(): Int {
        return messages.size
    }

    override fun getItemViewType(position: Int): Int {
        if (messages[position].otherId.equals(currentUserId)) {
            return VIEW_TYPE_OTHER_MESSAGE
        } else {
            return VIEW_TYPE_MY_MESSAGE
        }
    }

    class MessageViewHolder(itemView: View) : ViewHolder(itemView) {
        var textViewMessage = itemView.findViewById<TextView>(R.id.textViewMessage)
    }
}