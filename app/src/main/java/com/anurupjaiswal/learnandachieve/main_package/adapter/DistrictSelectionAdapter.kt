package com.anurupjaiswal.learnandachieve.main_package.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.anurupjaiswal.learnandachieve.R
import com.anurupjaiswal.learnandachieve.model.District
import com.anurupjaiswal.learnandachieve.model.Districts

class DistrictSelectionAdapter(
    private val districts: List<Districts>,
    private val onItemSelected: (String, String) -> Unit
) : RecyclerView.Adapter<DistrictSelectionAdapter.DistrictViewHolder>() {

    // Create a ViewHolder for the district item
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DistrictViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_popup_text, parent, false) // Inflate your district item layout
        return DistrictViewHolder(view)
    }

    // Bind the data to the ViewHolder
    override fun onBindViewHolder(holder: DistrictViewHolder, position: Int) {
        val district = districts[position]
        holder.bind(district)
    }

    // Return the total number of districts in the list
    override fun getItemCount(): Int = districts.size

    // ViewHolder to hold the district item view
    inner class DistrictViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val districtName: TextView = itemView.findViewById(R.id.textView) // TextView to display district name

        // Bind the district data to the TextView and handle item click
        fun bind(district: Districts) {
            districtName.text = district.name // Set district name to the TextView

            // Set up the click listener for the item
            itemView.setOnClickListener {
                // When an item is clicked, return the district name and ID
                district.name?.let { it1 -> district._id?.let { it2 -> onItemSelected(it1, it2) } }
            }
        }
    }
}
