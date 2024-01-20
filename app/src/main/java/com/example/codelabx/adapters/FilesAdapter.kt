package com.example.codelabx.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.codelabx.R
import java.io.File

class FilesAdapter(private val fileOnClick : CodeLabXFileOnClick) : ListAdapter<File, FilesAdapter.MyViewHolder >(DiffUtil()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.file_layout , parent , false)
        return MyViewHolder(view)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val item = getItem(position)
        holder.fileName.text = item.name

        if (item.isDirectory){
            holder.fileImage.setImageResource(R.mipmap.folder)
        }
        else{
            when(item.extension){
                "py" -> holder.fileImage.setImageResource(R.mipmap.python)
                "java" -> holder.fileImage.setImageResource(R.mipmap.java)
                "cpp" -> holder.fileImage.setImageResource(R.mipmap.cpp)
            }
        }
        holder.file.setOnClickListener(View.OnClickListener {
            if (item.isDirectory){
                fileOnClick.folderClicked(item)
            }
            else{
                fileOnClick.fileClicked(item)
            }
        })
    }

    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        val fileName : TextView = itemView.findViewById(R.id.file_name)
        val fileImage : ImageView = itemView.findViewById(R.id.folder_image)
        val file : RelativeLayout = itemView.findViewById(R.id.file_parent)
    }

    class DiffUtil : androidx.recyclerview.widget.DiffUtil.ItemCallback<File>(){
        override fun areItemsTheSame(oldItem: File, newItem: File): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(oldItem: File, newItem: File): Boolean {
            return oldItem.name == newItem.name
        }
    }

    interface CodeLabXFileOnClick{
        fun folderClicked(folder : File)
        fun fileClicked(file : File)
    }
}