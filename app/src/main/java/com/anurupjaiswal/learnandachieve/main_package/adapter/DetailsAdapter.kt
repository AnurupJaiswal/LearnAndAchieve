package com.anurupjaiswal.learnandachieve.main_package.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.anurupjaiswal.learnandachieve.R
import org.json.JSONObject
class DetailsAdapter(private var userDetails: Map<String, String?>) :
    RecyclerView.Adapter<DetailsAdapter.DetailsViewHolder>() {

    class DetailsViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val fieldLabel: TextView = view.findViewById(R.id.labelTextView)
        val fieldValue: TextView = view.findViewById(R.id.valueTextView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DetailsViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_detail, parent, false)
        return DetailsViewHolder(view)
    }

    override fun onBindViewHolder(holder: DetailsViewHolder, position: Int) {
        val field = userDetails.keys.toList()[position]
        val value = userDetails[field]

        holder.fieldLabel.text = field
        holder.fieldValue.text = value
    }
    fun updateData(newDetails: Map<String, String?>) {
        userDetails = newDetails
        notifyDataSetChanged()
    }
    override fun getItemCount(): Int = userDetails.size
}
