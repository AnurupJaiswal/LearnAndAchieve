package com.anurupjaiswal.learnandachieve.main_package.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.anurupjaiswal.learnandachieve.databinding.ItemPrivacyPolicyBinding
import com.anurupjaiswal.learnandachieve.model.PrivacyPolicyItem

class PrivacyPolicyAdapter(
    private val items: List<PrivacyPolicyItem>
) : RecyclerView.Adapter<PrivacyPolicyAdapter.PrivacyPolicyViewHolder>() {

    inner class PrivacyPolicyViewHolder(private val binding: ItemPrivacyPolicyBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: PrivacyPolicyItem) {
            binding.tvTitle.text = item.title
            binding.tvDescription.text = item.description
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PrivacyPolicyViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemPrivacyPolicyBinding.inflate(inflater, parent, false)
        return PrivacyPolicyViewHolder(binding)
    }

    override fun onBindViewHolder(holder: PrivacyPolicyViewHolder, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount(): Int = items.size
}
