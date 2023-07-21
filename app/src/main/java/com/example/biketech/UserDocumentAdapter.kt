package com.example.biketech

import android.content.Context
import android.graphics.drawable.Drawable
import android.media.ImageReader
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide

class UserDocumentAdapter(private val context: Context, private val users: ArrayList<String>): RecyclerView.Adapter<UserViewHolder> (){
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserViewHolder {

        return UserViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.document_item_layout,parent,false)
        )
    }

    override fun onBindViewHolder(holder: UserViewHolder, position: Int) {


            val user = users[position]

            Glide.with(context).load(user).into(holder.documentImage)



    }

    override fun getItemCount(): Int {
        return users.size
    }
}

class UserViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){

    val documentImage: ImageView = itemView.findViewById(R.id.imageViewDocument)

}