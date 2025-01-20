package com.anurupjaiswal.learnandachieve.main_package.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.anurupjaiswal.learnandachieve.R
import com.anurupjaiswal.learnandachieve.model.RankPrize
import com.anurupjaiswal.learnandachieve.databinding.ItemRankPrizeBinding



class RankPrizeAdapter(private val rankPrizes: List<RankPrize>) :
    RecyclerView.Adapter<RankPrizeAdapter.RankPrizeViewHolder>()    {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RankPrizeViewHolder {
        // Inflate the layout using ViewBinding
        val binding = ItemRankPrizeBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return RankPrizeViewHolder(binding)
    }

    override fun onBindViewHolder(holder: RankPrizeViewHolder, position: Int) {
        val rankPrize = rankPrizes[position]
        holder.bind(rankPrize)
    }

    override fun getItemCount(): Int = rankPrizes.size

    // ViewHolder class using ViewBinding
    class RankPrizeViewHolder(private val binding: ItemRankPrizeBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(rankPrize: RankPrize) {
            binding.tvRank.text = rankPrize.rankName
            binding.tvPrize.text = rankPrize.prizeAmount
        }
    }
}
