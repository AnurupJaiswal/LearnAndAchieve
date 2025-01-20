package com.anurupjaiswal.learnandachieve.main_package.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.anurupjaiswal.learnandachieve.R


import com.anurupjaiswal.learnandachieve.model.ClassData

class ClassSelectionAdapter(
    private val classDataList: List<ClassData>,
    private val onItemSelected: (String, String) -> Unit // To handle both class name and id
) : RecyclerView.Adapter<ClassSelectionAdapter.ClassViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ClassViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_popup_text, parent, false)
        return ClassViewHolder(view)
    }

    override fun onBindViewHolder(holder: ClassViewHolder, position: Int) {
        val classData = classDataList[position]
        holder.className.text = classData.class_name

        holder.itemView.setOnClickListener {
            onItemSelected(classData.class_name, classData._id)
        }
    }

    override fun getItemCount(): Int = classDataList.size

    inner class ClassViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val className: TextView = itemView.findViewById(R.id.textView)
    }
}
