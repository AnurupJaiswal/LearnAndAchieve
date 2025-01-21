package com.anurupjaiswal.learnandachieve.main_package.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Paint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.anurupjaiswal.learnandachieve.R
import com.anurupjaiswal.learnandachieve.basic.utilitytools.Utils
import com.anurupjaiswal.learnandachieve.databinding.ItemPurchasePackageBinding
import com.anurupjaiswal.learnandachieve.model.PackageData

class PurchasePackageAdapter(
    private val context: Context,
    private val packages: List<PackageData>
) : RecyclerView.Adapter<PurchasePackageAdapter.PackageViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PackageViewHolder {
        val binding = ItemPurchasePackageBinding.inflate(
            LayoutInflater.from(context),
            parent,
            false
        )
        return PackageViewHolder(binding)
    }

    override fun onBindViewHolder(holder: PackageViewHolder, position: Int) {
        val packageData = packages[position]
        holder.bind(packageData)
    }

    override fun getItemCount(): Int {
        return packages.size
    }

    inner class PackageViewHolder(private val binding: ItemPurchasePackageBinding) :
        RecyclerView.ViewHolder(binding.root) {

        @SuppressLint("SetTextI18n")
        fun bind(packageData: PackageData) {
            // Set title
            binding.title.text = packageData.packageName

            // Load image using Glide or Picasso
            Utils.Picasso(packageData.mainImage, binding.image, R.drawable.ic_package)

            // Set price
            binding.price.text = "₹${packageData.discountedPrice}"
            binding.strikethroughPrice.text = "₹${packageData.actualPrice}"
            binding.strikethroughPrice.paintFlags =
                binding.strikethroughPrice.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG

            // Handle Add to Cart button click
            binding.mcvAddToCart.setOnClickListener {

            }
        }
    }



}
