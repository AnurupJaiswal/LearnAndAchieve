package com.anurupjaiswal.learnandachieve.main_package.adapter

import android.graphics.Paint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.anurupjaiswal.learnandachieve.R
import com.anurupjaiswal.learnandachieve.basic.utilitytools.Utils
import com.anurupjaiswal.learnandachieve.databinding.ItemCartBinding

import com.anurupjaiswal.learnandachieve.model.CartItem

class CartAdapter(
    private val cartItems: List<CartItem>,
    private var visibilityCondition: Boolean ,// Control whether views should be visible or not

    private val onDeleteClick: (cartId :String ) -> Unit
) : RecyclerView.Adapter<CartAdapter.CartViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CartViewHolder {
        val binding = ItemCartBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return CartViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CartViewHolder, position: Int) {
        val cartItem = cartItems[position]
        holder.bind(cartItem,visibilityCondition)
    }

    override fun getItemCount(): Int = cartItems.size

    inner class CartViewHolder(private val binding: ItemCartBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(cartItem: CartItem,visibilityCondition: Boolean) {
            // Set data to views
            binding.itemTitle.text = cartItem.packageName
            binding.itemDiscountedPrice.text = "₹ ${cartItem.discountedPrice}"
            binding.itemOriginalPrice.apply {
                text = "₹ ${cartItem.actualPrice}"
                paintFlags = paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
            }



            Utils.Picasso(cartItem.mainImage.toString(), binding.itemImage, R.drawable.ic_package)
            // Control the visibility of the delete button based on visibilityCondition
            if (visibilityCondition) {
                // If visibilityCondition is true, show and enable the delete button
                binding.ivDelete.visibility = View.VISIBLE
                binding.ivDelete.isEnabled = true
            } else {
                // If visibilityCondition is false, hide or disable the delete button
                binding.ivDelete.visibility = View.GONE
                binding.ivDelete.isEnabled = false
            }



            // Set delete button click listener
            binding.ivDelete.setOnClickListener {
                cartItem.cart_id?.let { it1 -> onDeleteClick(it1) }
            }
        }
    }
}
