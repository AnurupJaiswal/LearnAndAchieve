package com.anurupjaiswal.learnandachieve.main_package.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.anurupjaiswal.learnandachieve.R
import com.anurupjaiswal.learnandachieve.databinding.ItemStandardSelactionBinding

class StandardSelectionAdapter(
    private val standardList: List<String>,
    private val listener: OnStandardSelectedListener
) : RecyclerView.Adapter<StandardSelectionAdapter.StandardViewHolder>() {

    private var selectedPosition = -1 // Track the selected item position

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StandardViewHolder {
        val binding = ItemStandardSelactionBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return StandardViewHolder(binding) // Pass the binding to the view holder
    }

    override fun onBindViewHolder(holder: StandardViewHolder, position: Int) {
        val standardName = standardList[position]
        holder.bind(standardName, position == selectedPosition)
    }

    override fun getItemCount(): Int = standardList.size

    inner class StandardViewHolder(private val binding: ItemStandardSelactionBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bind(standardName: String, isSelected: Boolean) {
            binding.tvStandardName.text = standardName
            binding.ivTick.visibility = if (isSelected) View.VISIBLE else View.INVISIBLE

            // Handle click listener to update selection
            binding.root.setOnClickListener {
                selectedPosition = adapterPosition // Update the selected position
                notifyDataSetChanged() // Notify that data has changed to update the view
                listener.onStandardSelected(standardName) // Notify listener about selection
            }
        }
    }

    interface OnStandardSelectedListener {
        fun onStandardSelected(standardName: String)
    }
}
