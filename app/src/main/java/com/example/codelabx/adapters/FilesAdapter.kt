package com.example.codelabx.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.codelabx.R
import java.io.File

class FilesAdapter : ListAdapter<File, FilesAdapter.MyViewHolder >(DiffUtil()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.file_layout , parent , false)
        return MyViewHolder(view)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.fileName.text = getItem(position).name
    }

    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        val fileName : TextView = itemView.findViewById(R.id.file_name)
        val fileImage : ImageView = itemView.findViewById(R.id.folder_image)
    }

    class DiffUtil : androidx.recyclerview.widget.DiffUtil.ItemCallback<File>(){
        override fun areItemsTheSame(oldItem: File, newItem: File): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(oldItem: File, newItem: File): Boolean {
            return oldItem.name == newItem.name
        }
    }
}