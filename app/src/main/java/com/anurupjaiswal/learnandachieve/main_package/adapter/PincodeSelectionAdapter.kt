package com.anurupjaiswal.learnandachieve.main_package.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.anurupjaiswal.learnandachieve.databinding.ItemPopupTextBinding
import com.anurupjaiswal.learnandachieve.model.Pincode

class PincodeSelectionAdapter(
    private val pincodeList: List<Pincode>,
    private val onItemSelected: (String, String) -> Unit
) : RecyclerView.Adapter<PincodeSelectionAdapter.PincodeViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PincodeViewHolder {
        // Inflate the item_popup_text layout
        val binding = ItemPopupTextBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return PincodeViewHolder(binding)
    }

    override fun onBindViewHolder(holder: PincodeViewHolder, position: Int) {
        val pincode = pincodeList[position]
        holder.bind(pincode)
    }

    override fun getItemCount(): Int {
        return pincodeList.size
    }

    inner class PincodeViewHolder(private val binding: ItemPopupTextBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(pincode: Pincode) {
            // Set the Pincode name to the TextView in the layout
            binding.textView.text = pincode.name

            // Handle item click
            binding.root.setOnClickListener {
                pincode.name?.let { it1 -> pincode._id?.let { it2 -> onItemSelected(it1, it2) } }  // Pass name and ID on selection
            }
        }
    }
}
