package com.anurupjaiswal.learnandachieve.main_package.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.anurupjaiswal.learnandachieve.R
import com.anurupjaiswal.learnandachieve.basic.utilitytools.FileDownloadListener
import com.anurupjaiswal.learnandachieve.basic.utilitytools.FileDownloader
import com.anurupjaiswal.learnandachieve.basic.utilitytools.Utils
import com.anurupjaiswal.learnandachieve.databinding.ItemOrderHistoryBinding
import com.anurupjaiswal.learnandachieve.model.Order

class OrderHistoryAdapter(
    private val orders: List<Order>,
    private val downloadListener: FileDownloadListener  // Pass the listener from your fragment
) : RecyclerView.Adapter<OrderHistoryAdapter.OrderHistoryViewHolder>() {

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

        // Set click listener for file download
        binding.ivFileDowload.setOnClickListener {
            Utils.GetSession().token?.let { it1 ->
                FileDownloader.downloadFile(
                    context = binding.root.context, // Use the current view's context
                    fileUrl = "https://stage-api.learnandachieve.in/order/invoice/${order.order_id}",
                    token = it1,
                    fileName = "${order.order_id}invoice.pdf",
                    listener = downloadListener // Pass the listener here
                )
            }
        }

        // Expand/Collapse UI logic
        binding.tvTitle.setTextColor(
            if (position == expandedPosition)
                binding.root.context.getColor(R.color.blue) // Blue when expanded
            else
                binding.root.context.getColor(R.color.black) // Black when not expanded
        )

        binding.llDetails.visibility = if (position == expandedPosition) View.VISIBLE else View.GONE
        binding.ivExpandCollapse.rotation = if (position == expandedPosition) 180f else 0f

        binding.llTitle.setOnClickListener {
            if (expandedPosition == position) {
                // Collapse if already expanded
                expandedPosition = -1
            } else {
                // Collapse previously expanded item and expand current
                val previousPosition = expandedPosition
                expandedPosition = position
                notifyItemChanged(previousPosition)
            }
            notifyItemChanged(position)
        }
    }

    override fun getItemCount(): Int = orders.size
}
