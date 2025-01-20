package com.anurupjaiswal.learnandachieve.main_package

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.anurupjaiswal.learnandachieve.R


import com.anurupjaiswal.learnandachieve.model.State

class StateSelectionAdapter(
    private val StateDataList: List<State>,
    private val onItemSelected: (String, String) -> Unit // To handle both class name and id
) : RecyclerView.Adapter<StateSelectionAdapter.ClassViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ClassViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_popup_text, parent, false)
        return ClassViewHolder(view)
    }

    override fun onBindViewHolder(holder: ClassViewHolder, position: Int) {
        val stateData = StateDataList[position]
        holder.className.text = stateData.name

        holder.itemView.setOnClickListener {
            stateData.name?.let { it1 -> stateData._id?.let { it2 -> onItemSelected(it1, it2) } }
        }
    }

    override fun getItemCount(): Int = StateDataList.size

    inner class ClassViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val className: TextView = itemView.findViewById(R.id.textView)
    }
}
