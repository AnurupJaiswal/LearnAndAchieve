package com.anurupjaiswal.learnandachieve.main_package.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.anurupjaiswal.learnandachieve.R
import com.anurupjaiswal.learnandachieve.model.CardItem

class ExpandableCardAdapter(private val cardItems: List<CardItem>) :
    RecyclerView.Adapter<ExpandableCardAdapter.CardViewHolder>() {

    private val expandedPositions = mutableSetOf<Int>() // Store multiple expanded positions

    inner class CardViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val titleTextView: TextView = itemView.findViewById(R.id.titleTextView)
        val descriptionTextView: TextView = itemView.findViewById(R.id.descriptionTextView)
        val arrowImageView: ImageView = itemView.findViewById(R.id.arrowImageView)
        val cardView: CardView = itemView.findViewById(R.id.cardView)

        init {
            cardView.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    // Toggle the expanded state for the clicked card
                    if (expandedPositions.contains(position)) {
                        expandedPositions.remove(position) // Collapse the card
                    } else {
                        expandedPositions.add(position) // Expand the card
                    }

                    // Notify that the item has changed (to update the UI)
                    notifyItemChanged(position)
                }
            }
        }

        // Function to handle the rotation of the arrow icon
        fun rotateArrow(imageView: ImageView, expand: Boolean) {
            val rotationAngle = if (expand) 180f else 0f
            imageView.animate()
                .rotation(rotationAngle) // Rotate the arrow
                .setDuration(300) // Duration of the animation
                .start()
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CardViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_expandable_card, parent, false)
        return CardViewHolder(view)
    }

    override fun onBindViewHolder(holder: CardViewHolder, position: Int) {
        val item = cardItems[position]
        holder.titleTextView.text = item.title
        holder.descriptionTextView.text = item.description

        // Set the card background color dynamically
        holder.cardView.setCardBackgroundColor(item.color)

        // Determine whether the current card should be expanded
        val isExpanded = expandedPositions.contains(position)
        holder.descriptionTextView.visibility = if (isExpanded) View.VISIBLE else View.GONE

        // Animate the arrow based on whether the card is expanded or collapsed
        holder.rotateArrow(holder.arrowImageView, isExpanded)
    }

    override fun getItemCount(): Int = cardItems.size
}
