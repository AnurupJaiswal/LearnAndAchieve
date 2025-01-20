package com.anurupjaiswal.learnandachieve.main_package.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.anurupjaiswal.learnandachieve.R
import com.anurupjaiswal.learnandachieve.databinding.ItemOrderHistoryBinding
import com.anurupjaiswal.learnandachieve.model.OrderHistoryItem


class OrderHistoryAdapter(private val items: List<OrderHistoryItem>) :
    RecyclerView.Adapter<OrderHistoryAdapter.OrderHistoryViewHolder>() {

    // ViewHolder class with View Binding
    inner class OrderHistoryViewHolder(val binding: ItemOrderHistoryBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrderHistoryViewHolder {
        val binding = ItemOrderHistoryBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return OrderHistoryViewHolder(binding)
    }

    override fun onBindViewHolder(holder: OrderHistoryViewHolder, position: Int) {
        val item = items[position]
        val binding = holder.binding

        // Bind data to views
        binding.tvDateTime.text = item.dateTime
        binding.tvTitle.text = item.title
        binding.tvOrderId.text = item.orderId
        binding.tvTransactionId.text = item.transactionId
        binding.tvAmount.text = item.amount

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

    override fun getItemCount() = items.size
}
