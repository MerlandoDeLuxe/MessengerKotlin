package com.example.messengerkotlin

import android.net.Uri
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.bumptech.glide.Glide
import java.util.concurrent.ConcurrentHashMap
import kotlin.math.log

class UserPhotoAdapter : RecyclerView.Adapter<UserPhotoAdapter.UserPhotoViewHolder>() {
    private val TAG = "UserPhotoAdapter"

    var urlUserPhotoList: List<Uri> = mutableListOf()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

//    var userPhotoTempList: MutableList<Uri> = mutableListOf()
//        set(value) {
//            for ((key, value) in urlUserPhotoList) {
//                userPhotoTempList.add(value)
//            }
//            field = userPhotoTempList
//            Log.d(TAG, "userPhotoTempList = $userPhotoTempList")
//        }

    private lateinit var onPhotoClickListenerInterface: OnPhotoClickListener

    fun onPhotoClickListener(onPhotoClickListener: OnPhotoClickListener) {
        onPhotoClickListenerInterface = onPhotoClickListener
    }

    private lateinit var onDeletePhotoClickListenerInterface: OnDeletePhotoClickListener

    fun onDeletePhotoClickListener(onDeletePhotoClickListener: OnDeletePhotoClickListener) {
        onDeletePhotoClickListenerInterface = onDeletePhotoClickListener
    }

    private lateinit var onMakePhotoMainListenerInterface: OnMakePhotoMainListener

    fun onMakePhotoClickListener(onMakePhotoMainListener: OnMakePhotoMainListener) {
        onMakePhotoMainListenerInterface = onMakePhotoMainListener
    }

    class UserPhotoViewHolder(itemView: View) : ViewHolder(itemView) {
        val imageViewFromStorage = itemView.findViewById<ImageView>(R.id.imageViewFromStorage)
        val imageViewDeletePhoto = itemView.findViewById<ImageView>(R.id.imageViewDeletePhoto)
        val imageViewMakePhotoMain = itemView.findViewById<ImageView>(R.id.imageViewMakePhotoMain)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserPhotoViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.user_photo_template, parent, false)
        return UserPhotoViewHolder(view)
    }

    override fun onBindViewHolder(holder: UserPhotoViewHolder, position: Int) {

        //var photoUrl = urlUserPhotoList.get(position)

            Glide.with(holder.itemView.context)
                .load(urlUserPhotoList.get(position))
                .into(holder.imageViewFromStorage)
            Log.d(TAG, "onBindViewHolder: photoUrl = $urlUserPhotoList.get(position)")


        holder.imageViewDeletePhoto.setOnClickListener {
            if (onDeletePhotoClickListenerInterface != null) {
                onDeletePhotoClickListenerInterface.onDeletePhotoClick(urlUserPhotoList.get(position))
            }
        }
        holder.imageViewMakePhotoMain.setOnClickListener {
            if (onMakePhotoMainListenerInterface != null) {
                onMakePhotoMainListenerInterface.onMakePhotoMain(position)
            }
        }
    }

    fun interface OnPhotoClickListener {
        fun onPhotoClick()
    }

    fun interface OnDeletePhotoClickListener {
        fun onDeletePhotoClick(uri: Uri)
    }

    fun interface OnMakePhotoMainListener {
        fun onMakePhotoMain(position: Int)
    }

    override fun getItemCount(): Int {
        return urlUserPhotoList.size
    }
}