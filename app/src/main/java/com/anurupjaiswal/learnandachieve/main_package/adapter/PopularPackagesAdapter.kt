package com.anurupjaiswal.learnandachieve.main_package.adapter

import android.text.Spannable
import android.text.SpannableString
import android.text.style.StrikethroughSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.anurupjaiswal.learnandachieve.R
import com.anurupjaiswal.learnandachieve.databinding.ItemPackageCardBinding
import com.anurupjaiswal.learnandachieve.model.PopularPackageItem


class PopularPackagesAdapter(
    private val packageList: List<PopularPackageItem>
) : RecyclerView.Adapter<PopularPackagesAdapter.PackageViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PackageViewHolder {
        val binding = ItemPackageCardBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return PackageViewHolder(binding)
    }

    override fun onBindViewHolder(holder: PackageViewHolder, position: Int) {
        val packageItem = packageList[position]
        holder.bind(packageItem)
    }

    override fun getItemCount(): Int = packageList.size

    inner class PackageViewHolder(private val binding: ItemPackageCardBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bind(popularPackageItem: PopularPackageItem) {
            with(binding) {
                // Set text values
                tvTitle.text = popularPackageItem.title
                tvDescription.text = popularPackageItem.description
                discountedPrice.text = popularPackageItem.discountedPrice

                // Set strikethrough on original price
                val originalPriceText = popularPackageItem.originalPrice
                val spannableString = SpannableString(originalPriceText).apply {
                    setSpan(StrikethroughSpan(), 0, originalPriceText.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
                }
                originalPrice.text = spannableString

                // Show or hide the "Popular" label based on `showPopular`
               tvBadge .visibility = if (popularPackageItem.showPopular) View.VISIBLE else View.INVISIBLE

                // Load image using Glide (if you're using it)
                // Glide.with(itemView.context).load(popularPackageItem.imageUrl).into(imagePackage)
            }
        }
    }
}
