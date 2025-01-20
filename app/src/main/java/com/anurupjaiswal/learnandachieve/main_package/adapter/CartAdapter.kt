package com.anurupjaiswal.learnandachieve.main_package.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.anurupjaiswal.learnandachieve.databinding.ItemCartBinding
import com.anurupjaiswal.learnandachieve.model.PackageModel

class CartAdapter(
    private val packageModels: List<PackageModel>,
    private val onDeleteClick: (Int) -> Unit
) : RecyclerView.Adapter<CartAdapter.CartViewHolder>() {

    class CartViewHolder(val binding: ItemCartBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CartViewHolder {
        val binding = ItemCartBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return CartViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CartViewHolder, position: Int) {
        val packageModel = packageModels[position]
        with(holder.binding) {
            itemImage.setImageResource(packageModel.imageRes)
            itemTitle.text = packageModel.title
            itemDiscountedPrice.text = packageModel.price
            itemOriginalPrice.text = packageModel.originalPrice
            ivDelete.setOnClickListener { onDeleteClick(position) }
        }
    }

    override fun getItemCount(): Int = packageModels.size
}
