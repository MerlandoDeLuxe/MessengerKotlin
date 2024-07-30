package com.example.messengerkotlin

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView.Adapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder

class UserAdapter : Adapter<UserAdapter.UserViewHolder>() {
    var userList: List<User> = listOf()
        set(value) {
            field = value
            notifyDataSetChanged()
        }
    private lateinit var onItemClickListenerInterface: OnItemClickListener

    fun onItemClickListener(onItemClickListener: OnItemClickListener) {
        onItemClickListenerInterface = onItemClickListener
    }

    class UserViewHolder(itemView: View) : ViewHolder(itemView) {
        val imageViewUserPhoto = itemView.findViewById<ImageView>(R.id.imageViewUserPhoto)
        val imageViewUserStatus = itemView.findViewById<ImageView>(R.id.imageViewUserStatus)
        val textViewUserName = itemView.findViewById<TextView>(R.id.textViewUserName)
        val textViewUserSurname = itemView.findViewById<TextView>(R.id.textViewUserSurname)
        val textViewUserAge = itemView.findViewById<TextView>(R.id.textViewUserAge)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(
                R.layout.user_item_template,
                parent,
                false
            )
        return UserViewHolder(view)
    }

    override fun onBindViewHolder(holder: UserViewHolder, position: Int) {
        val user = userList.get(position)
        holder.textViewUserName.text = user.name
        holder.textViewUserSurname.text = user.surname
        holder.textViewUserAge.text = user.age.toString()

        var backgroundIntRes: Int
        if (user.online) {
            backgroundIntRes = R.drawable.circle_green_online
        } else backgroundIntRes = R.drawable.circle_red_offline

        var drawable =
            ContextCompat.getDrawable(holder.imageViewUserStatus.context, backgroundIntRes)
        holder.imageViewUserStatus.setImageDrawable(drawable)

        drawable =
            ContextCompat.getDrawable(holder.imageViewUserPhoto.context, R.drawable.colt_bg_logo)
        holder.imageViewUserPhoto.setImageDrawable(drawable)

        holder.itemView.setOnClickListener({
            if (onItemClickListenerInterface != null) {
                onItemClickListenerInterface.onItemClickListener(user)
            }
        })
    }

    fun interface OnItemClickListener {
        fun onItemClickListener(user: User)
    }

    override fun getItemCount() = userList.size
}