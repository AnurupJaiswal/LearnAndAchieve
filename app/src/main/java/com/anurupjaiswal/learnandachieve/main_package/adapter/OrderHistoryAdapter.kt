package com.anurupjaiswal.learnandachieve.main_package.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.anurupjaiswal.learnandachieve.R
import com.anurupjaiswal.learnandachieve.databinding.ItemOrderHistoryBinding
import com.anurupjaiswal.learnandachieve.model.Order

/*
class OrderHistoryAdapter(private val orders: List<Order>) :
    RecyclerView.Adapter<OrderHistoryAdapter.OrderHistoryViewHolder>() {

    // ViewHolder class with ViewBinding
    inner class OrderHistoryViewHolder(val binding: ItemOrderHistoryBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrderHistoryViewHolder {
        val binding = ItemOrderHistoryBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return OrderHistoryViewHolder(binding)
    }

    override fun onBindViewHolder(holder: OrderHistoryViewHolder, position: Int) {
        val order = orders[position]
        val binding = holder.binding


        binding.tvDateTime.text = order.date
        binding.tvTitle.text = order.packageName
        binding.tvOrderId.text = order.orderId
        binding.tvTransactionId.text = order.transaction_id
        binding.tvAmount.text = order.amount

        // Expand/Collapse logic
        binding.llTitle.setOnClickListener {
            val isVisible = binding.llDetails.visibility == View.VISIBLE
            binding.llDetails.visibility = if (isVisible) View.GONE else View.VISIBLE

            // Rotate the arrow icon
            binding.ivExpandCollapse.animate()
                .rotation(if (isVisible) 0f else 180f)
                .setDuration(300)
                .start()
        }
    }

    override fun getItemCount(): Int = orders.size
}
*/
class OrderHistoryAdapter(private val orders: List<Order>) :
    RecyclerView.Adapter<OrderHistoryAdapter.OrderHistoryViewHolder>() {

    private var expandedPosition = -1 // Track the currently expanded item

    inner class OrderHistoryViewHolder(val binding: ItemOrderHistoryBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrderHistoryViewHolder {
        val binding = ItemOrderHistoryBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return OrderHistoryViewHolder(binding)
    }

    override fun onBindViewHolder(holder: OrderHistoryViewHolder, position: Int) {
        val order = orders[position]
        val binding = holder.binding

        // Bind data to views
        binding.tvDateTime.text = order.date
        binding.tvTitle.text = order.packageName
        binding.tvOrderId.text = order.orderId
        binding.tvTransactionId.text = order.transaction_id
        binding.tvAmount.text = order.amount

        // Set the title color based on whether the item is expanded
        binding.tvTitle.setTextColor(
            if (position == expandedPosition) {
                binding.root.context.getColor(R.color.blue) // Blue when expanded
            } else {
                binding.root.context.getColor(R.color.black) // Black when not expanded
            }
        )

        // Set the visibility of the details and arrow rotation
        binding.llDetails.visibility = if (position == expandedPosition) View.VISIBLE else View.GONE
        binding.ivExpandCollapse.rotation = if (position == expandedPosition) 180f else 0f

        // Expand/Collapse logic
        binding.llTitle.setOnClickListener {
            if (expandedPosition == position) {
                // If the clicked item is already expanded, collapse it
                expandedPosition = -1 // Reset expanded position
            } else {
                // Collapse previously expanded item if any
                val previousPosition = expandedPosition
                expandedPosition = position // Set the new expanded position
                notifyItemChanged(previousPosition) // Collapse the previous item
            }

            // Notify the adapter to update the view
            notifyItemChanged(position)
        }
    }

    override fun getItemCount(): Int = orders.size
}

