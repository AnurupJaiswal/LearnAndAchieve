package com.anurupjaiswal.learnandachieve.main_package.adapter

import android.graphics.Paint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.anurupjaiswal.learnandachieve.R
import com.anurupjaiswal.learnandachieve.model.PackageModel

import com.google.android.material.card.MaterialCardView

class PurchasePackageAdapter(private val packageList: List<PackageModel>) :
    RecyclerView.Adapter<PurchasePackageAdapter.PurchasePackageViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PurchasePackageViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_purchase_package, parent, false)
        return PurchasePackageViewHolder(view)
    }

    override fun onBindViewHolder(holder: PurchasePackageViewHolder, position: Int) {
        val item = packageList[position]
        holder.bind(item)
    }

    override fun getItemCount() = packageList.size

    class PurchasePackageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val title: TextView = itemView.findViewById(R.id.title)
        private val price: TextView = itemView.findViewById(R.id.price)
        private val tvPackageDetails: TextView = itemView.findViewById(R.id.tvPackageDetails)
        private val strikePrice: TextView = itemView.findViewById(R.id.strikethrough_price)
        private val image: ImageView = itemView.findViewById(R.id.image)
        private val button: MaterialCardView = itemView.findViewById(R.id.mcvAddToCart)

        fun bind(packageItem: PackageModel) {
            title.text = packageItem.title
            price.text = "₹ ${packageItem.price}"
            strikePrice.text = "₹ ${packageItem.originalPrice}"
            strikePrice.paintFlags = strikePrice.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
            image.setImageResource(packageItem.imageRes)

            tvPackageDetails.setOnClickListener {
                val navController = itemView.findNavController()
                val bundle = Bundle().apply {
                    putString("title", packageItem.title)
                    putString("price", packageItem.price)
                    putString("originalPrice", packageItem.originalPrice)
                    putInt("imageRes", packageItem.imageRes)
                    putString("description", packageItem.description)
                }
                navController.navigate(R.id.packageDetailsFragment, bundle)
            }
        }


    }
}
