package com.anurupjaiswal.learnandachieve.main_package.adapter

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.anurupjaiswal.learnandachieve.R
import com.anurupjaiswal.learnandachieve.model.ServiceItem
import com.google.android.material.card.MaterialCardView

class OurServiceAdapter(private val services: List<ServiceItem>) : RecyclerView.Adapter<OurServiceAdapter.ServiceViewHolder>() {

    // ViewHolder class to hold references to the views
    class ServiceViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val cardItem: MaterialCardView = view.findViewById(R.id.cardItem)
        val imageView: ImageView = view.findViewById(R.id.imageView)
        val titleText: TextView = view.findViewById(R.id.titleText)
        val descriptionText: TextView = view.findViewById(R.id.descriptionText)
    }

    // Inflating the layout for each item
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ServiceViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_our_service, parent, false)
        return ServiceViewHolder(view)
    }

    // Binding data to the views
    override fun onBindViewHolder(holder: ServiceViewHolder, position: Int) {
        val service = services[position]

        // Set title, description, and image
        holder.titleText.text = service.title
        holder.descriptionText.text = service.description
        holder.imageView.setImageResource(service.imageResId)

        // Set background color using hexadecimal color code
        val color = Color.parseColor(service.backgroundColor)
        holder.cardItem.setCardBackgroundColor(color)
    }

    override fun getItemCount(): Int = services.size
}
