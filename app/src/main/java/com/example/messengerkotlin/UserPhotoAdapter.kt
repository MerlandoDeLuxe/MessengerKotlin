package com.example.messengerkotlin

import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.bumptech.glide.Glide

class UserPhotoAdapter : RecyclerView.Adapter<UserPhotoAdapter.UserPhotoViewHolder>() {

    var urlUserPhotoList: List<Uri> = listOf()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    class UserPhotoViewHolder(itemView: View) : ViewHolder(itemView) {
        val imageViewFromStorage = itemView.findViewById<ImageView>(R.id.imageViewFromStorage)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserPhotoViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.user_photo_template, parent, false)
        return UserPhotoViewHolder(view)
    }

    override fun onBindViewHolder(holder: UserPhotoViewHolder, position: Int) {
        Glide.with(holder.itemView.context)
            .load(urlUserPhotoList.get(position))
            .into(holder.imageViewFromStorage)
    }

    override fun getItemCount(): Int {
        return urlUserPhotoList.size
    }
}