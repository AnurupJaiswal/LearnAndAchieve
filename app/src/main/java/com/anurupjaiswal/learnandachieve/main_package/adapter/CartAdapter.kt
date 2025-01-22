package com.anurupjaiswal.learnandachieve.main_package.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.anurupjaiswal.learnandachieve.R
import com.anurupjaiswal.learnandachieve.databinding.ItemCartBinding
import com.anurupjaiswal.learnandachieve.model.PackageModel

import com.anurupjaiswal.learnandachieve.model.CartItem
import com.bumptech.glide.Glide

class CartAdapter(
    private val cartItems: List<CartItem>,
    private val onDeleteClick: (cart_id :String ) -> Unit
) : RecyclerView.Adapter<CartAdapter.CartViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CartViewHolder {
        val binding = ItemCartBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return CartViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CartViewHolder, position: Int) {
        val cartItem = cartItems[position]
        holder.bind(cartItem)
    }

    override fun getItemCount(): Int = cartItems.size

    inner class CartViewHolder(private val binding: ItemCartBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(cartItem: CartItem) {
            // Set data to views
            binding.itemTitle.text = cartItem.packageName
            binding.itemDiscountedPrice.text = "₹ ${cartItem.discountedPrice}"
            binding.itemOriginalPrice.text = "₹ ${cartItem.actualPrice}"

            // Load image using Glide
            /*Glide.with(binding.root.context)
                .load("https://your_base_url/${cartItem.mainImage}")
                .placeholder(R.drawable.placeholder_image) // Replace with a valid drawable resource
                .into(binding.itemImage)*/

            // Set delete button click listener
            binding.ivDelete.setOnClickListener {
                cartItem.cart_id?.let { it1 -> onDeleteClick(it1) }
            }
        }
    }
}
