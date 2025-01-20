package com.anurupjaiswal.learnandachieve.main_package.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.anurupjaiswal.learnandachieve.databinding.ItemPopupTextBinding
import com.anurupjaiswal.learnandachieve.model.Talukas

class TalukaSelectionAdapter(
    private val talukaList: List<Talukas>,
    private val onItemSelected: (String, String) -> Unit
) : RecyclerView.Adapter<TalukaSelectionAdapter.TalukaViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TalukaViewHolder {
        // Inflate the item_popup_text layout
        val binding = ItemPopupTextBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return TalukaViewHolder(binding)
    }

    override fun onBindViewHolder(holder: TalukaViewHolder, position: Int) {
        val taluka = talukaList[position]
        holder.bind(taluka)
    }

    override fun getItemCount(): Int {
        return talukaList.size
    }

    inner class TalukaViewHolder(private val binding: ItemPopupTextBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(taluka: Talukas) {
            // Set the Taluka name to the TextView in the layout
            binding.textView.text = taluka.name

            // Handle item click
            binding.root.setOnClickListener {
                taluka.name?.let { it1 -> taluka._id?.let { it2 -> onItemSelected(it1, it2) } }  // Pass name and ID on selection
            }
        }
    }
}

